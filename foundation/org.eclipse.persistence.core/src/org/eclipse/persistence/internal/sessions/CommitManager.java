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
package org.eclipse.persistence.internal.sessions;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.descriptors.OptimisticLockingPolicy;
import org.eclipse.persistence.mappings.*;
import org.eclipse.persistence.internal.databaseaccess.DatasourceCall;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.sessions.UnitOfWork.CommitOrderType;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.internal.localization.*;
import org.eclipse.persistence.internal.queries.DatabaseQueryMechanism;
import org.eclipse.persistence.descriptors.ClassDescriptor;


/**
 * This class maintains a commit stack and resolves circular references.
 */
public class CommitManager {
    /** Order based on mapping foreign key constraints on how to insert objects by class. */
    protected List<Class> commitOrder;

    /**
     * This tracks the commit state for the objects, PENDING, PRE, POST, COMPLETE.
     * The key is the object and the value is the state.
     */
    protected Map<Object, Integer> commitState;

    /** The commit is in progress, but the row has not been written. */
    protected static final Integer PRE = Integer.valueOf(1);
    /** The commit is in progress, and the row has been written. */
    protected static final Integer POST = Integer.valueOf(2);
    /** The commit is complete for the object. */
    protected static final Integer COMPLETE = Integer.valueOf(3);
    /** This object should be ignored. */
    protected static final Integer IGNORE = Integer.valueOf(4);

    /** Set of objects that had partial row written to resolve constraints. */
    protected Map shallowCommits;

    protected AbstractSession session;

    /** The commit manager is active while writing a set of objects (UOW), it is not active when writing a single object (DB session). */
    protected boolean isActive;

    /** Map of modification events used to defer insertion into m-m, dc, join tables. */
    protected Map<DatabaseMapping, List<Object[]>> dataModifications;

    /**
     * Map of deferred calls groups by their table.
     * This is used to defer multiple table writes for batching and deadlock avoidance.
     */
    protected Map<DatabaseTable, List<Object[]>> deferredCalls;

    /** List of orphaned objects pending deletion. */
    protected List objectsToDelete;

    /** Counter used to keep track of commit depth for non-UOW writes. */
    protected int commitDepth;

    /**
     * Create the commit manager on the session.
     * It must be initialized later on after the descriptors have been added.
     */
    public CommitManager(AbstractSession session) {
        this.session = session;
    }

    /**
     * Add the data query to be performed at the end of the commit.
     * This is done to decrease dependencies and avoid deadlock.
     */
    public void addDataModificationEvent(DatabaseMapping mapping, Object[] event) {
        if (!getDataModifications().containsKey(mapping)) {
            this.dataModifications.put(mapping, new ArrayList());
        }
        this.dataModifications.get(mapping).add(event);
    }

    /**
     * Add the data query to be performed at the end of the commit.
     * This is done to decrease dependencies and avoid deadlock.
     */
    public void addDeferredCall(DatabaseTable table, DatasourceCall call, DatabaseQueryMechanism mechanism) {
        if (!getDeferredCalls().containsKey(table)) {
            this.deferredCalls.put(table, new ArrayList());
        }
        Object[] arguments = new Object[2];
        arguments[0] = call;
        arguments[1] = mechanism;
        this.deferredCalls.get(table).add(arguments);
    }

    /**
     * Deletion are cached until the end.
     */
    public void addObjectToDelete(Object objectToDelete) {
        getObjectsToDelete().add(objectToDelete);
    }

