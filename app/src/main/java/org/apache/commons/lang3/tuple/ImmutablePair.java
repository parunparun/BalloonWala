package org.apache.commons.lang3.tuple;

import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public final class ImmutablePair<L, R> extends Pair<L, R> {
    public static final ImmutablePair<?, ?>[] EMPTY_ARRAY = new ImmutablePair[0];
    private static final ImmutablePair NULL = of((Object) null, (Object) null);
    private static final long serialVersionUID = 4954918890077093841L;
    public final L left;
    public final R right;

    /* JADX WARN: Multi-variable type inference failed */
    public static <L, R> ImmutablePair<L, R>[] emptyArray() {
        return EMPTY_ARRAY;
    }

    public static <L, R> ImmutablePair<L, R> nullPair() {
        return NULL;
    }

    public static <L, R> ImmutablePair<L, R> of(L left, R right) {
        return new ImmutablePair<>(left, right);
    }

    public static <L, R> ImmutablePair<L, R> of(Map.Entry<L, R> pair) {
        L left;
        R right;
        if (pair != null) {
            left = pair.getKey();
            right = pair.getValue();
        } else {
            left = null;
            right = null;
        }
        return new ImmutablePair<>(left, right);
    }

    public ImmutablePair(L left, R right) {
        this.left = left;
        this.right = right;
    }

    @Override // org.apache.commons.lang3.tuple.Pair
    public L getLeft() {
        return this.left;
    }

    @Override // org.apache.commons.lang3.tuple.Pair
    public R getRight() {
        return this.right;
    }

    @Override // java.util.Map.Entry
    public R setValue(R value) {
        throw new UnsupportedOperationException();
    }
}
