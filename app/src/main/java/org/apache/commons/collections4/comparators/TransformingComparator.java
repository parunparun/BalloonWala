package org.apache.commons.collections4.comparators;

import java.io.Serializable;
import java.util.Comparator;
import org.apache.commons.collections4.ComparatorUtils;
import org.apache.commons.collections4.Transformer;

/* JADX INFO: loaded from: classes.dex */
public class TransformingComparator<I, O> implements Comparator<I>, Serializable {
    private static final long serialVersionUID = 3456940356043606220L;
    private final Comparator<O> decorated;
    private final Transformer<? super I, ? extends O> transformer;

    public TransformingComparator(Transformer<? super I, ? extends O> transformer) {
        this(transformer, ComparatorUtils.NATURAL_COMPARATOR);
    }

    public TransformingComparator(Transformer<? super I, ? extends O> transformer, Comparator<O> decorated) {
        this.decorated = decorated;
        this.transformer = transformer;
    }

    @Override // java.util.Comparator
    public int compare(I obj1, I obj2) {
        O value1 = this.transformer.transform(obj1);
        O value2 = this.transformer.transform(obj2);
        return this.decorated.compare(value1, value2);
    }

    public int hashCode() {
        int i = 17 * 37;
        Comparator<O> comparator = this.decorated;
        int total = i + (comparator == null ? 0 : comparator.hashCode());
        int total2 = total * 37;
        Transformer<? super I, ? extends O> transformer = this.transformer;
        return total2 + (transformer != null ? transformer.hashCode() : 0);
    }

    @Override // java.util.Comparator
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || !object.getClass().equals(getClass())) {
            return false;
        }
        TransformingComparator<?, ?> comp = (TransformingComparator) object;
        Comparator<O> comparator = this.decorated;
        if (comparator != null ? comparator.equals(comp.decorated) : comp.decorated == null) {
            Transformer<? super I, ? extends O> transformer = this.transformer;
            if (transformer == null) {
                if (comp.transformer == null) {
                    return true;
                }
            } else if (transformer.equals(comp.transformer)) {
                return true;
            }
        }
        return false;
    }
}
