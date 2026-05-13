package org.apache.commons.collections4.trie;

import java.io.Serializable;
import java.util.Comparator;

/* JADX INFO: loaded from: classes.dex */
public abstract class KeyAnalyzer<K> implements Comparator<K>, Serializable {
    public static final int EQUAL_BIT_KEY = -2;
    public static final int NULL_BIT_KEY = -1;
    public static final int OUT_OF_BOUNDS_BIT_KEY = -3;
    private static final long serialVersionUID = -20497563720380683L;

    public abstract int bitIndex(K k, int i, int i2, K k2, int i3, int i4);

    public abstract int bitsPerElement();

    public abstract boolean isBitSet(K k, int i, int i2);

    public abstract boolean isPrefix(K k, int i, int i2, K k2);

    public abstract int lengthInBits(K k);

    static boolean isOutOfBoundsIndex(int bitIndex) {
        return bitIndex == -3;
    }

    static boolean isEqualBitKey(int bitIndex) {
        return bitIndex == -2;
    }

    static boolean isNullBitKey(int bitIndex) {
        return bitIndex == -1;
    }

    static boolean isValidBitIndex(int bitIndex) {
        return bitIndex >= 0;
    }

    @Override // java.util.Comparator
    public int compare(K o1, K o2) {
        if (o1 == null) {
            return o2 == null ? 0 : -1;
        }
        if (o2 == null) {
            return 1;
        }
        return ((Comparable) o1).compareTo(o2);
    }
}
