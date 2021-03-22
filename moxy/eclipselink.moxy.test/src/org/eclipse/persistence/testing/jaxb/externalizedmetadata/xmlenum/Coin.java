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
// dmccann - January 12/2010 - 2.0 - Initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlenum;

@javax.xml.bind.annotation.XmlType(name="coin-enum")
@javax.xml.bind.annotation.XmlEnum(String.class)
public enum Coin {
    @javax.xml.bind.annotation.XmlEnumValue("1") PENNY,
    @javax.xml.bind.annotation.XmlEnumValue("5") NICKEL,
    @javax.xml.bind.annotation.XmlEnumValue("10") DIME,
    @javax.xml.bind.annotation.XmlEnumValue("24") QUARTER
}
