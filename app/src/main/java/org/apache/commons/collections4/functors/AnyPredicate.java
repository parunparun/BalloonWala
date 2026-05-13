package org.apache.commons.collections4.functors;

import java.util.Collection;
import org.apache.commons.collections4.Predicate;

/* JADX INFO: loaded from: classes.dex */
public final class AnyPredicate<T> extends AbstractQuantifierPredicate<T> {
    private static final long serialVersionUID = 7429999530934647542L;

    public static <T> Predicate<T> anyPredicate(Predicate<? super T>... predicateArr) {
        FunctorUtils.validate(predicateArr);
        if (predicateArr.length == 0) {
            return FalsePredicate.falsePredicate();
        }
        if (predicateArr.length == 1) {
            return (Predicate<T>) predicateArr[0];
        }
        return new AnyPredicate(FunctorUtils.copy(predicateArr));
    }

    public static <T> Predicate<T> anyPredicate(Collection<? extends Predicate<? super T>> predicates) {
        Predicate<T>[] predicateArrValidate = FunctorUtils.validate(predicates);
        if (predicateArrValidate.length == 0) {
            return FalsePredicate.falsePredicate();
        }
        if (predicateArrValidate.length == 1) {
            return predicateArrValidate[0];
        }
        return new AnyPredicate(predicateArrValidate);
    }

    public AnyPredicate(Predicate<? super T>... predicates) {
        super(predicates);
    }

    @Override // org.apache.commons.collections4.Predicate
    public boolean evaluate(T object) {
        for (Predicate<? super T> iPredicate : this.iPredicates) {
            if (iPredicate.evaluate(object)) {
                return true;
            }
        }
        return false;
    }
}
