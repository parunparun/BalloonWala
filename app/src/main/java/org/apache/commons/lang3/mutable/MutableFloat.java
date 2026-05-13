package org.apache.commons.lang3.mutable;

/* JADX INFO: loaded from: classes.dex */
public class MutableFloat extends Number implements Comparable<MutableFloat>, Mutable<Number> {
    private static final long serialVersionUID = 5787169186L;
    private float value;

    public MutableFloat() {
    }

    public MutableFloat(float value) {
        this.value = value;
    }

    public MutableFloat(Number value) {
        this.value = value.floatValue();
    }

    public MutableFloat(String value) {
        this.value = Float.parseFloat(value);
    }

    @Override // org.apache.commons.lang3.mutable.Mutable
    /* JADX INFO: renamed from: getValue */
    public Number getValue2() {
        return Float.valueOf(this.value);
    }

    public void setValue(float value) {
        this.value = value;
    }

    @Override // org.apache.commons.lang3.mutable.Mutable
    public void setValue(Number value) {
        this.value = value.floatValue();
    }

    public boolean isNaN() {
        return Float.isNaN(this.value);
    }

    public boolean isInfinite() {
        return Float.isInfinite(this.value);
    }

    public void increment() {
        this.value += 1.0f;
    }

    public float getAndIncrement() {
        float last = this.value;
        this.value += 1.0f;
        return last;
    }

    public float incrementAndGet() {
        float f = this.value + 1.0f;
        this.value = f;
        return f;
    }

    public void decrement() {
        this.value -= 1.0f;
    }

    public float getAndDecrement() {
        float last = this.value;
        this.value -= 1.0f;
        return last;
    }

    public float decrementAndGet() {
        float f = this.value - 1.0f;
        this.value = f;
        return f;
    }

    public void add(float operand) {
        this.value += operand;
    }

    public void add(Number operand) {
        this.value += operand.floatValue();
    }

    public void subtract(float operand) {
        this.value -= operand;
    }

    public void subtract(Number operand) {
        this.value -= operand.floatValue();
    }

    public float addAndGet(float operand) {
        float f = this.value + operand;
        this.value = f;
        return f;
    }

    public float addAndGet(Number operand) {
        float fFloatValue = this.value + operand.floatValue();
        this.value = fFloatValue;
        return fFloatValue;
    }

    public float getAndAdd(float operand) {
        float last = this.value;
        this.value += operand;
        return last;
    }

    public float getAndAdd(Number operand) {
        float last = this.value;
        this.value += operand.floatValue();
        return last;
    }

    @Override // java.lang.Number
    public int intValue() {
        return (int) this.value;
    }

    @Override // java.lang.Number
    public long longValue() {
        return (long) this.value;
    }

    @Override // java.lang.Number
    public float floatValue() {
        return this.value;
    }

    @Override // java.lang.Number
    public double doubleValue() {
        return this.value;
    }

    public Float toFloat() {
        return Float.valueOf(floatValue());
    }

    public boolean equals(Object obj) {
        return (obj instanceof MutableFloat) && Float.floatToIntBits(((MutableFloat) obj).value) == Float.floatToIntBits(this.value);
    }

    public int hashCode() {
        return Float.floatToIntBits(this.value);
    }

    @Override // java.lang.Comparable
    public int compareTo(MutableFloat other) {
        return Float.compare(this.value, other.value);
    }

    public String toString() {
        return String.valueOf(this.value);
    }
}
