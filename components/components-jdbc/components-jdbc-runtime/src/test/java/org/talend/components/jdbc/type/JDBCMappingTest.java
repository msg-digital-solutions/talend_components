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
package org.talend.components.jdbc.type;

import java.sql.Types;
import java.util.List;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Field;
import org.junit.Assert;
import org.junit.Test;
import org.talend.components.jdbc.common.DBTestUtils;
import org.talend.components.jdbc.runtime.type.JDBCMapping;


public class JDBCMappingTest {

    private static final String tablename = "JDBCMAPPING";
    
    @Test
    public void testGetSQLTypeFromAvroType() {
        Schema schema = DBTestUtils.createAllTypesSchema(tablename);
        List<Field> fields = schema.getFields();
        
        Assert.assertEquals(Types.SMALLINT, JDBCMapping.getSQLTypeFromAvroType(fields.get(0)));
        Assert.assertEquals(Types.INTEGER, JDBCMapping.getSQLTypeFromAvroType(fields.get(1)));
        Assert.assertEquals(Types.BIGINT, JDBCMapping.getSQLTypeFromAvroType(fields.get(2)));
        Assert.assertEquals(Types.FLOAT, JDBCMapping.getSQLTypeFromAvroType(fields.get(3)));
        Assert.assertEquals(Types.DOUBLE, JDBCMapping.getSQLTypeFromAvroType(fields.get(4)));
        Assert.assertEquals(Types.DECIMAL, JDBCMapping.getSQLTypeFromAvroType(fields.get(5)));
        Assert.assertEquals(Types.VARCHAR, JDBCMapping.getSQLTypeFromAvroType(fields.get(6)));
        Assert.assertEquals(Types.VARCHAR, JDBCMapping.getSQLTypeFromAvroType(fields.get(7)));
        Assert.assertEquals(Types.OTHER, JDBCMapping.getSQLTypeFromAvroType(fields.get(8)));
        Assert.assertEquals(Types.VARCHAR, JDBCMapping.getSQLTypeFromAvroType(fields.get(9)));
        Assert.assertEquals(Types.DATE, JDBCMapping.getSQLTypeFromAvroType(fields.get(10)));
        Assert.assertEquals(Types.DATE, JDBCMapping.getSQLTypeFromAvroType(fields.get(11)));
        Assert.assertEquals(Types.DATE, JDBCMapping.getSQLTypeFromAvroType(fields.get(12)));
        Assert.assertEquals(Types.BOOLEAN, JDBCMapping.getSQLTypeFromAvroType(fields.get(13)));
    }
}
