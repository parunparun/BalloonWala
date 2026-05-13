package org.apache.commons.collections4.map;

import org.apache.commons.collections4.OrderedMap;
import org.apache.commons.collections4.OrderedMapIterator;

/* JADX INFO: loaded from: classes.dex */
public abstract class AbstractOrderedMapDecorator<K, V> extends AbstractMapDecorator<K, V> implements OrderedMap<K, V> {
    protected AbstractOrderedMapDecorator() {
    }

    public AbstractOrderedMapDecorator(OrderedMap<K, V> map) {
        super(map);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.commons.collections4.map.AbstractMapDecorator
    public OrderedMap<K, V> decorated() {
        return (OrderedMap) super.decorated();
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

    @Override // org.apache.commons.collections4.map.AbstractIterableMap, org.apache.commons.collections4.IterableGet
    public OrderedMapIterator<K, V> mapIterator() {
        return decorated().mapIterator();
    }
}
