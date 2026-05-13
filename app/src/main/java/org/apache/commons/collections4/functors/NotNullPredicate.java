package org.apache.commons.collections4.functors;

import java.io.Serializable;
import org.apache.commons.collections4.Predicate;

/* JADX INFO: loaded from: classes.dex */
public final class NotNullPredicate<T> implements Predicate<T>, Serializable {
    public static final Predicate INSTANCE = new NotNullPredicate();
    private static final long serialVersionUID = 7533784454832764388L;

    public static <T> Predicate<T> notNullPredicate() {
        return INSTANCE;
    }

    private NotNullPredicate() {
    }

    @Override // org.apache.commons.collections4.Predicate
    public boolean evaluate(T object) {
        return object != null;
    }

    private Object readResolve() {
        return INSTANCE;
    }
}
