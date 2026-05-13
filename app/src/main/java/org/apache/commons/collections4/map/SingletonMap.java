package org.apache.commons.collections4.map;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import org.apache.commons.collections4.BoundedMap;
import org.apache.commons.collections4.KeyValue;
import org.apache.commons.collections4.OrderedMap;
import org.apache.commons.collections4.OrderedMapIterator;
import org.apache.commons.collections4.ResettableIterator;
import org.apache.commons.collections4.iterators.SingletonIterator;
import org.apache.commons.collections4.keyvalue.TiedMapEntry;

/* JADX INFO: loaded from: classes.dex */
public class SingletonMap<K, V> implements OrderedMap<K, V>, BoundedMap<K, V>, KeyValue<K, V>, Serializable, Cloneable {
    private static final long serialVersionUID = -8931271118676803261L;
    private final K key;
    private V value;

    public SingletonMap() {
        this.key = null;
    }

    public SingletonMap(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public SingletonMap(KeyValue<K, V> keyValue) {
        this.key = keyValue.getKey();
        this.value = keyValue.getValue();
    }

    public SingletonMap(Map.Entry<? extends K, ? extends V> mapEntry) {
        this.key = mapEntry.getKey();
        this.value = mapEntry.getValue();
    }

    public SingletonMap(Map<? extends K, ? extends V> map) {
        if (map.size() != 1) {
            throw new IllegalArgumentException("The map size must be 1");
        }
        Map.Entry<? extends K, ? extends V> entry = map.entrySet().iterator().next();
        this.key = entry.getKey();
        this.value = entry.getValue();
    }

    @Override // org.apache.commons.collections4.KeyValue
    public K getKey() {
        return this.key;
    }

    @Override // org.apache.commons.collections4.KeyValue
    public V getValue() {
        return this.value;
    }

    public V setValue(V value) {
        V old = this.value;
        this.value = value;
        return old;
    }

    @Override // org.apache.commons.collections4.BoundedMap
    public boolean isFull() {
        return true;
    }

    @Override // org.apache.commons.collections4.BoundedMap
    public int maxSize() {
        return 1;
    }

    @Override // java.util.Map, org.apache.commons.collections4.Get
    public V get(Object key) {
        if (isEqualKey(key)) {
            return this.value;
        }
        return null;
    }

    @Override // java.util.Map, org.apache.commons.collections4.Get
    public int size() {
        return 1;
    }

    @Override // java.util.Map, org.apache.commons.collections4.Get
    public boolean isEmpty() {
        return false;
    }

    @Override // java.util.Map, org.apache.commons.collections4.Get
    public boolean containsKey(Object key) {
        return isEqualKey(key);
    }

    @Override // java.util.Map, org.apache.commons.collections4.Get
    public boolean containsValue(Object value) {
        return isEqualValue(value);
    }

    @Override // java.util.Map, org.apache.commons.collections4.Put
    public V put(K key, V value) {
        if (isEqualKey(key)) {
            return setValue(value);
        }
        throw new IllegalArgumentException("Cannot put new key/value pair - Map is fixed size singleton");
    }

    @Override // java.util.Map, org.apache.commons.collections4.Put
    public void putAll(Map<? extends K, ? extends V> map) {
        int size = map.size();
        if (size != 0) {
            if (size == 1) {
                Map.Entry<? extends K, ? extends V> entry = map.entrySet().iterator().next();
                put(entry.getKey(), entry.getValue());
                return;
            }
            throw new IllegalArgumentException("The map size must be 0 or 1");
        }
    }

    @Override // java.util.Map, org.apache.commons.collections4.Get
    public V remove(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override // java.util.Map, org.apache.commons.collections4.Put
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override // java.util.Map, org.apache.commons.collections4.Get
    public Set<Map.Entry<K, V>> entrySet() {
        Map.Entry<K, V> entry = new TiedMapEntry<>(this, getKey());
        return Collections.singleton(entry);
    }

    @Override // java.util.Map, org.apache.commons.collections4.Get
    public Set<K> keySet() {
        return Collections.singleton(this.key);
    }

    @Override // java.util.Map, org.apache.commons.collections4.Get
    public Collection<V> values() {
        return new SingletonValues(this);
    }

    @Override // org.apache.commons.collections4.OrderedMap, org.apache.commons.collections4.IterableGet
    public OrderedMapIterator<K, V> mapIterator() {
        return new SingletonMapIterator(this);
    }

    @Override // org.apache.commons.collections4.OrderedMap
    public K firstKey() {
        return getKey();
    }

    @Override // org.apache.commons.collections4.OrderedMap
    public K lastKey() {
        return getKey();
    }

    @Override // org.apache.commons.collections4.OrderedMap
    public K nextKey(K key) {
        return null;
    }

    @Override // org.apache.commons.collections4.OrderedMap
    public K previousKey(K key) {
        return null;
    }

    protected boolean isEqualKey(Object key) {
        K key2 = getKey();
        return key == null ? key2 == null : key.equals(key2);
    }

    protected boolean isEqualValue(Object value) {
        V value2 = getValue();
        return value == null ? value2 == null : value.equals(value2);
    }

    static class SingletonMapIterator<K, V> implements OrderedMapIterator<K, V>, ResettableIterator<K> {
        private final SingletonMap<K, V> parent;
        private boolean hasNext = true;
        private boolean canGetSet = false;

        SingletonMapIterator(SingletonMap<K, V> parent) {
            this.parent = parent;
        }

        @Override // org.apache.commons.collections4.MapIterator, java.util.Iterator
        public boolean hasNext() {
            return this.hasNext;
        }

        @Override // org.apache.commons.collections4.MapIterator, java.util.Iterator
        public K next() {
            if (!this.hasNext) {
                throw new NoSuchElementException("No next() entry in the iteration");
            }
            this.hasNext = false;
            this.canGetSet = true;
            return this.parent.getKey();
        }

        @Override // org.apache.commons.collections4.OrderedMapIterator, org.apache.commons.collections4.OrderedIterator
        public boolean hasPrevious() {
            return !this.hasNext;
        }

        @Override // org.apache.commons.collections4.OrderedMapIterator, org.apache.commons.collections4.OrderedIterator
        public K previous() {
            if (this.hasNext) {
                throw new NoSuchElementException("No previous() entry in the iteration");
            }
            this.hasNext = true;
            return this.parent.getKey();
        }

        @Override // org.apache.commons.collections4.MapIterator, java.util.Iterator
        public void remove() {
            throw new UnsupportedOperationException();
        }

        @Override // org.apache.commons.collections4.MapIterator
        public K getKey() {
            if (!this.canGetSet) {
                throw new IllegalStateException("getKey() can only be called after next() and before remove()");
            }
            return this.parent.getKey();
        }

        @Override // org.apache.commons.collections4.MapIterator
        public V getValue() {
            if (!this.canGetSet) {
                throw new IllegalStateException("getValue() can only be called after next() and before remove()");
            }
            return this.parent.getValue();
        }

        @Override // org.apache.commons.collections4.MapIterator
        public V setValue(V value) {
            if (!this.canGetSet) {
                throw new IllegalStateException("setValue() can only be called after next() and before remove()");
            }
            return this.parent.setValue(value);
        }

        @Override // org.apache.commons.collections4.ResettableIterator
        public void reset() {
            this.hasNext = true;
        }

        public String toString() {
            if (this.hasNext) {
                return "Iterator[]";
            }
            return "Iterator[" + getKey() + "=" + getValue() + "]";
        }
    }

    static class SingletonValues<V> extends AbstractSet<V> implements Serializable {
        private static final long serialVersionUID = -3689524741863047872L;
        private final SingletonMap<?, V> parent;

        SingletonValues(SingletonMap<?, V> parent) {
            this.parent = parent;
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public int size() {
            return 1;
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public boolean isEmpty() {
            return false;
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public boolean contains(Object object) {
            return this.parent.containsValue(object);
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Set
        public Iterator<V> iterator() {
            return new SingletonIterator(this.parent.getValue(), false);
        }
    }

    public SingletonMap<K, V> clone() {
        try {
            return (SingletonMap) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }

    @Override // java.util.Map
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Map)) {
            return false;
        }
        Map<?, ?> other = (Map) obj;
        if (other.size() != 1) {
            return false;
        }
        Map.Entry<K, V> next = other.entrySet().iterator().next();
        return isEqualKey(next.getKey()) && isEqualValue(next.getValue());
    }

    @Override // java.util.Map
    public int hashCode() {
        return (getKey() == null ? 0 : getKey().hashCode()) ^ (getValue() != null ? getValue().hashCode() : 0);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(128);
        sb.append('{');
        sb.append(getKey() == this ? "(this Map)" : getKey());
        sb.append('=');
        sb.append(getValue() != this ? getValue() : "(this Map)");
        sb.append('}');
        return sb.toString();
    }
}
