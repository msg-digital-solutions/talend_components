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
package org.talend.components.marklogic.data;

import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.talend.components.marklogic.tmarklogicconnection.MarkLogicConnectionProperties;
import org.talend.daikon.properties.presentation.Form;

public class MarkLogicDatasetPropertiesTest {

    private MarkLogicDatasetProperties properties;

    private MarkLogicConnectionProperties datastore;

    @Before
    public void setup() {
        properties = new MarkLogicDatasetProperties("dataset");
        datastore = new MarkLogicConnectionProperties("datastore");
        properties.setDatastoreProperties(datastore);
    }

    @Test
    public void testSetupLayout() {
        Form reference = properties.getForm(Form.REFERENCE);
        Assert.assertNull(reference);

        properties.main.setupLayout();
        properties.setupLayout();

        reference = properties.getForm(Form.REFERENCE);
        Assert.assertNotNull(reference);
        Assert.assertNotNull(reference.getWidget(properties.main));
    }

    @Test
    public void testGetDatastoreProperties() {
        Assert.assertEquals(datastore, properties.getDatastoreProperties());
    }


    @Test
    public void testAfterUseQueryOption() {
        properties.init();

        properties.useQueryOption.setValue(true);
        properties.afterUseQueryOption();

        boolean isQueryLiteralTypeVisible = properties.getForm(Form.ADVANCED).getWidget(properties.queryLiteralType).isVisible();
        boolean isQueryOptionNameVisible = properties.getForm(Form.ADVANCED).getWidget(properties.queryOptionName).isVisible();
        boolean isQueryLiteralsVisible = properties.getForm(Form.ADVANCED).getWidget(properties.queryOptionLiterals).isVisible();

        assertTrue(isQueryLiteralTypeVisible);
        assertTrue(isQueryOptionNameVisible);
        assertTrue(isQueryLiteralsVisible);
    }
}
