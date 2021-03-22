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
package org.eclipse.persistence.testing.models.transparentindirection;

import java.util.*;
import org.eclipse.persistence.descriptors.RelationalDescriptor;

/**
 * TopLink generated Project class.
 * <b>WARNING</b>: This code was generated by an automated tool.
 * Any changes will be lost when the code is re-generated
 */
public class GeneratedIndirectContainerProject extends org.eclipse.persistence.sessions.Project {

    /**
     * <b>WARNING</b>: This code was generated by an automated tool.
     * Any changes will be lost when the code is re-generated
     */
    public GeneratedIndirectContainerProject() {
        applyPROJECT();
        applyLOGIN();
        buildOrderDescriptor();
        buildOrderLineDescriptor();
        buildSalesRepDescriptor();
    }

    /**
     * TopLink generated method.
     * <b>WARNING</b>: This code was generated by an automated tool.
     * Any changes will be lost when the code is re-generated
     */
    protected void applyLOGIN() {
    }

    /**
     * TopLink generated method.
     * <b>WARNING</b>: This code was generated by an automated tool.
     * Any changes will be lost when the code is re-generated
     */
    protected void applyPROJECT() {
        setName("TransparentIndirection");
    }

    /**
     * TopLink generated method.
     * <b>WARNING</b>: This code was generated by an automated tool.
     * Any changes will be lost when the code is re-generated
     */
    protected void buildOrderDescriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();

        // SECTION: DESCRIPTOR
        descriptor.setJavaClass(org.eclipse.persistence.testing.models.transparentindirection.Order.class);
        Vector vector = new Vector();
        vector.addElement("ORD");
        descriptor.setTableNames(vector);
        descriptor.addPrimaryKeyFieldName("ORD.ID");

        // SECTION: PROPERTIES
        descriptor.setIdentityMapClass(org.eclipse.persistence.internal.identitymaps.FullIdentityMap.class);
        descriptor.setSequenceNumberName("order_seq");
        descriptor.setSequenceNumberFieldName("ID");
        descriptor.setExistenceChecking("Check cache");
        descriptor.setIdentityMapSize(100);

        // SECTION: COPY POLICY
        descriptor.createCopyPolicy("constructor");

        // SECTION: INSTANTIATION POLICY
        descriptor.createInstantiationPolicy("constructor");

        // SECTION: DIRECTCOLLECTIONMAPPING
        org.eclipse.persistence.mappings.DirectCollectionMapping directcollectionmapping = new org.eclipse.persistence.mappings.DirectCollectionMapping();
        directcollectionmapping.setAttributeName("contacts");
        directcollectionmapping.setIsReadOnly(false);
        directcollectionmapping.setUsesIndirection(false);
        directcollectionmapping.setIsPrivateOwned(true);
        directcollectionmapping.useCollectionClass(java.util.Vector.class);
        directcollectionmapping.setDirectFieldName("CONTACT.NAME");
        directcollectionmapping.setReferenceTableName("CONTACT");
        directcollectionmapping.addReferenceKeyFieldName("CONTACT.ORDER_ID", "ORD.ID");
        descriptor.addMapping(directcollectionmapping);

        // SECTION: DIRECTTOFIELDMAPPING
        org.eclipse.persistence.mappings.DirectToFieldMapping directtofieldmapping = new org.eclipse.persistence.mappings.DirectToFieldMapping();
        directtofieldmapping.setAttributeName("customerName");
        directtofieldmapping.setIsReadOnly(false);
        directtofieldmapping.setFieldName("ORD.CUSTNAME");
        descriptor.addMapping(directtofieldmapping);

        // SECTION: DIRECTTOFIELDMAPPING
        org.eclipse.persistence.mappings.DirectToFieldMapping directtofieldmapping1 = new org.eclipse.persistence.mappings.DirectToFieldMapping();
        directtofieldmapping1.setAttributeName("id");
        directtofieldmapping1.setIsReadOnly(false);
        directtofieldmapping1.setFieldName("ORD.ID");
        descriptor.addMapping(directtofieldmapping1);

