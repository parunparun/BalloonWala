package org.apache.commons.collections4.collection;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Predicate;

/* JADX INFO: loaded from: classes.dex */
public abstract class AbstractCollectionDecorator<E> implements Collection<E>, Serializable {
    private static final long serialVersionUID = 6249888059822088500L;
    private Collection<E> collection;

    protected AbstractCollectionDecorator() {
    }

    protected AbstractCollectionDecorator(Collection<E> coll) {
        if (coll == null) {
            throw new NullPointerException("Collection must not be null.");
        }
        this.collection = coll;
    }

    protected Collection<E> decorated() {
        return this.collection;
    }

    protected void setCollection(Collection<E> coll) {
        this.collection = coll;
    }

    @Override // java.util.Collection, org.apache.commons.collections4.Bag
    public boolean add(E object) {
        return decorated().add(object);
    }

    @Override // java.util.Collection
    public boolean addAll(Collection<? extends E> coll) {
        return decorated().addAll(coll);
    }

    @Override // java.util.Collection
    public void clear() {
        decorated().clear();
    }

    @Override // java.util.Collection, java.util.Set
    public boolean contains(Object object) {
        return decorated().contains(object);
    }

    @Override // java.util.Collection
    public boolean isEmpty() {
        return decorated().isEmpty();
    }

    @Override // java.util.Collection, java.lang.Iterable, org.apache.commons.collections4.Bag
    public Iterator<E> iterator() {
        return decorated().iterator();
    }

    @Override // java.util.Collection, org.apache.commons.collections4.Bag
    public boolean remove(Object object) {
        return decorated().remove(object);
    }

    @Override // java.util.Collection
    public int size() {
        return decorated().size();
    }

    @Override // java.util.Collection
    public Object[] toArray() {
        return decorated().toArray();
    }

    @Override // java.util.Collection
    public <T> T[] toArray(T[] tArr) {
        return (T[]) decorated().toArray(tArr);
    }

    @Override // java.util.Collection, org.apache.commons.collections4.Bag
    public boolean containsAll(Collection<?> coll) {
        return decorated().containsAll(coll);
    }

    @Override // java.util.Collection
    public boolean removeIf(Predicate<? super E> filter) {
        return decorated().removeIf(filter);
    }

    @Override // java.util.Collection, org.apache.commons.collections4.Bag
    public boolean removeAll(Collection<?> coll) {
        return decorated().removeAll(coll);
    }

    @Override // java.util.Collection, org.apache.commons.collections4.Bag
    public boolean retainAll(Collection<?> coll) {
        return decorated().retainAll(coll);
    }

    public String toString() {
        return decorated().toString();
    }
}
