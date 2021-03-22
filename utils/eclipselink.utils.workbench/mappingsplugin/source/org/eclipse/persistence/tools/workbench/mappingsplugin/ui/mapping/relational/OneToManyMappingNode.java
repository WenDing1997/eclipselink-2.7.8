/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.relational;

import org.eclipse.persistence.tools.workbench.framework.app.SelectionActionsPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWOneToManyMapping;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.MappingDescriptorNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.MappingNode;


public final class OneToManyMappingNode extends MappingNode {


    public OneToManyMappingNode(MWOneToManyMapping value, SelectionActionsPolicy mappingNodeTypePolicy, MappingDescriptorNode parent) {
        super(value, mappingNodeTypePolicy, parent);
    }


    // ************** AbstractApplicationNode overrides *************

    protected String accessibleNameKey() {
        return "ACCESSIBLE_ONE_TO_MANY_MAPPING_NODE";
    }


    // ************** ApplicationNode implementation *************

    public String helpTopicID() {
        return this.getDescriptorNode().mappingHelpTopicPrefix() + ".oneToMany";
    }

    protected String buildIconKey() {
        return ((MWOneToManyMapping) getMapping()).iconKey();
    }

    // ********** MWApplicationNode overrides **********

    protected Class propertiesPageClass() {
        return OneToManyMappingTabbedPropertiesPage.class;
    }

}
