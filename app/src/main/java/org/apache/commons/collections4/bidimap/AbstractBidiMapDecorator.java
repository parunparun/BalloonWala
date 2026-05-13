package org.apache.commons.collections4.bidimap;

import java.util.Set;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.map.AbstractMapDecorator;

/* JADX INFO: loaded from: classes.dex */
public abstract class AbstractBidiMapDecorator<K, V> extends AbstractMapDecorator<K, V> implements BidiMap<K, V> {
    protected AbstractBidiMapDecorator(BidiMap<K, V> map) {
        super(map);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.commons.collections4.map.AbstractMapDecorator
    public BidiMap<K, V> decorated() {
        return (BidiMap) super.decorated();
    }

    @Override // org.apache.commons.collections4.map.AbstractIterableMap, org.apache.commons.collections4.IterableGet
    public MapIterator<K, V> mapIterator() {
        return decorated().mapIterator();
    }

    @Override // org.apache.commons.collections4.BidiMap
    public K getKey(Object value) {
        return decorated().getKey(value);
    }

    @Override // org.apache.commons.collections4.BidiMap
    public K removeValue(Object value) {
        return decorated().removeValue(value);
    }

    @Override // org.apache.commons.collections4.BidiMap
    public BidiMap<V, K> inverseBidiMap() {
        return decorated().inverseBidiMap();
    }

    @Override // org.apache.commons.collections4.map.AbstractMapDecorator, java.util.Map, org.apache.commons.collections4.Get
    public Set<V> values() {
        return decorated().values();
    }
}
