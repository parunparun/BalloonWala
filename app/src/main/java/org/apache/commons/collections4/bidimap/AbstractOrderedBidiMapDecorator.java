package org.apache.commons.collections4.bidimap;

import org.apache.commons.collections4.OrderedBidiMap;
import org.apache.commons.collections4.OrderedMapIterator;

/* JADX INFO: loaded from: classes.dex */
public abstract class AbstractOrderedBidiMapDecorator<K, V> extends AbstractBidiMapDecorator<K, V> implements OrderedBidiMap<K, V> {
    protected AbstractOrderedBidiMapDecorator(OrderedBidiMap<K, V> map) {
        super(map);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.commons.collections4.bidimap.AbstractBidiMapDecorator, org.apache.commons.collections4.map.AbstractMapDecorator
    public OrderedBidiMap<K, V> decorated() {
        return (OrderedBidiMap) super.decorated();
    }

    @Override // org.apache.commons.collections4.bidimap.AbstractBidiMapDecorator, org.apache.commons.collections4.map.AbstractIterableMap, org.apache.commons.collections4.IterableGet
    public OrderedMapIterator<K, V> mapIterator() {
        return decorated().mapIterator();
    }

    @Override // org.apache.commons.collections4.OrderedMap
    public K firstKey() {
        return decorated().firstKey();
    }

    @Override // org.apache.commons.collections4.OrderedMap
    public K lastKey() {
        return decorated().lastKey();
    }

    @Override // org.apache.commons.collections4.OrderedMap
    public K nextKey(K key) {
        return decorated().nextKey(key);
    }

    @Override // org.apache.commons.collections4.OrderedMap
    public K previousKey(K key) {
        return decorated().previousKey(key);
    }

    @Override // org.apache.commons.collections4.bidimap.AbstractBidiMapDecorator, org.apache.commons.collections4.BidiMap
    public OrderedBidiMap<V, K> inverseBidiMap() {
        return decorated().inverseBidiMap();
    }
}
