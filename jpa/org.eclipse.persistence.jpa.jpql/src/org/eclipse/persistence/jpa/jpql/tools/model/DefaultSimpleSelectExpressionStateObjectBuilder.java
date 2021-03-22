/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation
//
package org.eclipse.persistence.jpa.jpql.tools.model;

import org.eclipse.persistence.jpa.jpql.tools.model.query.SimpleSelectClauseStateObject;

/**
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public class DefaultSimpleSelectExpressionStateObjectBuilder extends AbstractSimpleSelectExpressionStateObjectBuilder {

    /**
     * Creates a new <code>DefaultSimpleSelectExpressionStateObjectBuilder</code>.
     *
     * @param parent The select clause for which this builder can create a select expression
     */
    public DefaultSimpleSelectExpressionStateObjectBuilder(SimpleSelectClauseStateObject parent) {
        super(parent);
    }

    /**
     * {@inheritDoc}
     */
    public void commit() {
        getParent().setSelectItem(pop());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected SimpleSelectClauseStateObject getParent() {
        return (SimpleSelectClauseStateObject) super.getParent();
    }
}
