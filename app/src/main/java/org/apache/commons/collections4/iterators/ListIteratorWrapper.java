package org.apache.commons.collections4.iterators;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import org.apache.commons.collections4.ResettableListIterator;

/* JADX INFO: loaded from: classes.dex */
public class ListIteratorWrapper<E> implements ResettableListIterator<E> {
    private static final String CANNOT_REMOVE_MESSAGE = "Cannot remove element at index {0}.";
    private static final String UNSUPPORTED_OPERATION_MESSAGE = "ListIteratorWrapper does not support optional operations of ListIterator.";
    private final Iterator<? extends E> iterator;
    private boolean removeState;
    private final List<E> list = new ArrayList();
    private int currentIndex = 0;
    private int wrappedIteratorIndex = 0;

    public ListIteratorWrapper(Iterator<? extends E> iterator) {
        if (iterator == null) {
            throw new NullPointerException("Iterator must not be null");
        }
        this.iterator = iterator;
    }

    @Override // java.util.ListIterator
    public void add(E obj) throws UnsupportedOperationException {
        Iterator<? extends E> it = this.iterator;
        if (it instanceof ListIterator) {
            ListIterator<E> li = (ListIterator) it;
            li.add(obj);
            return;
        }
        throw new UnsupportedOperationException(UNSUPPORTED_OPERATION_MESSAGE);
    }

    @Override // java.util.ListIterator, java.util.Iterator
    public boolean hasNext() {
        if (this.currentIndex == this.wrappedIteratorIndex || (this.iterator instanceof ListIterator)) {
            return this.iterator.hasNext();
        }
        return true;
    }

    @Override // java.util.ListIterator, org.apache.commons.collections4.OrderedIterator
    public boolean hasPrevious() {
        Iterator<? extends E> it = this.iterator;
        if (!(it instanceof ListIterator)) {
            return this.currentIndex > 0;
        }
        ListIterator<?> li = (ListIterator) it;
        return li.hasPrevious();
    }

    @Override // java.util.ListIterator, java.util.Iterator
    public E next() throws NoSuchElementException {
        Iterator<? extends E> it = this.iterator;
        if (it instanceof ListIterator) {
            return it.next();
        }
        int i = this.currentIndex;
        if (i < this.wrappedIteratorIndex) {
            int i2 = i + 1;
            this.currentIndex = i2;
            return this.list.get(i2 - 1);
        }
        E retval = it.next();
        this.list.add(retval);
        this.currentIndex++;
        this.wrappedIteratorIndex++;
        this.removeState = true;
        return retval;
    }

    @Override // java.util.ListIterator
    public int nextIndex() {
        Iterator<? extends E> it = this.iterator;
        if (it instanceof ListIterator) {
            ListIterator<?> li = (ListIterator) it;
            return li.nextIndex();
        }
        return this.currentIndex;
    }

    @Override // java.util.ListIterator, org.apache.commons.collections4.OrderedIterator
    public E previous() throws NoSuchElementException {
        Iterator<? extends E> it = this.iterator;
        if (it instanceof ListIterator) {
            ListIterator<E> li = (ListIterator) it;
            return li.previous();
        }
        int i = this.currentIndex;
        if (i == 0) {
            throw new NoSuchElementException();
        }
        this.removeState = this.wrappedIteratorIndex == i;
        List<E> list = this.list;
        int i2 = this.currentIndex - 1;
        this.currentIndex = i2;
        return list.get(i2);
    }

    @Override // java.util.ListIterator
    public int previousIndex() {
        Iterator<? extends E> it = this.iterator;
        if (it instanceof ListIterator) {
            ListIterator<?> li = (ListIterator) it;
            return li.previousIndex();
        }
        return this.currentIndex - 1;
    }

    @Override // java.util.ListIterator, java.util.Iterator
    public void remove() throws UnsupportedOperationException {
        Iterator<? extends E> it = this.iterator;
        if (it instanceof ListIterator) {
            it.remove();
            return;
        }
        int removeIndex = this.currentIndex;
        if (this.currentIndex == this.wrappedIteratorIndex) {
            removeIndex--;
        }
        if (!this.removeState || this.wrappedIteratorIndex - this.currentIndex > 1) {
            throw new IllegalStateException(MessageFormat.format(CANNOT_REMOVE_MESSAGE, Integer.valueOf(removeIndex)));
        }
        this.iterator.remove();
        this.list.remove(removeIndex);
        this.currentIndex = removeIndex;
        this.wrappedIteratorIndex--;
        this.removeState = false;
    }

    @Override // java.util.ListIterator
    public void set(E obj) throws UnsupportedOperationException {
        Iterator<? extends E> it = this.iterator;
        if (it instanceof ListIterator) {
            ListIterator<E> li = (ListIterator) it;
            li.set(obj);
            return;
        }
        throw new UnsupportedOperationException(UNSUPPORTED_OPERATION_MESSAGE);
    }

    @Override // org.apache.commons.collections4.ResettableIterator
    public void reset() {
        Iterator<? extends E> it = this.iterator;
        if (it instanceof ListIterator) {
            ListIterator<?> li = (ListIterator) it;
            while (li.previousIndex() >= 0) {
                li.previous();
            }
            return;
        }
        this.currentIndex = 0;
    }
}
