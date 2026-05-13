package org.apache.commons.collections4.set;

import java.util.Comparator;
import java.util.Set;
import java.util.SortedSet;

/* JADX INFO: loaded from: classes.dex */
public abstract class AbstractSortedSetDecorator<E> extends AbstractSetDecorator<E> implements SortedSet<E> {
    private static final long serialVersionUID = -3462240946294214398L;

    protected AbstractSortedSetDecorator() {
    }

    protected AbstractSortedSetDecorator(Set<E> set) {
        super(set);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.commons.collections4.set.AbstractSetDecorator, org.apache.commons.collections4.collection.AbstractCollectionDecorator
    public SortedSet<E> decorated() {
        return (SortedSet) super.decorated();
    }

    @Override // java.util.SortedSet
    public SortedSet<E> subSet(E fromElement, E toElement) {
        return decorated().subSet(fromElement, toElement);
    }

    @Override // java.util.SortedSet
    public SortedSet<E> headSet(E toElement) {
        return decorated().headSet(toElement);
    }

    @Override // java.util.SortedSet
    public SortedSet<E> tailSet(E fromElement) {
        return decorated().tailSet(fromElement);
    }

    @Override // java.util.SortedSet
    public E first() {
        return decorated().first();
    }

    @Override // java.util.SortedSet
    public E last() {
        return decorated().last();
    }

    @Override // java.util.SortedSet
    public Comparator<? super E> comparator() {
        return decorated().comparator();
    }
}