    /**
     * Commit all of the objects as a single transaction.
     * This should commit the object in the correct order to maintain referential integrity.
     */
    public void commitAllObjectsWithChangeSet(UnitOfWorkChangeSet uowChangeSet) throws RuntimeException, DatabaseException, OptimisticLockException {
        reinitialize();
        this.isActive = true;
        this.session.beginTransaction();
        try {
            // PERF: if the number of classes in the project is large this loop can be a perf issue.
            // If only one class types changed, then avoid loop.
            if ((uowChangeSet.getObjectChanges().size() + uowChangeSet.getNewObjectChangeSets().size()) <= 1) {
                Iterator<Class> classes = uowChangeSet.getNewObjectChangeSets().keySet().iterator();
                if (classes.hasNext()) {
                    Class theClass = classes.next();
                    commitNewObjectsForClassWithChangeSet(uowChangeSet, theClass);
                }
                classes = uowChangeSet.getObjectChanges().keySet().iterator();
                if (classes.hasNext()) {
                    Class theClass = classes.next();
                    commitChangedObjectsForClassWithChangeSet(uowChangeSet, theClass);
                }
            } else {
                // The commit order is all of the classes ordered by dependencies, this is done for deadlock avoidance.
                List commitOrder = getCommitOrder();
                int size = commitOrder.size();
                for (int index = 0; index < size; index++) {
                    Class theClass = (Class)commitOrder.get(index);
                    commitAllObjectsForClassWithChangeSet(uowChangeSet, theClass);
                }
            }

            if (hasDeferredCalls()) {
                // Perform all batched up calls, done to avoid dependencies.
                for (List<Object[]> calls: this.deferredCalls.values()) {
                    for (Object[] argument : calls) {
                        ((DatabaseQueryMechanism)argument[1]).executeDeferredCall((DatasourceCall)argument[0]);
                    }
                }
            }

            if (hasDataModifications()) {
                // Perform all batched up data modifications, done to avoid dependencies.
                for (Map.Entry<DatabaseMapping, List<Object[]>> entry: this.dataModifications.entrySet()) {
                    List<Object[]> events = entry.getValue();
                    int size = events.size();
                    DatabaseMapping mapping = entry.getKey();
                    for (int index = 0; index < size; index++) {
                        Object[] event = events.get(index);
                        mapping.performDataModificationEvent(event, getSession());
                    }
                }
            }

            if (hasObjectsToDelete()) {
                // These are orphaned objects, to be deleted from private ownership updates.
                // TODO: These should be added to the unit of work deleted so they are deleted in the correct order.
                List objects = getObjectsToDelete();
                int size = objects.size();
                reinitialize();
                for (int index = 0; index < size; index++) {
                    this.session.deleteObject(objects.get(index));
                }
            }

            this.session.commitTransaction();
        } catch (RuntimeException exception) {
            this.session.rollbackTransaction();
            throw exception;
        } finally {
            reinitialize();
            this.isActive = false;
        }
    }

    /**
     * Commit all of the objects of the class type in the change set.
     * This allows for the order of the classes to be processed optimally.
     */
    protected void commitAllObjectsForClassWithChangeSet(UnitOfWorkChangeSet uowChangeSet, Class theClass) {
        // Although new objects should be first, there is an issue that new objects get added to non-new after the insert,
        // so the object would be written twice.
        commitChangedObjectsForClassWithChangeSet(uowChangeSet, theClass);
        commitNewObjectsForClassWithChangeSet(uowChangeSet, theClass);
    }

