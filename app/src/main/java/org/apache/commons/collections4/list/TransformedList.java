package org.apache.commons.collections4.list;

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.collections4.collection.TransformedCollection;
import org.apache.commons.collections4.iterators.AbstractListIteratorDecorator;

/* JADX INFO: loaded from: classes.dex */
public class TransformedList<E> extends TransformedCollection<E> implements List<E> {
    private static final long serialVersionUID = 1077193035000013141L;

    public static <E> TransformedList<E> transformingList(List<E> list, Transformer<? super E, ? extends E> transformer) {
        return new TransformedList<>(list, transformer);
    }

    public static <E> TransformedList<E> transformedList(List<E> list, Transformer<? super E, ? extends E> transformer) {
        TransformedList<E> decorated = new TransformedList<>(list, transformer);
        if (list.size() > 0) {
            Object[] array = list.toArray();
            list.clear();
            for (Object obj : array) {
                decorated.decorated().add(transformer.transform(obj));
            }
        }
        return decorated;
    }

    protected TransformedList(List<E> list, Transformer<? super E, ? extends E> transformer) {
        super(list, transformer);
    }

    protected List<E> getList() {
        return (List) decorated();
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
        return getList().get(index);
    }

    @Override // java.util.List
    public int indexOf(Object object) {
        return getList().indexOf(object);
    }

    @Override // java.util.List
    public int lastIndexOf(Object object) {
        return getList().lastIndexOf(object);
    }

    @Override // java.util.List
    public E remove(int index) {
        return getList().remove(index);
    }

    @Override // java.util.List
    public void add(int index, E object) {
        getList().add(index, transform(object));
    }

    @Override // java.util.List
    public boolean addAll(int index, Collection<? extends E> coll) {
        return getList().addAll(index, transform((Collection) coll));
    }

    @Override // java.util.List
    public ListIterator<E> listIterator() {
        return listIterator(0);
    }

    @Override // java.util.List
    public ListIterator<E> listIterator(int i) {
        return new TransformedListIterator(getList().listIterator(i));
    }

    @Override // java.util.List
    public E set(int index, E object) {
        return getList().set(index, transform(object));
    }

    @Override // java.util.List
    public List<E> subList(int fromIndex, int toIndex) {
        List<E> sub = getList().subList(fromIndex, toIndex);
        return new TransformedList(sub, this.transformer);
    }

    protected class TransformedListIterator extends AbstractListIteratorDecorator<E> {
        protected TransformedListIterator(ListIterator<E> iterator) {
            super(iterator);
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // org.apache.commons.collections4.iterators.AbstractListIteratorDecorator, java.util.ListIterator
        public void add(E e) {
            getListIterator().add(TransformedList.this.transform(e));
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // org.apache.commons.collections4.iterators.AbstractListIteratorDecorator, java.util.ListIterator
        public void set(E e) {
            getListIterator().set(TransformedList.this.transform(e));
        }
    }
}
