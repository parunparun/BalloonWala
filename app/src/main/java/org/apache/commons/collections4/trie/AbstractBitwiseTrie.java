package org.apache.commons.collections4.trie;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Map;
import org.apache.commons.collections4.Trie;
import org.apache.commons.lang3.StringUtils;

/* JADX INFO: loaded from: classes.dex */
public abstract class AbstractBitwiseTrie<K, V> extends AbstractMap<K, V> implements Trie<K, V>, Serializable {
    private static final long serialVersionUID = 5826987063535505652L;
    private final KeyAnalyzer<? super K> keyAnalyzer;

    protected AbstractBitwiseTrie(KeyAnalyzer<? super K> keyAnalyzer) {
        if (keyAnalyzer == null) {
            throw new NullPointerException("keyAnalyzer");
        }
        this.keyAnalyzer = keyAnalyzer;
    }

    protected KeyAnalyzer<? super K> getKeyAnalyzer() {
        return this.keyAnalyzer;
    }

    @Override // java.util.AbstractMap
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("Trie[");
        buffer.append(size());
        buffer.append("]={\n");
        for (Map.Entry<K, V> entry : entrySet()) {
            buffer.append("  ");
            buffer.append(entry);
            buffer.append(StringUtils.LF);
        }
        buffer.append("}\n");
        return buffer.toString();
    }

    /* JADX WARN: Multi-variable type inference failed */
    final K castKey(Object obj) {
        return obj;
    }

    final int lengthInBits(K key) {
        if (key == null) {
            return 0;
        }
        return this.keyAnalyzer.lengthInBits(key);
    }

    final int bitsPerElement() {
        return this.keyAnalyzer.bitsPerElement();
    }

    final boolean isBitSet(K key, int bitIndex, int lengthInBits) {
        if (key == null) {
            return false;
        }
        return this.keyAnalyzer.isBitSet(key, bitIndex, lengthInBits);
    }

    final int bitIndex(K key, K foundKey) {
        return this.keyAnalyzer.bitIndex(key, 0, lengthInBits(key), foundKey, 0, lengthInBits(foundKey));
    }

    final boolean compareKeys(K key, K other) {
        return key == null ? other == null : other != null && this.keyAnalyzer.compare(key, other) == 0;
    }

    static boolean compare(Object a, Object b) {
        return a == null ? b == null : a.equals(b);
    }

    static abstract class BasicEntry<K, V> implements Map.Entry<K, V>, Serializable {
        private static final long serialVersionUID = -944364551314110330L;
        protected K key;
        protected V value;

        public BasicEntry(K key) {
            this.key = key;
        }

        public BasicEntry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public V setKeyValue(K key, V value) {
            this.key = key;
            return setValue(value);
        }

        @Override // java.util.Map.Entry
        public K getKey() {
            return this.key;
        }

        @Override // java.util.Map.Entry
        public V getValue() {
            return this.value;
        }

        @Override // java.util.Map.Entry
        public V setValue(V value) {
            V previous = this.value;
            this.value = value;
            return previous;
        }

        @Override // java.util.Map.Entry
        public int hashCode() {
            return (getKey() == null ? 0 : getKey().hashCode()) ^ (getValue() != null ? getValue().hashCode() : 0);
        }

        @Override // java.util.Map.Entry
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry<?, ?> other = (Map.Entry) o;
            return AbstractBitwiseTrie.compare(this.key, other.getKey()) && AbstractBitwiseTrie.compare(this.value, other.getValue());
        }

        public String toString() {
            return this.key + "=" + this.value;
        }
    }
}
