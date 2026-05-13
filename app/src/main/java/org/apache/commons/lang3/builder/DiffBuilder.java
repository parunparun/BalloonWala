package org.apache.commons.lang3.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.Validate;

/* JADX INFO: loaded from: classes.dex */
public class DiffBuilder<T> implements Builder<DiffResult<T>> {
    private final List<Diff<?>> diffs;
    private final T left;
    private final boolean objectsTriviallyEqual;
    private final T right;
    private final ToStringStyle style;

    public DiffBuilder(T lhs, T rhs, ToStringStyle style, boolean testTriviallyEqual) {
        boolean z = false;
        Validate.notNull(lhs, "lhs cannot be null", new Object[0]);
        Validate.notNull(rhs, "rhs cannot be null", new Object[0]);
        this.diffs = new ArrayList();
        this.left = lhs;
        this.right = rhs;
        this.style = style;
        if (testTriviallyEqual && (lhs == rhs || lhs.equals(rhs))) {
            z = true;
        }
        this.objectsTriviallyEqual = z;
    }

    public DiffBuilder(T lhs, T rhs, ToStringStyle style) {
        this(lhs, rhs, style, true);
    }

    public DiffBuilder<T> append(String fieldName, final boolean lhs, final boolean rhs) {
        validateFieldNameNotNull(fieldName);
        if (!this.objectsTriviallyEqual && lhs != rhs) {
            this.diffs.add(new Diff<Boolean>(fieldName) { // from class: org.apache.commons.lang3.builder.DiffBuilder.1
                private static final long serialVersionUID = 1;

                @Override // org.apache.commons.lang3.tuple.Pair
                public Boolean getLeft() {
                    return Boolean.valueOf(lhs);
                }

                @Override // org.apache.commons.lang3.tuple.Pair
                public Boolean getRight() {
                    return Boolean.valueOf(rhs);
                }
            });
        }
        return this;
    }

    public DiffBuilder<T> append(String str, final boolean[] zArr, final boolean[] zArr2) {
        validateFieldNameNotNull(str);
        if (!this.objectsTriviallyEqual && !Arrays.equals(zArr, zArr2)) {
            this.diffs.add((Diff<?>) new Diff<Boolean[]>(str) { // from class: org.apache.commons.lang3.builder.DiffBuilder.2
                private static final long serialVersionUID = 1;

                @Override // org.apache.commons.lang3.tuple.Pair
                public Boolean[] getLeft() {
                    return ArrayUtils.toObject(zArr);
                }

                @Override // org.apache.commons.lang3.tuple.Pair
                public Boolean[] getRight() {
                    return ArrayUtils.toObject(zArr2);
                }
            });
        }
        return this;
    }

    public DiffBuilder<T> append(String fieldName, final byte lhs, final byte rhs) {
        validateFieldNameNotNull(fieldName);
        if (!this.objectsTriviallyEqual && lhs != rhs) {
            this.diffs.add(new Diff<Byte>(fieldName) { // from class: org.apache.commons.lang3.builder.DiffBuilder.3
                private static final long serialVersionUID = 1;

                @Override // org.apache.commons.lang3.tuple.Pair
                public Byte getLeft() {
                    return Byte.valueOf(lhs);
                }

                @Override // org.apache.commons.lang3.tuple.Pair
                public Byte getRight() {
                    return Byte.valueOf(rhs);
                }
            });
        }
        return this;
    }

    public DiffBuilder<T> append(String str, final byte[] bArr, final byte[] bArr2) {
        validateFieldNameNotNull(str);
        if (!this.objectsTriviallyEqual && !Arrays.equals(bArr, bArr2)) {
            this.diffs.add((Diff<?>) new Diff<Byte[]>(str) { // from class: org.apache.commons.lang3.builder.DiffBuilder.4
                private static final long serialVersionUID = 1;

                @Override // org.apache.commons.lang3.tuple.Pair
                public Byte[] getLeft() {
                    return ArrayUtils.toObject(bArr);
                }

                @Override // org.apache.commons.lang3.tuple.Pair
                public Byte[] getRight() {
                    return ArrayUtils.toObject(bArr2);
                }
            });
        }
        return this;
    }

