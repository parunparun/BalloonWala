package org.apache.commons.lang3.mutable;

import org.apache.commons.lang3.math.NumberUtils;

/* JADX INFO: loaded from: classes.dex */
public class MutableShort extends Number implements Comparable<MutableShort>, Mutable<Number> {
    private static final long serialVersionUID = -2135791679;
    private short value;

    public MutableShort() {
    }

    public MutableShort(short value) {
        this.value = value;
    }

    public MutableShort(Number value) {
        this.value = value.shortValue();
    }

    public MutableShort(String value) {
        this.value = Short.parseShort(value);
    }

    @Override // org.apache.commons.lang3.mutable.Mutable
    /* JADX INFO: renamed from: getValue, reason: avoid collision after fix types in other method */
    public Number getValue2() {
        return Short.valueOf(this.value);
    }

    public void setValue(short value) {
        this.value = value;
    }

    @Override // org.apache.commons.lang3.mutable.Mutable
    public void setValue(Number value) {
        this.value = value.shortValue();
    }

    public void increment() {
        this.value = (short) (this.value + 1);
    }

    public short getAndIncrement() {
        short last = this.value;
        this.value = (short) (this.value + 1);
        return last;
    }

    public short incrementAndGet() {
        short s = (short) (this.value + 1);
        this.value = s;
        return s;
    }

    public void decrement() {
        this.value = (short) (this.value - 1);
    }

    public short getAndDecrement() {
        short last = this.value;
        this.value = (short) (this.value - 1);
        return last;
    }

    public short decrementAndGet() {
        short s = (short) (this.value - 1);
        this.value = s;
        return s;
    }

    public void add(short operand) {
        this.value = (short) (this.value + operand);
    }

    public void add(Number operand) {
        this.value = (short) (this.value + operand.shortValue());
    }

    public void subtract(short operand) {
        this.value = (short) (this.value - operand);
    }

    public void subtract(Number operand) {
        this.value = (short) (this.value - operand.shortValue());
    }

    public short addAndGet(short operand) {
        short s = (short) (this.value + operand);
        this.value = s;
        return s;
    }

    public short addAndGet(Number operand) {
        short sShortValue = (short) (this.value + operand.shortValue());
        this.value = sShortValue;
        return sShortValue;
    }

    public short getAndAdd(short operand) {
        short last = this.value;
        this.value = (short) (this.value + operand);
        return last;
    }

    public short getAndAdd(Number operand) {
        short last = this.value;
        this.value = (short) (this.value + operand.shortValue());
        return last;
    }

    @Override // java.lang.Number
    public short shortValue() {
        return this.value;
    }

    @Override // java.lang.Number
    public int intValue() {
        return this.value;
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

    public Short toShort() {
        return Short.valueOf(shortValue());
    }

    public boolean equals(Object obj) {
        return (obj instanceof MutableShort) && this.value == ((MutableShort) obj).shortValue();
    }

    public int hashCode() {
        return this.value;
    }

    @Override // java.lang.Comparable
    public int compareTo(MutableShort other) {
        return NumberUtils.compare(this.value, other.value);
    }

    public String toString() {
        return String.valueOf((int) this.value);
    }
}
