package org.apache.commons.lang3.compare;

import java.util.function.Predicate;

/* JADX INFO: loaded from: classes.dex */
public class ComparableUtils {

    public static class ComparableCheckBuilder<A extends Comparable<A>> {
        private final A a;

        private ComparableCheckBuilder(A a) {
            this.a = a;
        }

        public boolean between(A b, A c) {
            return betweenOrdered(b, c) || betweenOrdered(c, b);
        }

        public boolean betweenExclusive(A b, A c) {
            return betweenOrderedExclusive(b, c) || betweenOrderedExclusive(c, b);
        }

        private boolean betweenOrdered(A b, A c) {
            return greaterThanOrEqualTo(b) && lessThanOrEqualTo(c);
        }

        private boolean betweenOrderedExclusive(A b, A c) {
            return greaterThan(b) && lessThan(c);
        }

        public boolean equalTo(A b) {
            return this.a.compareTo(b) == 0;
        }

        public boolean greaterThan(A b) {
            return this.a.compareTo(b) > 0;
        }

        public boolean greaterThanOrEqualTo(A b) {
            return this.a.compareTo(b) >= 0;
        }

        public boolean lessThan(A b) {
            return this.a.compareTo(b) < 0;
        }

        public boolean lessThanOrEqualTo(A b) {
            return this.a.compareTo(b) <= 0;
        }
    }

    public static <A extends Comparable<A>> Predicate<A> between(final A b, final A c) {
        return new Predicate() { // from class: org.apache.commons.lang3.compare.-$$Lambda$ComparableUtils$bJPOLvCIE42KsklTLQvkD76-WHQ
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return ComparableUtils.is((Comparable) obj).between(b, c);
            }
        };
    }

    public static <A extends Comparable<A>> Predicate<A> betweenExclusive(final A b, final A c) {
        return new Predicate() { // from class: org.apache.commons.lang3.compare.-$$Lambda$ComparableUtils$1PcpLtFjDByJ9CCier5d1xU4jh4
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return ComparableUtils.is((Comparable) obj).betweenExclusive(b, c);
            }
        };
    }

    public static <A extends Comparable<A>> Predicate<A> ge(final A b) {
        return new Predicate() { // from class: org.apache.commons.lang3.compare.-$$Lambda$ComparableUtils$ZX0Hhp6bkdESvOqxzFPX33T1veo
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return ComparableUtils.is((Comparable) obj).greaterThanOrEqualTo(b);
            }
        };
    }

    public static <A extends Comparable<A>> Predicate<A> gt(final A b) {
        return new Predicate() { // from class: org.apache.commons.lang3.compare.-$$Lambda$ComparableUtils$VJll9q3DLzN2y8KYe16pJJkCnpA
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return ComparableUtils.is((Comparable) obj).greaterThan(b);
            }
        };
    }

    public static <A extends Comparable<A>> ComparableCheckBuilder<A> is(A a) {
        return new ComparableCheckBuilder<>(a);
    }

    public static <A extends Comparable<A>> Predicate<A> le(final A b) {
        return new Predicate() { // from class: org.apache.commons.lang3.compare.-$$Lambda$ComparableUtils$3EEpnxVsPG7iqGkEbcadHLPgKQM
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return ComparableUtils.is((Comparable) obj).lessThanOrEqualTo(b);
            }
        };
    }

    public static <A extends Comparable<A>> Predicate<A> lt(final A b) {
        return new Predicate() { // from class: org.apache.commons.lang3.compare.-$$Lambda$ComparableUtils$Kgpd2AfgGmqNlF3w25wvWl3OpD4
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return ComparableUtils.is((Comparable) obj).lessThan(b);
            }
        };
    }

    private ComparableUtils() {
    }
}
