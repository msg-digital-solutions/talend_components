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
package org.talend.components.marketo.runtime.client.rest.type;

public class AuthenticationInfo {

    private String secretKey;

    private String clientAccessID;

    public AuthenticationInfo() {
    }

    public AuthenticationInfo(String secretKey, String clientAccessID) {
        this.secretKey = secretKey;
        this.clientAccessID = clientAccessID;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getClientAccessID() {
        return clientAccessID;
    }

    public void setClientAccessID(String clientAccessID) {
        this.clientAccessID = clientAccessID;
    }
}
