package org.apache.commons.collections4.keyvalue;

import java.io.Serializable;
import java.util.Arrays;

/* JADX INFO: loaded from: classes.dex */
public class MultiKey<K> implements Serializable {
    private static final long serialVersionUID = 4465448607415788805L;
    private transient int hashCode;
    private final K[] keys;

    public MultiKey(K key1, K key2) {
        this(new Object[]{key1, key2}, false);
    }

    public MultiKey(K key1, K key2, K key3) {
        this(new Object[]{key1, key2, key3}, false);
    }

    public MultiKey(K key1, K key2, K key3, K key4) {
        this(new Object[]{key1, key2, key3, key4}, false);
    }

    public MultiKey(K key1, K key2, K key3, K key4, K key5) {
        this(new Object[]{key1, key2, key3, key4, key5}, false);
    }

    public MultiKey(K[] keys) {
        this((Object[]) keys, true);
    }

    public MultiKey(K[] kArr, boolean z) {
        if (kArr == null) {
            throw new IllegalArgumentException("The array of keys must not be null");
        }
        if (z) {
            this.keys = (K[]) ((Object[]) kArr.clone());
        } else {
            this.keys = kArr;
        }
        calculateHashCode(kArr);
    }

    public K[] getKeys() {
        return (K[]) ((Object[]) this.keys.clone());
    }

    public K getKey(int index) {
        return this.keys[index];
    }

    public int size() {
        return this.keys.length;
    }

    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (other instanceof MultiKey) {
            MultiKey<?> otherMulti = (MultiKey) other;
            return Arrays.equals(this.keys, otherMulti.keys);
        }
        return false;
    }

    public int hashCode() {
        return this.hashCode;
    }

    public String toString() {
        return "MultiKey" + Arrays.toString(this.keys);
    }

    private void calculateHashCode(Object[] keys) {
        int total = 0;
        for (Object key : keys) {
            if (key != null) {
                total ^= key.hashCode();
            }
        }
        this.hashCode = total;
    }

    protected Object readResolve() {
        calculateHashCode(this.keys);
        return this;
    }
}
