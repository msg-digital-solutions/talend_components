//============================================================================
//
// Copyright (C) 2006-2023 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
//============================================================================
package org.talend.components.snowflake.tsnowflakeoutput;

import static org.talend.daikon.properties.presentation.Widget.widget;
import static org.talend.daikon.properties.property.PropertyFactory.newEnum;
import static org.talend.daikon.properties.property.PropertyFactory.newString;
import static org.talend.daikon.properties.property.PropertyFactory.newBoolean;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.avro.Schema;
import org.talend.components.api.component.Connector;
import org.talend.components.api.component.ISchemaListener;
import org.talend.components.api.component.PropertyPathConnector;
import org.talend.components.common.SchemaProperties;
import org.talend.components.common.tableaction.TableAction;
import org.talend.components.snowflake.SnowflakeConnectionTableProperties;
import org.talend.components.snowflake.SnowflakeDbTypeProperties;
import org.talend.components.snowflake.SnowflakeTableProperties;
import org.talend.daikon.avro.AvroUtils;
import org.talend.daikon.avro.SchemaConstants;
import org.talend.daikon.properties.ValidationResult;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.presentation.Widget;
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.properties.property.PropertyFactory;
import org.talend.daikon.serialize.PostDeserializeSetup;
import org.talend.daikon.serialize.migration.SerializeSetVersion;

public class TSnowflakeOutputProperties extends SnowflakeConnectionTableProperties implements SerializeSetVersion {

    private static final int CONVERT_COLUMNS_AND_TABLE_TO_UPPERCASE_VERSION = 1;
    private static final int TABLE_ACTION_VERSION = 2;
    private static final int CONVERT_EMPTY_STRINGS_TO_NULL_VERSION = 3;
    private static final int USE_SCHEMA_KEYS_FOR_UPSERT_VERSION = 4;
    private static final int ENFORCE_USE_DATABASE_SCHEMA_VERSION = 5;

    public enum OutputAction {
        INSERT,
        UPDATE,
        UPSERT,
        DELETE
    }

    public Property<TableAction.TableActionEnum> tableAction = newEnum("tableAction", TableAction.TableActionEnum.class);

    public Property<OutputAction> outputAction = newEnum("outputAction", OutputAction.class); // $NON-NLS-1$

    public Property<String> upsertKeyColumn = newString("upsertKeyColumn"); //$NON-NLS-1$

    public Property<Boolean> useSchemaKeysForUpsert = newBoolean("useSchemaKeysForUpsert");
    
    public Property<Boolean> enforceDatabaseSchema = newBoolean("enforceDatabaseSchema");

    protected transient PropertyPathConnector FLOW_CONNECTOR = new PropertyPathConnector(Connector.MAIN_NAME, "schemaFlow");

    protected transient PropertyPathConnector REJECT_CONNECTOR = new PropertyPathConnector(Connector.REJECT_NAME, "schemaReject");

    public SchemaProperties schemaReject = new SchemaProperties("schemaReject"); //$NON-NLS-1$

    public Property<Boolean> convertColumnsAndTableToUppercase = newBoolean("convertColumnsAndTableToUppercase");

    public Property<Boolean> usePersonalDBType = newBoolean("usePersonalDBType");
    public SnowflakeDbTypeProperties dbtypeTable = new SnowflakeDbTypeProperties("dbtypeTable");

    public Property<Boolean> convertEmptyStringsToNull = newBoolean("convertEmptyStringsToNull");

    /**
     * Advanced property which specifies whether date mapping should be used
     */
    public Property<Boolean> useDateMapping = newBoolean("useDateMapping");

    /**
     * Advanced property which sets Date columns mapping to one of Snowflake Date and Time types.
     * Default value is DATE - the same default mapping as it was before this property introduction
     */
    public Property<DateMapping> dateMapping = newEnum("dateMapping", DateMapping.class);

    /**
     * Advanced property to indicate if we use default snowflake driver date -> string conversion,
     * or if we use the date pattern from schema.
     */
    public Property<Boolean> useSchemaDatePattern = newBoolean("useSchemaDatePattern");

    public Property<Boolean> dieOnError = PropertyFactory.newBoolean("dieOnError").setRequired();

    // Have to use an explicit class to get the override of afterTableName(), an anonymous
    // class cannot be public and thus cannot be called.
    public class TableSubclass extends SnowflakeTableProperties {

        public TableSubclass(String name) {
            super(name);
        }

        @Override
        public ValidationResult afterTableName() throws Exception {
            ValidationResult validationResult = super.afterTableName();
            if (table.main.schema.getValue() != null) {
                List<String> fieldNames = getFieldNames(table.main.schema);
                upsertKeyColumn.setPossibleValues(fieldNames);
            }
            return validationResult;
        }
    }

