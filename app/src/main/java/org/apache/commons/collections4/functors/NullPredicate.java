package org.apache.commons.collections4.functors;

import java.io.Serializable;
import org.apache.commons.collections4.Predicate;

/* JADX INFO: loaded from: classes.dex */
public final class NullPredicate<T> implements Predicate<T>, Serializable {
    public static final Predicate INSTANCE = new NullPredicate();
    private static final long serialVersionUID = 7533784454832764388L;

    public static <T> Predicate<T> nullPredicate() {
        return INSTANCE;
    }

    private NullPredicate() {
    }

    @Override // org.apache.commons.collections4.Predicate
    public boolean evaluate(T object) {
        return object == null;
    }

    private Object readResolve() {
        return INSTANCE;
    }
}
