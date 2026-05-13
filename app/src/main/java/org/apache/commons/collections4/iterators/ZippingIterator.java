package org.apache.commons.collections4.iterators;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.apache.commons.collections4.FluentIterable;

/* JADX INFO: loaded from: classes.dex */
public class ZippingIterator<E> implements Iterator<E> {
    private final Iterator<Iterator<? extends E>> iterators;
    private Iterator<? extends E> lastReturned;
    private Iterator<? extends E> nextIterator;

    public ZippingIterator(Iterator<? extends E> a, Iterator<? extends E> b) {
        this(a, b);
    }

    public ZippingIterator(Iterator<? extends E> a, Iterator<? extends E> b, Iterator<? extends E> c) {
        this(a, b, c);
    }

    public ZippingIterator(Iterator<? extends E>... itArr) {
        this.nextIterator = null;
        this.lastReturned = null;
        ArrayList arrayList = new ArrayList();
        for (Iterator<? extends E> it : itArr) {
            if (it == null) {
                throw new NullPointerException("Iterator must not be null.");
            }
            arrayList.add(it);
        }
        this.iterators = FluentIterable.of((Iterable) arrayList).loop().iterator();
    }

    @Override // java.util.Iterator
    public boolean hasNext() {
        if (this.nextIterator != null) {
            return true;
        }
        while (this.iterators.hasNext()) {
            Iterator<? extends E> childIterator = this.iterators.next();
            if (childIterator.hasNext()) {
                this.nextIterator = childIterator;
                return true;
            }
            this.iterators.remove();
        }
        return false;
    }

    @Override // java.util.Iterator
    public E next() throws NoSuchElementException {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        E val = this.nextIterator.next();
        this.lastReturned = this.nextIterator;
        this.nextIterator = null;
        return val;
    }

    @Override // java.util.Iterator
    public void remove() {
        Iterator<? extends E> it = this.lastReturned;
        if (it == null) {
            throw new IllegalStateException("No value can be removed at present");
        }
        it.remove();
        this.lastReturned = null;
    }
}
