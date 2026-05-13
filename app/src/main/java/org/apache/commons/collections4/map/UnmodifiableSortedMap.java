package org.apache.commons.collections4.map;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import org.apache.commons.collections4.Unmodifiable;
import org.apache.commons.collections4.collection.UnmodifiableCollection;
import org.apache.commons.collections4.set.UnmodifiableSet;

/* JADX INFO: loaded from: classes.dex */
public final class UnmodifiableSortedMap<K, V> extends AbstractSortedMapDecorator<K, V> implements Unmodifiable, Serializable {
    private static final long serialVersionUID = 5805344239827376360L;

    /* JADX WARN: Multi-variable type inference failed */
    public static <K, V> SortedMap<K, V> unmodifiableSortedMap(SortedMap<K, ? extends V> sortedMap) {
        if (sortedMap instanceof Unmodifiable) {
            return sortedMap;
        }
        return new UnmodifiableSortedMap(sortedMap);
    }

    private UnmodifiableSortedMap(SortedMap<K, ? extends V> map) {
        super(map);
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(this.map);
    }

    private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
        in.defaultReadObject();
        this.map = (Map) in.readObject();
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
        return UnmodifiableEntrySet.unmodifiableEntrySet(super.entrySet());
    }

    @Override // org.apache.commons.collections4.map.AbstractMapDecorator, java.util.Map, org.apache.commons.collections4.Get
    public Set<K> keySet() {
        return UnmodifiableSet.unmodifiableSet(super.keySet());
    }

    @Override // org.apache.commons.collections4.map.AbstractMapDecorator, java.util.Map, org.apache.commons.collections4.Get
    public Collection<V> values() {
        return UnmodifiableCollection.unmodifiableCollection(super.values());
    }

    @Override // org.apache.commons.collections4.map.AbstractSortedMapDecorator, java.util.SortedMap, org.apache.commons.collections4.OrderedMap
    public K firstKey() {
        return (K) decorated().firstKey();
    }

    @Override // org.apache.commons.collections4.map.AbstractSortedMapDecorator, java.util.SortedMap, org.apache.commons.collections4.OrderedMap
    public K lastKey() {
        return (K) decorated().lastKey();
    }

    @Override // org.apache.commons.collections4.map.AbstractSortedMapDecorator, java.util.SortedMap
    public Comparator<? super K> comparator() {
        return decorated().comparator();
    }

    @Override // org.apache.commons.collections4.map.AbstractSortedMapDecorator, java.util.SortedMap
    public SortedMap<K, V> subMap(K fromKey, K toKey) {
        return new UnmodifiableSortedMap(decorated().subMap(fromKey, toKey));
    }

    @Override // org.apache.commons.collections4.map.AbstractSortedMapDecorator, java.util.SortedMap
    public SortedMap<K, V> headMap(K toKey) {
        return new UnmodifiableSortedMap(decorated().headMap(toKey));
    }

    @Override // org.apache.commons.collections4.map.AbstractSortedMapDecorator, java.util.SortedMap
    public SortedMap<K, V> tailMap(K fromKey) {
        return new UnmodifiableSortedMap(decorated().tailMap(fromKey));
    }
}