        // SECTION: MANYTOMANYMAPPING
        org.eclipse.persistence.mappings.ManyToManyMapping manytomanymapping = new org.eclipse.persistence.mappings.ManyToManyMapping();
        manytomanymapping.setAttributeName("salesReps");
        manytomanymapping.setIsReadOnly(false);
        manytomanymapping.setUsesIndirection(false);
        manytomanymapping.setReferenceClass(org.eclipse.persistence.testing.models.transparentindirection.SalesRep.class);
        manytomanymapping.setIsPrivateOwned(false);
        manytomanymapping.useCollectionClass(java.util.Vector.class);
        manytomanymapping.setRelationTableName("ORDREP");
        manytomanymapping.addSourceRelationKeyFieldName("ORDREP.ORDER_ID", "ORD.ID");
        manytomanymapping.addTargetRelationKeyFieldName("ORDREP.SALEREP_ID", "SALEREP.ID");
        descriptor.addMapping(manytomanymapping);

        // SECTION: ONETOMANYMAPPING
        org.eclipse.persistence.mappings.OneToManyMapping onetomanymapping = new org.eclipse.persistence.mappings.OneToManyMapping();
        onetomanymapping.setAttributeName("lines");
        onetomanymapping.setIsReadOnly(false);
        onetomanymapping.setUsesIndirection(false);
        onetomanymapping.setReferenceClass(org.eclipse.persistence.testing.models.transparentindirection.OrderLine.class);
        onetomanymapping.setIsPrivateOwned(true);
        onetomanymapping.useCollectionClass(java.util.Vector.class);
        onetomanymapping.addTargetForeignKeyFieldName("ORDLINE.ORDER_ID", "ORD.ID");
        descriptor.addMapping(onetomanymapping);

