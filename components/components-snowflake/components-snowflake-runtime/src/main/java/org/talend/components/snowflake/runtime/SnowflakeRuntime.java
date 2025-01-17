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
package org.talend.components.snowflake.runtime;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.snowflake.SnowflakeConnectionProperties;
import org.talend.components.snowflake.runtime.utils.DriverManagerUtils;
import org.talend.daikon.i18n.GlobalI18N;
import org.talend.daikon.i18n.I18nMessages;

/**
 * Contains methods for creating and closing Connection.
 */
public abstract class SnowflakeRuntime {

    protected static final I18nMessages I18N_MESSAGES = GlobalI18N.getI18nMessageProvider()
            .getI18nMessages(SnowflakeRuntime.class);

    public static final String KEY_CONNECTION = "connection";

    public static final String KEY_CONNECTION_PROPERTIES = "ConnectionProperties";

    public static final String KEY_TALEND_PRODUCT_VERSION = "TALEND_PRODUCT_VERSION";

    public abstract SnowflakeConnectionProperties getConnectionProperties();

    private transient Connection connection;

    /**
     * Creates or gets connection.
     * If component use existing connection then get it by reference component id from container(and check if it's not closed),
     * else creates new connection using {@link DriverManager} and saves it in container.
     *
     * @param container - runtime container
     * @return active connection.
     * @throws IOException may be thrown if referenced connection is closed or if failed to create connection using
     * {@link DriverManager}
     */
    public Connection createConnection(RuntimeContainer container)
            throws IOException {
        if (connection == null) {
            connection = createNewConnection(container);
        }
        return connection;
    }

    public Connection createNewConnection(RuntimeContainer container) throws IOException {
        Connection conn = null;
        SnowflakeConnectionProperties connectionProperties = getConnectionProperties();
        String refComponentId = connectionProperties.getReferencedComponentId();
        // Using another component's connection
        if (refComponentId != null) {
            // In a runtime container
            if (container != null) {
                conn = getConnection(container, refComponentId);
                if (isConnectionValid(conn)) {
                    return conn;
                }
                throw new IOException(I18N_MESSAGES.getMessage("error.refComponentNotConnected", refComponentId));
            }
            // Design time
            connectionProperties = connectionProperties.getReferencedConnectionProperties();
            // FIXME This should not happen - but does as of now
            if (connectionProperties == null) {
                throw new IOException(I18N_MESSAGES.getMessage("error.refComponentWithoutProperties", refComponentId));
            }
        }

        if (container != null) {
            connectionProperties.talendProductVersion = (String) container.getGlobalData(KEY_TALEND_PRODUCT_VERSION);
        }

        conn = DriverManagerUtils.getConnection(connectionProperties);
        try {
            conn.setAutoCommit(connectionProperties.autoCommit.getValue());
        } catch (SQLException e) {
            throw new IOException(e);
        }
        if (container != null) {
            container.setComponentData(container.getCurrentComponentId(), KEY_CONNECTION, conn);
            container.setComponentData(container.getCurrentComponentId(), KEY_CONNECTION_PROPERTIES, connectionProperties);
        }
        return conn;
    }

    public void closeConnection(RuntimeContainer container, Connection conn)
            throws SQLException {
        String refComponentId = getConnectionProperties().getReferencedComponentId();
        if ((refComponentId == null || container == null) && (conn != null && !conn.isClosed())) {
            conn.close();
        }
    }

    private Connection getConnection(RuntimeContainer container, String componentId) throws IOException {
        return  (Connection) container.getComponentData(componentId, KEY_CONNECTION);
    }

    private boolean isConnectionValid(Connection connection) throws IOException {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            throw new IOException(e);
        }
    }
}
