package org.apache.commons.collections4.set;

import java.util.Iterator;
import java.util.NavigableSet;

/* JADX INFO: loaded from: classes.dex */
public abstract class AbstractNavigableSetDecorator<E> extends AbstractSortedSetDecorator<E> implements NavigableSet<E> {
    private static final long serialVersionUID = 20150528;

    protected AbstractNavigableSetDecorator() {
    }

    protected AbstractNavigableSetDecorator(NavigableSet<E> set) {
        super(set);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.commons.collections4.set.AbstractSortedSetDecorator, org.apache.commons.collections4.set.AbstractSetDecorator, org.apache.commons.collections4.collection.AbstractCollectionDecorator
    public NavigableSet<E> decorated() {
        return (NavigableSet) super.decorated();
    }

    @Override // java.util.NavigableSet
    public E lower(E e) {
        return decorated().lower(e);
    }

    @Override // java.util.NavigableSet
    public E floor(E e) {
        return decorated().floor(e);
    }

    @Override // java.util.NavigableSet
    public E ceiling(E e) {
        return decorated().ceiling(e);
    }

    @Override // java.util.NavigableSet
    public E higher(E e) {
        return decorated().higher(e);
    }

    @Override // java.util.NavigableSet
    public E pollFirst() {
        return decorated().pollFirst();
    }

    @Override // java.util.NavigableSet
    public E pollLast() {
        return decorated().pollLast();
    }

    @Override // java.util.NavigableSet
    public NavigableSet<E> descendingSet() {
        return decorated().descendingSet();
    }

    @Override // java.util.NavigableSet
    public Iterator<E> descendingIterator() {
        return decorated().descendingIterator();
    }

    @Override // java.util.NavigableSet
    public NavigableSet<E> subSet(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {
        return decorated().subSet(fromElement, fromInclusive, toElement, toInclusive);
    }

    @Override // java.util.NavigableSet
    public NavigableSet<E> headSet(E toElement, boolean inclusive) {
        return decorated().headSet(toElement, inclusive);
    }

    @Override // java.util.NavigableSet
    public NavigableSet<E> tailSet(E fromElement, boolean inclusive) {
        return decorated().tailSet(fromElement, inclusive);
    }
}
