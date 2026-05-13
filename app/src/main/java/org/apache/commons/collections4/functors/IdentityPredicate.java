package org.apache.commons.collections4.functors;

import java.io.Serializable;
import org.apache.commons.collections4.Predicate;

/* JADX INFO: loaded from: classes.dex */
public final class IdentityPredicate<T> implements Predicate<T>, Serializable {
    private static final long serialVersionUID = -89901658494523293L;
    private final T iValue;

    public static <T> Predicate<T> identityPredicate(T object) {
        if (object == null) {
            return NullPredicate.nullPredicate();
        }
        return new IdentityPredicate(object);
    }

    public IdentityPredicate(T object) {
        this.iValue = object;
    }

    @Override // org.apache.commons.collections4.Predicate
    public boolean evaluate(T object) {
        return this.iValue == object;
    }

    public T getValue() {
        return this.iValue;
    }
}
