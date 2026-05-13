package org.apache.commons.collections4.iterators;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

/* JADX INFO: loaded from: classes.dex */
public class PushbackIterator<E> implements Iterator<E> {
    private final Deque<E> items = new ArrayDeque();
    private final Iterator<? extends E> iterator;

    public static <E> PushbackIterator<E> pushbackIterator(Iterator<? extends E> iterator) {
        if (iterator == null) {
            throw new NullPointerException("Iterator must not be null");
        }
        if (iterator instanceof PushbackIterator) {
            PushbackIterator<E> it = (PushbackIterator) iterator;
            return it;
        }
        return new PushbackIterator<>(iterator);
    }

    public PushbackIterator(Iterator<? extends E> iterator) {
        this.iterator = iterator;
    }

    public void pushback(E item) {
        this.items.push(item);
    }

    @Override // java.util.Iterator
    public boolean hasNext() {
        return !this.items.isEmpty() || this.iterator.hasNext();
    }

    @Override // java.util.Iterator
    public E next() {
        return !this.items.isEmpty() ? this.items.pop() : this.iterator.next();
    }

    @Override // java.util.Iterator
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