    public DiffBuilder<T> append(String fieldName, final char lhs, final char rhs) {
        validateFieldNameNotNull(fieldName);
        if (!this.objectsTriviallyEqual && lhs != rhs) {
            this.diffs.add(new Diff<Character>(fieldName) { // from class: org.apache.commons.lang3.builder.DiffBuilder.5
                private static final long serialVersionUID = 1;

                @Override // org.apache.commons.lang3.tuple.Pair
                public Character getLeft() {
                    return Character.valueOf(lhs);
                }

                @Override // org.apache.commons.lang3.tuple.Pair
                public Character getRight() {
                    return Character.valueOf(rhs);
                }
            });
        }
        return this;
    }

    public DiffBuilder<T> append(String str, final char[] cArr, final char[] cArr2) {
        validateFieldNameNotNull(str);
        if (!this.objectsTriviallyEqual && !Arrays.equals(cArr, cArr2)) {
            this.diffs.add((Diff<?>) new Diff<Character[]>(str) { // from class: org.apache.commons.lang3.builder.DiffBuilder.6
                private static final long serialVersionUID = 1;

                @Override // org.apache.commons.lang3.tuple.Pair
                public Character[] getLeft() {
                    return ArrayUtils.toObject(cArr);
                }

                @Override // org.apache.commons.lang3.tuple.Pair
                public Character[] getRight() {
                    return ArrayUtils.toObject(cArr2);
                }
            });
        }
        return this;
    }

    public DiffBuilder<T> append(String fieldName, final double lhs, final double rhs) {
        validateFieldNameNotNull(fieldName);
        if (!this.objectsTriviallyEqual && Double.doubleToLongBits(lhs) != Double.doubleToLongBits(rhs)) {
            this.diffs.add(new Diff<Double>(fieldName) { // from class: org.apache.commons.lang3.builder.DiffBuilder.7
                private static final long serialVersionUID = 1;

                @Override // org.apache.commons.lang3.tuple.Pair
                public Double getLeft() {
                    return Double.valueOf(lhs);
                }

                @Override // org.apache.commons.lang3.tuple.Pair
                public Double getRight() {
                    return Double.valueOf(rhs);
                }
            });
        }
        return this;
    }

    public DiffBuilder<T> append(String str, final double[] dArr, final double[] dArr2) {
        validateFieldNameNotNull(str);
        if (!this.objectsTriviallyEqual && !Arrays.equals(dArr, dArr2)) {
            this.diffs.add((Diff<?>) new Diff<Double[]>(str) { // from class: org.apache.commons.lang3.builder.DiffBuilder.8
                private static final long serialVersionUID = 1;

                @Override // org.apache.commons.lang3.tuple.Pair
                public Double[] getLeft() {
                    return ArrayUtils.toObject(dArr);
                }

                @Override // org.apache.commons.lang3.tuple.Pair
                public Double[] getRight() {
                    return ArrayUtils.toObject(dArr2);
                }
            });
        }
        return this;
    }

    public DiffBuilder<T> append(String fieldName, final float lhs, final float rhs) {
        validateFieldNameNotNull(fieldName);
        if (!this.objectsTriviallyEqual && Float.floatToIntBits(lhs) != Float.floatToIntBits(rhs)) {
            this.diffs.add(new Diff<Float>(fieldName) { // from class: org.apache.commons.lang3.builder.DiffBuilder.9
                private static final long serialVersionUID = 1;

                @Override // org.apache.commons.lang3.tuple.Pair
                public Float getLeft() {
                    return Float.valueOf(lhs);
                }

                @Override // org.apache.commons.lang3.tuple.Pair
                public Float getRight() {
                    return Float.valueOf(rhs);
                }
            });
        }
        return this;
    }

    public DiffBuilder<T> append(String str, final float[] fArr, final float[] fArr2) {
        validateFieldNameNotNull(str);
        if (!this.objectsTriviallyEqual && !Arrays.equals(fArr, fArr2)) {
            this.diffs.add((Diff<?>) new Diff<Float[]>(str) { // from class: org.apache.commons.lang3.builder.DiffBuilder.10
                private static final long serialVersionUID = 1;

                @Override // org.apache.commons.lang3.tuple.Pair
                public Float[] getLeft() {
                    return ArrayUtils.toObject(fArr);
                }

                @Override // org.apache.commons.lang3.tuple.Pair
                public Float[] getRight() {
                    return ArrayUtils.toObject(fArr2);
                }
            });
        }
        return this;
    }

