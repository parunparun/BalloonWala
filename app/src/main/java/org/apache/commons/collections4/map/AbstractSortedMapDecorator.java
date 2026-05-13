package org.apache.commons.collections4.map;

import java.util.Comparator;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import org.apache.commons.collections4.IterableSortedMap;
import org.apache.commons.collections4.OrderedMapIterator;
import org.apache.commons.collections4.iterators.ListIteratorWrapper;

/* JADX INFO: loaded from: classes.dex */
public abstract class AbstractSortedMapDecorator<K, V> extends AbstractMapDecorator<K, V> implements IterableSortedMap<K, V> {
    protected AbstractSortedMapDecorator() {
    }

    public AbstractSortedMapDecorator(SortedMap<K, V> map) {
        super(map);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.commons.collections4.map.AbstractMapDecorator
    public SortedMap<K, V> decorated() {
        return (SortedMap) super.decorated();
    }

    @Override // java.util.SortedMap
    public Comparator<? super K> comparator() {
        return decorated().comparator();
    }

    @Override // java.util.SortedMap, org.apache.commons.collections4.OrderedMap
    public K firstKey() {
        return decorated().firstKey();
    }

    @Override // java.util.SortedMap, org.apache.commons.collections4.OrderedMap
    public K lastKey() {
        return decorated().lastKey();
    }

    public SortedMap<K, V> subMap(K fromKey, K toKey) {
        return decorated().subMap(fromKey, toKey);
    }

    public SortedMap<K, V> headMap(K toKey) {
        return decorated().headMap(toKey);
    }

    public SortedMap<K, V> tailMap(K fromKey) {
        return decorated().tailMap(fromKey);
    }

    public K previousKey(K key) {
        SortedMap<K, V> headMap = headMap(key);
        if (headMap.isEmpty()) {
            return null;
        }
        return headMap.lastKey();
    }

    public K nextKey(K key) {
        Iterator<K> it = tailMap(key).keySet().iterator();
        it.next();
        if (it.hasNext()) {
            return it.next();
        }
        return null;
    }

    @Override // org.apache.commons.collections4.map.AbstractIterableMap, org.apache.commons.collections4.IterableGet
    public OrderedMapIterator<K, V> mapIterator() {
        return new SortedMapIterator(entrySet());
    }

    protected static class SortedMapIterator<K, V> extends EntrySetToMapIteratorAdapter<K, V> implements OrderedMapIterator<K, V> {
        protected SortedMapIterator(Set<Map.Entry<K, V>> entrySet) {
            super(entrySet);
        }

        @Override // org.apache.commons.collections4.map.EntrySetToMapIteratorAdapter, org.apache.commons.collections4.ResettableIterator
        public synchronized void reset() {
            super.reset();
            this.iterator = new ListIteratorWrapper(this.iterator);
        }

        @Override // org.apache.commons.collections4.OrderedMapIterator, org.apache.commons.collections4.OrderedIterator
        public boolean hasPrevious() {
            return ((ListIterator) this.iterator).hasPrevious();
        }

        @Override // org.apache.commons.collections4.OrderedMapIterator, org.apache.commons.collections4.OrderedIterator
        public K previous() {
            this.entry = (Map.Entry) ((ListIterator) this.iterator).previous();
            return getKey();
        }
    }
}
