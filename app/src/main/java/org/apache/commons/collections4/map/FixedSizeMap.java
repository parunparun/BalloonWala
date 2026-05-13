package org.apache.commons.collections4.map;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import org.apache.commons.collections4.BoundedMap;
import org.apache.commons.collections4.collection.UnmodifiableCollection;
import org.apache.commons.collections4.set.UnmodifiableSet;

/* JADX INFO: loaded from: classes.dex */
public class FixedSizeMap<K, V> extends AbstractMapDecorator<K, V> implements BoundedMap<K, V>, Serializable {
    private static final long serialVersionUID = 7450927208116179316L;

    public static <K, V> FixedSizeMap<K, V> fixedSizeMap(Map<K, V> map) {
        return new FixedSizeMap<>(map);
    }

    protected FixedSizeMap(Map<K, V> map) {
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
    public V put(K key, V value) {
        if (!this.map.containsKey(key)) {
            throw new IllegalArgumentException("Cannot put new key/value pair - Map is fixed size");
        }
        return this.map.put(key, value);
    }

    @Override // org.apache.commons.collections4.map.AbstractMapDecorator, java.util.Map, org.apache.commons.collections4.Put
    public void putAll(Map<? extends K, ? extends V> mapToCopy) {
        for (K key : mapToCopy.keySet()) {
            if (!containsKey(key)) {
                throw new IllegalArgumentException("Cannot put new key/value pair - Map is fixed size");
            }
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
        Set<Map.Entry<K, V>> set = this.map.entrySet();
        return UnmodifiableSet.unmodifiableSet(set);
    }

    @Override // org.apache.commons.collections4.map.AbstractMapDecorator, java.util.Map, org.apache.commons.collections4.Get
    public Set<K> keySet() {
        Set<K> set = this.map.keySet();
        return UnmodifiableSet.unmodifiableSet(set);
    }

    @Override // org.apache.commons.collections4.map.AbstractMapDecorator, java.util.Map, org.apache.commons.collections4.Get
    public Collection<V> values() {
        Collection<V> coll = this.map.values();
        return UnmodifiableCollection.unmodifiableCollection(coll);
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
