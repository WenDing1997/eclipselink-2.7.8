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
package org.eclipse.persistence.testing.tests.lob;

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.mappings.*;
import org.eclipse.persistence.descriptors.RelationalDescriptor;

/**
 * This class was generated by the TopLink project class generator.
 * It stores the meta-data (descriptors) that define the TopLink mappings.
 * ## TopLink - 4.6.0 (Build 417) ##
 * @see org.eclipse.persistence.sessions.factories.ProjectClassGenerator
 */
public class LOBImageModelProject extends org.eclipse.persistence.sessions.Project {

    public LOBImageModelProject() {
        setName("LOBImageModelProject");
        applyLogin();

        addDescriptor(buildImageDescriptor());
    }

    public void applyLogin() {
        DatabaseLogin login = new DatabaseLogin();
        setLogin(login);
    }

    public RelationalDescriptor buildImageDescriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(org.eclipse.persistence.testing.tests.lob.Image.class);
        descriptor.addTableName("IMAGE");
        descriptor.addTableName("CLIP");
        descriptor.addPrimaryKeyFieldName("IMAGE.ID");

        // Descriptor properties.
        descriptor.useSoftCacheWeakIdentityMap();
        descriptor.setIdentityMapSize(100);
        descriptor.useRemoteSoftCacheWeakIdentityMap();
        descriptor.setRemoteIdentityMapSize(100);
        descriptor.setSequenceNumberFieldName("IMAGE.ID");
        descriptor.setSequenceNumberName("image_lob_seq");
        descriptor.setAlias("Image");

        // Query manager.
        descriptor.getQueryManager().checkCacheForDoesExist();
        //Named Queries

        // Event manager.

        // Mappings.
        DirectToFieldMapping idMapping = new DirectToFieldMapping();
        idMapping.setAttributeName("id");
        idMapping.setFieldName("IMAGE.ID");
        descriptor.addMapping(idMapping);

        DirectToFieldMapping pictureMapping = new DirectToFieldMapping();
        pictureMapping.setAttributeName("picture");
        pictureMapping.setFieldName("IMAGE.PICTURE");
        pictureMapping.setFieldClassification(java.sql.Blob.class);
        descriptor.addMapping(pictureMapping);

        DirectToFieldMapping scriptMapping = new DirectToFieldMapping();
        scriptMapping.setAttributeName("script");
        scriptMapping.setFieldName("IMAGE.SCRIPT");
        scriptMapping.setFieldClassification(java.sql.Clob.class);
        descriptor.addMapping(scriptMapping);

        DirectToFieldMapping audioMapping = new DirectToFieldMapping();
        audioMapping.setAttributeName("audio");
        audioMapping.setFieldName("CLIP.AUDIO");
        audioMapping.setFieldClassification(java.sql.Blob.class);
        descriptor.addMapping(audioMapping);

        DirectToFieldMapping commentaryMapping = new DirectToFieldMapping();
        commentaryMapping.setAttributeName("commentary");
        commentaryMapping.setFieldName("CLIP.COMMENTARY");
        commentaryMapping.setFieldClassification(java.sql.Clob.class);
        descriptor.addMapping(commentaryMapping);

        return descriptor;
    }

}