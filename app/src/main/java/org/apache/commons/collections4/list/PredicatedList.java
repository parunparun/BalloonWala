package org.apache.commons.collections4.list;

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.collection.PredicatedCollection;
import org.apache.commons.collections4.iterators.AbstractListIteratorDecorator;

/* JADX INFO: loaded from: classes.dex */
public class PredicatedList<E> extends PredicatedCollection<E> implements List<E> {
    private static final long serialVersionUID = -5722039223898659102L;

    public static <T> PredicatedList<T> predicatedList(List<T> list, Predicate<? super T> predicate) {
        return new PredicatedList<>(list, predicate);
    }

    protected PredicatedList(List<E> list, Predicate<? super E> predicate) {
        super(list, predicate);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.commons.collections4.collection.AbstractCollectionDecorator
    public List<E> decorated() {
        return (List) super.decorated();
    }

    @Override // java.util.Collection, java.util.List
    public boolean equals(Object object) {
        return object == this || decorated().equals(object);
    }

    @Override // java.util.Collection, java.util.List
    public int hashCode() {
        return decorated().hashCode();
    }

    @Override // java.util.List
    public E get(int index) {
        return decorated().get(index);
    }

    @Override // java.util.List
    public int indexOf(Object object) {
        return decorated().indexOf(object);
    }

    @Override // java.util.List
    public int lastIndexOf(Object object) {
        return decorated().lastIndexOf(object);
    }

    @Override // java.util.List
    public E remove(int index) {
        return decorated().remove(index);
    }

    @Override // java.util.List
    public void add(int index, E object) {
        validate(object);
        decorated().add(index, object);
    }

    @Override // java.util.List
    public boolean addAll(int index, Collection<? extends E> coll) {
        for (E aColl : coll) {
            validate(aColl);
        }
        return decorated().addAll(index, coll);
    }

    @Override // java.util.List
    public ListIterator<E> listIterator() {
        return listIterator(0);
    }

    @Override // java.util.List
    public ListIterator<E> listIterator(int i) {
        return new PredicatedListIterator(decorated().listIterator(i));
    }

    @Override // java.util.List
    public E set(int index, E object) {
        validate(object);
        return decorated().set(index, object);
    }

    @Override // java.util.List
    public List<E> subList(int fromIndex, int toIndex) {
        List<E> sub = decorated().subList(fromIndex, toIndex);
        return new PredicatedList(sub, this.predicate);
    }

    protected class PredicatedListIterator extends AbstractListIteratorDecorator<E> {
        protected PredicatedListIterator(ListIterator<E> iterator) {
            super(iterator);
        }

        @Override // org.apache.commons.collections4.iterators.AbstractListIteratorDecorator, java.util.ListIterator
        public void add(E object) {
            PredicatedList.this.validate(object);
            getListIterator().add(object);
        }

        @Override // org.apache.commons.collections4.iterators.AbstractListIteratorDecorator, java.util.ListIterator
        public void set(E object) {
            PredicatedList.this.validate(object);
            getListIterator().set(object);
        }
    }
}
