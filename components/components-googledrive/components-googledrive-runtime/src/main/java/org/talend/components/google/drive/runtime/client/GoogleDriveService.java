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
package org.talend.components.google.drive.runtime.client;

import java.io.IOException;
import java.security.GeneralSecurityException;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;

public class GoogleDriveService {

    private String applicationName;

    private NetHttpTransport httpTransport;

    private Credential credential;

    private Integer readTimeout;

    public Credential getCredential() {
        return credential;
    }

    public void setCredential(Credential credential) {
        this.credential = credential;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public NetHttpTransport getHttpTransport() {
        return httpTransport;
    }

    public void setHttpTransport(NetHttpTransport httpTransport) {
        this.httpTransport = httpTransport;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(Integer readTimeout) {
        this.readTimeout = readTimeout;
    }

    public GoogleDriveService(String applicationName, NetHttpTransport httpTransport, Credential credential) {
        this(applicationName, httpTransport, credential, null);
    }

    public GoogleDriveService(String applicationName, NetHttpTransport httpTransport, Credential credential, Integer readTimeout) {
        this.applicationName = applicationName;
        this.credential = credential;
        this.httpTransport = httpTransport;
        this.readTimeout = readTimeout;
    }

    /**
     * Build and return an authorized Drive client service.
     * 
     * @return authorized Drive client service
     * @throws GeneralSecurityException when credentials fails
     * @throws IOException when credentials fails
     */
    public Drive getDriveService() throws GeneralSecurityException, IOException {
        return new Drive.Builder(getHttpTransport(), JacksonFactory.getDefaultInstance(), setHttpTimeout(getCredential()))
                .setApplicationName(getApplicationName()).build();
    }

    private HttpRequestInitializer setHttpTimeout(final HttpRequestInitializer requestInitializer) {
        return readTimeout != null ? httpRequest -> {
            requestInitializer.initialize(httpRequest);
            httpRequest.setReadTimeout(readTimeout * 1000);
        } : requestInitializer;
    }

}
