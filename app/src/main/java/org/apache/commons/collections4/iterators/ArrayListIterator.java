package org.apache.commons.collections4.iterators;

import java.lang.reflect.Array;
import java.util.NoSuchElementException;
import org.apache.commons.collections4.ResettableListIterator;

/* JADX INFO: loaded from: classes.dex */
public class ArrayListIterator<E> extends ArrayIterator<E> implements ResettableListIterator<E> {
    private int lastItemIndex;

    public ArrayListIterator(Object array) {
        super(array);
        this.lastItemIndex = -1;
    }

    public ArrayListIterator(Object array, int startIndex) {
        super(array, startIndex);
        this.lastItemIndex = -1;
    }

    public ArrayListIterator(Object array, int startIndex, int endIndex) {
        super(array, startIndex, endIndex);
        this.lastItemIndex = -1;
    }

    @Override // java.util.ListIterator, org.apache.commons.collections4.OrderedIterator
    public boolean hasPrevious() {
        return this.index > this.startIndex;
    }

    @Override // java.util.ListIterator, org.apache.commons.collections4.OrderedIterator
    public E previous() {
        if (!hasPrevious()) {
            throw new NoSuchElementException();
        }
        int i = this.index - 1;
        this.index = i;
        this.lastItemIndex = i;
        return (E) Array.get(this.array, this.index);
    }

    @Override // org.apache.commons.collections4.iterators.ArrayIterator, java.util.Iterator
    public E next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        this.lastItemIndex = this.index;
        Object obj = this.array;
        int i = this.index;
        this.index = i + 1;
        return (E) Array.get(obj, i);
    }

    @Override // java.util.ListIterator
    public int nextIndex() {
        return this.index - this.startIndex;
    }

    @Override // java.util.ListIterator
    public int previousIndex() {
        return (this.index - this.startIndex) - 1;
    }

    @Override // java.util.ListIterator
    public void add(Object o) {
        throw new UnsupportedOperationException("add() method is not supported");
    }

    @Override // java.util.ListIterator
    public void set(Object o) {
        if (this.lastItemIndex == -1) {
            throw new IllegalStateException("must call next() or previous() before a call to set()");
        }
        Array.set(this.array, this.lastItemIndex, o);
    }

    @Override // org.apache.commons.collections4.iterators.ArrayIterator, org.apache.commons.collections4.ResettableIterator
    public void reset() {
        super.reset();
        this.lastItemIndex = -1;
    }
}
