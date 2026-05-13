package org.apache.commons.collections4.functors;

import java.io.Serializable;
import org.apache.commons.collections4.Predicate;

/* JADX INFO: loaded from: classes.dex */
public final class TruePredicate<T> implements Predicate<T>, Serializable {
    public static final Predicate INSTANCE = new TruePredicate();
    private static final long serialVersionUID = 3374767158756189740L;

    public static <T> Predicate<T> truePredicate() {
        return INSTANCE;
    }

    private TruePredicate() {
    }

    @Override // org.apache.commons.collections4.Predicate
    public boolean evaluate(T object) {
        return true;
    }

    private Object readResolve() {
        return INSTANCE;
    }
}
