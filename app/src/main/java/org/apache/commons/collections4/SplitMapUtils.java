package org.apache.commons.collections4;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import org.apache.commons.collections4.collection.UnmodifiableCollection;
import org.apache.commons.collections4.iterators.UnmodifiableMapIterator;
import org.apache.commons.collections4.map.EntrySetToMapIteratorAdapter;
import org.apache.commons.collections4.map.UnmodifiableEntrySet;
import org.apache.commons.collections4.set.UnmodifiableSet;

/* JADX INFO: loaded from: classes.dex */
public class SplitMapUtils {
    private SplitMapUtils() {
    }

    private static class WrappedGet<K, V> implements IterableMap<K, V>, Unmodifiable {
        private final Get<K, V> get;

        private WrappedGet(Get<K, V> get) {
            this.get = get;
        }

        @Override // java.util.Map, org.apache.commons.collections4.Put
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Override // java.util.Map, org.apache.commons.collections4.Get
        public boolean containsKey(Object key) {
            return this.get.containsKey(key);
        }

        @Override // java.util.Map, org.apache.commons.collections4.Get
        public boolean containsValue(Object value) {
            return this.get.containsValue(value);
        }

        @Override // java.util.Map, org.apache.commons.collections4.Get
        public Set<Map.Entry<K, V>> entrySet() {
            return UnmodifiableEntrySet.unmodifiableEntrySet(this.get.entrySet());
        }

        @Override // java.util.Map
        public boolean equals(Object arg0) {
            if (arg0 == this) {
                return true;
            }
            return (arg0 instanceof WrappedGet) && ((WrappedGet) arg0).get.equals(this.get);
        }

        @Override // java.util.Map, org.apache.commons.collections4.Get
        public V get(Object key) {
            return this.get.get(key);
        }

        @Override // java.util.Map
        public int hashCode() {
            return ("WrappedGet".hashCode() << 4) | this.get.hashCode();
        }

        @Override // java.util.Map, org.apache.commons.collections4.Get
        public boolean isEmpty() {
            return this.get.isEmpty();
        }

        @Override // java.util.Map, org.apache.commons.collections4.Get
        public Set<K> keySet() {
            return UnmodifiableSet.unmodifiableSet(this.get.keySet());
        }

        @Override // java.util.Map, org.apache.commons.collections4.Put
        public V put(K key, V value) {
            throw new UnsupportedOperationException();
        }

        @Override // java.util.Map, org.apache.commons.collections4.Put
        public void putAll(Map<? extends K, ? extends V> t) {
            throw new UnsupportedOperationException();
        }

        @Override // java.util.Map, org.apache.commons.collections4.Get
        public V remove(Object key) {
            return this.get.remove(key);
        }

        @Override // java.util.Map, org.apache.commons.collections4.Get
        public int size() {
            return this.get.size();
        }

        @Override // java.util.Map, org.apache.commons.collections4.Get
        public Collection<V> values() {
            return UnmodifiableCollection.unmodifiableCollection(this.get.values());
        }

        @Override // org.apache.commons.collections4.IterableGet
        public MapIterator<K, V> mapIterator() {
            MapIterator<K, V> it;
            Get<K, V> get = this.get;
            if (get instanceof IterableGet) {
                it = ((IterableGet) get).mapIterator();
            } else {
                it = new EntrySetToMapIteratorAdapter<>(this.get.entrySet());
            }
            return UnmodifiableMapIterator.unmodifiableMapIterator(it);
        }
    }

    private static class WrappedPut<K, V> implements Map<K, V>, Put<K, V> {
        private final Put<K, V> put;

        private WrappedPut(Put<K, V> put) {
            this.put = put;
        }

        @Override // java.util.Map, org.apache.commons.collections4.Put
        public void clear() {
            this.put.clear();
        }

        @Override // java.util.Map
        public boolean containsKey(Object key) {
            throw new UnsupportedOperationException();
        }

        @Override // java.util.Map
        public boolean containsValue(Object value) {
            throw new UnsupportedOperationException();
        }

        @Override // java.util.Map
        public Set<Map.Entry<K, V>> entrySet() {
            throw new UnsupportedOperationException();
        }

        @Override // java.util.Map
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            return (obj instanceof WrappedPut) && ((WrappedPut) obj).put.equals(this.put);
        }

        @Override // java.util.Map
        public V get(Object key) {
            throw new UnsupportedOperationException();
        }

        @Override // java.util.Map
        public int hashCode() {
            return ("WrappedPut".hashCode() << 4) | this.put.hashCode();
        }

        @Override // java.util.Map
        public boolean isEmpty() {
            throw new UnsupportedOperationException();
        }

        @Override // java.util.Map
        public Set<K> keySet() {
            throw new UnsupportedOperationException();
        }

        @Override // java.util.Map, org.apache.commons.collections4.Put
        public V put(K k, V v) {
            return (V) this.put.put(k, v);
        }

        @Override // java.util.Map, org.apache.commons.collections4.Put
        public void putAll(Map<? extends K, ? extends V> t) {
            this.put.putAll(t);
        }

        @Override // java.util.Map
        public V remove(Object key) {
            throw new UnsupportedOperationException();
        }

        @Override // java.util.Map
        public int size() {
            throw new UnsupportedOperationException();
        }

        @Override // java.util.Map
        public Collection<V> values() {
            throw new UnsupportedOperationException();
        }
    }

    public static <K, V> IterableMap<K, V> readableMap(Get<K, V> get) {
        if (get == null) {
            throw new NullPointerException("Get must not be null");
        }
        if (get instanceof Map) {
            return get instanceof IterableMap ? (IterableMap) get : MapUtils.iterableMap((Map) get);
        }
        return new WrappedGet(get);
    }

    public static <K, V> Map<K, V> writableMap(Put<K, V> put) {
        if (put == null) {
            throw new NullPointerException("Put must not be null");
        }
        if (put instanceof Map) {
            return (Map) put;
        }
        return new WrappedPut(put);
    }
}