        org.eclipse.persistence.testing.tests.transparentindirection.IndirectListTestDatabase.modifyOrderDescriptor(descriptor);
        addDescriptor(descriptor);
    }

    /**
     * TopLink generated method.
     * <b>WARNING</b>: This code was generated by an automated tool.
     * Any changes will be lost when the code is re-generated
     */
    protected void buildOrderLineDescriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();

        // SECTION: DESCRIPTOR
        descriptor.setJavaClass(org.eclipse.persistence.testing.models.transparentindirection.OrderLine.class);
        Vector vector = new Vector();
        vector.addElement("ORDLINE");
        descriptor.setTableNames(vector);
        descriptor.addPrimaryKeyFieldName("ORDLINE.ID");

        // SECTION: PROPERTIES
        descriptor.setIdentityMapClass(org.eclipse.persistence.internal.identitymaps.FullIdentityMap.class);
        descriptor.setSequenceNumberName("orderline");
        descriptor.setSequenceNumberFieldName("ID");
        descriptor.setExistenceChecking("Check cache");
        descriptor.setIdentityMapSize(100);

        // SECTION: COPY POLICY
        descriptor.createCopyPolicy("constructor");

        // SECTION: INSTANTIATION POLICY
        descriptor.createInstantiationPolicy("constructor");

        // SECTION: DIRECTTOFIELDMAPPING
        org.eclipse.persistence.mappings.DirectToFieldMapping directtofieldmapping = new org.eclipse.persistence.mappings.DirectToFieldMapping();
        directtofieldmapping.setAttributeName("id");
        directtofieldmapping.setIsReadOnly(false);
        directtofieldmapping.setFieldName("ORDLINE.ID");
        descriptor.addMapping(directtofieldmapping);

        // SECTION: DIRECTTOFIELDMAPPING
        org.eclipse.persistence.mappings.DirectToFieldMapping directtofieldmapping1 = new org.eclipse.persistence.mappings.DirectToFieldMapping();
        directtofieldmapping1.setAttributeName("itemName");
        directtofieldmapping1.setIsReadOnly(false);
        directtofieldmapping1.setFieldName("ORDLINE.ITEM_NAME");
        descriptor.addMapping(directtofieldmapping1);

        // SECTION: DIRECTTOFIELDMAPPING
        org.eclipse.persistence.mappings.DirectToFieldMapping directtofieldmapping2 = new org.eclipse.persistence.mappings.DirectToFieldMapping();
        directtofieldmapping2.setAttributeName("quantity");
        directtofieldmapping2.setIsReadOnly(false);
        directtofieldmapping2.setFieldName("ORDLINE.QUANTITY");
        descriptor.addMapping(directtofieldmapping2);

        // SECTION: ONETOONEMAPPING
        org.eclipse.persistence.mappings.OneToOneMapping onetoonemapping = new org.eclipse.persistence.mappings.OneToOneMapping();
        onetoonemapping.setAttributeName("order");
        onetoonemapping.setIsReadOnly(false);
        onetoonemapping.setUsesIndirection(false);
        onetoonemapping.setReferenceClass(org.eclipse.persistence.testing.models.transparentindirection.Order.class);
        onetoonemapping.setIsPrivateOwned(false);
        onetoonemapping.addForeignKeyFieldName("ORDLINE.ORDER_ID", "ORD.ID");
        descriptor.addMapping(onetoonemapping);
        addDescriptor(descriptor);
    }

    /**
     * TopLink generated method.
     * <b>WARNING</b>: This code was generated by an automated tool.
     * Any changes will be lost when the code is re-generated
     */
    protected void buildSalesRepDescriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();

        // SECTION: DESCRIPTOR
        descriptor.setJavaClass(org.eclipse.persistence.testing.models.transparentindirection.SalesRep.class);
        Vector vector = new Vector();
        vector.addElement("SALEREP");
        descriptor.setTableNames(vector);
        descriptor.addPrimaryKeyFieldName("SALEREP.ID");

        // SECTION: PROPERTIES
        descriptor.setIdentityMapClass(org.eclipse.persistence.internal.identitymaps.FullIdentityMap.class);
        descriptor.setSequenceNumberName("salesrep");
        descriptor.setSequenceNumberFieldName("ID");
        descriptor.setExistenceChecking("Check cache");
        descriptor.setIdentityMapSize(100);

        // SECTION: COPY POLICY
        descriptor.createCopyPolicy("constructor");

        // SECTION: INSTANTIATION POLICY
        descriptor.createInstantiationPolicy("constructor");

        // SECTION: DIRECTTOFIELDMAPPING
        org.eclipse.persistence.mappings.DirectToFieldMapping directtofieldmapping = new org.eclipse.persistence.mappings.DirectToFieldMapping();
        directtofieldmapping.setAttributeName("id");
        directtofieldmapping.setIsReadOnly(false);
        directtofieldmapping.setFieldName("SALEREP.ID");
        descriptor.addMapping(directtofieldmapping);

        // SECTION: DIRECTTOFIELDMAPPING
        org.eclipse.persistence.mappings.DirectToFieldMapping directtofieldmapping1 = new org.eclipse.persistence.mappings.DirectToFieldMapping();
        directtofieldmapping1.setAttributeName("name");
        directtofieldmapping1.setIsReadOnly(false);
        directtofieldmapping1.setFieldName("SALEREP.NAME");
        descriptor.addMapping(directtofieldmapping1);

        // SECTION: MANYTOMANYMAPPING
        org.eclipse.persistence.mappings.ManyToManyMapping manytomanymapping = new org.eclipse.persistence.mappings.ManyToManyMapping();
        manytomanymapping.setAttributeName("orders");
        manytomanymapping.setIsReadOnly(true);
        manytomanymapping.setUsesIndirection(false);
        manytomanymapping.setReferenceClass(org.eclipse.persistence.testing.models.transparentindirection.Order.class);
        manytomanymapping.setIsPrivateOwned(false);
        manytomanymapping.useCollectionClass(java.util.Vector.class);
        manytomanymapping.setRelationTableName("ORDREP");
        manytomanymapping.addSourceRelationKeyFieldName("ORDREP.SALEREP_ID", "SALEREP.ID");
        manytomanymapping.addTargetRelationKeyFieldName("ORDREP.ORDER_ID", "ORD.ID");
        descriptor.addMapping(manytomanymapping);
        addDescriptor(descriptor);
    }
}
