package org.apache.commons.collections4.list;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Predicate;
import org.apache.commons.collections4.BoundedCollection;
import org.apache.commons.collections4.iterators.AbstractListIteratorDecorator;
import org.apache.commons.collections4.iterators.UnmodifiableIterator;

/* JADX INFO: loaded from: classes.dex */
public class FixedSizeList<E> extends AbstractSerializableListDecorator<E> implements BoundedCollection<E> {
    private static final long serialVersionUID = -2218010673611160319L;

    public static <E> FixedSizeList<E> fixedSizeList(List<E> list) {
        return new FixedSizeList<>(list);
    }

    protected FixedSizeList(List<E> list) {
        super(list);
    }

    @Override // org.apache.commons.collections4.collection.AbstractCollectionDecorator, java.util.Collection, org.apache.commons.collections4.Bag
    public boolean add(E object) {
        throw unsupportedOperationException();
    }

    @Override // org.apache.commons.collections4.list.AbstractListDecorator, java.util.List
    public void add(int index, E object) {
        throw unsupportedOperationException();
    }

    @Override // org.apache.commons.collections4.collection.AbstractCollectionDecorator, java.util.Collection
    public boolean addAll(Collection<? extends E> coll) {
        throw unsupportedOperationException();
    }

    @Override // org.apache.commons.collections4.list.AbstractListDecorator, java.util.List
    public boolean addAll(int index, Collection<? extends E> coll) {
        throw unsupportedOperationException();
    }

    @Override // org.apache.commons.collections4.collection.AbstractCollectionDecorator, java.util.Collection
    public void clear() {
        throw unsupportedOperationException();
    }

    @Override // org.apache.commons.collections4.list.AbstractListDecorator, java.util.List
    public E get(int i) {
        return (E) decorated().get(i);
    }

    @Override // org.apache.commons.collections4.list.AbstractListDecorator, java.util.List
    public int indexOf(Object object) {
        return decorated().indexOf(object);
    }

    @Override // org.apache.commons.collections4.collection.AbstractCollectionDecorator, java.util.Collection, java.lang.Iterable, org.apache.commons.collections4.Bag
    public Iterator<E> iterator() {
        return UnmodifiableIterator.unmodifiableIterator(decorated().iterator());
    }

    @Override // org.apache.commons.collections4.list.AbstractListDecorator, java.util.List
    public int lastIndexOf(Object object) {
        return decorated().lastIndexOf(object);
    }

    @Override // org.apache.commons.collections4.list.AbstractListDecorator, java.util.List
    public ListIterator<E> listIterator() {
        return new FixedSizeListIterator(decorated().listIterator(0));
    }

    @Override // org.apache.commons.collections4.list.AbstractListDecorator, java.util.List
    public ListIterator<E> listIterator(int index) {
        return new FixedSizeListIterator(decorated().listIterator(index));
    }

    @Override // org.apache.commons.collections4.list.AbstractListDecorator, java.util.List
    public E remove(int index) {
        throw unsupportedOperationException();
    }

    @Override // org.apache.commons.collections4.collection.AbstractCollectionDecorator, java.util.Collection, org.apache.commons.collections4.Bag
    public boolean remove(Object object) {
        throw unsupportedOperationException();
    }

    @Override // org.apache.commons.collections4.collection.AbstractCollectionDecorator, java.util.Collection
    public boolean removeIf(Predicate<? super E> filter) {
        throw unsupportedOperationException();
    }

    @Override // org.apache.commons.collections4.collection.AbstractCollectionDecorator, java.util.Collection, org.apache.commons.collections4.Bag
    public boolean removeAll(Collection<?> coll) {
        throw unsupportedOperationException();
    }

    @Override // org.apache.commons.collections4.collection.AbstractCollectionDecorator, java.util.Collection, org.apache.commons.collections4.Bag
    public boolean retainAll(Collection<?> coll) {
        throw unsupportedOperationException();
    }

    @Override // org.apache.commons.collections4.list.AbstractListDecorator, java.util.List
    public E set(int i, E e) {
        return (E) decorated().set(i, e);
    }

    @Override // org.apache.commons.collections4.list.AbstractListDecorator, java.util.List
    public List<E> subList(int fromIndex, int toIndex) {
        List<E> sub = decorated().subList(fromIndex, toIndex);
        return new FixedSizeList(sub);
    }

    private class FixedSizeListIterator extends AbstractListIteratorDecorator<E> {
        protected FixedSizeListIterator(ListIterator<E> iterator) {
            super(iterator);
        }

        @Override // org.apache.commons.collections4.iterators.AbstractListIteratorDecorator, java.util.ListIterator, java.util.Iterator
        public void remove() {
            throw FixedSizeList.unsupportedOperationException();
        }

        @Override // org.apache.commons.collections4.iterators.AbstractListIteratorDecorator, java.util.ListIterator
        public void add(Object object) {
            throw FixedSizeList.unsupportedOperationException();
        }
    }

    @Override // org.apache.commons.collections4.BoundedCollection
    public boolean isFull() {
        return true;
    }

    @Override // org.apache.commons.collections4.BoundedCollection
    public int maxSize() {
        return size();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static UnsupportedOperationException unsupportedOperationException() {
        return new UnsupportedOperationException("List is fixed size");
    }
}
