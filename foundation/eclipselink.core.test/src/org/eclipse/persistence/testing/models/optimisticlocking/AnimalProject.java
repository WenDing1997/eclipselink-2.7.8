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
//     tware - added cascaded locking testing
package org.eclipse.persistence.testing.models.optimisticlocking;

import java.util.Vector;

import org.eclipse.persistence.descriptors.VersionLockingPolicy;

public class AnimalProject extends org.eclipse.persistence.sessions.Project {

    public AnimalProject() {
        applyPROJECT();
        applyLOGIN();
        buildAnimalDescriptor();
        buildCatDescriptor();
        buildToyDescriptor();
        buildVetAppointmentDescriptor();
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
        setName("Animal");
    }

    /**
     * TopLink generated method.
     * <b>WARNING</b>: This code was generated by an automated tool.
     * Any changes will be lost when the code is re-generated
     */
    protected void buildAnimalDescriptor() {
        org.eclipse.persistence.descriptors.RelationalDescriptor descriptor = new org.eclipse.persistence.descriptors.RelationalDescriptor();

        // SECTION: DESCRIPTOR
        descriptor.setJavaClass(Animal.class);
        Vector vector = new Vector();
        vector.addElement("OL_ANIMAL");
        descriptor.setTableNames(vector);
        descriptor.addPrimaryKeyFieldName("OL_ANIMAL.ID");

        // SECTION: PROPERTIES
        descriptor.setIdentityMapClass(org.eclipse.persistence.internal.identitymaps.FullIdentityMap.class);
        descriptor.setSequenceNumberName("OL_ANIMAL");
        descriptor.setSequenceNumberFieldName("ID");
        descriptor.setExistenceChecking("Check cache");
        descriptor.setIdentityMapSize(100);
        descriptor.getInheritancePolicy().setShouldReadSubclasses(true);
        descriptor.getInheritancePolicy().setClassIndicatorFieldName("ANIMAL_TYPE");
        descriptor.getInheritancePolicy().setShouldUseClassNameAsIndicator(true);

        VersionLockingPolicy lockingPolicy = new VersionLockingPolicy();
        lockingPolicy.setWriteLockFieldName("OL_ANIMAL.VERSION");
        lockingPolicy.setIsCascaded(true);
        lockingPolicy.setIsStoredInCache(false);
        descriptor.setOptimisticLockingPolicy(lockingPolicy);

        // SECTION: COPY POLICY
        descriptor.createCopyPolicy("constructor");

        // SECTION: INSTANTIATION POLICY
        descriptor.createInstantiationPolicy("constructor");

        // SECTION: DIRECTTOFIELDMAPPING
        org.eclipse.persistence.mappings.DirectToFieldMapping directtofieldmapping = new org.eclipse.persistence.mappings.DirectToFieldMapping();
        directtofieldmapping.setAttributeName("id");
        directtofieldmapping.setIsReadOnly(false);
        directtofieldmapping.setFieldName("OL_ANIMAL.ID");
        descriptor.addMapping(directtofieldmapping);

        // SECTION: DIRECTTOFIELDMAPPING
        org.eclipse.persistence.mappings.DirectToFieldMapping directtofieldmapping1 = new org.eclipse.persistence.mappings.DirectToFieldMapping();
        directtofieldmapping1.setAttributeName("version");
        directtofieldmapping1.setIsReadOnly(false);
        directtofieldmapping1.setFieldName("OL_ANIMAL.VERSION");
        descriptor.addMapping(directtofieldmapping1);

        // SECTION: ONETOMANYMAPPING
        org.eclipse.persistence.mappings.OneToManyMapping onetomanymapping = new org.eclipse.persistence.mappings.OneToManyMapping();
        onetomanymapping.setAttributeName("appointments");
        onetomanymapping.setIsReadOnly(false);
        onetomanymapping.useTransparentList();
        onetomanymapping.setReferenceClass(VetAppointment.class);
        onetomanymapping.setIsPrivateOwned(true);
        onetomanymapping.addTargetForeignKeyFieldName("OL_VET_APPT.ANIMAL_ID", "OL_ANIMAL.ID");
        descriptor.addMapping(onetomanymapping);

        addDescriptor(descriptor);
    }

