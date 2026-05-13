package org.apache.commons.collections4.set;

import java.util.Comparator;
import java.util.SortedSet;
import org.apache.commons.collections4.Predicate;

/* JADX INFO: loaded from: classes.dex */
public class PredicatedSortedSet<E> extends PredicatedSet<E> implements SortedSet<E> {
    private static final long serialVersionUID = -9110948148132275052L;

    public static <E> PredicatedSortedSet<E> predicatedSortedSet(SortedSet<E> set, Predicate<? super E> predicate) {
        return new PredicatedSortedSet<>(set, predicate);
    }

    protected PredicatedSortedSet(SortedSet<E> set, Predicate<? super E> predicate) {
        super(set, predicate);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.commons.collections4.set.PredicatedSet, org.apache.commons.collections4.collection.AbstractCollectionDecorator
    public SortedSet<E> decorated() {
        return (SortedSet) super.decorated();
    }

    @Override // java.util.SortedSet
    public Comparator<? super E> comparator() {
        return decorated().comparator();
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
    public SortedSet<E> subSet(E fromElement, E toElement) {
        SortedSet<E> sub = decorated().subSet(fromElement, toElement);
        return new PredicatedSortedSet(sub, this.predicate);
    }

    @Override // java.util.SortedSet
    public SortedSet<E> headSet(E toElement) {
        SortedSet<E> head = decorated().headSet(toElement);
        return new PredicatedSortedSet(head, this.predicate);
    }

    @Override // java.util.SortedSet
    public SortedSet<E> tailSet(E fromElement) {
        SortedSet<E> tail = decorated().tailSet(fromElement);
        return new PredicatedSortedSet(tail, this.predicate);
    }
}
