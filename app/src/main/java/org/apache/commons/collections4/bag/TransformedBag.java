package org.apache.commons.collections4.bag;

import java.util.Set;
import org.apache.commons.collections4.Bag;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.collections4.collection.TransformedCollection;
import org.apache.commons.collections4.set.TransformedSet;

/* JADX INFO: loaded from: classes.dex */
public class TransformedBag<E> extends TransformedCollection<E> implements Bag<E> {
    private static final long serialVersionUID = 5421170911299074185L;

    public static <E> Bag<E> transformingBag(Bag<E> bag, Transformer<? super E, ? extends E> transformer) {
        return new TransformedBag(bag, transformer);
    }

    public static <E> Bag<E> transformedBag(Bag<E> bag, Transformer<? super E, ? extends E> transformer) {
        TransformedBag<E> decorated = new TransformedBag<>(bag, transformer);
        if (bag.size() > 0) {
            Object[] array = bag.toArray();
            bag.clear();
            for (Object obj : array) {
                decorated.decorated().add(transformer.transform(obj));
            }
        }
        return decorated;
    }

    protected TransformedBag(Bag<E> bag, Transformer<? super E, ? extends E> transformer) {
        super(bag, transformer);
    }

    protected Bag<E> getBag() {
        return (Bag) decorated();
    }

    @Override // java.util.Collection
    public boolean equals(Object object) {
        return object == this || decorated().equals(object);
    }

    @Override // java.util.Collection
    public int hashCode() {
        return decorated().hashCode();
    }

    @Override // org.apache.commons.collections4.Bag
    public int getCount(Object object) {
        return getBag().getCount(object);
    }

    @Override // org.apache.commons.collections4.Bag
    public boolean remove(Object object, int nCopies) {
        return getBag().remove(object, nCopies);
    }

    @Override // org.apache.commons.collections4.Bag
    public boolean add(E object, int nCopies) {
        return getBag().add(transform(object), nCopies);
    }

    @Override // org.apache.commons.collections4.Bag
    public Set<E> uniqueSet() {
        Set<E> set = getBag().uniqueSet();
        return TransformedSet.transformingSet(set, this.transformer);
    }
}