    /**
     * Commit all of the objects of the class type in the change set.
     * This allows for the order of the classes to be processed optimally.
     */
    protected void commitNewObjectsForClassWithChangeSet(UnitOfWorkChangeSet uowChangeSet, Class theClass) {
        Map<ObjectChangeSet, ObjectChangeSet> newObjectChangesList = uowChangeSet.getNewObjectChangeSets().get(theClass);
        if (newObjectChangesList != null) { // may be no changes for that class type.
            AbstractSession session = getSession();
            ClassDescriptor descriptor = session.getDescriptor(theClass);
            List<ObjectChangeSet> newChangeSets = new ArrayList(newObjectChangesList.values());
            int size = newChangeSets.size();
            for (int index = 0; index < size; index++) {
                ObjectChangeSet changeSetToWrite = newChangeSets.get(index);
                Object objectToWrite = changeSetToWrite.getUnitOfWorkClone();
                if (!isProcessedCommit(objectToWrite)) {
                    // PERF: Get the descriptor query, to avoid extra query creation.
                    InsertObjectQuery commitQuery = descriptor.getQueryManager().getInsertQuery();
                    if (commitQuery == null) {
                        commitQuery = new InsertObjectQuery();
                        commitQuery.setDescriptor(descriptor);
                    } else {
                        // Ensure original query has been prepared.
                        commitQuery.checkPrepare(session, commitQuery.getTranslationRow());
                        commitQuery = (InsertObjectQuery)commitQuery.clone();
                    }
                    commitQuery.setIsExecutionClone(true);
                    commitQuery.setObjectChangeSet(changeSetToWrite);
                    commitQuery.setObject(objectToWrite);
                    commitQuery.cascadeOnlyDependentParts();
                    commitQuery.setModifyRow(null);
                    session.executeQuery(commitQuery);
                }
                uowChangeSet.putNewObjectInChangesList(changeSetToWrite, session);
            }
        }
    }

