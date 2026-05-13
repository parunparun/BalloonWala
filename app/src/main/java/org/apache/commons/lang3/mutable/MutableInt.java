package org.apache.commons.lang3.mutable;

import org.apache.commons.lang3.math.NumberUtils;

/* JADX INFO: loaded from: classes.dex */
public class MutableInt extends Number implements Comparable<MutableInt>, Mutable<Number> {
    private static final long serialVersionUID = 512176391864L;
    private int value;

    public MutableInt() {
    }

    public MutableInt(int value) {
        this.value = value;
    }

    public MutableInt(Number value) {
        this.value = value.intValue();
    }

    public MutableInt(String value) {
        this.value = Integer.parseInt(value);
    }

    @Override // org.apache.commons.lang3.mutable.Mutable
    /* JADX INFO: renamed from: getValue */
    public Number getValue2() {
        return Integer.valueOf(this.value);
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override // org.apache.commons.lang3.mutable.Mutable
    public void setValue(Number value) {
        this.value = value.intValue();
    }

    public void increment() {
        this.value++;
    }

    public int getAndIncrement() {
        int last = this.value;
        this.value++;
        return last;
    }

    public int incrementAndGet() {
        int i = this.value + 1;
        this.value = i;
        return i;
    }

    public void decrement() {
        this.value--;
    }

    public int getAndDecrement() {
        int last = this.value;
        this.value--;
        return last;
    }

    public int decrementAndGet() {
        int i = this.value - 1;
        this.value = i;
        return i;
    }

    public void add(int operand) {
        this.value += operand;
    }

    public void add(Number operand) {
        this.value += operand.intValue();
    }

    public void subtract(int operand) {
        this.value -= operand;
    }

    public void subtract(Number operand) {
        this.value -= operand.intValue();
    }

    public int addAndGet(int operand) {
        int i = this.value + operand;
        this.value = i;
        return i;
    }

    public int addAndGet(Number operand) {
        int iIntValue = this.value + operand.intValue();
        this.value = iIntValue;
        return iIntValue;
    }

    public int getAndAdd(int operand) {
        int last = this.value;
        this.value += operand;
        return last;
    }

    public int getAndAdd(Number operand) {
        int last = this.value;
        this.value += operand.intValue();
        return last;
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

    public Integer toInteger() {
        return Integer.valueOf(intValue());
    }

    public boolean equals(Object obj) {
        return (obj instanceof MutableInt) && this.value == ((MutableInt) obj).intValue();
    }

    public int hashCode() {
        return this.value;
    }

    @Override // java.lang.Comparable
    public int compareTo(MutableInt other) {
        return NumberUtils.compare(this.value, other.value);
    }

    public String toString() {
        return String.valueOf(this.value);
    }
}
