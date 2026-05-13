package org.apache.commons.collections4.set;

import java.util.Comparator;
import java.util.SortedSet;
import org.apache.commons.collections4.Transformer;

/* JADX INFO: loaded from: classes.dex */
public class TransformedSortedSet<E> extends TransformedSet<E> implements SortedSet<E> {
    private static final long serialVersionUID = -1675486811351124386L;

    public static <E> TransformedSortedSet<E> transformingSortedSet(SortedSet<E> set, Transformer<? super E, ? extends E> transformer) {
        return new TransformedSortedSet<>(set, transformer);
    }

    public static <E> TransformedSortedSet<E> transformedSortedSet(SortedSet<E> set, Transformer<? super E, ? extends E> transformer) {
        TransformedSortedSet<E> decorated = new TransformedSortedSet<>(set, transformer);
        if (set.size() > 0) {
            Object[] array = set.toArray();
            set.clear();
            for (Object obj : array) {
                decorated.decorated().add(transformer.transform(obj));
            }
        }
        return decorated;
    }

    protected TransformedSortedSet(SortedSet<E> set, Transformer<? super E, ? extends E> transformer) {
        super(set, transformer);
    }

    protected SortedSet<E> getSortedSet() {
        return (SortedSet) decorated();
    }

    @Override // java.util.SortedSet
    public E first() {
        return getSortedSet().first();
    }

    @Override // java.util.SortedSet
    public E last() {
        return getSortedSet().last();
    }

    @Override // java.util.SortedSet
    public Comparator<? super E> comparator() {
        return getSortedSet().comparator();
    }

    @Override // java.util.SortedSet
    public SortedSet<E> subSet(E fromElement, E toElement) {
        SortedSet<E> set = getSortedSet().subSet(fromElement, toElement);
        return new TransformedSortedSet(set, this.transformer);
    }

    @Override // java.util.SortedSet
    public SortedSet<E> headSet(E toElement) {
        SortedSet<E> set = getSortedSet().headSet(toElement);
        return new TransformedSortedSet(set, this.transformer);
    }

    @Override // java.util.SortedSet
    public SortedSet<E> tailSet(E fromElement) {
        SortedSet<E> set = getSortedSet().tailSet(fromElement);
        return new TransformedSortedSet(set, this.transformer);
    }
}
