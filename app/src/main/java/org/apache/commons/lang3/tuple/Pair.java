package org.apache.commons.lang3.tuple;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.lang3.builder.CompareToBuilder;

/* JADX INFO: loaded from: classes.dex */
public abstract class Pair<L, R> implements Map.Entry<L, R>, Comparable<Pair<L, R>>, Serializable {
    public static final Pair<?, ?>[] EMPTY_ARRAY = new PairAdapter[0];
    private static final long serialVersionUID = 4954918890077093841L;

    public abstract L getLeft();

    public abstract R getRight();

    private static final class PairAdapter<L, R> extends Pair<L, R> {
        private static final long serialVersionUID = 1;

        private PairAdapter() {
        }

        @Override // org.apache.commons.lang3.tuple.Pair, java.lang.Comparable
        public /* bridge */ /* synthetic */ int compareTo(Object obj) {
            return super.compareTo((Pair) obj);
        }

        @Override // org.apache.commons.lang3.tuple.Pair
        public L getLeft() {
            return null;
        }

        @Override // org.apache.commons.lang3.tuple.Pair
        public R getRight() {
            return null;
        }

        @Override // java.util.Map.Entry
        public R setValue(R value) {
            return null;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static <L, R> Pair<L, R>[] emptyArray() {
        return EMPTY_ARRAY;
    }

    public static <L, R> Pair<L, R> of(L left, R right) {
        return ImmutablePair.of((Object) left, (Object) right);
    }

    public static <L, R> Pair<L, R> of(Map.Entry<L, R> pair) {
        return ImmutablePair.of((Map.Entry) pair);
    }

    @Override // java.lang.Comparable
    public int compareTo(Pair<L, R> other) {
        return new CompareToBuilder().append(getLeft(), other.getLeft()).append(getRight(), other.getRight()).toComparison();
    }

    @Override // java.util.Map.Entry
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Map.Entry)) {
            return false;
        }
        Map.Entry<?, ?> other = (Map.Entry) obj;
        return Objects.equals(getKey(), other.getKey()) && Objects.equals(getValue(), other.getValue());
    }

    @Override // java.util.Map.Entry
    public final L getKey() {
        return getLeft();
    }

    @Override // java.util.Map.Entry
    public R getValue() {
        return getRight();
    }

    @Override // java.util.Map.Entry
    public int hashCode() {
        return (getKey() == null ? 0 : getKey().hashCode()) ^ (getValue() != null ? getValue().hashCode() : 0);
    }

    public String toString() {
        return "(" + getLeft() + ',' + getRight() + ')';
    }

    public String toString(String format) {
        return String.format(format, getLeft(), getRight());
    }
}
