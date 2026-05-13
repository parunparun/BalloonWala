package org.apache.commons.collections4.bidimap;

import java.util.Comparator;
import java.util.SortedMap;
import org.apache.commons.collections4.SortedBidiMap;

/* JADX INFO: loaded from: classes.dex */
public abstract class AbstractSortedBidiMapDecorator<K, V> extends AbstractOrderedBidiMapDecorator<K, V> implements SortedBidiMap<K, V> {
    public AbstractSortedBidiMapDecorator(SortedBidiMap<K, V> map) {
        super(map);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.commons.collections4.bidimap.AbstractOrderedBidiMapDecorator, org.apache.commons.collections4.bidimap.AbstractBidiMapDecorator, org.apache.commons.collections4.map.AbstractMapDecorator
    public SortedBidiMap<K, V> decorated() {
        return (SortedBidiMap) super.decorated();
    }

    @Override // org.apache.commons.collections4.bidimap.AbstractOrderedBidiMapDecorator, org.apache.commons.collections4.bidimap.AbstractBidiMapDecorator, org.apache.commons.collections4.BidiMap
    public SortedBidiMap<V, K> inverseBidiMap() {
        return decorated().inverseBidiMap();
    }

    @Override // java.util.SortedMap
    public Comparator<? super K> comparator() {
        return decorated().comparator();
    }

    @Override // org.apache.commons.collections4.SortedBidiMap
    public Comparator<? super V> valueComparator() {
        return decorated().valueComparator();
    }

    @Override // java.util.SortedMap
    public SortedMap<K, V> subMap(K fromKey, K toKey) {
        return decorated().subMap(fromKey, toKey);
    }

    @Override // java.util.SortedMap
    public SortedMap<K, V> headMap(K toKey) {
        return decorated().headMap(toKey);
    }

    @Override // java.util.SortedMap
    public SortedMap<K, V> tailMap(K fromKey) {
        return decorated().tailMap(fromKey);
    }
}
