package org.apache.commons.collections4.bidimap;

import java.util.Map;
import java.util.Set;
import org.apache.commons.collections4.OrderedBidiMap;
import org.apache.commons.collections4.OrderedMapIterator;
import org.apache.commons.collections4.Unmodifiable;
import org.apache.commons.collections4.iterators.UnmodifiableOrderedMapIterator;
import org.apache.commons.collections4.map.UnmodifiableEntrySet;
import org.apache.commons.collections4.set.UnmodifiableSet;

/* JADX INFO: loaded from: classes.dex */
public final class UnmodifiableOrderedBidiMap<K, V> extends AbstractOrderedBidiMapDecorator<K, V> implements Unmodifiable {
    private UnmodifiableOrderedBidiMap<V, K> inverse;

    /* JADX WARN: Multi-variable type inference failed */
    public static <K, V> OrderedBidiMap<K, V> unmodifiableOrderedBidiMap(OrderedBidiMap<? extends K, ? extends V> orderedBidiMap) {
        if (orderedBidiMap instanceof Unmodifiable) {
            return orderedBidiMap;
        }
        return new UnmodifiableOrderedBidiMap(orderedBidiMap);
    }

    private UnmodifiableOrderedBidiMap(OrderedBidiMap<? extends K, ? extends V> map) {
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

    @Override // org.apache.commons.collections4.bidimap.AbstractOrderedBidiMapDecorator, org.apache.commons.collections4.bidimap.AbstractBidiMapDecorator, org.apache.commons.collections4.BidiMap
    public OrderedBidiMap<V, K> inverseBidiMap() {
        return inverseOrderedBidiMap();
    }

    @Override // org.apache.commons.collections4.bidimap.AbstractOrderedBidiMapDecorator, org.apache.commons.collections4.bidimap.AbstractBidiMapDecorator, org.apache.commons.collections4.map.AbstractIterableMap, org.apache.commons.collections4.IterableGet
    public OrderedMapIterator<K, V> mapIterator() {
        return UnmodifiableOrderedMapIterator.unmodifiableOrderedMapIterator(decorated().mapIterator());
    }

    /* JADX WARN: Multi-variable type inference failed */
    public OrderedBidiMap<V, K> inverseOrderedBidiMap() {
        if (this.inverse == null) {
            UnmodifiableOrderedBidiMap<V, K> unmodifiableOrderedBidiMap = new UnmodifiableOrderedBidiMap<>(decorated().inverseBidiMap());
            this.inverse = unmodifiableOrderedBidiMap;
            unmodifiableOrderedBidiMap.inverse = this;
        }
        return this.inverse;
    }
}
