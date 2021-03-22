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
package org.eclipse.persistence.tools.workbench.uitools.app;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Iterator;

import org.eclipse.persistence.tools.workbench.utility.Transformer;
import org.eclipse.persistence.tools.workbench.utility.events.CollectionChangeEvent;
import org.eclipse.persistence.tools.workbench.utility.events.CollectionChangeListener;
import org.eclipse.persistence.tools.workbench.utility.iterators.CompositeIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.TransformationIterator;


/**
 * A <code>CompositeCollectionValueModel</code> wraps another
 * <code>CollectionValueModel</code> and uses a <code>Transformer</code>
 * to convert each item in the wrapped collection to yet another
 * <code>CollectionValueModel</code>. This composite collection contains
 * the combined items from all these component collections.
 *
 * NB: The wrapped collection must be an "identity set" that does not
 * contain the same item twice or this class will throw an exception.
 *
 * Terminology:
 * - sources - the items in the wrapped collection value model; these
 *     are converted into components by the transformer
 * - components - the component collection value models that are combined
 *     by this composite collection value model
 */
public class CompositeCollectionValueModel
    extends CollectionValueModelWrapper
{
    /**
     * This is the (optional) user-supplied object that transforms
     * the items in the wrapped collection to collection value models.
     */
    private Transformer transformer;

    /**
     * Cache of the component collection value models that
     * were generated by the transformer; keyed by the item
     * in the wrapped collection that was passed to the transformer.
     */
    private IdentityHashMap components;

    /**
     * Cache of the collections corresponding to the component
     * collection value models above; keyed by the component
     * collection value models.
     */
    private IdentityHashMap collections;

    /** Listener that listens to all the component collection value models. */
    private CollectionChangeListener componentListener;

    /** Cache the size of the composite collection. */
    private int size;

    /** The transformer used when the #transform(Object) method is overridden. */
    private static final Transformer DISABLED_TRANSFORMER = new Transformer() {
        public Object transform(Object o) {
            throw new IllegalStateException("CompositeCollectionValueModel.transform(Object) was not implemented.");
        }
        public String toString() {
            return "Disabled Transformer";
        }
    };


    // ********** constructors **********

    /**
     * Construct a collection value model with the specified wrapped
     * collection value model. Use this constructor if you want to override the
     * <code>transform(Object)</code> method instead of building a
     * <code>Transformer</code>.
     */
    public CompositeCollectionValueModel(CollectionValueModel collectionHolder) {
        this(collectionHolder, DISABLED_TRANSFORMER);
    }

    /**
     * Construct a collection value model with the specified wrapped
     * collection value model and transformer.
     */
    public CompositeCollectionValueModel(CollectionValueModel collectionHolder, Transformer transformer) {
        super(collectionHolder);
        this.transformer = transformer;
    }

    /**
     * Construct a collection value model with the specified wrapped
     * list value model. Use this constructor if you want to override the
     * <code>transform(Object)</code> method instead of building a
     * <code>Transformer</code>.
     */
    public CompositeCollectionValueModel(ListValueModel listHolder) {
        this(new ListCollectionValueModelAdapter(listHolder));
    }

    /**
     * Construct a collection value model with the specified wrapped
     * list value model and transformer.
     */
    public CompositeCollectionValueModel(ListValueModel listHolder, Transformer transformer) {
        this(new ListCollectionValueModelAdapter(listHolder), transformer);
    }


    // ********** initialization **********

    protected void initialize() {
        super.initialize();
        this.components = new IdentityHashMap();
        this.collections = new IdentityHashMap();
        this.componentListener = this.buildComponentListener();
        this.size = 0;
    }

    protected CollectionChangeListener buildComponentListener() {
        return new CollectionChangeListener() {
            public void itemsAdded(CollectionChangeEvent e) {
                CompositeCollectionValueModel.this.componentItemsAdded(e);
            }
            public void itemsRemoved(CollectionChangeEvent e) {
                CompositeCollectionValueModel.this.componentItemsRemoved(e);
            }
            public void collectionChanged(CollectionChangeEvent e) {
                CompositeCollectionValueModel.this.componentCollectionChanged(e);
            }
            public String toString() {
                return "component listener";
            }
        };
    }


    // ********** ValueModel implementation **********

    /**
     * @see ValueModel#getValue()
     */
    public Object getValue() {
        return new CompositeIterator(this.buildCollectionsIterators());
    }

    protected Iterator buildCollectionsIterators() {
        return new TransformationIterator(this.collections.values().iterator()) {
            protected Object transform(Object next) {
                return ((ArrayList) next).iterator();
            }
        };
    }


    // ********** CollectionValueModel implementation **********

    /**
     * @see CollectionValueModel#size()
     */
    public int size() {
        return this.size;
    }


    // ********** CollectionValueModelWrapper overrides/implementation **********

    /**
     * @see CollectionValueModelWrapper#engageModel()
     */
    protected void engageModel() {
        super.engageModel();
        // synch our cache *after* we start listening to the wrapped collection,
        // since its value might change when a listener is added;
        // the following will trigger the firing of a number of unnecessary events
        // (since we don't have any listeners yet),
        // but it reduces the amount of duplicate code
        this.addComponentSources((Iterator) this.collectionHolder.getValue());
    }

    /**
     * @see CollectionValueModelWrapper#disengageModel()
     */
    protected void disengageModel() {
        super.disengageModel();
        // stop listening to the components...
        for (Iterator stream = this.components.values().iterator(); stream.hasNext(); ) {
            ((CollectionValueModel) stream.next()).removeCollectionChangeListener(ValueModel.VALUE, this.componentListener);
        }
        // ...and clear the cache
        this.components.clear();
        this.collections.clear();
        this.size = 0;
    }

    /**
     * Some component sources were added;
     * add their corresponding items to our cache.
     * @see CollectionValueModelWrapper#itemsAdded(org.eclipse.persistence.tools.workbench.utility.events.CollectionChangeEvent)
     */
    protected void itemsAdded(CollectionChangeEvent e) {
        this.addComponentSources(e.items());
    }

    /**
     * Transform the specified sources to collection value models
     * and add their items to our cache.
     */
    protected void addComponentSources(Iterator sources) {
        while (sources.hasNext()) {
            this.addComponentSource(sources.next());
        }
    }

    /**
     * Transform the specified source to a collection value model
     * and add its items to our cache.
     */
    protected void addComponentSource(Object source) {
        CollectionValueModel component = this.transform(source);
        if (this.components.put(source, component) != null) {
            throw new IllegalStateException("duplicate component: " + source);
        }
        component.addCollectionChangeListener(ValueModel.VALUE, this.componentListener);
        ArrayList componentCollection = new ArrayList(component.size());
        if (this.collections.put(component, componentCollection) != null) {
            throw new IllegalStateException("duplicate collection: " + source);
        }
        this.addComponentItems(component, componentCollection);
    }

    /**
     * Some component sources were removed;
     * remove their corresponding items from our cache.
     * @see CollectionValueModelWrapper#itemsRemoved(org.eclipse.persistence.tools.workbench.utility.events.CollectionChangeEvent)
     */
    protected void itemsRemoved(CollectionChangeEvent e) {
        this.removeComponentSources(e.items());
    }

    /**
     * Remove the items corresponding to the specified sources
     * from our cache.
     */
    protected void removeComponentSources(Iterator sources) {
        while (sources.hasNext()) {
            this.removeComponentSource(sources.next());
        }
    }

    /**
     * Remove the items corresponding to the specified source
     * from our cache.
     */
    protected void removeComponentSource(Object source) {
        CollectionValueModel component = (CollectionValueModel) this.components.remove(source);
        if (component == null) {
            throw new IllegalStateException("missing component: " + source);
        }
        component.removeCollectionChangeListener(ValueModel.VALUE, this.componentListener);
        ArrayList componentCollection = (ArrayList) this.collections.remove(component);
        if (componentCollection == null) {
            throw new IllegalStateException("missing collection: " + source);
        }
        this.clearComponentItems(componentCollection);
    }

    /**
     * The component sources changed;
     * rebuild our cache.
     * @see CollectionValueModelWrapper#collectionChanged(org.eclipse.persistence.tools.workbench.utility.events.CollectionChangeEvent)
     */
    protected void collectionChanged(CollectionChangeEvent e) {
        // copy the keys so we don't eat our own tail
        this.removeComponentSources(new ArrayList(this.components.keySet()).iterator());
        this.addComponentSources((Iterator) this.collectionHolder.getValue());
    }


    // ********** queries **********

    /**
     * Return the cached collection for the specified component model.
     * Cast to ArrayList so we can use ArrayList-specific methods
     * (e.g. #clone() and #ensureCapacity()).
     */
    protected ArrayList getComponentCollection(CollectionValueModel collectionValueModel) {
        return (ArrayList) this.collections.get(collectionValueModel);
    }


    // ********** behavior **********

    /**
     * Transform the specified object into a collection value model.
     * <p>
     * This method can be overridden by a subclass as an
     * alternative to building a <code>Transformer</code>.
     */
    protected CollectionValueModel transform(Object value) {
        return (CollectionValueModel) this.transformer.transform(value);
    }

    /**
     * One of the component collections had items added;
     * synchronize our caches.
     */
    protected void componentItemsAdded(CollectionChangeEvent e) {
        this.addComponentItems(e.items(), e.size(), (CollectionValueModel) e.getSource());
    }

    /**
     * Update our cache.
     */
    protected void addComponentItems(Iterator items, int itemsSize, CollectionValueModel cvm) {
        this.addComponentItems(items, itemsSize, this.getComponentCollection(cvm));
    }

    /**
     * Update our cache.
     */
    protected void addComponentItems(CollectionValueModel itemsHolder, ArrayList componentCollection) {
        this.addComponentItems((Iterator) itemsHolder.getValue(), itemsHolder.size(), componentCollection);
    }

    /**
     * Update our size and collection cache.
     */
    protected void addComponentItems(Iterator items, int itemsSize, ArrayList componentCollection) {
        this.size += itemsSize;
        componentCollection.ensureCapacity(componentCollection.size() + itemsSize);
        this.addItemsToCollection(items, componentCollection, VALUE);
    }

    /**
     * One of the component collections had items removed;
     * synchronize our caches.
     */
    protected void componentItemsRemoved(CollectionChangeEvent e) {
        this.removeComponentItems(e.items(), e.size(), (CollectionValueModel) e.getSource());
    }

    /**
     * Update our size and collection cache.
     */
    protected void removeComponentItems(Iterator items, int itemsSize, CollectionValueModel cvm) {
        this.removeComponentItems(items, itemsSize, this.getComponentCollection(cvm));
    }

    /**
     * Update our size and collection cache.
     */
    protected void clearComponentItems(ArrayList items) {
        // clone the collection so we don't eat our own tail
        this.removeComponentItems(((ArrayList) items.clone()).iterator(), items.size(), items);
    }

    /**
     * Update our size and collection cache.
     */
    protected void removeComponentItems(Iterator items, int itemsSize, ArrayList componentCollection) {
        this.size -= itemsSize;
        this.removeItemsFromCollection(items, componentCollection, VALUE);
    }

    /**
     * One of the component collections changed;
     * synchronize our caches by clearing out the appropriate
     * collection and then rebuilding it.
     */
    protected void componentCollectionChanged(CollectionChangeEvent e) {
        CollectionValueModel component = (CollectionValueModel) e.getSource();
        ArrayList items = this.getComponentCollection(component);
        this.clearComponentItems(items);
        this.addComponentItems(component, items);
    }

}