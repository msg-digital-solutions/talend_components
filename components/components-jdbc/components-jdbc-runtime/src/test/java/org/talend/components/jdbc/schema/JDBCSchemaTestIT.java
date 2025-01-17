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
package org.talend.components.jdbc.schema;

import static org.junit.Assert.assertEquals;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Field;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.talend.components.api.container.DefaultComponentRuntimeContainerImpl;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.jdbc.ComponentConstants;
import org.talend.components.jdbc.common.DBTestUtils;
import org.talend.components.jdbc.runtime.JDBCSource;
import org.talend.components.jdbc.runtime.JdbcRuntimeUtils;
import org.talend.components.jdbc.runtime.setting.AllSetting;
import org.talend.components.jdbc.tjdbcinput.TJDBCInputDefinition;
import org.talend.components.jdbc.tjdbcinput.TJDBCInputProperties;
import org.talend.daikon.avro.AvroUtils;
import org.talend.daikon.avro.SchemaConstants;

public class JDBCSchemaTestIT {

    public static AllSetting allSetting;

    private static final String tablename = "JDBCSCHEMA";
    
    @BeforeClass
    public static void beforeClass() throws Exception {
        allSetting = DBTestUtils.createAllSetting();

        try (Connection conn = JdbcRuntimeUtils.createConnection(allSetting)) {
          DBTestUtils.createTableWithSpecialName(conn,tablename);
        }
    }

    @AfterClass
    public static void afterClass() throws ClassNotFoundException, SQLException {
        try (Connection conn = JdbcRuntimeUtils.createConnection(allSetting)) {
            DBTestUtils.dropTestTable(conn, tablename);
        } finally {
            DBTestUtils.shutdownDBIfNecessary();
        }
    }

    @Test
    public void testGetSchema() throws Exception {
        TJDBCInputDefinition definition = new TJDBCInputDefinition();
        TJDBCInputProperties properties = DBTestUtils.createCommonJDBCInputProperties(allSetting, definition);

        properties.main.schema.setValue(DBTestUtils.createTestSchema3(true,tablename));
        properties.tableSelection.tablename.setValue(tablename);
        properties.sql.setValue(DBTestUtils.getSQL(tablename));

        JDBCSource source = DBTestUtils.createCommonJDBCSource(properties);

        RuntimeContainer container = new DefaultComponentRuntimeContainerImpl() {
            @Override
            public String getCurrentComponentId() {
                return "tJDBCInput1";
            }
        };
        java.net.URL mappings_url = this.getClass().getResource("/mappings");
        mappings_url = DBTestUtils.correctURL(mappings_url);
        container.setComponentData(container.getCurrentComponentId(), ComponentConstants.MAPPING_URL_SUBFIX, mappings_url);
        
        Schema schema = source.getEndpointSchema(container, tablename);
        assertEquals(tablename, schema.getName().toUpperCase());
        List<Field> columns = schema.getFields();
        DBTestUtils.testMetadata4SpecialName(columns);
    }

}
