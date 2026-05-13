package org.apache.commons.collections4.set;

import java.util.Set;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.collections4.collection.TransformedCollection;

/* JADX INFO: loaded from: classes.dex */
public class TransformedSet<E> extends TransformedCollection<E> implements Set<E> {
    private static final long serialVersionUID = 306127383500410386L;

    public static <E> TransformedSet<E> transformingSet(Set<E> set, Transformer<? super E, ? extends E> transformer) {
        return new TransformedSet<>(set, transformer);
    }

    public static <E> Set<E> transformedSet(Set<E> set, Transformer<? super E, ? extends E> transformer) {
        TransformedSet<E> decorated = new TransformedSet<>(set, transformer);
        if (set.size() > 0) {
            Object[] array = set.toArray();
            set.clear();
            for (Object obj : array) {
                decorated.decorated().add(transformer.transform(obj));
            }
        }
        return decorated;
    }

    protected TransformedSet(Set<E> set, Transformer<? super E, ? extends E> transformer) {
        super(set, transformer);
    }

    @Override // java.util.Collection, java.util.Set
    public boolean equals(Object object) {
        return object == this || decorated().equals(object);
    }

    @Override // java.util.Collection, java.util.Set
    public int hashCode() {
        return decorated().hashCode();
    }
}