    public static final String FIELD_COLUMN_NAME= "columnName";
    public static final String FIELD_ROW_NUMBER= "rowNumber";
    public static final String FIELD_CATEGORY= "category";
    public static final String FIELD_CHARACTER= "character";
    public static final String FIELD_ERROR_MESSAGE= "errorMessage";
    public static final String FIELD_BYTE_OFFSET= "byteOffset";
    public static final String FIELD_LINE= "line";
    public static final String FIELD_SQL_STATE= "sqlState";
    public static final String FIELD_CODE= "code";


    public TSnowflakeOutputProperties(String name) {
        super(name);
    }

    @Override
    public void setupProperties() {
        super.setupProperties();

        tableAction.setValue(TableAction.TableActionEnum.NONE);
        outputAction.setValue(OutputAction.INSERT);
        ISchemaListener listener;

        //This condition was added due to some strange behaviour of serialization.
        if (table != null) {
            listener = table.schemaListener;
        } else {
            listener = new ISchemaListener() {

                @Override
                public void afterSchema() {
                    afterMainSchema();
                }
            };
        }

        table = new TableSubclass("table");
        table.connection = connection;
        table.setSchemaListener(listener);
        table.setupProperties();
        useSchemaKeysForUpsert.setValue(true);
        convertColumnsAndTableToUppercase.setValue(true);
        convertEmptyStringsToNull.setValue(false);

        usePersonalDBType.setValue(false);
        useDateMapping.setValue(false);
        dateMapping.setValue(DateMapping.DATE);

        useSchemaDatePattern.setValue(false);
    }

    @Override
    public void setupLayout() {
        super.setupLayout();
        Form mainForm = getForm(Form.MAIN);
        mainForm.addRow(tableAction);
        mainForm.addRow(outputAction);
        mainForm.addColumn(widget(upsertKeyColumn).setWidgetType(Widget.ENUMERATION_WIDGET_TYPE));
        mainForm.addRow(dieOnError);

        Form advancedForm = getForm(Form.ADVANCED);
        advancedForm.addRow(convertColumnsAndTableToUppercase);
        advancedForm.addRow(convertEmptyStringsToNull);
        advancedForm.addRow(widget(useSchemaKeysForUpsert));
        advancedForm.addRow(widget(enforceDatabaseSchema));

        advancedForm.addRow(usePersonalDBType);
        widget(usePersonalDBType).setVisible(false);

        Widget dbTypeTableWidget = new Widget(dbtypeTable);
        advancedForm.addRow(dbTypeTableWidget.setWidgetType(Widget.TABLE_WIDGET_TYPE));
        dbTypeTableWidget.setVisible(false);

        Widget useSnowflakeDatePatternWidget = new Widget(useSchemaDatePattern);
        useSnowflakeDatePatternWidget.setVisible(true);
        advancedForm.addRow(useSnowflakeDatePatternWidget);

        Widget useDateMappingWidget = new Widget(useDateMapping);
        useDateMappingWidget.setVisible(false);
        advancedForm.addRow(useDateMappingWidget);

        Widget dateMappingWidget = new Widget(dateMapping);
        dateMappingWidget.setVisible(false);
        advancedForm.addColumn(dateMappingWidget);
    }

    public void afterOutputAction() {
        refreshLayout(getForm(Form.MAIN));
    }

    public void afterTableAction() {
        refreshLayout(getForm(Form.MAIN));
    }

    public void afterUseDateMapping(){
        refreshLayout(getForm(Form.MAIN));
    }

    @Override
    public void refreshLayout(Form form) {
        super.refreshLayout(form);
        if (form.getName().equals(Form.MAIN)) {

            TableAction.TableActionEnum tableAction = this.tableAction.getValue();
            boolean isCreateTableAction = tableAction != null && tableAction.isCreateTableAction();

            Form advForm = getForm(Form.ADVANCED);
            if (advForm != null) {
                advForm.getWidget(dbtypeTable.getName()).setVisible(usePersonalDBType.getValue() && isCreateTableAction);
                advForm.getWidget(usePersonalDBType.getName()).setVisible(isCreateTableAction);
                advForm.getWidget(useDateMapping.getName()).setVisible(isCreateTableAction && isDesignSchemaDynamic());
                advForm.getWidget(dateMapping.getName()).setVisible(useDateMapping.getValue() && isCreateTableAction
                        && isDesignSchemaDynamic());

                boolean isUpsert = OutputAction.UPSERT.equals(outputAction.getValue());
                boolean isUseSchemaKeysForUpsert = useSchemaKeysForUpsert.getValue();
                advForm.getWidget(useSchemaKeysForUpsert.getName()).setHidden(!isUpsert);
                form.getWidget(upsertKeyColumn.getName()).setHidden(!isUpsert || isUseSchemaKeysForUpsert);
                if (isUpsert && !isUseSchemaKeysForUpsert) {
                    beforeUpsertKeyColumn();
                }
                advForm.getWidget(enforceDatabaseSchema.getName()).setVisible(isDesignSchemaDynamic());
            }
        }
    }

