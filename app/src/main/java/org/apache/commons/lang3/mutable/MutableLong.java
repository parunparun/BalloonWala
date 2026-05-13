package org.apache.commons.lang3.mutable;

import org.apache.commons.lang3.math.NumberUtils;

/* JADX INFO: loaded from: classes.dex */
public class MutableLong extends Number implements Comparable<MutableLong>, Mutable<Number> {
    private static final long serialVersionUID = 62986528375L;
    private long value;

    public MutableLong() {
    }

    public MutableLong(long value) {
        this.value = value;
    }

    public MutableLong(Number value) {
        this.value = value.longValue();
    }

    public MutableLong(String value) {
        this.value = Long.parseLong(value);
    }

    @Override // org.apache.commons.lang3.mutable.Mutable
    /* JADX INFO: renamed from: getValue */
    public Number getValue2() {
        return Long.valueOf(this.value);
    }

    public void setValue(long value) {
        this.value = value;
    }

    @Override // org.apache.commons.lang3.mutable.Mutable
    public void setValue(Number value) {
        this.value = value.longValue();
    }

    public void increment() {
        this.value++;
    }

    public long getAndIncrement() {
        long last = this.value;
        this.value++;
        return last;
    }

    public long incrementAndGet() {
        long j = this.value + 1;
        this.value = j;
        return j;
    }

    public void decrement() {
        this.value--;
    }

    public long getAndDecrement() {
        long last = this.value;
        this.value--;
        return last;
    }

    public long decrementAndGet() {
        long j = this.value - 1;
        this.value = j;
        return j;
    }

    public void add(long operand) {
        this.value += operand;
    }

    public void add(Number operand) {
        this.value += operand.longValue();
    }

    public void subtract(long operand) {
        this.value -= operand;
    }

    public void subtract(Number operand) {
        this.value -= operand.longValue();
    }

    public long addAndGet(long operand) {
        long j = this.value + operand;
        this.value = j;
        return j;
    }

    public long addAndGet(Number operand) {
        long jLongValue = this.value + operand.longValue();
        this.value = jLongValue;
        return jLongValue;
    }

    public long getAndAdd(long operand) {
        long last = this.value;
        this.value += operand;
        return last;
    }

    public long getAndAdd(Number operand) {
        long last = this.value;
        this.value += operand.longValue();
        return last;
    }

    @Override // java.lang.Number
    public int intValue() {
        return (int) this.value;
    }

    @Override // java.lang.Number
    public long longValue() {
        return this.value;
    }

    @Override // java.lang.Number
    public float floatValue() {
        return this.value;
    }

    @Override // java.lang.Number
    public double doubleValue() {
        return this.value;
    }

    public Long toLong() {
        return Long.valueOf(longValue());
    }

    public boolean equals(Object obj) {
        return (obj instanceof MutableLong) && this.value == ((MutableLong) obj).longValue();
    }

    public int hashCode() {
        long j = this.value;
        return (int) (j ^ (j >>> 32));
    }

    @Override // java.lang.Comparable
    public int compareTo(MutableLong other) {
        return NumberUtils.compare(this.value, other.value);
    }

    public String toString() {
        return String.valueOf(this.value);
    }
}
