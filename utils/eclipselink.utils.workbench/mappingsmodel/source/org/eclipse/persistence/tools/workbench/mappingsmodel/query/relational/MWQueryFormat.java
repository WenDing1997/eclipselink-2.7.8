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
package org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQuery;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQueryParameter;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.InheritancePolicy;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;

/**
 * An abstract class used to determine the format of a named query
 * Possible formats are auto-generated, sql, ejbql, and expression
 */
public abstract class MWQueryFormat extends MWModel {

    // ************ Static Methods *************

    public static XMLDescriptor buildDescriptor()
    {
        XMLDescriptor descriptor = new XMLDescriptor();

        descriptor.setJavaClass(MWQueryFormat.class);

        org.eclipse.persistence.descriptors.InheritancePolicy ip = (org.eclipse.persistence.descriptors.InheritancePolicy)descriptor.getInheritancePolicy();
        ip.setClassIndicatorFieldName("@type");
        ip.readSubclassesOnQueries();
        ip.addClassIndicator(MWStringQueryFormat.class, "string");
        ip.addClassIndicator(MWSQLQueryFormat.class, "sql");
        ip.addClassIndicator(MWEJBQLQueryFormat.class, "ejb-ql");
        ip.addClassIndicator(MWAutoGeneratedQueryFormat.class, "auto-generated");
        ip.addClassIndicator(MWExpressionQueryFormat.class, "expression");
        ip.addClassIndicator(MWStoredProcedureQueryFormat.class, "procedure");

        return descriptor;
    }

    public static ClassDescriptor legacy60BuildDescriptor()
    {
        ClassDescriptor descriptor = MWModel.legacy60BuildStandardDescriptor();
        descriptor.descriptorIsAggregate();
        descriptor.setJavaClass(MWQueryFormat.class);
        descriptor.setTableName("query-format");

        InheritancePolicy ip = descriptor.getInheritancePolicy();
        ip.setClassIndicatorFieldName("query-format-class");
        ip.readSubclassesOnQueries();
        Class<?>[] classes = {
                MWStringQueryFormat.class,
                MWSQLQueryFormat.class,
                MWEJBQLQueryFormat.class,
                MWAutoGeneratedQueryFormat.class,
                MWExpressionQueryFormat.class
        };
        for (int i = 0; i < classes.length; i++) {
            ip.addClassIndicator(classes[i], ClassTools.shortNameFor(classes[i]));
        }

        return descriptor;
    }

    /** Default constructor - for TopLink use only. */
    protected MWQueryFormat() {
        super();
    }

    MWQueryFormat(MWRelationalSpecificQueryOptions parent) {
        super(parent);
    }

    abstract String getType();


    public MWQuery getQuery() {
        return ((MWRelationalSpecificQueryOptions) getParent()).getQuery();
    }

    public MWCompoundExpression getExpression() {
        return null;
    }

    public String getQueryString() {
        return "";
    }

    public boolean orderingAttributesAllowed() {
        return false;
    }

    public boolean reportAttributesAllowed() {
        return false;
    }

    public boolean batchReadAttributesAllowed() {
        return false;
    }

    public boolean groupingAtributesAllowed() {
        return false;
    }

    // **************** Runtime Conversion ****************

    //Conversion to Runtime
    abstract void convertToRuntime(DatabaseQuery runtimeQuery);

    //Conversion from runtime
    abstract void convertFromRuntime(DatabaseQuery runtimeQuery);

    String getRuntimeParameterName(MWQueryParameter parameter) {
        return parameter.getName();
    }

}
