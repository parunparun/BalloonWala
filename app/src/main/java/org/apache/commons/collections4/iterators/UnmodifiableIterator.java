package org.apache.commons.collections4.iterators;

import java.util.Iterator;
import org.apache.commons.collections4.Unmodifiable;

/* JADX INFO: loaded from: classes.dex */
public final class UnmodifiableIterator<E> implements Iterator<E>, Unmodifiable {
    private final Iterator<? extends E> iterator;

    /* JADX WARN: Multi-variable type inference failed */
    public static <E> Iterator<E> unmodifiableIterator(Iterator<? extends E> it) {
        if (it == 0) {
            throw new NullPointerException("Iterator must not be null");
        }
        if (it instanceof Unmodifiable) {
            return it;
        }
        return new UnmodifiableIterator(it);
    }

    private UnmodifiableIterator(Iterator<? extends E> iterator) {
        this.iterator = iterator;
    }

    @Override // java.util.Iterator
    public boolean hasNext() {
        return this.iterator.hasNext();
    }

    @Override // java.util.Iterator
    public E next() {
        return this.iterator.next();
    }

    @Override // java.util.Iterator
    public void remove() {
        throw new UnsupportedOperationException("remove() is not supported");
    }
}
