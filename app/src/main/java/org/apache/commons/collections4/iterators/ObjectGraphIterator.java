package org.apache.commons.collections4.iterators;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.apache.commons.collections4.Transformer;

/* JADX INFO: loaded from: classes.dex */
public class ObjectGraphIterator<E> implements Iterator<E> {
    private Iterator<? extends E> currentIterator;
    private E currentValue;
    private boolean hasNext;
    private Iterator<? extends E> lastUsedIterator;
    private E root;
    private final Deque<Iterator<? extends E>> stack;
    private final Transformer<? super E, ? extends E> transformer;

    public ObjectGraphIterator(E root, Transformer<? super E, ? extends E> transformer) {
        this.stack = new ArrayDeque(8);
        this.hasNext = false;
        if (root instanceof Iterator) {
            this.currentIterator = (Iterator) root;
        } else {
            this.root = root;
        }
        this.transformer = transformer;
    }

    public ObjectGraphIterator(Iterator<? extends E> rootIterator) {
        this.stack = new ArrayDeque(8);
        this.hasNext = false;
        this.currentIterator = rootIterator;
        this.transformer = null;
    }

    protected void updateCurrentIterator() {
        if (this.hasNext) {
            return;
        }
        Iterator<? extends E> it = this.currentIterator;
        if (it == null) {
            E e = this.root;
            if (e != null) {
                Transformer<? super E, ? extends E> transformer = this.transformer;
                if (transformer == null) {
                    findNext(e);
                } else {
                    findNext(transformer.transform(e));
                }
                this.root = null;
                return;
            }
            return;
        }
        findNextByIterator(it);
    }

    protected void findNext(E value) {
        if (value instanceof Iterator) {
            findNextByIterator((Iterator) value);
        } else {
            this.currentValue = value;
            this.hasNext = true;
        }
    }

    protected void findNextByIterator(Iterator<? extends E> it) {
        Iterator<? extends E> it2 = this.currentIterator;
        if (it != it2) {
            if (it2 != null) {
                this.stack.push(it2);
            }
            this.currentIterator = it;
        }
        while (this.currentIterator.hasNext() && !this.hasNext) {
            E next = this.currentIterator.next();
            Transformer<? super E, ? extends E> transformer = this.transformer;
            if (transformer != null) {
                next = transformer.transform(next);
            }
            findNext(next);
        }
        if (!this.hasNext && !this.stack.isEmpty()) {
            Iterator<? extends E> itPop = this.stack.pop();
            this.currentIterator = itPop;
            findNextByIterator(itPop);
        }
    }

    @Override // java.util.Iterator
    public boolean hasNext() {
        updateCurrentIterator();
        return this.hasNext;
    }

    @Override // java.util.Iterator
    public E next() {
        updateCurrentIterator();
        if (!this.hasNext) {
            throw new NoSuchElementException("No more elements in the iteration");
        }
        this.lastUsedIterator = this.currentIterator;
        E result = this.currentValue;
        this.currentValue = null;
        this.hasNext = false;
        return result;
    }

    @Override // java.util.Iterator
    public void remove() {
        Iterator<? extends E> it = this.lastUsedIterator;
        if (it == null) {
            throw new IllegalStateException("Iterator remove() cannot be called at this time");
        }
        it.remove();
        this.lastUsedIterator = null;
    }
}
