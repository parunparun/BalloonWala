package org.apache.commons.collections4.bag;

import java.util.Set;
import org.apache.commons.collections4.Bag;
import org.apache.commons.collections4.collection.AbstractCollectionDecorator;

/* JADX INFO: loaded from: classes.dex */
public abstract class AbstractBagDecorator<E> extends AbstractCollectionDecorator<E> implements Bag<E> {
    private static final long serialVersionUID = -3768146017343785417L;

    protected AbstractBagDecorator() {
    }

    protected AbstractBagDecorator(Bag<E> bag) {
        super(bag);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.commons.collections4.collection.AbstractCollectionDecorator
    public Bag<E> decorated() {
        return (Bag) super.decorated();
    }

    @Override // java.util.Collection
    public boolean equals(Object object) {
        return object == this || decorated().equals(object);
    }

    @Override // java.util.Collection
    public int hashCode() {
        return decorated().hashCode();
    }

    @Override // org.apache.commons.collections4.Bag
    public int getCount(Object object) {
        return decorated().getCount(object);
    }

    @Override // org.apache.commons.collections4.Bag
    public boolean add(E object, int count) {
        return decorated().add(object, count);
    }

    @Override // org.apache.commons.collections4.Bag
    public boolean remove(Object object, int count) {
        return decorated().remove(object, count);
    }

    @Override // org.apache.commons.collections4.Bag
    public Set<E> uniqueSet() {
        return decorated().uniqueSet();
    }
}
