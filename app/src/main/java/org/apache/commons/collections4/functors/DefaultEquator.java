package org.apache.commons.collections4.functors;

import java.io.Serializable;
import org.apache.commons.collections4.Equator;

/* JADX INFO: loaded from: classes.dex */
public class DefaultEquator<T> implements Equator<T>, Serializable {
    public static final int HASHCODE_NULL = -1;
    public static final DefaultEquator INSTANCE = new DefaultEquator();
    private static final long serialVersionUID = 825802648423525485L;

    public static <T> DefaultEquator<T> defaultEquator() {
        return INSTANCE;
    }

    private DefaultEquator() {
    }

    @Override // org.apache.commons.collections4.Equator
    public boolean equate(T o1, T o2) {
        return o1 == o2 || (o1 != null && o1.equals(o2));
    }

    @Override // org.apache.commons.collections4.Equator
    public int hash(T o) {
        if (o == null) {
            return -1;
        }
        return o.hashCode();
    }

    private Object readResolve() {
        return INSTANCE;
    }
}
