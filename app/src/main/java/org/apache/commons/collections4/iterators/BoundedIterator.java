package org.apache.commons.collections4.iterators;

import java.util.Iterator;
import java.util.NoSuchElementException;

/* JADX INFO: loaded from: classes.dex */
public class BoundedIterator<E> implements Iterator<E> {
    private final Iterator<? extends E> iterator;
    private final long max;
    private final long offset;
    private long pos;

    public BoundedIterator(Iterator<? extends E> iterator, long offset, long max) {
        if (iterator == null) {
            throw new NullPointerException("Iterator must not be null");
        }
        if (offset < 0) {
            throw new IllegalArgumentException("Offset parameter must not be negative.");
        }
        if (max < 0) {
            throw new IllegalArgumentException("Max parameter must not be negative.");
        }
        this.iterator = iterator;
        this.offset = offset;
        this.max = max;
        this.pos = 0L;
        init();
    }

    private void init() {
        while (this.pos < this.offset && this.iterator.hasNext()) {
            this.iterator.next();
            this.pos++;
        }
    }

    @Override // java.util.Iterator
    public boolean hasNext() {
        if (!checkBounds()) {
            return false;
        }
        return this.iterator.hasNext();
    }

    private boolean checkBounds() {
        if ((this.pos - this.offset) + 1 > this.max) {
            return false;
        }
        return true;
    }

    @Override // java.util.Iterator
    public E next() {
        if (!checkBounds()) {
            throw new NoSuchElementException();
        }
        E next = this.iterator.next();
        this.pos++;
        return next;
    }

    @Override // java.util.Iterator
    public void remove() {
        if (this.pos <= this.offset) {
            throw new IllegalStateException("remove() can not be called before calling next()");
        }
        this.iterator.remove();
    }
}
