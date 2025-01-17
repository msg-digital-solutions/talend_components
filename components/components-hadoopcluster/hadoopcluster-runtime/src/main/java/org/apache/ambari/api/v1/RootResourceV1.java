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
package org.apache.ambari.api.v1;

import javax.ws.rs.Path;

/**
 * The root of the Ambari API. Provides access to all sub-resources available in version 1 of the
 * API.
 */
public interface RootResourceV1 {

    /**
     * @return The clusters resource handler.
     */
    @Path("/clusters")
    public ClustersResource getClustersResource();

}
