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
package org.talend.components.jdbc.wizard;

import org.talend.components.api.wizard.ComponentWizard;
import org.talend.components.api.wizard.ComponentWizardDefinition;
import org.talend.daikon.properties.presentation.Form;

public class JDBCConnectionWizard extends ComponentWizard {

    JDBCConnectionWizardProperties connectionProperties;

    JDBCConnectionWizard(ComponentWizardDefinition def, String repositoryLocation) {
        super(def, repositoryLocation);

        connectionProperties = new JDBCConnectionWizardProperties("connection").setRepositoryLocation(repositoryLocation);
        connectionProperties.init();
        addForm(connectionProperties.getForm(Form.MAIN));
    }

    public void setupProperties(JDBCConnectionWizardProperties connectionProperties) {
        this.connectionProperties.copyValuesFrom(connectionProperties);
    }

}
