package org.apache.commons.collections4.set;

import java.util.Iterator;
import java.util.NavigableSet;
import org.apache.commons.collections4.Transformer;

/* JADX INFO: loaded from: classes.dex */
public class TransformedNavigableSet<E> extends TransformedSortedSet<E> implements NavigableSet<E> {
    private static final long serialVersionUID = 20150528;

    public static <E> TransformedNavigableSet<E> transformingNavigableSet(NavigableSet<E> set, Transformer<? super E, ? extends E> transformer) {
        return new TransformedNavigableSet<>(set, transformer);
    }

    public static <E> TransformedNavigableSet<E> transformedNavigableSet(NavigableSet<E> set, Transformer<? super E, ? extends E> transformer) {
        TransformedNavigableSet<E> decorated = new TransformedNavigableSet<>(set, transformer);
        if (set.size() > 0) {
            Object[] array = set.toArray();
            set.clear();
            for (Object obj : array) {
                decorated.decorated().add(transformer.transform(obj));
            }
        }
        return decorated;
    }

    protected TransformedNavigableSet(NavigableSet<E> set, Transformer<? super E, ? extends E> transformer) {
        super(set, transformer);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.commons.collections4.collection.AbstractCollectionDecorator
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
        return transformingNavigableSet(decorated().descendingSet(), this.transformer);
    }

    @Override // java.util.NavigableSet
    public Iterator<E> descendingIterator() {
        return decorated().descendingIterator();
    }

    @Override // java.util.NavigableSet
    public NavigableSet<E> subSet(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {
        NavigableSet<E> sub = decorated().subSet(fromElement, fromInclusive, toElement, toInclusive);
        return transformingNavigableSet(sub, this.transformer);
    }

    @Override // java.util.NavigableSet
    public NavigableSet<E> headSet(E toElement, boolean inclusive) {
        NavigableSet<E> head = decorated().headSet(toElement, inclusive);
        return transformingNavigableSet(head, this.transformer);
    }

    @Override // java.util.NavigableSet
    public NavigableSet<E> tailSet(E fromElement, boolean inclusive) {
        NavigableSet<E> tail = decorated().tailSet(fromElement, inclusive);
        return transformingNavigableSet(tail, this.transformer);
    }
}
