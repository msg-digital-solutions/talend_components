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
package org.talend.components.salesforce.integration;

import static org.junit.Assert.fail;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.talend.components.api.service.ComponentService;
import org.talend.components.common.test.DisablablePaxExam;
import org.talend.components.salesforce.SalesforceConnectionProperties;
import org.talend.components.salesforce.connection.oauth.SalesforceOAuthConnection;

@RunWith(DisablablePaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class OAthDepsTestIT {

    @Inject
    ComponentService osgiCompService;

    @Configuration
    public Option[] config() {
        return OsgiSalesforceComponentTestIT.getSalesforcePaxExamOption();
    }

    @Test
    public void setupComponentService() {
        try {
            // this the only checks that all import and export for OAuth ahtentication are correctly set for OSGI.
            SalesforceOAuthConnection salesforceOAuthConnection = new SalesforceOAuthConnection(
                    new SalesforceConnectionProperties("foo"), "http://localhost", null);
            salesforceOAuthConnection.login(null);// this should not throw ClassNotFoundException
        } catch (Exception e) {
            if (e instanceof ClassNotFoundException) {
                fail("this should not throw ClassNotFoundException");
            }
        }
    }

}
