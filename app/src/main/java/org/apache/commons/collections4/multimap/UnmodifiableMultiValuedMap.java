package org.apache.commons.collections4.multimap;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.MultiSet;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.Unmodifiable;
import org.apache.commons.collections4.collection.UnmodifiableCollection;
import org.apache.commons.collections4.iterators.UnmodifiableMapIterator;
import org.apache.commons.collections4.map.UnmodifiableMap;
import org.apache.commons.collections4.multiset.UnmodifiableMultiSet;
import org.apache.commons.collections4.set.UnmodifiableSet;

/* JADX INFO: loaded from: classes.dex */
public final class UnmodifiableMultiValuedMap<K, V> extends AbstractMultiValuedMapDecorator<K, V> implements Unmodifiable {
    private static final long serialVersionUID = 20150612;

    public static <K, V> UnmodifiableMultiValuedMap<K, V> unmodifiableMultiValuedMap(MultiValuedMap<? extends K, ? extends V> map) {
        if (map instanceof Unmodifiable) {
            return (UnmodifiableMultiValuedMap) map;
        }
        return new UnmodifiableMultiValuedMap<>(map);
    }

    private UnmodifiableMultiValuedMap(MultiValuedMap<? extends K, ? extends V> map) {
        super(map);
    }

    @Override // org.apache.commons.collections4.multimap.AbstractMultiValuedMapDecorator, org.apache.commons.collections4.MultiValuedMap
    public Collection<V> remove(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override // org.apache.commons.collections4.multimap.AbstractMultiValuedMapDecorator, org.apache.commons.collections4.MultiValuedMap
    public boolean removeMapping(Object key, Object item) {
        throw new UnsupportedOperationException();
    }

    @Override // org.apache.commons.collections4.multimap.AbstractMultiValuedMapDecorator, org.apache.commons.collections4.MultiValuedMap
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override // org.apache.commons.collections4.multimap.AbstractMultiValuedMapDecorator, org.apache.commons.collections4.MultiValuedMap
    public Collection<V> get(K key) {
        return UnmodifiableCollection.unmodifiableCollection(decorated().get(key));
    }

    @Override // org.apache.commons.collections4.multimap.AbstractMultiValuedMapDecorator, org.apache.commons.collections4.MultiValuedMap
    public boolean put(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override // org.apache.commons.collections4.multimap.AbstractMultiValuedMapDecorator, org.apache.commons.collections4.MultiValuedMap
    public Set<K> keySet() {
        return UnmodifiableSet.unmodifiableSet(decorated().keySet());
    }

    @Override // org.apache.commons.collections4.multimap.AbstractMultiValuedMapDecorator, org.apache.commons.collections4.MultiValuedMap
    public Collection<Map.Entry<K, V>> entries() {
        return UnmodifiableCollection.unmodifiableCollection(decorated().entries());
    }

    @Override // org.apache.commons.collections4.multimap.AbstractMultiValuedMapDecorator, org.apache.commons.collections4.MultiValuedMap
    public MultiSet<K> keys() {
        return UnmodifiableMultiSet.unmodifiableMultiSet(decorated().keys());
    }

    @Override // org.apache.commons.collections4.multimap.AbstractMultiValuedMapDecorator, org.apache.commons.collections4.MultiValuedMap
    public Collection<V> values() {
        return UnmodifiableCollection.unmodifiableCollection(decorated().values());
    }

    @Override // org.apache.commons.collections4.multimap.AbstractMultiValuedMapDecorator, org.apache.commons.collections4.MultiValuedMap
    public Map<K, Collection<V>> asMap() {
        return UnmodifiableMap.unmodifiableMap(decorated().asMap());
    }

    @Override // org.apache.commons.collections4.multimap.AbstractMultiValuedMapDecorator, org.apache.commons.collections4.MultiValuedMap
    public MapIterator<K, V> mapIterator() {
        return UnmodifiableMapIterator.unmodifiableMapIterator(decorated().mapIterator());
    }

    @Override // org.apache.commons.collections4.multimap.AbstractMultiValuedMapDecorator, org.apache.commons.collections4.MultiValuedMap
    public boolean putAll(K key, Iterable<? extends V> values) {
        throw new UnsupportedOperationException();
    }

    @Override // org.apache.commons.collections4.multimap.AbstractMultiValuedMapDecorator, org.apache.commons.collections4.MultiValuedMap
    public boolean putAll(Map<? extends K, ? extends V> map) {
        throw new UnsupportedOperationException();
    }

    @Override // org.apache.commons.collections4.multimap.AbstractMultiValuedMapDecorator, org.apache.commons.collections4.MultiValuedMap
    public boolean putAll(MultiValuedMap<? extends K, ? extends V> map) {
        throw new UnsupportedOperationException();
    }
}
