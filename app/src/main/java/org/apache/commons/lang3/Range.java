package org.apache.commons.lang3;

import java.io.Serializable;
import java.util.Comparator;

/* JADX INFO: loaded from: classes.dex */
public final class Range<T> implements Serializable {
    private static final long serialVersionUID = 1;
    private final Comparator<T> comparator;
    private transient int hashCode;
    private final T maximum;
    private final T minimum;
    private transient String toString;

    private enum ComparableComparator implements Comparator {
        INSTANCE;

        @Override // java.util.Comparator
        public int compare(Object obj1, Object obj2) {
            return ((Comparable) obj1).compareTo(obj2);
        }
    }

    /* JADX WARN: Incorrect types in method signature: <T::Ljava/lang/Comparable<TT;>;>(TT;TT;)Lorg/apache/commons/lang3/Range<TT;>; */
    public static Range between(Comparable comparable, Comparable comparable2) {
        return between(comparable, comparable2, null);
    }

    public static <T> Range<T> between(T fromInclusive, T toInclusive, Comparator<T> comparator) {
        return new Range<>(fromInclusive, toInclusive, comparator);
    }

    /* JADX WARN: Incorrect types in method signature: <T::Ljava/lang/Comparable<TT;>;>(TT;)Lorg/apache/commons/lang3/Range<TT;>; */
    public static Range is(Comparable comparable) {
        return between(comparable, comparable, null);
    }

    public static <T> Range<T> is(T element, Comparator<T> comparator) {
        return between(element, element, comparator);
    }

    private Range(T element1, T element2, Comparator<T> comp) {
        if (element1 == null || element2 == null) {
            throw new IllegalArgumentException("Elements in a range must not be null: element1=" + element1 + ", element2=" + element2);
        }
        if (comp == null) {
            this.comparator = ComparableComparator.INSTANCE;
        } else {
            this.comparator = comp;
        }
        if (this.comparator.compare(element1, element2) < 1) {
            this.minimum = element1;
            this.maximum = element2;
        } else {
            this.minimum = element2;
            this.maximum = element1;
        }
    }

    public boolean contains(T element) {
        return element != null && this.comparator.compare(element, this.minimum) > -1 && this.comparator.compare(element, this.maximum) < 1;
    }

    public boolean containsRange(Range<T> otherRange) {
        return otherRange != null && contains(otherRange.minimum) && contains(otherRange.maximum);
    }

    public int elementCompareTo(T element) {
        Validate.notNull(element, "Element is null", new Object[0]);
        if (isAfter(element)) {
            return -1;
        }
        if (!isBefore(element)) {
            return 0;
        }
        return 1;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != getClass()) {
            return false;
        }
        Range<T> range = (Range) obj;
        if (this.minimum.equals(range.minimum) && this.maximum.equals(range.maximum)) {
            return true;
        }
        return false;
    }

    public Comparator<T> getComparator() {
        return this.comparator;
    }

    public T getMaximum() {
        return this.maximum;
    }

    public T getMinimum() {
        return this.minimum;
    }

    public int hashCode() {
        int result = this.hashCode;
        if (this.hashCode == 0) {
            int result2 = (17 * 37) + getClass().hashCode();
            int result3 = (((result2 * 37) + this.minimum.hashCode()) * 37) + this.maximum.hashCode();
            this.hashCode = result3;
            return result3;
        }
        return result;
    }

    public Range<T> intersectionWith(Range<T> other) {
        if (!isOverlappedBy(other)) {
            throw new IllegalArgumentException(String.format("Cannot calculate intersection with non-overlapping range %s", other));
        }
        if (equals(other)) {
            return this;
        }
        T min = getComparator().compare(this.minimum, other.minimum) < 0 ? other.minimum : this.minimum;
        T max = getComparator().compare(this.maximum, other.maximum) < 0 ? this.maximum : other.maximum;
        return between(min, max, getComparator());
    }

    public boolean isAfter(T element) {
        return element != null && this.comparator.compare(element, this.minimum) < 0;
    }

    public boolean isAfterRange(Range<T> otherRange) {
        if (otherRange == null) {
            return false;
        }
        return isAfter(otherRange.maximum);
    }

    public boolean isBefore(T element) {
        return element != null && this.comparator.compare(element, this.maximum) > 0;
    }

    public boolean isBeforeRange(Range<T> otherRange) {
        if (otherRange == null) {
            return false;
        }
        return isBefore(otherRange.minimum);
    }

    public boolean isEndedBy(T element) {
        return element != null && this.comparator.compare(element, this.maximum) == 0;
    }

    public boolean isNaturalOrdering() {
        return this.comparator == ComparableComparator.INSTANCE;
    }

    public boolean isOverlappedBy(Range<T> otherRange) {
        if (otherRange == null) {
            return false;
        }
        return otherRange.contains(this.minimum) || otherRange.contains(this.maximum) || contains(otherRange.minimum);
    }

    public boolean isStartedBy(T element) {
        return element != null && this.comparator.compare(element, this.minimum) == 0;
    }

    public T fit(T element) {
        Validate.notNull(element, "element", new Object[0]);
        if (isAfter(element)) {
            return this.minimum;
        }
        if (isBefore(element)) {
            return this.maximum;
        }
        return element;
    }

    public String toString() {
        if (this.toString == null) {
            this.toString = "[" + this.minimum + ".." + this.maximum + "]";
        }
        return this.toString;
    }

    public String toString(String format) {
        return String.format(format, this.minimum, this.maximum, this.comparator);
    }
}
