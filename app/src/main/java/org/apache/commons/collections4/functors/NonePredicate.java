package org.apache.commons.collections4.functors;

import java.util.Collection;
import org.apache.commons.collections4.Predicate;

/* JADX INFO: loaded from: classes.dex */
public final class NonePredicate<T> extends AbstractQuantifierPredicate<T> {
    private static final long serialVersionUID = 2007613066565892961L;

    public static <T> Predicate<T> nonePredicate(Predicate<? super T>... predicates) {
        FunctorUtils.validate(predicates);
        if (predicates.length == 0) {
            return TruePredicate.truePredicate();
        }
        return new NonePredicate(FunctorUtils.copy(predicates));
    }

    public static <T> Predicate<T> nonePredicate(Collection<? extends Predicate<? super T>> predicates) {
        Predicate<? super T>[] preds = FunctorUtils.validate(predicates);
        if (preds.length == 0) {
            return TruePredicate.truePredicate();
        }
        return new NonePredicate(preds);
    }

    public NonePredicate(Predicate<? super T>... predicates) {
        super(predicates);
    }

    @Override // org.apache.commons.collections4.Predicate
    public boolean evaluate(T object) {
        for (Predicate<? super T> iPredicate : this.iPredicates) {
            if (iPredicate.evaluate(object)) {
                return false;
            }
        }
        return true;
    }
}
