package org.apache.commons.collections4.queue;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;
import org.apache.commons.collections4.BoundedCollection;

/* JADX INFO: loaded from: classes.dex */
public class CircularFifoQueue<E> extends AbstractCollection<E> implements Queue<E>, BoundedCollection<E>, Serializable {
    private static final long serialVersionUID = -8423413834657610406L;
    private transient E[] elements;
    private transient int end;
    private transient boolean full;
    private final int maxElements;
    private transient int start;

    public CircularFifoQueue() {
        this(32);
    }

    public CircularFifoQueue(int i) {
        this.start = 0;
        this.end = 0;
        this.full = false;
        if (i <= 0) {
            throw new IllegalArgumentException("The size must be greater than 0");
        }
        E[] eArr = (E[]) new Object[i];
        this.elements = eArr;
        this.maxElements = eArr.length;
    }

    public CircularFifoQueue(Collection<? extends E> coll) {
        this(coll.size());
        addAll(coll);
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeInt(size());
        for (E e : this) {
            out.writeObject(e);
        }
    }

    private void readObject(ObjectInputStream objectInputStream) throws ClassNotFoundException, IOException {
        objectInputStream.defaultReadObject();
        this.elements = (E[]) new Object[this.maxElements];
        int i = objectInputStream.readInt();
        for (int i2 = 0; i2 < i; i2++) {
            ((E[]) this.elements)[i2] = objectInputStream.readObject();
        }
        this.start = 0;
        boolean z = i == this.maxElements;
        this.full = z;
        if (z) {
            this.end = 0;
        } else {
            this.end = i;
        }
    }

    @Override // java.util.AbstractCollection, java.util.Collection
    public int size() {
        int i = this.end;
        int i2 = this.start;
        if (i < i2) {
            int size = (this.maxElements - i2) + i;
            return size;
        }
        if (i == i2) {
            int size2 = this.full ? this.maxElements : 0;
            return size2;
        }
        int size3 = i - i2;
        return size3;
    }

    @Override // java.util.AbstractCollection, java.util.Collection
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override // org.apache.commons.collections4.BoundedCollection
    public boolean isFull() {
        return false;
    }

    public boolean isAtFullCapacity() {
        return size() == this.maxElements;
    }

    @Override // org.apache.commons.collections4.BoundedCollection
    public int maxSize() {
        return this.maxElements;
    }

    @Override // java.util.AbstractCollection, java.util.Collection
    public void clear() {
        this.full = false;
        this.start = 0;
        this.end = 0;
        Arrays.fill(this.elements, (Object) null);
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.Queue
    public boolean add(E element) {
        if (element == null) {
            throw new NullPointerException("Attempted to add null object to queue");
        }
        if (isAtFullCapacity()) {
            remove();
        }
        E[] eArr = this.elements;
        int i = this.end;
        int i2 = i + 1;
        this.end = i2;
        eArr[i] = element;
        if (i2 >= this.maxElements) {
            this.end = 0;
        }
        if (this.end == this.start) {
            this.full = true;
        }
        return true;
    }

    public E get(int index) {
        int sz = size();
        if (index < 0 || index >= sz) {
            throw new NoSuchElementException(String.format("The specified index (%1$d) is outside the available range [0, %2$d)", Integer.valueOf(index), Integer.valueOf(sz)));
        }
        int idx = (this.start + index) % this.maxElements;
        return this.elements[idx];
    }

    @Override // java.util.Queue
    public boolean offer(E element) {
        return add(element);
    }

    @Override // java.util.Queue
    public E poll() {
        if (isEmpty()) {
            return null;
        }
        return remove();
    }

    @Override // java.util.Queue
    public E element() {
        if (isEmpty()) {
            throw new NoSuchElementException("queue is empty");
        }
        return peek();
    }

    @Override // java.util.Queue
    public E peek() {
        if (isEmpty()) {
            return null;
        }
        return this.elements[this.start];
    }

    @Override // java.util.Queue
    public E remove() {
        if (isEmpty()) {
            throw new NoSuchElementException("queue is empty");
        }
        E[] eArr = this.elements;
        int i = this.start;
        E element = eArr[i];
        if (element != null) {
            int i2 = i + 1;
            this.start = i2;
            eArr[i] = null;
            if (i2 >= this.maxElements) {
                this.start = 0;
            }
            this.full = false;
        }
        return element;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int increment(int index) {
        int index2 = index + 1;
        if (index2 >= this.maxElements) {
            return 0;
        }
        return index2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int decrement(int index) {
        int index2 = index - 1;
        if (index2 < 0) {
            int index3 = this.maxElements - 1;
            return index3;
        }
        return index2;
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable
    public Iterator<E> iterator() {
        return new Iterator<E>() { // from class: org.apache.commons.collections4.queue.CircularFifoQueue.1
            private int index;
            private boolean isFirst;
            private int lastReturnedIndex = -1;

            {
                this.index = CircularFifoQueue.this.start;
                this.isFirst = CircularFifoQueue.this.full;
            }

            @Override // java.util.Iterator
            public boolean hasNext() {
                return this.isFirst || this.index != CircularFifoQueue.this.end;
            }

            @Override // java.util.Iterator
            public E next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                this.isFirst = false;
                int i = this.index;
                this.lastReturnedIndex = i;
                this.index = CircularFifoQueue.this.increment(i);
                return (E) CircularFifoQueue.this.elements[this.lastReturnedIndex];
            }

            @Override // java.util.Iterator
            public void remove() {
                int i = this.lastReturnedIndex;
                if (i != -1) {
                    if (i == CircularFifoQueue.this.start) {
                        CircularFifoQueue.this.remove();
                        this.lastReturnedIndex = -1;
                        return;
                    }
                    int pos = this.lastReturnedIndex + 1;
                    if (CircularFifoQueue.this.start >= this.lastReturnedIndex || pos >= CircularFifoQueue.this.end) {
                        while (pos != CircularFifoQueue.this.end) {
                            if (pos >= CircularFifoQueue.this.maxElements) {
                                CircularFifoQueue.this.elements[pos - 1] = CircularFifoQueue.this.elements[0];
                                pos = 0;
                            } else {
                                CircularFifoQueue.this.elements[CircularFifoQueue.this.decrement(pos)] = CircularFifoQueue.this.elements[pos];
                                pos = CircularFifoQueue.this.increment(pos);
                            }
                        }
                    } else {
                        System.arraycopy(CircularFifoQueue.this.elements, pos, CircularFifoQueue.this.elements, this.lastReturnedIndex, CircularFifoQueue.this.end - pos);
                    }
                    this.lastReturnedIndex = -1;
                    CircularFifoQueue circularFifoQueue = CircularFifoQueue.this;
                    circularFifoQueue.end = circularFifoQueue.decrement(circularFifoQueue.end);
                    CircularFifoQueue.this.elements[CircularFifoQueue.this.end] = null;
                    CircularFifoQueue.this.full = false;
                    this.index = CircularFifoQueue.this.decrement(this.index);
                    return;
                }
                throw new IllegalStateException();
            }
        };
    }
}
