package org.apache.commons.collections4.list;

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import org.apache.commons.collections4.collection.AbstractCollectionDecorator;

/* JADX INFO: loaded from: classes.dex */
public abstract class AbstractListDecorator<E> extends AbstractCollectionDecorator<E> implements List<E> {
    private static final long serialVersionUID = 4500739654952315623L;

    protected AbstractListDecorator() {
    }

    protected AbstractListDecorator(List<E> list) {
        super(list);
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
    public void add(int index, E object) {
        decorated().add(index, object);
    }

    @Override // java.util.List
    public boolean addAll(int index, Collection<? extends E> coll) {
        return decorated().addAll(index, coll);
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
    public ListIterator<E> listIterator() {
        return decorated().listIterator();
    }

    @Override // java.util.List
    public ListIterator<E> listIterator(int index) {
        return decorated().listIterator(index);
    }

    @Override // java.util.List
    public E remove(int index) {
        return decorated().remove(index);
    }

    @Override // java.util.List
    public E set(int index, E object) {
        return decorated().set(index, object);
    }

    @Override // java.util.List
    public List<E> subList(int fromIndex, int toIndex) {
        return decorated().subList(fromIndex, toIndex);
    }
}
