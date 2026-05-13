package org.apache.commons.collections4.queue;

import java.util.Queue;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.collection.PredicatedCollection;

/* JADX INFO: loaded from: classes.dex */
public class PredicatedQueue<E> extends PredicatedCollection<E> implements Queue<E> {
    private static final long serialVersionUID = 2307609000539943581L;

    public static <E> PredicatedQueue<E> predicatedQueue(Queue<E> Queue, Predicate<? super E> predicate) {
        return new PredicatedQueue<>(Queue, predicate);
    }

    protected PredicatedQueue(Queue<E> queue, Predicate<? super E> predicate) {
        super(queue, predicate);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.commons.collections4.collection.AbstractCollectionDecorator
    public Queue<E> decorated() {
        return (Queue) super.decorated();
    }

    @Override // java.util.Queue
    public boolean offer(E object) {
        validate(object);
        return decorated().offer(object);
    }

    @Override // java.util.Queue
    public E poll() {
        return decorated().poll();
    }

    @Override // java.util.Queue
    public E peek() {
        return decorated().peek();
    }

    @Override // java.util.Queue
    public E element() {
        return decorated().element();
    }

    @Override // java.util.Queue
    public E remove() {
        return decorated().remove();
    }
}
