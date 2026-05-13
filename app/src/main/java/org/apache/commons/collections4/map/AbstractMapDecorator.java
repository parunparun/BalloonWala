package org.apache.commons.collections4.map;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/* JADX INFO: loaded from: classes.dex */
public abstract class AbstractMapDecorator<K, V> extends AbstractIterableMap<K, V> {
    transient Map<K, V> map;

    protected AbstractMapDecorator() {
    }

    protected AbstractMapDecorator(Map<K, V> map) {
        if (map == null) {
            throw new NullPointerException("Map must not be null.");
        }
        this.map = map;
    }

    protected Map<K, V> decorated() {
        return this.map;
    }

    @Override // java.util.Map, org.apache.commons.collections4.Put
    public void clear() {
        decorated().clear();
    }

    @Override // java.util.Map, org.apache.commons.collections4.Get
    public boolean containsKey(Object key) {
        return decorated().containsKey(key);
    }

    @Override // java.util.Map, org.apache.commons.collections4.Get
    public boolean containsValue(Object value) {
        return decorated().containsValue(value);
    }

    @Override // java.util.Map, org.apache.commons.collections4.Get
    public Set<Map.Entry<K, V>> entrySet() {
        return decorated().entrySet();
    }

    @Override // java.util.Map, org.apache.commons.collections4.Get
    public V get(Object key) {
        return decorated().get(key);
    }

    @Override // java.util.Map, org.apache.commons.collections4.Get
    public boolean isEmpty() {
        return decorated().isEmpty();
    }

    @Override // java.util.Map, org.apache.commons.collections4.Get
    public Set<K> keySet() {
        return decorated().keySet();
    }

    @Override // java.util.Map, org.apache.commons.collections4.Put
    public V put(K key, V value) {
        return decorated().put(key, value);
    }

    @Override // java.util.Map, org.apache.commons.collections4.Put
    public void putAll(Map<? extends K, ? extends V> mapToCopy) {
        decorated().putAll(mapToCopy);
    }

    @Override // java.util.Map, org.apache.commons.collections4.Get
    public V remove(Object key) {
        return decorated().remove(key);
    }

    @Override // java.util.Map, org.apache.commons.collections4.Get
    public int size() {
        return decorated().size();
    }

    @Override // java.util.Map, org.apache.commons.collections4.Get
    public Collection<V> values() {
        return decorated().values();
    }

    @Override // java.util.Map
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        return decorated().equals(object);
    }

    @Override // java.util.Map
    public int hashCode() {
        return decorated().hashCode();
    }

    public String toString() {
        return decorated().toString();
    }
}
