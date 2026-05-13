package org.apache.commons.lang3.tuple;

import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class MutablePair<L, R> extends Pair<L, R> {
    public static final MutablePair<?, ?>[] EMPTY_ARRAY = new MutablePair[0];
    private static final long serialVersionUID = 4954918890077093841L;
    public L left;
    public R right;

    /* JADX WARN: Multi-variable type inference failed */
    public static <L, R> MutablePair<L, R>[] emptyArray() {
        return EMPTY_ARRAY;
    }

    public static <L, R> MutablePair<L, R> of(L left, R right) {
        return new MutablePair<>(left, right);
    }

    public static <L, R> MutablePair<L, R> of(Map.Entry<L, R> pair) {
        L left;
        R right;
        if (pair != null) {
            left = pair.getKey();
            right = pair.getValue();
        } else {
            left = null;
            right = null;
        }
        return new MutablePair<>(left, right);
    }

    public MutablePair() {
    }

    public MutablePair(L left, R right) {
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

    public void setLeft(L left) {
        this.left = left;
    }

    public void setRight(R right) {
        this.right = right;
    }

    @Override // java.util.Map.Entry
    public R setValue(R value) {
        R result = getRight();
        setRight(value);
        return result;
    }
}