    /**
     * TopLink generated method.
     * <b>WARNING</b>: This code was generated by an automated tool.
     * Any changes will be lost when the code is re-generated
     */
    protected void buildCatDescriptor() {
        org.eclipse.persistence.descriptors.RelationalDescriptor descriptor = new org.eclipse.persistence.descriptors.RelationalDescriptor();

        // SECTION: DESCRIPTOR
        descriptor.setJavaClass(Cat.class);
        descriptor.getInheritancePolicy().setParentClass(Animal.class);
        descriptor.addTableName("OL_CAT");

        // SECTION: PROPERTIES
        descriptor.setIdentityMapClass(org.eclipse.persistence.internal.identitymaps.FullIdentityMap.class);
        descriptor.setExistenceChecking("Check cache");
        descriptor.setIdentityMapSize(100);
        descriptor.getInheritancePolicy().setShouldReadSubclasses(true);

        // SECTION: COPY POLICY
        descriptor.createCopyPolicy("constructor");

        // SECTION: INSTANTIATION POLICY
        descriptor.createInstantiationPolicy("constructor");

        // SECTION: DIRECTTOFIELDMAPPING
        org.eclipse.persistence.mappings.DirectToFieldMapping directtofieldmapping = new org.eclipse.persistence.mappings.DirectToFieldMapping();
        directtofieldmapping.setAttributeName("name");
        directtofieldmapping.setIsReadOnly(false);
        directtofieldmapping.setFieldName("OL_CAT.NAME");
        descriptor.addMapping(directtofieldmapping);

        // SECTION: ONETOMANYMAPPING
        org.eclipse.persistence.mappings.OneToManyMapping onetomanymapping = new org.eclipse.persistence.mappings.OneToManyMapping();
        onetomanymapping.setAttributeName("toys");
        onetomanymapping.setIsReadOnly(false);
        onetomanymapping.setUsesIndirection(true);
        onetomanymapping.setReferenceClass(Toy.class);
        onetomanymapping.setIsPrivateOwned(true);
        onetomanymapping.addTargetForeignKeyFieldName("OL_TOY.ANIMAL_ID", "OL_CAT.ID");
        descriptor.addMapping(onetomanymapping);
        addDescriptor(descriptor);
    }


    protected void buildToyDescriptor() {
        org.eclipse.persistence.descriptors.RelationalDescriptor descriptor = new org.eclipse.persistence.descriptors.RelationalDescriptor();

        // SECTION: DESCRIPTOR
        descriptor.setJavaClass(Toy.class);
        Vector vector = new Vector();
        vector.addElement("OL_TOY");
        descriptor.setTableNames(vector);
        descriptor.addPrimaryKeyFieldName("OL_TOY.ID");

        // SECTION: PROPERTIES
        descriptor.setIdentityMapClass(org.eclipse.persistence.internal.identitymaps.FullIdentityMap.class);
        descriptor.setSequenceNumberName("OL_TOY_SEQ");
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
        directtofieldmapping.setFieldName("OL_TOY.ID");
        descriptor.addMapping(directtofieldmapping);

        // SECTION: DIRECTTOFIELDMAPPING
        org.eclipse.persistence.mappings.DirectToFieldMapping directtofieldmapping1 = new org.eclipse.persistence.mappings.DirectToFieldMapping();
        directtofieldmapping1.setAttributeName("name");
        directtofieldmapping1.setIsReadOnly(false);
        directtofieldmapping1.setFieldName("OL_TOY.NAME");
        descriptor.addMapping(directtofieldmapping1);


        // SECTION: ONETOONEMAPPING
        org.eclipse.persistence.mappings.OneToOneMapping onetoonemapping = new org.eclipse.persistence.mappings.OneToOneMapping();
        onetoonemapping.setAttributeName("owner");
        onetoonemapping.setIsReadOnly(false);
        onetoonemapping.setUsesIndirection(true);
        onetoonemapping.setReferenceClass(Animal.class);
        onetoonemapping.setIsPrivateOwned(false);
        onetoonemapping.addForeignKeyFieldName("OL_TOY.ANIMAL_ID", "OL_ANIMAL.ID");
        descriptor.addMapping(onetoonemapping);

        addDescriptor(descriptor);
    }

    protected void buildVetAppointmentDescriptor() {
        org.eclipse.persistence.descriptors.RelationalDescriptor descriptor = new org.eclipse.persistence.descriptors.RelationalDescriptor();

        // SECTION: DESCRIPTOR
        descriptor.setJavaClass(VetAppointment.class);
        Vector vector = new Vector();
        vector.addElement("OL_VET_APPT");
        descriptor.setTableNames(vector);
        descriptor.addPrimaryKeyFieldName("OL_VET_APPT.ID");

        // SECTION: PROPERTIES
        descriptor.setIdentityMapClass(org.eclipse.persistence.internal.identitymaps.FullIdentityMap.class);
        descriptor.setSequenceNumberName("OL_VET_APPT_SEQ");
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
        directtofieldmapping.setFieldName("OL_VET_APPT.ID");
        descriptor.addMapping(directtofieldmapping);

        // SECTION: DIRECTTOFIELDMAPPING
        org.eclipse.persistence.mappings.DirectToFieldMapping directtofieldmapping1 = new org.eclipse.persistence.mappings.DirectToFieldMapping();
        directtofieldmapping1.setAttributeName("cost");
        directtofieldmapping1.setIsReadOnly(false);
        directtofieldmapping1.setFieldName("OL_VET_APPT.COST");
        descriptor.addMapping(directtofieldmapping1);

        // SECTION: ONETOONEMAPPING
        org.eclipse.persistence.mappings.OneToOneMapping onetoonemapping = new org.eclipse.persistence.mappings.OneToOneMapping();
        onetoonemapping.setAttributeName("animal");
        onetoonemapping.setIsReadOnly(false);
        onetoonemapping.setUsesIndirection(true);
        onetoonemapping.setReferenceClass(Animal.class);
        onetoonemapping.setIsPrivateOwned(false);
        onetoonemapping.addForeignKeyFieldName("OL_VET_APPT.ANIMAL_ID", "OL_ANIMAL.ID");
        descriptor.addMapping(onetoonemapping);

        addDescriptor(descriptor);
    }

}
