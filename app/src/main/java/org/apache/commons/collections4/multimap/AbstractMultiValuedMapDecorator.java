package org.apache.commons.collections4.multimap;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.MultiSet;
import org.apache.commons.collections4.MultiValuedMap;

/* JADX INFO: loaded from: classes.dex */
public abstract class AbstractMultiValuedMapDecorator<K, V> implements MultiValuedMap<K, V>, Serializable {
    private static final long serialVersionUID = 20150612;
    private final MultiValuedMap<K, V> map;

    protected AbstractMultiValuedMapDecorator(MultiValuedMap<K, V> map) {
        if (map == null) {
            throw new NullPointerException("MultiValuedMap must not be null.");
        }
        this.map = map;
    }

    protected MultiValuedMap<K, V> decorated() {
        return this.map;
    }

    @Override // org.apache.commons.collections4.MultiValuedMap
    public int size() {
        return decorated().size();
    }

    @Override // org.apache.commons.collections4.MultiValuedMap
    public boolean isEmpty() {
        return decorated().isEmpty();
    }

    @Override // org.apache.commons.collections4.MultiValuedMap
    public boolean containsKey(Object key) {
        return decorated().containsKey(key);
    }

    @Override // org.apache.commons.collections4.MultiValuedMap
    public boolean containsValue(Object value) {
        return decorated().containsValue(value);
    }

    @Override // org.apache.commons.collections4.MultiValuedMap
    public boolean containsMapping(Object key, Object value) {
        return decorated().containsMapping(key, value);
    }

    @Override // org.apache.commons.collections4.MultiValuedMap
    public Collection<V> get(K key) {
        return decorated().get(key);
    }

    @Override // org.apache.commons.collections4.MultiValuedMap
    public Collection<V> remove(Object key) {
        return decorated().remove(key);
    }

    @Override // org.apache.commons.collections4.MultiValuedMap
    public boolean removeMapping(Object key, Object item) {
        return decorated().removeMapping(key, item);
    }

    @Override // org.apache.commons.collections4.MultiValuedMap
    public void clear() {
        decorated().clear();
    }

    @Override // org.apache.commons.collections4.MultiValuedMap
    public boolean put(K key, V value) {
        return decorated().put(key, value);
    }

    @Override // org.apache.commons.collections4.MultiValuedMap
    public Set<K> keySet() {
        return decorated().keySet();
    }

    @Override // org.apache.commons.collections4.MultiValuedMap
    public Collection<Map.Entry<K, V>> entries() {
        return decorated().entries();
    }

    @Override // org.apache.commons.collections4.MultiValuedMap
    public MultiSet<K> keys() {
        return decorated().keys();
    }

    @Override // org.apache.commons.collections4.MultiValuedMap
    public Collection<V> values() {
        return decorated().values();
    }

    @Override // org.apache.commons.collections4.MultiValuedMap
    public Map<K, Collection<V>> asMap() {
        return decorated().asMap();
    }

    @Override // org.apache.commons.collections4.MultiValuedMap
    public boolean putAll(K key, Iterable<? extends V> values) {
        return decorated().putAll(key, values);
    }

    @Override // org.apache.commons.collections4.MultiValuedMap
    public boolean putAll(Map<? extends K, ? extends V> map) {
        return decorated().putAll(map);
    }

    @Override // org.apache.commons.collections4.MultiValuedMap
    public boolean putAll(MultiValuedMap<? extends K, ? extends V> map) {
        return decorated().putAll(map);
    }

    @Override // org.apache.commons.collections4.MultiValuedMap
    public MapIterator<K, V> mapIterator() {
        return decorated().mapIterator();
    }

    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        return decorated().equals(object);
    }

    public int hashCode() {
        return decorated().hashCode();
    }

    public String toString() {
        return decorated().toString();
    }
}
