package org.apache.commons.collections4.iterators;

import java.util.Iterator;
import org.apache.commons.collections4.ResettableIterator;

/* JADX INFO: loaded from: classes.dex */
public class IteratorIterable<E> implements Iterable<E> {
    private final Iterator<? extends E> iterator;
    private final Iterator<E> typeSafeIterator;

    private static <E> Iterator<E> createTypesafeIterator(final Iterator<? extends E> iterator) {
        return new Iterator<E>() { // from class: org.apache.commons.collections4.iterators.IteratorIterable.1
            @Override // java.util.Iterator
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override // java.util.Iterator
            public E next() {
                return (E) iterator.next();
            }

            @Override // java.util.Iterator
            public void remove() {
                iterator.remove();
            }
        };
    }

    public IteratorIterable(Iterator<? extends E> iterator) {
        this(iterator, false);
    }

    public IteratorIterable(Iterator<? extends E> iterator, boolean multipleUse) {
        if (multipleUse && !(iterator instanceof ResettableIterator)) {
            this.iterator = new ListIteratorWrapper(iterator);
        } else {
            this.iterator = iterator;
        }
        this.typeSafeIterator = createTypesafeIterator(this.iterator);
    }

    @Override // java.lang.Iterable
    public Iterator<E> iterator() {
        Iterator<? extends E> it = this.iterator;
        if (it instanceof ResettableIterator) {
            ((ResettableIterator) it).reset();
        }
        return this.typeSafeIterator;
    }
}
