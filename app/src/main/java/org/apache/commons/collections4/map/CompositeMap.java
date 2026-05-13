package org.apache.commons.collections4.map;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.collection.CompositeCollection;
import org.apache.commons.collections4.set.CompositeSet;

/* JADX INFO: loaded from: classes.dex */
public class CompositeMap<K, V> extends AbstractIterableMap<K, V> implements Serializable {
    private static final long serialVersionUID = -6096931280583808322L;
    private Map<K, V>[] composite;
    private MapMutator<K, V> mutator;

    public interface MapMutator<K, V> extends Serializable {
        V put(CompositeMap<K, V> compositeMap, Map<K, V>[] mapArr, K k, V v);

        void putAll(CompositeMap<K, V> compositeMap, Map<K, V>[] mapArr, Map<? extends K, ? extends V> map);

        void resolveCollision(CompositeMap<K, V> compositeMap, Map<K, V> map, Map<K, V> map2, Collection<K> collection);
    }

    public CompositeMap() {
        this(new Map[0], (MapMutator) null);
    }

    public CompositeMap(Map<K, V> one, Map<K, V> two) {
        this(new Map[]{one, two}, (MapMutator) null);
    }

    public CompositeMap(Map<K, V> one, Map<K, V> two, MapMutator<K, V> mutator) {
        this(new Map[]{one, two}, mutator);
    }

    public CompositeMap(Map<K, V>... composite) {
        this(composite, (MapMutator) null);
    }

    public CompositeMap(Map<K, V>[] composite, MapMutator<K, V> mutator) {
        this.mutator = mutator;
        this.composite = new Map[0];
        for (int i = composite.length - 1; i >= 0; i--) {
            addComposited(composite[i]);
        }
    }

    public void setMutator(MapMutator<K, V> mutator) {
        this.mutator = mutator;
    }

    public synchronized void addComposited(Map<K, V> map) throws IllegalArgumentException {
        if (map != null) {
            for (int i = this.composite.length - 1; i >= 0; i--) {
                Collection<K> intersect = CollectionUtils.intersection(this.composite[i].keySet(), map.keySet());
                if (intersect.size() != 0) {
                    if (this.mutator == null) {
                        throw new IllegalArgumentException("Key collision adding Map to CompositeMap");
                    }
                    this.mutator.resolveCollision(this, this.composite[i], map, intersect);
                }
            }
            Map<K, V>[] temp = new Map[this.composite.length + 1];
            System.arraycopy(this.composite, 0, temp, 0, this.composite.length);
            temp[temp.length - 1] = map;
            this.composite = temp;
        }
    }

    public synchronized Map<K, V> removeComposited(Map<K, V> map) {
        int size = this.composite.length;
        for (int i = 0; i < size; i++) {
            if (this.composite[i].equals(map)) {
                Map<K, V>[] temp = new Map[size - 1];
                System.arraycopy(this.composite, 0, temp, 0, i);
                System.arraycopy(this.composite, i + 1, temp, i, (size - i) - 1);
                this.composite = temp;
                return map;
            }
        }
        return null;
    }

    @Override // java.util.Map, org.apache.commons.collections4.Put
    public void clear() {
        for (int i = this.composite.length - 1; i >= 0; i--) {
            this.composite[i].clear();
        }
    }

    @Override // java.util.Map, org.apache.commons.collections4.Get
    public boolean containsKey(Object key) {
        for (int i = this.composite.length - 1; i >= 0; i--) {
            if (this.composite[i].containsKey(key)) {
                return true;
            }
        }
        return false;
    }

    @Override // java.util.Map, org.apache.commons.collections4.Get
    public boolean containsValue(Object value) {
        for (int i = this.composite.length - 1; i >= 0; i--) {
            if (this.composite[i].containsValue(value)) {
                return true;
            }
        }
        return false;
    }

    @Override // java.util.Map, org.apache.commons.collections4.Get
    public Set<Map.Entry<K, V>> entrySet() {
        CompositeSet<Map.Entry<K, V>> entries = new CompositeSet<>();
        for (int i = this.composite.length - 1; i >= 0; i--) {
            entries.addComposited(this.composite[i].entrySet());
        }
        return entries;
    }

    @Override // java.util.Map, org.apache.commons.collections4.Get
    public V get(Object key) {
        for (int i = this.composite.length - 1; i >= 0; i--) {
            if (this.composite[i].containsKey(key)) {
                return this.composite[i].get(key);
            }
        }
        return null;
    }

    @Override // java.util.Map, org.apache.commons.collections4.Get
    public boolean isEmpty() {
        for (int i = this.composite.length - 1; i >= 0; i--) {
            if (!this.composite[i].isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override // java.util.Map, org.apache.commons.collections4.Get
    public Set<K> keySet() {
        CompositeSet<K> keys = new CompositeSet<>();
        for (int i = this.composite.length - 1; i >= 0; i--) {
            keys.addComposited(this.composite[i].keySet());
        }
        return keys;
    }

    @Override // java.util.Map, org.apache.commons.collections4.Put
    public V put(K key, V value) {
        MapMutator<K, V> mapMutator = this.mutator;
        if (mapMutator == null) {
            throw new UnsupportedOperationException("No mutator specified");
        }
        return mapMutator.put(this, this.composite, key, value);
    }

    @Override // java.util.Map, org.apache.commons.collections4.Put
    public void putAll(Map<? extends K, ? extends V> map) {
        MapMutator<K, V> mapMutator = this.mutator;
        if (mapMutator == null) {
            throw new UnsupportedOperationException("No mutator specified");
        }
        mapMutator.putAll(this, this.composite, map);
    }

    @Override // java.util.Map, org.apache.commons.collections4.Get
    public V remove(Object key) {
        for (int i = this.composite.length - 1; i >= 0; i--) {
            if (this.composite[i].containsKey(key)) {
                return this.composite[i].remove(key);
            }
        }
        return null;
    }

    @Override // java.util.Map, org.apache.commons.collections4.Get
    public int size() {
        int size = 0;
        for (int i = this.composite.length - 1; i >= 0; i--) {
            size += this.composite[i].size();
        }
        return size;
    }

    @Override // java.util.Map, org.apache.commons.collections4.Get
    public Collection<V> values() {
        CompositeCollection<V> values = new CompositeCollection<>();
        for (int i = this.composite.length - 1; i >= 0; i--) {
            values.addComposited(this.composite[i].values());
        }
        return values;
    }

    @Override // java.util.Map
    public boolean equals(Object obj) {
        if (obj instanceof Map) {
            Map<?, ?> map = (Map) obj;
            return entrySet().equals(map.entrySet());
        }
        return false;
    }

    @Override // java.util.Map
    public int hashCode() {
        int code = 0;
        for (Map.Entry<K, V> entry : entrySet()) {
            code += entry.hashCode();
        }
        return code;
    }
}
