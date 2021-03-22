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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.relational;

import java.util.ListResourceBundle;

public class UiDescriptorRelationalBundle
    extends ListResourceBundle
{
    static final Object[][] contents =
    {
        //MorphToAggregateAction
        {"MORPH_TO_AGGREGATE_DESCRIPTOR_ACTION.text", "&Aggregate"},
        {"MORPH_TO_AGGREGATE_DESCRIPTOR_ACTION.toolTipText", "Change Descriptor Type to Aggregate Descriptor"},

        //MorphToRelationalAction
        {"MORPH_TO_CLASS_DESCRIPTOR_ACTION.text", "&Class"},
        {"MORPH_TO_CLASS_DESCRIPTOR_ACTION.toolTipText", "Change Descriptor Type to Class Descriptor"},

        {"TRANSACTIONAL_DESCRIPTOR_EJB_POLICY_TOOLTIP", "Change Descriptor Type to EJB Descriptor"},

        //RelationalDescriptorTabbedPropertiesPage
        {"RELATIONAL_DESCRIPTOR_QUERIES_TAB",         "Queries"},
        {"RELATIONAL_DESCRIPTOR_QUERY_KEYS_TAB",      "Query Keys"},
        {"RELATIONAL_DESCRIPTOR_INHERITANCE_TAB",     "Inheritance"},
        {"RELATIONAL_DESCRIPTOR_CACHING_TAB",         "Caching"},
        {"RELATIONAL_DESCRIPTOR_EVENTS_TAB",          "Events"},
        {"RELATIONAL_DESCRIPTOR_LOCKING_TAB",         "Locking"},
        {"RELATIONAL_DESCRIPTOR_MULTI_TABLE_TAB",     "Multitable Info"},
        {"RELATIONAL_DESCRIPTOR_RETURNING_TAB",       "Returning"},
        {"RELATIONAL_DESCRIPTOR_EJB_INFO_TAB",        "EJB Info"},


        //RelationalDescriptorInfoPropertiesPage
        {"associatedTable*", "&Associated Table:"},
        {"PRIMARY_TABLE_LIST_BROWSER_DIALOG.title", "Select Primary Table"},
        {"PRIMARY_TABLE_LIST_BROWSER_DIALOG.listLabel", "&Tables:"},


        //RelationalDescriptorPrimaryKeysPanel
        {"PRIMARY_KEYS_PANEL.TITLE", "Primary Keys"},
        {"PRIMARY_KEYS_DIALOG.TITLE", "Select Primary Keys"},
        {"PRIMARY_KEYS_PANEL_ADD_BUTTON", "A&dd..."},
        {"PRIMARY_KEYS_PANEL_REMOVE_BUTTON", "Re&move"},

        //RelationalDescriptorQueryKeysProperyPage
        {"ADD_NEW_QUERYKEY", "Add New Query Key"},
        {"ENTER_NEW_QUERYKEY_NAME", "Enter the name of the new query key:"},
        {"RENAME_QUERYKEY_TITLE", "Rename ''{0}''"},
        {"RENAME_QUERYKEY_MESSAGE", "Enter a new name for the query key:"},
        {"CANNOT_REMOVE_AUTOGENERATED", "Cannot remove auto-generated query keys."},
        {"QUERYKEY_REMOVE", "Query Key Remove"},
        {"QUERYKEYS", "Query Keys"},
        {"FIELD", "Fiel&d:"},

        //RelationalDescriptorSequencingPanel
        {"useSequencing", "&Use Sequencing:"},
         {"name", "Nam&e:"},
        {"table", "Ta&ble:"},
        {"field*", "Fie&ld:"},
        {"SEQUENCE_TABLE_LIST_BROWSER_DIALOG.title", "Select Sequencing Table"},
        {"SEQUENCE_TABLE_LIST_BROWSER_DIALOG.listLabel", "&Tables:"},
        {"SEQUENCE_FIELD_LIST_BROWSER_DIALOG.title", "Select Sequencing Database Field"},
        {"SEQUENCE_FIELD_LIST_BROWSER_DIALOG.listLabel", "&Database Fields:"},

        // RelationalLockingPolicyPage
        {"LOCKING_ADD_REMOVE_DIALOG_TITLE", "Select Locking Fields"},

        //InterfaceDescriptorPropertiesPage
        {"CHOOSE_DESCRIPTORS_THAT_IMPLEMENT_LABEL", "Choose the descriptors that implement this interface and share at least one common query key."},
        {"IMPLEMENTOR_DESCRIPTOR_LIST_BROWSER_DIALOG.title", "Choose Implementor Descriptor"},
        {"IMPLEMENTOR_DESCRIPTOR_LIST_BROWSER_DIALOG.listLabel", "&Implementors:"},
        {"IMPLEMENTORS_LABEL", "&Implementors:"},
        {"COMMON_QUERY_KEYS_LABEL", "&Common Query Keys:"},

        // Multitable Info Properties Page
        {"MULTI_TABLE_INFO_POLICY_PRIMARY_TABLE", "Primary Table:"},
        {"MULTI_TABLE_INFO_POLICY_ADDITIONAL_TABLES", "Add&itional Tables:"},
        {"MULTI_TABLE_INFO_POLICY_ASSOCIATION_TO_PRIMARY", "Association To Primary Table:"},
        {"MULTI_TABLE_INFO_POLICY_TABLE_REFERENCE", "Table Refere&nce:"},
        {"MULTI_TABLE_INFO_POLICY_PRIMARY_KEYS_HAVE", "Primary &Keys Have the Same Name"},
        {"MULTI_TABLE_INFO_POLICY_REFERENCE", "Ref&erence"},
        {"MULTI_TABLE_INFO_POLICY_ADD_TABLE_DIALOG.title", "Select Associated Table"},
        {"MULTI_TABLE_INFO_POLICY_ADD_TABLE_DIALOG.listLabel", "&Tables:"},

        // Advanced Properties Menu Items
        {"MULTI_TABLE_INFO_ACTION", "Multitable Info"},

        // Relational Locking Policy
        {"RELATIONAL_LOCKING_POLICY_DATABASE_FIELD", "&Database Field:"},
        {"VERSION_LOCKING_FIELD_LIST_BROWSER_DIALOG.title", "Select Version Locking Field"},
        {"VERSION_LOCKING_FIELD_LIST_BROWSER_DIALOG.listLabel", "&Database Fields:"},

        // ColumnPairsPanel
        {"SOURCE_COLUMN_COLUMN_HEADER", "Source Column"},
        {"TARGET_COLUMN_COLUMN_HEADER", "Target Column"},
        {"ADD_ASSOCIATION_BUTTON_TEXT", "A&dd"},
        {"REMOVE_ASSOCIATION_BUTTON_TEXT", "Re&move"},
        {"REMOVE_FIELD_ASSOCIATIONS_WARNING_DIALOG.title", "Remove Selected Field Associations"},
        {"REMOVE_FIELD_ASSOCIATIONS_WARNING_DIALOG.message", "Are you sure you want to delete the selected field asociations?"},

        // Returning Properties Page
        {"RETURNING_POLICY_ADD_FIELD_DIALOG.title", "Select a Database Field"},
        {"DATABASE_FIELD_LIST_BROWSER_DIALOG.title", "Select a Database Field"},
        {"DATABASE_FIELD_LIST_BROWSER_DIALOG.listLabel", "&Database Fields:"},
        {"UPDATE_ADD_BUTTON", "A&dd..."},
        {"UPDATE_REMOVE_BUTTON", "Rem&ove..."},

        {"GENERATE_TABLES_FROM_DESCRIPTORS_MENU_ITEM", "&Generate Tables From Descriptors"},
        {"ALL_DESCRIPTORS_LABEL_MENU_ITEM", "&All Class Descriptors"},
        {"SELECTED_DESCRIPTORS_MENU_ITEM", "&Selected Class Descriptors"},

        //TableGenerator
        {"unableToFindKeyFieldsForInSuperclasses", "Unable to find key fields for {0} in superclasses."},
        {"noKeyFieldFound", "No key field found in {0}. Adding one to proceed."},
        {"pleaseEditJavaSourceFileAppropriately", " Edit java source file appropriately!"},
        {"refersToWhichHasNoBackpointer", "{0}.{1} refers to {2} which has no backpointer. Creating a separate relationship table."},
        {"isBackpointerFor", "{0}.{1} is backpointer for {2}.{3}."},
        {"generateTablesFromDescriptors", "Generate Tables from Descriptors"},
        {"creatingTables", "Creating Tables..."},
        {"TABLE_GENERATOR_ASSUMPTION", "Assumption: {0}"},
        {"TABLE_GENERATOR_ERROR",      "Error: {0}"},
        {"TABLE_GENERATOR_WARNING",    "Note: {0}"},
        {"TABLE_GENERATOR_URGENT",     "Urgent: {0}"},
        {"TABLE_GENERATOR_SUCCESSFUL", "Auto-generating the table definitions has completed successfully."},
        {"isUnique", "{0} unique"},
        {"directCollectionHasEntries", "Direct collection {0}.{1} has {2} entries"},
        {"couldNotFindMapping", "In {0} mapping of {1} descriptor, couldn''t find a descriptor for class {2}"},
        {"descriptorDoesNotHaveAPrimaryTable", "Descriptor {0} does not have a primary table."},
        {"thereIsNoOneToOneBackpointer", "There is no One-To-One backpointer for the Many-To-Many Mapping {0} in descriptor {1}"},
        {"errorMapOneToManyMapping", "Error(3): Should not be here in TableGenerator.mapOneToManyMapping()"},
        {"errorCyclicDependency", "Error: Cyclic dependency in TableGenerator.sortClasses()"},
        {"creatingTablesFor", "Creating Tables for: {0}"},
        {"mappingCollectionVariables", "Mapping Collection Variables"},
        {"mappingNonCollectionVariables", "Mapping Non-Collection Variables"},
        {"VALUE_HOLDER_TYPE_SELECT", "No value type has been selected for the attribute \"{0}\" in the \"{1}\" class.  To facilitate automapping of value holders, it is necessary to know the value type first.  Please select it before automapping."},

        // AbstractGenerateTablesFromDescriptorsAction
        {"AUTO_GENERATING_TABLE_DEFINITIONS_STATUS_MESSAGE",       "Auto-generating table definitions may change existing definitions. If a descriptor does not have a primary key attribute matching the *id pattern, then the Workbench will add one automatically. Do you wish to continue?"},
        {"AUTO_GENERATING_TABLE_DEFINITIONS_STATUS_MESSAGE_SAVE",  "Auto-generating table definitions may change existing definitions. If a descriptor does not have a primary key attribute matching the *id pattern, then the Workbench will add one automatically. Do you wish to save the project first?"},
        {"AUTO_GENERATING_TABLE_DEFINITIONS_STATUS_DIALOG_TITLE",  "Status of Table Generation"},

        {"VIEW_CHOOSER_LIST_BROWSER_DIALOG.title", "Select Read Subclasses View"},
        {"VIEW_CHOOSER_LIST_BROWSER_DIALOG.listLabel", "&Tables:"},
    };


    /**
     * Returns the initialized array of keys and values that
     * represents the strings used by the classes in the descriptor
     * package.
     *
     * @return An table where the first element is the key used to
     * retrieve the second element, which is the value
     */
    public Object[][] getContents()
    {
        return contents;
    }
}
