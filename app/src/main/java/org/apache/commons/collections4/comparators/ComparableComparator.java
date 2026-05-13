package org.apache.commons.collections4.comparators;

import java.io.Serializable;
import java.lang.Comparable;
import java.util.Comparator;

/* JADX INFO: loaded from: classes.dex */
public class ComparableComparator<E extends Comparable<? super E>> implements Comparator<E>, Serializable {
    public static final ComparableComparator INSTANCE = new ComparableComparator();
    private static final long serialVersionUID = -291439688585137865L;

    public static <E extends Comparable<? super E>> ComparableComparator<E> comparableComparator() {
        return INSTANCE;
    }

    @Override // java.util.Comparator
    public int compare(E obj1, E obj2) {
        return obj1.compareTo(obj2);
    }

    public int hashCode() {
        return "ComparableComparator".hashCode();
    }

    @Override // java.util.Comparator
    public boolean equals(Object object) {
        return this == object || (object != null && object.getClass().equals(getClass()));
    }
}
