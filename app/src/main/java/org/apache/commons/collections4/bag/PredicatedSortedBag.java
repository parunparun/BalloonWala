package org.apache.commons.collections4.bag;

import java.util.Comparator;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.SortedBag;

/* JADX INFO: loaded from: classes.dex */
public class PredicatedSortedBag<E> extends PredicatedBag<E> implements SortedBag<E> {
    private static final long serialVersionUID = 3448581314086406616L;

    public static <E> PredicatedSortedBag<E> predicatedSortedBag(SortedBag<E> bag, Predicate<? super E> predicate) {
        return new PredicatedSortedBag<>(bag, predicate);
    }

    protected PredicatedSortedBag(SortedBag<E> bag, Predicate<? super E> predicate) {
        super(bag, predicate);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.commons.collections4.bag.PredicatedBag, org.apache.commons.collections4.collection.AbstractCollectionDecorator
    public SortedBag<E> decorated() {
        return (SortedBag) super.decorated();
    }

    @Override // org.apache.commons.collections4.SortedBag
    public E first() {
        return decorated().first();
    }

    @Override // org.apache.commons.collections4.SortedBag
    public E last() {
        return decorated().last();
    }

    @Override // org.apache.commons.collections4.SortedBag
    public Comparator<? super E> comparator() {
        return decorated().comparator();
    }
}
