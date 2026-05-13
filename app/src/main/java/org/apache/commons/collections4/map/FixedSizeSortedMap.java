package org.apache.commons.collections4.map;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import org.apache.commons.collections4.BoundedMap;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.collection.UnmodifiableCollection;
import org.apache.commons.collections4.set.UnmodifiableSet;

/* JADX INFO: loaded from: classes.dex */
public class FixedSizeSortedMap<K, V> extends AbstractSortedMapDecorator<K, V> implements BoundedMap<K, V>, Serializable {
    private static final long serialVersionUID = 3126019624511683653L;

    public static <K, V> FixedSizeSortedMap<K, V> fixedSizeSortedMap(SortedMap<K, V> map) {
        return new FixedSizeSortedMap<>(map);
    }

    protected FixedSizeSortedMap(SortedMap<K, V> map) {
        super(map);
    }

    protected SortedMap<K, V> getSortedMap() {
        return (SortedMap) this.map;
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
    public V put(K key, V value) {
        if (!this.map.containsKey(key)) {
            throw new IllegalArgumentException("Cannot put new key/value pair - Map is fixed size");
        }
        return this.map.put(key, value);
    }

    @Override // org.apache.commons.collections4.map.AbstractMapDecorator, java.util.Map, org.apache.commons.collections4.Put
    public void putAll(Map<? extends K, ? extends V> mapToCopy) {
        if (CollectionUtils.isSubCollection(mapToCopy.keySet(), keySet())) {
            throw new IllegalArgumentException("Cannot put new key/value pair - Map is fixed size");
        }
        this.map.putAll(mapToCopy);
    }

    @Override // org.apache.commons.collections4.map.AbstractMapDecorator, java.util.Map, org.apache.commons.collections4.Put
    public void clear() {
        throw new UnsupportedOperationException("Map is fixed size");
    }

    @Override // org.apache.commons.collections4.map.AbstractMapDecorator, java.util.Map, org.apache.commons.collections4.Get
    public V remove(Object key) {
        throw new UnsupportedOperationException("Map is fixed size");
    }

    @Override // org.apache.commons.collections4.map.AbstractMapDecorator, java.util.Map, org.apache.commons.collections4.Get
    public Set<Map.Entry<K, V>> entrySet() {
        return UnmodifiableSet.unmodifiableSet(this.map.entrySet());
    }

    @Override // org.apache.commons.collections4.map.AbstractMapDecorator, java.util.Map, org.apache.commons.collections4.Get
    public Set<K> keySet() {
        return UnmodifiableSet.unmodifiableSet(this.map.keySet());
    }

    @Override // org.apache.commons.collections4.map.AbstractMapDecorator, java.util.Map, org.apache.commons.collections4.Get
    public Collection<V> values() {
        return UnmodifiableCollection.unmodifiableCollection(this.map.values());
    }

    @Override // org.apache.commons.collections4.map.AbstractSortedMapDecorator, java.util.SortedMap
    public SortedMap<K, V> subMap(K fromKey, K toKey) {
        return new FixedSizeSortedMap(getSortedMap().subMap(fromKey, toKey));
    }

    @Override // org.apache.commons.collections4.map.AbstractSortedMapDecorator, java.util.SortedMap
    public SortedMap<K, V> headMap(K toKey) {
        return new FixedSizeSortedMap(getSortedMap().headMap(toKey));
    }

    @Override // org.apache.commons.collections4.map.AbstractSortedMapDecorator, java.util.SortedMap
    public SortedMap<K, V> tailMap(K fromKey) {
        return new FixedSizeSortedMap(getSortedMap().tailMap(fromKey));
    }

    @Override // org.apache.commons.collections4.BoundedMap
    public boolean isFull() {
        return true;
    }

    @Override // org.apache.commons.collections4.BoundedMap
    public int maxSize() {
        return size();
    }
}
