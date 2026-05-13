package org.apache.commons.collections4.bidimap;

import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import org.apache.commons.collections4.OrderedMapIterator;
import org.apache.commons.collections4.SortedBidiMap;
import org.apache.commons.collections4.Unmodifiable;
import org.apache.commons.collections4.iterators.UnmodifiableOrderedMapIterator;
import org.apache.commons.collections4.map.UnmodifiableEntrySet;
import org.apache.commons.collections4.map.UnmodifiableSortedMap;
import org.apache.commons.collections4.set.UnmodifiableSet;

/* JADX INFO: loaded from: classes.dex */
public final class UnmodifiableSortedBidiMap<K, V> extends AbstractSortedBidiMapDecorator<K, V> implements Unmodifiable {
    private UnmodifiableSortedBidiMap<V, K> inverse;

    /* JADX WARN: Multi-variable type inference failed */
    public static <K, V> SortedBidiMap<K, V> unmodifiableSortedBidiMap(SortedBidiMap<K, ? extends V> sortedBidiMap) {
        if (sortedBidiMap instanceof Unmodifiable) {
            return sortedBidiMap;
        }
        return new UnmodifiableSortedBidiMap(sortedBidiMap);
    }

    private UnmodifiableSortedBidiMap(SortedBidiMap<K, ? extends V> map) {
        super(map);
    }

    @Override // org.apache.commons.collections4.map.AbstractMapDecorator, java.util.Map, org.apache.commons.collections4.Put
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override // org.apache.commons.collections4.map.AbstractMapDecorator, java.util.Map, org.apache.commons.collections4.Put
    public V put(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override // org.apache.commons.collections4.map.AbstractMapDecorator, java.util.Map, org.apache.commons.collections4.Put
    public void putAll(Map<? extends K, ? extends V> mapToCopy) {
        throw new UnsupportedOperationException();
    }

    @Override // org.apache.commons.collections4.map.AbstractMapDecorator, java.util.Map, org.apache.commons.collections4.Get
    public V remove(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override // org.apache.commons.collections4.map.AbstractMapDecorator, java.util.Map, org.apache.commons.collections4.Get
    public Set<Map.Entry<K, V>> entrySet() {
        Set<Map.Entry<K, V>> set = super.entrySet();
        return UnmodifiableEntrySet.unmodifiableEntrySet(set);
    }

    @Override // org.apache.commons.collections4.map.AbstractMapDecorator, java.util.Map, org.apache.commons.collections4.Get
    public Set<K> keySet() {
        Set<K> set = super.keySet();
        return UnmodifiableSet.unmodifiableSet(set);
    }

    @Override // org.apache.commons.collections4.bidimap.AbstractBidiMapDecorator, org.apache.commons.collections4.map.AbstractMapDecorator, java.util.Map, org.apache.commons.collections4.Get
    public Set<V> values() {
        Set<V> set = super.values();
        return UnmodifiableSet.unmodifiableSet(set);
    }

    @Override // org.apache.commons.collections4.bidimap.AbstractBidiMapDecorator, org.apache.commons.collections4.BidiMap
    public K removeValue(Object value) {
        throw new UnsupportedOperationException();
    }

    @Override // org.apache.commons.collections4.bidimap.AbstractOrderedBidiMapDecorator, org.apache.commons.collections4.bidimap.AbstractBidiMapDecorator, org.apache.commons.collections4.map.AbstractIterableMap, org.apache.commons.collections4.IterableGet
    public OrderedMapIterator<K, V> mapIterator() {
        return UnmodifiableOrderedMapIterator.unmodifiableOrderedMapIterator(decorated().mapIterator());
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.apache.commons.collections4.bidimap.AbstractSortedBidiMapDecorator, org.apache.commons.collections4.bidimap.AbstractOrderedBidiMapDecorator, org.apache.commons.collections4.bidimap.AbstractBidiMapDecorator, org.apache.commons.collections4.BidiMap
    public SortedBidiMap<V, K> inverseBidiMap() {
        if (this.inverse == null) {
            UnmodifiableSortedBidiMap<V, K> unmodifiableSortedBidiMap = new UnmodifiableSortedBidiMap<>(decorated().inverseBidiMap());
            this.inverse = unmodifiableSortedBidiMap;
            unmodifiableSortedBidiMap.inverse = this;
        }
        return this.inverse;
    }

    @Override // org.apache.commons.collections4.bidimap.AbstractSortedBidiMapDecorator, java.util.SortedMap
    public SortedMap<K, V> subMap(K fromKey, K toKey) {
        SortedMap<K, V> sm = decorated().subMap(fromKey, toKey);
        return UnmodifiableSortedMap.unmodifiableSortedMap(sm);
    }

    @Override // org.apache.commons.collections4.bidimap.AbstractSortedBidiMapDecorator, java.util.SortedMap
    public SortedMap<K, V> headMap(K toKey) {
        SortedMap<K, V> sm = decorated().headMap(toKey);
        return UnmodifiableSortedMap.unmodifiableSortedMap(sm);
    }

    @Override // org.apache.commons.collections4.bidimap.AbstractSortedBidiMapDecorator, java.util.SortedMap
    public SortedMap<K, V> tailMap(K fromKey) {
        SortedMap<K, V> sm = decorated().tailMap(fromKey);
        return UnmodifiableSortedMap.unmodifiableSortedMap(sm);
    }
}
