package org.apache.commons.collections4.iterators;

import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import org.apache.commons.collections4.ResettableListIterator;

/* JADX INFO: loaded from: classes.dex */
public class LoopingListIterator<E> implements ResettableListIterator<E> {
    private ListIterator<E> iterator;
    private final List<E> list;

    public LoopingListIterator(List<E> list) {
        if (list == null) {
            throw new NullPointerException("The list must not be null");
        }
        this.list = list;
        _reset();
    }

    @Override // java.util.ListIterator, java.util.Iterator
    public boolean hasNext() {
        return !this.list.isEmpty();
    }

    @Override // java.util.ListIterator, java.util.Iterator
    public E next() {
        if (this.list.isEmpty()) {
            throw new NoSuchElementException("There are no elements for this iterator to loop on");
        }
        if (!this.iterator.hasNext()) {
            reset();
        }
        return this.iterator.next();
    }

    @Override // java.util.ListIterator
    public int nextIndex() {
        if (this.list.isEmpty()) {
            throw new NoSuchElementException("There are no elements for this iterator to loop on");
        }
        if (!this.iterator.hasNext()) {
            return 0;
        }
        return this.iterator.nextIndex();
    }

    @Override // java.util.ListIterator, org.apache.commons.collections4.OrderedIterator
    public boolean hasPrevious() {
        return !this.list.isEmpty();
    }

    @Override // java.util.ListIterator, org.apache.commons.collections4.OrderedIterator
    public E previous() {
        if (this.list.isEmpty()) {
            throw new NoSuchElementException("There are no elements for this iterator to loop on");
        }
        if (!this.iterator.hasPrevious()) {
            E result = null;
            while (this.iterator.hasNext()) {
                result = this.iterator.next();
            }
            this.iterator.previous();
            return result;
        }
        return this.iterator.previous();
    }

    @Override // java.util.ListIterator
    public int previousIndex() {
        if (this.list.isEmpty()) {
            throw new NoSuchElementException("There are no elements for this iterator to loop on");
        }
        if (!this.iterator.hasPrevious()) {
            return this.list.size() - 1;
        }
        return this.iterator.previousIndex();
    }

    @Override // java.util.ListIterator, java.util.Iterator
    public void remove() {
        this.iterator.remove();
    }

    @Override // java.util.ListIterator
    public void add(E obj) {
        this.iterator.add(obj);
    }

    @Override // java.util.ListIterator
    public void set(E obj) {
        this.iterator.set(obj);
    }

    @Override // org.apache.commons.collections4.ResettableIterator
    public void reset() {
        _reset();
    }

    private void _reset() {
        this.iterator = this.list.listIterator();
    }

    public int size() {
        return this.list.size();
    }
}
