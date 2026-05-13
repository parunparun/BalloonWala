package org.apache.commons.collections4.keyvalue;

import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public abstract class AbstractMapEntry<K, V> extends AbstractKeyValue<K, V> implements Map.Entry<K, V> {
    protected AbstractMapEntry(K key, V value) {
        super(key, value);
    }

    @Override // org.apache.commons.collections4.keyvalue.AbstractKeyValue, java.util.Map.Entry
    public V setValue(V v) {
        return (V) super.setValue(v);
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
        if (getKey() != null ? getKey().equals(other.getKey()) : other.getKey() == null) {
            if (getValue() == null) {
                if (other.getValue() == null) {
                    return true;
                }
            } else if (getValue().equals(other.getValue())) {
                return true;
            }
        }
        return false;
    }

    @Override // java.util.Map.Entry
    public int hashCode() {
        return (getKey() == null ? 0 : getKey().hashCode()) ^ (getValue() != null ? getValue().hashCode() : 0);
    }
}