    public DiffBuilder<T> append(String fieldName, final int lhs, final int rhs) {
        validateFieldNameNotNull(fieldName);
        if (!this.objectsTriviallyEqual && lhs != rhs) {
            this.diffs.add(new Diff<Integer>(fieldName) { // from class: org.apache.commons.lang3.builder.DiffBuilder.11
                private static final long serialVersionUID = 1;

                @Override // org.apache.commons.lang3.tuple.Pair
                public Integer getLeft() {
                    return Integer.valueOf(lhs);
                }

                @Override // org.apache.commons.lang3.tuple.Pair
                public Integer getRight() {
                    return Integer.valueOf(rhs);
                }
            });
        }
        return this;
    }

    public DiffBuilder<T> append(String str, final int[] iArr, final int[] iArr2) {
        validateFieldNameNotNull(str);
        if (!this.objectsTriviallyEqual && !Arrays.equals(iArr, iArr2)) {
            this.diffs.add((Diff<?>) new Diff<Integer[]>(str) { // from class: org.apache.commons.lang3.builder.DiffBuilder.12
                private static final long serialVersionUID = 1;

                @Override // org.apache.commons.lang3.tuple.Pair
                public Integer[] getLeft() {
                    return ArrayUtils.toObject(iArr);
                }

                @Override // org.apache.commons.lang3.tuple.Pair
                public Integer[] getRight() {
                    return ArrayUtils.toObject(iArr2);
                }
            });
        }
        return this;
    }

    public DiffBuilder<T> append(String fieldName, final long lhs, final long rhs) {
        validateFieldNameNotNull(fieldName);
        if (!this.objectsTriviallyEqual && lhs != rhs) {
            this.diffs.add(new Diff<Long>(fieldName) { // from class: org.apache.commons.lang3.builder.DiffBuilder.13
                private static final long serialVersionUID = 1;

                @Override // org.apache.commons.lang3.tuple.Pair
                public Long getLeft() {
                    return Long.valueOf(lhs);
                }

                @Override // org.apache.commons.lang3.tuple.Pair
                public Long getRight() {
                    return Long.valueOf(rhs);
                }
            });
        }
        return this;
    }

    public DiffBuilder<T> append(String str, final long[] jArr, final long[] jArr2) {
        validateFieldNameNotNull(str);
        if (!this.objectsTriviallyEqual && !Arrays.equals(jArr, jArr2)) {
            this.diffs.add((Diff<?>) new Diff<Long[]>(str) { // from class: org.apache.commons.lang3.builder.DiffBuilder.14
                private static final long serialVersionUID = 1;

                @Override // org.apache.commons.lang3.tuple.Pair
                public Long[] getLeft() {
                    return ArrayUtils.toObject(jArr);
                }

                @Override // org.apache.commons.lang3.tuple.Pair
                public Long[] getRight() {
                    return ArrayUtils.toObject(jArr2);
                }
            });
        }
        return this;
    }

    public DiffBuilder<T> append(String fieldName, final short lhs, final short rhs) {
        validateFieldNameNotNull(fieldName);
        if (!this.objectsTriviallyEqual && lhs != rhs) {
            this.diffs.add(new Diff<Short>(fieldName) { // from class: org.apache.commons.lang3.builder.DiffBuilder.15
                private static final long serialVersionUID = 1;

                @Override // org.apache.commons.lang3.tuple.Pair
                public Short getLeft() {
                    return Short.valueOf(lhs);
                }

                @Override // org.apache.commons.lang3.tuple.Pair
                public Short getRight() {
                    return Short.valueOf(rhs);
                }
            });
        }
        return this;
    }