    /**
     * Commit changed of the objects of the class type in the change set.
     * This allows for the order of the classes to be processed optimally.
     */
    protected void commitChangedObjectsForClassWithChangeSet(UnitOfWorkChangeSet uowChangeSet, Class theClass) {
        Map<ObjectChangeSet, ObjectChangeSet> objectChangesList = uowChangeSet.getObjectChanges().get(theClass);
        if (objectChangesList != null) {// may be no changes for that class type.
            ClassDescriptor descriptor = null;
            AbstractSession session = getSession();
            Collection<ObjectChangeSet> changes = objectChangesList.values();
            CommitOrderType order = ((UnitOfWorkImpl)session).getCommitOrder();
            if (order != CommitOrderType.NONE) {
                changes = new ArrayList(objectChangesList.values());
                if (order == CommitOrderType.CHANGES) {
                    Collections.sort((List)changes, new ObjectChangeSet.ObjectChangeSetComparator());
                } else {
                    Collections.sort((List)changes);
                }
            }
            String SQLCallString = null;
            Map<Object, Object> objectIdtoVersion = new HashMap<>();
            Class objectClass = null;
            String objectIdFieldName = null;
            String mode = "LongSelectWithIn";
            // Changes to write: includes actual changes and "updates" for read locks
            for (ObjectChangeSet changeSetToWrite : changes) {
                Object objectToWrite = changeSetToWrite.getUnitOfWorkClone();
                if (descriptor == null) {
                    descriptor = session.getDescriptor(objectToWrite);
                }
                if (!isProcessedCommit(objectToWrite)) {
                    List<org.eclipse.persistence.sessions.changesets.ChangeRecord> changeSetToWriteChanges = changeSetToWrite.changes;
                    if (changeSetToWrite.isNew() || (changeSetToWriteChanges != null && !changeSetToWriteChanges.isEmpty())) {
                        // Original code that issues insert and update queries
                        WriteObjectQuery commitQuery = null;
                        if (changeSetToWrite.isNew()) {
                            commitQuery = new InsertObjectQuery();
                        } else {
                            commitQuery = new UpdateObjectQuery();
                        }
                        commitQuery.setIsExecutionClone(true);
                        commitQuery.setDescriptor(descriptor);
                        commitQuery.setObjectChangeSet(changeSetToWrite);
                        commitQuery.setObject(objectToWrite);
                        commitQuery.cascadeOnlyDependentParts();
                        session.executeQuery(commitQuery);
                    } else {
                        // Use a long OR select query to handle read locks
                        objectClass = objectToWrite.getClass();
                        OptimisticLockingPolicy optimisticLockingPolicy = descriptor.getOptimisticLockingPolicy();
                        Object objectVersion = optimisticLockingPolicy.getWriteLockValue(objectToWrite, changeSetToWrite.id, session);
                        Object objectId = null;
                        Object objectIdSQL = null;
                        try {
                            String idAttributeName = descriptor.getMappings().get(0).getAttributeName(); // ID or NETID
                            Field fieldId = objectClass.getDeclaredField(idAttributeName);
                            fieldId.setAccessible(true);
                            objectId = fieldId.get(objectToWrite);
                            if (objectId instanceof String) {
                                objectIdSQL = "'" + objectId + "'";
                            } else {
                                objectIdSQL = objectId;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        objectIdtoVersion.put(objectId, objectVersion);
                        objectIdFieldName = descriptor.getPrimaryKeyFields().get(0).getName();
                        // Does not support selecting from multiple tables
                        switch (mode) {
                            case "LongSelectWithOr":
                                if (SQLCallString == null) {
                                    SQLCallString = "SELECT " + objectIdFieldName + ", VERSION FROM " + descriptor.getDefaultTable().getName() + " WHERE " + objectIdFieldName + " = " + objectIdSQL;
                                } else {
                                    SQLCallString += " OR " + objectIdFieldName + " = " + objectIdSQL;
                                }
                                break;
                            case "LongSelectWithUnionAll":
                                if (SQLCallString == null) {
                                    SQLCallString = "SELECT " + objectIdFieldName + ", VERSION FROM " + descriptor.getDefaultTable().getName() + " WHERE " + objectIdFieldName + " = " + objectIdSQL;
                                } else {
                                    SQLCallString += " UNION ALL " + "SELECT " + objectIdFieldName + ", VERSION FROM " + descriptor.getDefaultTable().getName() + " WHERE " + objectIdFieldName + " = " + objectIdSQL;
                                }
                                break;
                            case "LongSelectWithIn":
                                if (SQLCallString == null) {
                                    SQLCallString = "SELECT " + objectIdFieldName + ", VERSION FROM " + descriptor.getDefaultTable().getName() + " WHERE " + objectIdFieldName + " IN (" + objectIdSQL;
                                } else {
                                    SQLCallString += ", " + objectIdSQL;
                                }
                                break;
                        }
                    }
                }
            }
            if (SQLCallString != null) {
                // Check version matches for every object in batch select
                if (mode.equals("LongSelectWithIn")) {
                    SQLCallString += ")";
                }
                Call call = new SQLCall(SQLCallString);
                Vector<ArrayRecord> resultRows = session.executeSelectingCall(call);
                for (ArrayRecord arrayRecord : resultRows) {
                    Object id = arrayRecord.get(objectIdFieldName);
                    Long version = (Long) arrayRecord.get("VERSION");
                    if (id instanceof Long) {
                        Object idInt = Math.toIntExact((Long)id);
                        Object versionObject = objectIdtoVersion.get(idInt);
                        if (versionObject instanceof Integer) {
                            int versionInMap = (int) versionObject;
                            if (version == null || !(version == versionInMap)) {
                                throw OptimisticLockException.objectChangedSinceLastReadWhenUpdating(id);
                            }
                        } else if (versionObject instanceof Long) {
                            Long versionInMap = (Long) versionObject;
                            if (version == null || !(version.equals(versionInMap))) {
                                throw OptimisticLockException.objectChangedSinceLastReadWhenUpdating(id);
                            }
                        }

                    } else if (id instanceof Integer) {
                        Object idInt = (Integer)id;
                        Object versionInMap = objectIdtoVersion.get(idInt);
                        if (version == null || !version.equals(versionInMap)) {
                            throw OptimisticLockException.objectChangedSinceLastReadWhenUpdating(id);
                        }
                    }

                }
            }
        }
    }

    protected void commitChangedObjectsForClassWithChangeSet1(UnitOfWorkChangeSet uowChangeSet, Class theClass) {
        Map<ObjectChangeSet, ObjectChangeSet> objectChangesList = uowChangeSet.getObjectChanges().get(theClass);
        if (objectChangesList != null) {// may be no changes for that class type.
            ClassDescriptor descriptor = null;
            AbstractSession session = getSession();
            Collection<ObjectChangeSet> changes = objectChangesList.values();
            CommitOrderType order = ((UnitOfWorkImpl)session).getCommitOrder();
            if (order != CommitOrderType.NONE) {
                changes = new ArrayList(objectChangesList.values());
                if (order == CommitOrderType.CHANGES) {
                    Collections.sort((List)changes, new ObjectChangeSet.ObjectChangeSetComparator());
                } else {
                    Collections.sort((List)changes);
                }
            }
            for (ObjectChangeSet changeSetToWrite : changes) {
                Object objectToWrite = changeSetToWrite.getUnitOfWorkClone();
                if (descriptor == null) {
                    descriptor = session.getDescriptor(objectToWrite);
                }
                if (!isProcessedCommit(objectToWrite)) {
                    // Commit and resume on failure can cause a new change set to be in existing, so need to check here.
                    WriteObjectQuery commitQuery = null;
                    if (changeSetToWrite.isNew()) {
                        commitQuery = new InsertObjectQuery();
                        commitQuery.setIsExecutionClone(true);
                        commitQuery.setDescriptor(descriptor);
                        commitQuery.setObjectChangeSet(changeSetToWrite);
                        commitQuery.setObject(objectToWrite);
                        commitQuery.cascadeOnlyDependentParts();
                        // removed checking session type to set cascade level
                        // will always be a unitOfWork so we need to cascade dependent parts
                        session.executeQuery(commitQuery);
                    } else if (!changeSetToWrite.getChanges().isEmpty()) {
                        commitQuery = new UpdateObjectQuery();
                        commitQuery.setIsExecutionClone(true);
                        commitQuery.setDescriptor(descriptor);
                        commitQuery.setObjectChangeSet(changeSetToWrite);
                        commitQuery.setObject(objectToWrite);
                        commitQuery.cascadeOnlyDependentParts();
                        // removed checking session type to set cascade level
                        // will always be a unitOfWork so we need to cascade dependent parts
                        session.executeQuery(commitQuery);
                    } else {
                        ReadObjectQuery query = new ReadObjectQuery();
                        query.setIsExecutionClone(true);
                        query.setDescriptor(descriptor);
                        query.setReferenceClass(objectToWrite.getClass());
                        query.setIsReadOnly(true);
                        query.dontCheckCache();
                        OptimisticLockingPolicy optimisticLockingPolicy = descriptor.getOptimisticLockingPolicy();
                        if (optimisticLockingPolicy != null) {
                            ExpressionBuilder builder = query.getExpressionBuilder();
                            query.setSelectionCriteria(builder.getField(optimisticLockingPolicy.getWriteLockField()).equal(builder.getParameter(optimisticLockingPolicy.getWriteLockField())).and(
                                    builder.getField(descriptor.getPrimaryKeyFields().get(0)).equal(builder.getParameter(descriptor.getPrimaryKeyFields().get(0)))
                            ));
                            query.addArgument(optimisticLockingPolicy.getWriteLockField().getQualifiedName());
                            query.addArgument(descriptor.getPrimaryKeyFields().get(0).getQualifiedName());
                            List args = new ArrayList();
                            args.add(optimisticLockingPolicy.getWriteLockValue(objectToWrite, changeSetToWrite.id, session));
                            args.add(changeSetToWrite.id);
                            Object result = session.executeQuery(query, args);
                            if (result == null) {
                                throw OptimisticLockException.objectChangedSinceLastReadWhenUpdating(query);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * delete all of the objects as a single transaction.
     * This should delete the object in the correct order to maintain referential integrity.
     */
    public void deleteAllObjects(List objects) throws RuntimeException, DatabaseException, OptimisticLockException {
        this.isActive = true;
        AbstractSession session = getSession();
        session.beginTransaction();

        try {
            // PERF: Optimize single object case.
            if (objects.size() == 1) {
                deleteAllObjects(objects.get(0).getClass(), objects, session);
            } else {
                List commitOrder = getCommitOrder();
                for (int orderIndex = commitOrder.size() - 1; orderIndex >= 0; orderIndex--) {
                    Class theClass = (Class)commitOrder.get(orderIndex);
                    deleteAllObjects(theClass, objects, session);
                }
            }

            session.commitTransaction();
        } catch (RuntimeException exception) {
            try {
                session.rollbackTransaction();
            } catch (Exception ignore) {
            }
            throw exception;
        } finally {
            reinitialize();
            this.isActive = false;
        }
    }

    /**
     * Delete all of the objects with the matching class.
     */
    public void deleteAllObjects(Class theClass, List objects, AbstractSession session) {
        ClassDescriptor descriptor = null;

        if (((UnitOfWorkImpl)session).shouldOrderUpdates()) {// bug 331064 - Sort the delete order
            objects = sort(theClass, objects);
        }

        int size = objects.size();
        for (int index = 0; index < size; index++) {
            Object objectToDelete = objects.get(index);
            if (objectToDelete.getClass() == theClass) {
                if (descriptor == null) {
                    descriptor = session.getDescriptor(theClass);
                }
                // PERF: Get the descriptor query, to avoid extra query creation.
                DeleteObjectQuery deleteQuery = descriptor.getQueryManager().getDeleteQuery();
                if (deleteQuery == null) {
                    deleteQuery = new DeleteObjectQuery();
                    deleteQuery.setDescriptor(descriptor);
                } else {
                    // Ensure original query has been prepared.
                    deleteQuery.checkPrepare(session, deleteQuery.getTranslationRow());
                    deleteQuery = (DeleteObjectQuery)deleteQuery.clone();
                }
                deleteQuery.setIsExecutionClone(true);
                deleteQuery.setObject(objectToDelete);
                session.executeQuery(deleteQuery);
            }
        }
    }

    /**
     * Sort the objects based on PK.
     */
    // bug 331064 - Sort the delete order based on PKs.
    private List sort (Class theClass, List objects) {
        ClassDescriptor descriptor = session.getDescriptor(theClass);
        org.eclipse.persistence.internal.descriptors.ObjectBuilder objectBuilder = descriptor.getObjectBuilder();
        int size = objects.size();
        TreeMap sortedObjects = new TreeMap();
        for (int index = 0; index < size; index++) {
            Object objectToDelete = objects.get(index);
            if (objectToDelete.getClass() == theClass) {
                sortedObjects.put(objectBuilder.extractPrimaryKeyFromObject(objectToDelete, session), objectToDelete);
            }
        }
        return new ArrayList(sortedObjects.values());
    }

    /**
     * Return the order in which objects should be committed to the database.
     * This order is based on ownership in the descriptors and is require for referential integrity.
     * The commit order is a vector of vectors,
     * where the first vector is all root level classes, the second is classes owned by roots and so on.
     */
    public List<Class> getCommitOrder() {
        if (this.commitOrder == null) {
            this.commitOrder = new ArrayList();
        }
        return this.commitOrder;
    }

    /**
     * Return the map of states of the objects being committed.
     * The states are defined as static Integers (PENDING, PRE, POST, COMPLETE).
     */
    protected Map<Object, Integer> getCommitState() {
        if (this.commitState == null) {
            // 2612538 - the default size of Map (32) is appropriate
            this.commitState = new IdentityHashMap();
        }
        return this.commitState;
    }

    protected boolean hasDataModifications() {
        return ((this.dataModifications != null) && (!this.dataModifications.isEmpty()));
    }

    /**
     * Used to store data queries to be performed at the end of the commit.
     * This is done to decrease dependencies and avoid deadlock.
     */
    protected Map<DatabaseMapping, List<Object[]>> getDataModifications() {
        if (dataModifications == null) {
            dataModifications = new LinkedHashMap();
        }
        return dataModifications;
    }

    protected boolean hasDeferredCalls() {
        return ((this.deferredCalls != null) && (!this.deferredCalls.isEmpty()));
    }

    /**
     * Used to store calls to be performed at the end of the commit.
     * This is done for multiple table descriptors to allow batching and avoid deadlock.
     */
    protected Map<DatabaseTable, List<Object[]>> getDeferredCalls() {
        if (this.deferredCalls == null) {
            this.deferredCalls = new LinkedHashMap();
        }
        return this.deferredCalls;
    }

    protected boolean hasObjectsToDelete() {
        return ((objectsToDelete != null) && (!objectsToDelete.isEmpty()));
    }

    /**
     * Deletion are cached until the end.
     */
    public List getObjectsToDelete() {
        if (objectsToDelete == null) {
            objectsToDelete = new ArrayList();
        }
        return objectsToDelete;
    }

    /**
     * Return the session that this is managing commits for.
     */
    protected AbstractSession getSession() {
        return this.session;
    }

    /**
     * Return any objects that have been shallow committed during this commit process.
     */
    protected Map getShallowCommits() {
        if (this.shallowCommits == null) {
            // 2612538 - the default size of Map (32) is appropriate
            this.shallowCommits = new IdentityHashMap();
        }
        return this.shallowCommits;
    }

    /**
     * Reset the commit order from the session's descriptors.
     * This uses the constraint dependencies in the descriptor's mappings,
     * to decide which descriptors are dependent on which other descriptors.
     * Multiple computations of the commit order should produce the same ordering.
     * This is done to improve performance on unit of work writes through decreasing the
     * stack size, and acts as a deadlock avoidance mechanism.
     */
    public void initializeCommitOrder() {
        Vector descriptors = Helper.buildVectorFromMapElements(getSession().getDescriptors());

        // Must ensure uniqueness, some descriptor my be register twice for interfaces.
        descriptors = Helper.addAllUniqueToVector(new Vector(descriptors.size()), descriptors);
        Object[] descriptorsArray = new Object[descriptors.size()];
        for (int index = 0; index < descriptors.size(); index++) {
            descriptorsArray[index] = descriptors.elementAt(index);
        }
        Arrays.sort(descriptorsArray, new DescriptorCompare());
        descriptors = new Vector(descriptors.size());
        for (int index = 0; index < descriptorsArray.length; index++) {
            descriptors.addElement(descriptorsArray[index]);
        }

        CommitOrderCalculator calculator = new CommitOrderCalculator(getSession());
        calculator.addNodes(descriptors);
        calculator.calculateMappingDependencies();
        calculator.orderCommits();
        descriptors = calculator.getOrderedDescriptors();

        calculator = new CommitOrderCalculator(getSession());
        calculator.addNodes(descriptors);
        calculator.calculateSpecifiedDependencies();
        calculator.orderCommits();

        setCommitOrder(calculator.getOrderedClasses());
    }

    /**
     * Return if the commit manager is active.
     */
    public boolean isActive() {
        return isActive;
    }

    /**
     * Return if the object has been processed.
     * This should be called by any query that is writing an object,
     * if true the query should not write the object.
     */
    public boolean isProcessedCommit(Object object) {
        return getCommitState().get(object) != null;
    }

    /**
     * Return if the object has been committed.
     * This should be called by any query that is writing an object,
     * if true the query should not write the object.
     */
    public boolean isCommitCompleted(Object object) {
        return getCommitState().get(object) == COMPLETE;
    }

    /**
     * Return if the object has been committed.
     * This should be called by any query that is writing an object,
     * if true the query should not write the object.
     */
    public boolean isCommitCompletedInPostOrIgnore(Object object) {
        Integer state = getCommitState().get(object);
        return (state == COMPLETE) || (state == POST) || (state == IGNORE);
    }

    /**
     * Return if the object is being in progress of being post modify commit.
     * This should be called by any query that is writing an object.
     */
    public boolean isCommitInPostModify(Object object) {
        return getCommitState().get(object) == POST;
    }

    /**
     * Return if the object is being in progress of being pre modify commit.
     * This should be called by any query that is writing an object,
     * if true the query must force a shallow insert of the object if it is new.
     */
    public boolean isCommitInPreModify(Object objectOrChangeSet) {
        return getCommitState().get(objectOrChangeSet) == PRE;
    }

    /**
     * Return if the object is shallow committed.
     * This is required to resolve bidirectional references.
     */
    public boolean isShallowCommitted(Object object) {
        if (this.shallowCommits == null) {
            return false;
        }
        return this.shallowCommits.containsKey(object);
    }

    /**
     * Mark the commit of the object as being fully completed.
     * This should be called by any query that has finished writing an object.
     */
    public void markCommitCompleted(Object object) {
        this.commitDepth --;
        getCommitState().put(object, COMPLETE);
        // If not in a unit of work commit and the commit of this object is done reset the commit manager.
        if ((!this.isActive) && (this.commitDepth == 0)) {
            reinitialize();
            return;
        }
    }

    public void markIgnoreCommit(Object object){
        getCommitState().put(object, IGNORE);
    }

    /**
     * Add an object as being in progress of being committed.
     * This should be called by any query that is writing an object.
     */
    public void markPostModifyCommitInProgress(Object object) {
        getCommitState().put(object, POST);
    }

    /**
     * Add an object as being in progress of being committed.
     * This should be called by any query that is writing an object.
     */
    public void markPreModifyCommitInProgress(Object object) {
        this.commitDepth ++;
        getCommitState().put(object, PRE);
    }

    /**
     * Mark the object as shallow committed.
     * This is required to resolve bidirectional references.
     */
    public void markShallowCommit(Object object) {
        getShallowCommits().put(object, object); // Use as set.
    }

    /**
     * Reset the commits.
     * This must be done before a new commit process is begun.
     */
    public void reinitialize() {
        this.commitState = null;
        this.commitDepth = 0;
        this.shallowCommits = null;
        this.objectsToDelete = null;
        this.dataModifications = null;
        this.deferredCalls = null;
    }

    /**
     * Set the order in which objects should be committed to the database.
     * This order is based on ownership in the descriptors and is require for referential integrity.
     * The commit order is a vector of vectors,
     * where the first vector is all root level classes, the second is classes owned by roots and so on.
     */
    public void setCommitOrder(List commitOrder) {
        this.commitOrder = commitOrder;
    }

    /**
     * Used to store data queries to be performed at the end of the commit.
     * This is done to decrease dependencies and avoid deadlock.
     */
    protected void setDataModifications(Map<DatabaseMapping, List<Object[]>> dataModifications) {
        this.dataModifications = dataModifications;
    }

    /**
     * Set if the commit manager is active.
     */
    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    /**
     * Deletion are cached until the end.
     */
    protected void setObjectsToDelete(List objectsToDelete) {
        this.objectsToDelete = objectsToDelete;
    }

    /**
     * Set the session that this is managing commits for.
     */
    protected void setSession(AbstractSession session) {
        this.session = session;
    }

    /**
     * Set any objects that have been shallow committed during this commit process.
     */
    protected void setShallowCommits(Map shallowCommits) {
        this.shallowCommits = shallowCommits;
    }

    /**
     * Print the in progress depth.
     */
    public String toString() {
        Object[] args = { Integer.valueOf(this.commitDepth) };
        return Helper.getShortClassName(getClass()) + ToStringLocalization.buildMessage("commit_depth", args);
    }
}
