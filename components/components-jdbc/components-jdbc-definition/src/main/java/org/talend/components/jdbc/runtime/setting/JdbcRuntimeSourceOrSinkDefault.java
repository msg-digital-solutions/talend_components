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
package org.talend.components.jdbc.runtime.setting;

import org.apache.avro.Schema;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.common.UserPasswordProperties;
import org.talend.components.common.config.jdbc.Dbms;
import org.talend.daikon.NamedThing;
import org.talend.daikon.properties.Properties;
import org.talend.daikon.properties.property.Property;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public abstract class JdbcRuntimeSourceOrSinkDefault implements JdbcRuntimeSourceOrSink {

    private static final long serialVersionUID = 1L;

    private Connection conn;

    @Override
    public void setDBTypeMapping(Dbms mapping) {
        
    }
    
    @Override
    public Schema getSchemaFromQuery(RuntimeContainer runtime, String query) {
        return null;
    }

    @Override
    public List<NamedThing> getSchemaNames(RuntimeContainer runtime) throws IOException {
        return null;
    }

    @Override
    public Schema getEndpointSchema(RuntimeContainer runtime, String tableName) throws IOException {
        return null;
    }

    public Connection getConnection(RuntimeContainer runtime) throws ClassNotFoundException, SQLException {
        if (conn == null) {
            conn = connect(runtime);
        }
        return conn;
    }

    public void initConnection(RuntimeContainer runtime) throws ClassNotFoundException, SQLException {
        conn = connect(runtime);
    }

    protected Connection connect(RuntimeContainer runtime) throws ClassNotFoundException, SQLException {
        return null;
    }

    protected String getLogString(Properties properties) {
        StringBuilder sb = new StringBuilder();
        for (NamedThing nt : properties.getProperties()) {
            if (nt instanceof UserPasswordProperties) {
                continue;
            }
            if (nt instanceof Property) {
                sb.append(nt.getName()).append(":").append(((Property) nt).getValue()).append(", ");
            } else if (nt instanceof Properties) {
                sb.append(getLogString((Properties) nt));
            }
        }
            return sb.toString();
    }

}
