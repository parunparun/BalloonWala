package org.apache.commons.collections4.functors;

import java.io.Serializable;
import org.apache.commons.collections4.Equator;
import org.apache.commons.collections4.Predicate;

/* JADX INFO: loaded from: classes.dex */
public final class EqualPredicate<T> implements Predicate<T>, Serializable {
    private static final long serialVersionUID = 5633766978029907089L;
    private final Equator<T> equator;
    private final T iValue;

    public static <T> Predicate<T> equalPredicate(T object) {
        if (object == null) {
            return NullPredicate.nullPredicate();
        }
        return new EqualPredicate(object);
    }

    public static <T> Predicate<T> equalPredicate(T object, Equator<T> equator) {
        if (object == null) {
            return NullPredicate.nullPredicate();
        }
        return new EqualPredicate(object, equator);
    }

    public EqualPredicate(T object) {
        this(object, null);
    }

    public EqualPredicate(T object, Equator<T> equator) {
        this.iValue = object;
        this.equator = equator;
    }

    @Override // org.apache.commons.collections4.Predicate
    public boolean evaluate(T object) {
        Equator<T> equator = this.equator;
        if (equator != null) {
            return equator.equate(this.iValue, object);
        }
        return this.iValue.equals(object);
    }

    public Object getValue() {
        return this.iValue;
    }
}
