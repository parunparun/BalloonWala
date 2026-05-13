package org.apache.commons.collections4.functors;

import java.util.Collection;
import org.apache.commons.collections4.Predicate;

/* JADX INFO: loaded from: classes.dex */
public final class OnePredicate<T> extends AbstractQuantifierPredicate<T> {
    private static final long serialVersionUID = -8125389089924745785L;

    public static <T> Predicate<T> onePredicate(Predicate<? super T>... predicateArr) {
        FunctorUtils.validate(predicateArr);
        if (predicateArr.length == 0) {
            return FalsePredicate.falsePredicate();
        }
        if (predicateArr.length == 1) {
            return (Predicate<T>) predicateArr[0];
        }
        return new OnePredicate(FunctorUtils.copy(predicateArr));
    }

    public static <T> Predicate<T> onePredicate(Collection<? extends Predicate<? super T>> predicates) {
        Predicate<? super T>[] preds = FunctorUtils.validate(predicates);
        return new OnePredicate(preds);
    }

    public OnePredicate(Predicate<? super T>... predicates) {
        super(predicates);
    }

    @Override // org.apache.commons.collections4.Predicate
    public boolean evaluate(T object) {
        boolean match = false;
        for (Predicate<? super T> iPredicate : this.iPredicates) {
            if (iPredicate.evaluate(object)) {
                if (match) {
                    return false;
                }
                match = true;
            }
        }
        return match;
    }
}
