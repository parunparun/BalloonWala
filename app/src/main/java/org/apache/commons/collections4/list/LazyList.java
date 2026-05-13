package org.apache.commons.collections4.list;

import java.util.List;
import java.util.Objects;
import org.apache.commons.collections4.Factory;
import org.apache.commons.collections4.Transformer;

/* JADX INFO: loaded from: classes.dex */
public class LazyList<E> extends AbstractSerializableListDecorator<E> {
    private static final long serialVersionUID = -3677737457567429713L;
    private final Factory<? extends E> factory;
    private final Transformer<Integer, ? extends E> transformer;

    public static <E> LazyList<E> lazyList(List<E> list, Factory<? extends E> factory) {
        return new LazyList<>(list, factory);
    }

    public static <E> LazyList<E> lazyList(List<E> list, Transformer<Integer, ? extends E> transformer) {
        return new LazyList<>(list, transformer);
    }

    protected LazyList(List<E> list, Factory<? extends E> factory) {
        super(list);
        this.factory = (Factory) Objects.requireNonNull(factory);
        this.transformer = null;
    }

    protected LazyList(List<E> list, Transformer<Integer, ? extends E> transformer) {
        super(list);
        this.factory = null;
        this.transformer = (Transformer) Objects.requireNonNull(transformer);
    }

    @Override // org.apache.commons.collections4.list.AbstractListDecorator, java.util.List
    public E get(int i) {
        int size = decorated().size();
        if (i < size) {
            E e = (E) decorated().get(i);
            if (e == null) {
                E eElement = element(i);
                decorated().set(i, eElement);
                return eElement;
            }
            return e;
        }
        for (int i2 = size; i2 < i; i2++) {
            decorated().add(null);
        }
        E eElement2 = element(i);
        decorated().add(eElement2);
        return eElement2;
    }

    @Override // org.apache.commons.collections4.list.AbstractListDecorator, java.util.List
    public List<E> subList(int fromIndex, int toIndex) {
        List<E> sub = decorated().subList(fromIndex, toIndex);
        if (this.factory != null) {
            return new LazyList(sub, this.factory);
        }
        if (this.transformer != null) {
            return new LazyList(sub, this.transformer);
        }
        throw new IllegalStateException("Factory and Transformer are both null!");
    }

    private E element(int index) {
        Factory<? extends E> factory = this.factory;
        if (factory != null) {
            return factory.create();
        }
        Transformer<Integer, ? extends E> transformer = this.transformer;
        if (transformer != null) {
            return transformer.transform(Integer.valueOf(index));
        }
        throw new IllegalStateException("Factory and Transformer are both null!");
    }
}