    public DiffBuilder<T> append(String str, final short[] sArr, final short[] sArr2) {
        validateFieldNameNotNull(str);
        if (!this.objectsTriviallyEqual && !Arrays.equals(sArr, sArr2)) {
            this.diffs.add((Diff<?>) new Diff<Short[]>(str) { // from class: org.apache.commons.lang3.builder.DiffBuilder.16
                private static final long serialVersionUID = 1;

                @Override // org.apache.commons.lang3.tuple.Pair
                public Short[] getLeft() {
                    return ArrayUtils.toObject(sArr);
                }

                @Override // org.apache.commons.lang3.tuple.Pair
                public Short[] getRight() {
                    return ArrayUtils.toObject(sArr2);
                }
            });
        }
        return this;
    }

    public DiffBuilder<T> append(String fieldName, final Object lhs, final Object rhs) {
        Object objectToTest;
        validateFieldNameNotNull(fieldName);
        if (this.objectsTriviallyEqual || lhs == rhs) {
            return this;
        }
        if (lhs != null) {
            objectToTest = lhs;
        } else {
            objectToTest = rhs;
        }
        if (objectToTest.getClass().isArray()) {
            if (objectToTest instanceof boolean[]) {
                return append(fieldName, (boolean[]) lhs, (boolean[]) rhs);
            }
            if (objectToTest instanceof byte[]) {
                return append(fieldName, (byte[]) lhs, (byte[]) rhs);
            }
            if (objectToTest instanceof char[]) {
                return append(fieldName, (char[]) lhs, (char[]) rhs);
            }
            if (objectToTest instanceof double[]) {
                return append(fieldName, (double[]) lhs, (double[]) rhs);
            }
            if (objectToTest instanceof float[]) {
                return append(fieldName, (float[]) lhs, (float[]) rhs);
            }
            if (objectToTest instanceof int[]) {
                return append(fieldName, (int[]) lhs, (int[]) rhs);
            }
            if (objectToTest instanceof long[]) {
                return append(fieldName, (long[]) lhs, (long[]) rhs);
            }
            if (objectToTest instanceof short[]) {
                return append(fieldName, (short[]) lhs, (short[]) rhs);
            }
            return append(fieldName, (Object[]) lhs, (Object[]) rhs);
        }
        if (lhs != null && lhs.equals(rhs)) {
            return this;
        }
        this.diffs.add(new Diff<Object>(fieldName) { // from class: org.apache.commons.lang3.builder.DiffBuilder.17
            private static final long serialVersionUID = 1;

            @Override // org.apache.commons.lang3.tuple.Pair
            public Object getLeft() {
                return lhs;
            }

            @Override // org.apache.commons.lang3.tuple.Pair
            public Object getRight() {
                return rhs;
            }
        });
        return this;
    }

    public DiffBuilder<T> append(String str, final Object[] objArr, final Object[] objArr2) {
        validateFieldNameNotNull(str);
        if (!this.objectsTriviallyEqual && !Arrays.equals(objArr, objArr2)) {
            this.diffs.add((Diff<?>) new Diff<Object[]>(str) { // from class: org.apache.commons.lang3.builder.DiffBuilder.18
                private static final long serialVersionUID = 1;

                @Override // org.apache.commons.lang3.tuple.Pair
                public Object[] getLeft() {
                    return objArr;
                }

                @Override // org.apache.commons.lang3.tuple.Pair
                public Object[] getRight() {
                    return objArr2;
                }
            });
        }
        return this;
    }

    public DiffBuilder<T> append(String fieldName, DiffResult<T> diffResult) {
        validateFieldNameNotNull(fieldName);
        Validate.notNull(diffResult, "Diff result cannot be null", new Object[0]);
        if (this.objectsTriviallyEqual) {
            return this;
        }
        for (Diff<?> diff : diffResult.getDiffs()) {
            append(fieldName + "." + diff.getFieldName(), diff.getLeft(), diff.getRight());
        }
        return this;
    }

    @Override // org.apache.commons.lang3.builder.Builder
    public DiffResult<T> build() {
        return new DiffResult<>(this.left, this.right, this.diffs, this.style);
    }

    private void validateFieldNameNotNull(String fieldName) {
        Validate.notNull(fieldName, "Field name cannot be null", new Object[0]);
    }
}