    public Schema getDesignSchema() {
        return table.main.schema.getValue();
    }

    public boolean isDesignSchemaDynamic() {
        return AvroUtils.isIncludeAllFields(getDesignSchema());
    }

    protected List<String> getFieldNames(Property<?> schema) {
        Schema s = (Schema) schema.getValue();
        List<String> fieldNames = new ArrayList<>();
        for (Schema.Field f : s.getFields()) {
            fieldNames.add(f.name());
        }
        return fieldNames;
    }

    public void beforeUpsertKeyColumn() {
        if (getSchema() != null) {
            upsertKeyColumn.setPossibleValues(getFieldNames(table.main.schema));
        }
    }

    public void afterUseSchemaKeysForUpsert(){
        refreshLayout(getForm(Form.MAIN));
    }

    public void afterUsePersonalDBType(){
        refreshLayout(getForm(Form.MAIN));
    }

    private void addSchemaField(String name, List<Schema.Field> fields) {
        Schema.Field field = new Schema.Field(name, Schema.create(Schema.Type.STRING), null, (Object) null);
        field.addProp(SchemaConstants.TALEND_IS_LOCKED, "false");
        field.addProp(SchemaConstants.TALEND_FIELD_GENERATED, "true");
        field.addProp(SchemaConstants.TALEND_COLUMN_DB_LENGTH, "255");
        fields.add(field);
    }

    private void updateOutputSchemas() {
        Schema inputSchema = table.main.schema.getValue();

        final List<Schema.Field> additionalRejectFields = new ArrayList<Schema.Field>();
        addSchemaField(FIELD_COLUMN_NAME, additionalRejectFields);
        addSchemaField(FIELD_ROW_NUMBER, additionalRejectFields);
        addSchemaField(FIELD_CATEGORY, additionalRejectFields);
        addSchemaField(FIELD_CHARACTER, additionalRejectFields);
        addSchemaField(FIELD_ERROR_MESSAGE, additionalRejectFields);
        addSchemaField(FIELD_BYTE_OFFSET, additionalRejectFields);
        addSchemaField(FIELD_LINE, additionalRejectFields);
        addSchemaField(FIELD_SQL_STATE, additionalRejectFields);
        addSchemaField(FIELD_CODE, additionalRejectFields);

        Schema rejectSchema = Schema.createRecord("rejectOutput", inputSchema.getDoc(), inputSchema.getNamespace(),
                inputSchema.isError());

        List<Schema.Field> copyFieldList = new ArrayList<>();
        copyFieldList.addAll(additionalRejectFields);

        rejectSchema.setFields(copyFieldList);

        schemaReject.schema.setValue(rejectSchema);
    }

    @Override
    protected Set<PropertyPathConnector> getAllSchemaPropertiesConnectors(boolean isOutputConnection) {
        HashSet<PropertyPathConnector> connectors = new HashSet<>();
        if (isOutputConnection) {
            connectors.add(REJECT_CONNECTOR);
        } else {
            connectors.add(MAIN_CONNECTOR);
        }
        return connectors;
    }

    @Override
    public void afterMainSchema() {
        updateOutputSchemas();
        beforeUpsertKeyColumn();
        dbtypeTable.setFieldNames(getFieldNames(table.main.schema));
        refreshLayout(getForm(Form.MAIN));
    }

    @Override
    public int getVersionNumber() {
        return 5;
    }

    @Override
    public boolean postDeserialize(int version, PostDeserializeSetup setup, boolean persistent) {
        boolean migrated = super.postDeserialize(version, setup, persistent);
        boolean migratedProperties = migrateProperties(version);
        return migrated || migratedProperties;
    }

    protected boolean migrateProperties(int version) {
        boolean migrated = false;
        if (version < CONVERT_COLUMNS_AND_TABLE_TO_UPPERCASE_VERSION) {
            convertColumnsAndTableToUppercase.setValue(false);
            migrated = true;
        }

        if(version < TABLE_ACTION_VERSION) {
            tableAction.setValue(TableAction.TableActionEnum.NONE);
            migrated = true;
        }

        if (version < CONVERT_EMPTY_STRINGS_TO_NULL_VERSION) {
            convertEmptyStringsToNull.setValue(true);
            migrated = true;
        }

        if (version < USE_SCHEMA_KEYS_FOR_UPSERT_VERSION) {
            useSchemaKeysForUpsert.setValue(
                    !OutputAction.UPSERT.equals(outputAction.getValue()));
            migrated = true;
        }
        
        if (version < ENFORCE_USE_DATABASE_SCHEMA_VERSION) {
            enforceDatabaseSchema.setValue(true);
            migrated = true;
        }

        return migrated;
    }

}
