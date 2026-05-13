package org.apache.commons.collections4.functors;

import org.apache.commons.collections4.Predicate;

/* JADX INFO: loaded from: classes.dex */
public interface PredicateDecorator<T> extends Predicate<T> {
    Predicate<? super T>[] getPredicates();
}
