package org.apache.commons.collections4.set;

import java.util.Iterator;
import java.util.NavigableSet;
import org.apache.commons.collections4.Predicate;

/* JADX INFO: loaded from: classes.dex */
public class PredicatedNavigableSet<E> extends PredicatedSortedSet<E> implements NavigableSet<E> {
    private static final long serialVersionUID = 20150528;

    public static <E> PredicatedNavigableSet<E> predicatedNavigableSet(NavigableSet<E> set, Predicate<? super E> predicate) {
        return new PredicatedNavigableSet<>(set, predicate);
    }

    protected PredicatedNavigableSet(NavigableSet<E> set, Predicate<? super E> predicate) {
        super(set, predicate);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.commons.collections4.set.PredicatedSortedSet, org.apache.commons.collections4.set.PredicatedSet, org.apache.commons.collections4.collection.AbstractCollectionDecorator
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
        return predicatedNavigableSet(decorated().descendingSet(), this.predicate);
    }

    @Override // java.util.NavigableSet
    public Iterator<E> descendingIterator() {
        return decorated().descendingIterator();
    }

    @Override // java.util.NavigableSet
    public NavigableSet<E> subSet(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {
        NavigableSet<E> sub = decorated().subSet(fromElement, fromInclusive, toElement, toInclusive);
        return predicatedNavigableSet(sub, this.predicate);
    }

    @Override // java.util.NavigableSet
    public NavigableSet<E> headSet(E toElement, boolean inclusive) {
        NavigableSet<E> head = decorated().headSet(toElement, inclusive);
        return predicatedNavigableSet(head, this.predicate);
    }

    @Override // java.util.NavigableSet
    public NavigableSet<E> tailSet(E fromElement, boolean inclusive) {
        NavigableSet<E> tail = decorated().tailSet(fromElement, inclusive);
        return predicatedNavigableSet(tail, this.predicate);
    }
}
