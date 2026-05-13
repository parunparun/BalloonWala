package org.apache.commons.collections4.map;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractList;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import org.apache.commons.collections4.OrderedMap;
import org.apache.commons.collections4.OrderedMapIterator;
import org.apache.commons.collections4.ResettableIterator;
import org.apache.commons.collections4.iterators.AbstractUntypedIteratorDecorator;
import org.apache.commons.collections4.keyvalue.AbstractMapEntry;
import org.apache.commons.collections4.list.UnmodifiableList;

/* JADX INFO: loaded from: classes.dex */
public class ListOrderedMap<K, V> extends AbstractMapDecorator<K, V> implements OrderedMap<K, V>, Serializable {
    private static final long serialVersionUID = 2728177751851003750L;
    private final List<K> insertOrder;

    public static <K, V> ListOrderedMap<K, V> listOrderedMap(Map<K, V> map) {
        return new ListOrderedMap<>(map);
    }

    public ListOrderedMap() {
        this(new HashMap());
    }

    protected ListOrderedMap(Map<K, V> map) {
        super(map);
        ArrayList arrayList = new ArrayList();
        this.insertOrder = arrayList;
        arrayList.addAll(decorated().keySet());
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(this.map);
    }

    private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
        in.defaultReadObject();
        this.map = (Map) in.readObject();
    }

    @Override // org.apache.commons.collections4.map.AbstractIterableMap, org.apache.commons.collections4.IterableGet
    public OrderedMapIterator<K, V> mapIterator() {
        return new ListOrderedMapIterator(this);
    }

    @Override // org.apache.commons.collections4.OrderedMap
    public K firstKey() {
        if (size() == 0) {
            throw new NoSuchElementException("Map is empty");
        }
        return this.insertOrder.get(0);
    }

    @Override // org.apache.commons.collections4.OrderedMap
    public K lastKey() {
        if (size() == 0) {
            throw new NoSuchElementException("Map is empty");
        }
        return this.insertOrder.get(size() - 1);
    }

    @Override // org.apache.commons.collections4.OrderedMap
    public K nextKey(Object key) {
        int index = this.insertOrder.indexOf(key);
        if (index >= 0 && index < size() - 1) {
            return this.insertOrder.get(index + 1);
        }
        return null;
    }

    @Override // org.apache.commons.collections4.OrderedMap
    public K previousKey(Object key) {
        int index = this.insertOrder.indexOf(key);
        if (index > 0) {
            return this.insertOrder.get(index - 1);
        }
        return null;
    }

    @Override // org.apache.commons.collections4.map.AbstractMapDecorator, java.util.Map, org.apache.commons.collections4.Put
    public V put(K key, V value) {
        if (decorated().containsKey(key)) {
            return decorated().put(key, value);
        }
        V result = decorated().put(key, value);
        this.insertOrder.add(key);
        return result;
    }

    @Override // org.apache.commons.collections4.map.AbstractMapDecorator, java.util.Map, org.apache.commons.collections4.Put
    public void putAll(Map<? extends K, ? extends V> map) {
        for (Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    public void putAll(int index, Map<? extends K, ? extends V> map) {
        if (index < 0 || index > this.insertOrder.size()) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + this.insertOrder.size());
        }
        for (Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
            K key = entry.getKey();
            boolean contains = containsKey(key);
            put(index, entry.getKey(), entry.getValue());
            if (!contains) {
                index++;
            } else {
                index = indexOf(entry.getKey()) + 1;
            }
        }
    }

    @Override // org.apache.commons.collections4.map.AbstractMapDecorator, java.util.Map, org.apache.commons.collections4.Get
    public V remove(Object key) {
        if (!decorated().containsKey(key)) {
            return null;
        }
        V result = decorated().remove(key);
        this.insertOrder.remove(key);
        return result;
    }

    @Override // org.apache.commons.collections4.map.AbstractMapDecorator, java.util.Map, org.apache.commons.collections4.Put
    public void clear() {
        decorated().clear();
        this.insertOrder.clear();
    }

    @Override // org.apache.commons.collections4.map.AbstractMapDecorator, java.util.Map, org.apache.commons.collections4.Get
    public Set<K> keySet() {
        return new KeySetView(this);
    }

    public List<K> keyList() {
        return UnmodifiableList.unmodifiableList(this.insertOrder);
    }

    @Override // org.apache.commons.collections4.map.AbstractMapDecorator, java.util.Map, org.apache.commons.collections4.Get
    public Collection<V> values() {
        return new ValuesView(this);
    }

    public List<V> valueList() {
        return new ValuesView(this);
    }

    @Override // org.apache.commons.collections4.map.AbstractMapDecorator, java.util.Map, org.apache.commons.collections4.Get
    public Set<Map.Entry<K, V>> entrySet() {
        return new EntrySetView(this, this.insertOrder);
    }

    @Override // org.apache.commons.collections4.map.AbstractMapDecorator
    public String toString() {
        if (isEmpty()) {
            return "{}";
        }
        StringBuilder buf = new StringBuilder();
        buf.append('{');
        boolean first = true;
        for (Map.Entry<K, V> entry : entrySet()) {
            K key = entry.getKey();
            V value = entry.getValue();
            if (first) {
                first = false;
            } else {
                buf.append(", ");
            }
            Object obj = "(this Map)";
            buf.append(key == this ? "(this Map)" : key);
            buf.append('=');
            if (value != this) {
                obj = value;
            }
            buf.append(obj);
        }
        buf.append('}');
        return buf.toString();
    }

    public K get(int index) {
        return this.insertOrder.get(index);
    }

    public V getValue(int index) {
        return get(this.insertOrder.get(index));
    }

    public int indexOf(Object key) {
        return this.insertOrder.indexOf(key);
    }

    public V setValue(int index, V value) {
        K key = this.insertOrder.get(index);
        return put(key, value);
    }

    public V put(int index, K key, V value) {
        if (index < 0 || index > this.insertOrder.size()) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + this.insertOrder.size());
        }
        Map<K, V> m = decorated();
        if (m.containsKey(key)) {
            V result = m.remove(key);
            int pos = this.insertOrder.indexOf(key);
            this.insertOrder.remove(pos);
            if (pos < index) {
                index--;
            }
            this.insertOrder.add(index, key);
            m.put(key, value);
            return result;
        }
        this.insertOrder.add(index, key);
        m.put(key, value);
        return null;
    }

    public V remove(int index) {
        return remove(get(index));
    }

    public List<K> asList() {
        return keyList();
    }

    static class ValuesView<V> extends AbstractList<V> {
        private final ListOrderedMap<Object, V> parent;

        ValuesView(ListOrderedMap<?, V> parent) {
            this.parent = parent;
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
        public int size() {
            return this.parent.size();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
        public boolean contains(Object value) {
            return this.parent.containsValue(value);
        }

        @Override // java.util.AbstractList, java.util.AbstractCollection, java.util.Collection, java.util.List
        public void clear() {
            this.parent.clear();
        }

        @Override // java.util.AbstractList, java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.List
        public Iterator<V> iterator() {
            return new AbstractUntypedIteratorDecorator<Map.Entry<Object, V>, V>(this.parent.entrySet().iterator()) { // from class: org.apache.commons.collections4.map.ListOrderedMap.ValuesView.1
                @Override // java.util.Iterator
                public V next() {
                    return getIterator().next().getValue();
                }
            };
        }

        @Override // java.util.AbstractList, java.util.List
        public V get(int index) {
            return this.parent.getValue(index);
        }

        @Override // java.util.AbstractList, java.util.List
        public V set(int index, V value) {
            return this.parent.setValue(index, value);
        }

        @Override // java.util.AbstractList, java.util.List
        public V remove(int index) {
            return this.parent.remove(index);
        }
    }

    static class KeySetView<K> extends AbstractSet<K> {
        private final ListOrderedMap<K, Object> parent;

        KeySetView(ListOrderedMap<K, ?> parent) {
            this.parent = parent;
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public int size() {
            return this.parent.size();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public boolean contains(Object value) {
            return this.parent.containsKey(value);
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public void clear() {
            this.parent.clear();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Set
        public Iterator<K> iterator() {
            return new AbstractUntypedIteratorDecorator<Map.Entry<K, Object>, K>(this.parent.entrySet().iterator()) { // from class: org.apache.commons.collections4.map.ListOrderedMap.KeySetView.1
                @Override // java.util.Iterator
                public K next() {
                    return getIterator().next().getKey();
                }
            };
        }
    }

    static class EntrySetView<K, V> extends AbstractSet<Map.Entry<K, V>> {
        private Set<Map.Entry<K, V>> entrySet;
        private final List<K> insertOrder;
        private final ListOrderedMap<K, V> parent;

        public EntrySetView(ListOrderedMap<K, V> parent, List<K> insertOrder) {
            this.parent = parent;
            this.insertOrder = insertOrder;
        }

        private Set<Map.Entry<K, V>> getEntrySet() {
            if (this.entrySet == null) {
                this.entrySet = this.parent.decorated().entrySet();
            }
            return this.entrySet;
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public int size() {
            return this.parent.size();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public boolean isEmpty() {
            return this.parent.isEmpty();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public boolean contains(Object obj) {
            return getEntrySet().contains(obj);
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public boolean containsAll(Collection<?> coll) {
            return getEntrySet().containsAll(coll);
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public boolean remove(Object obj) {
            if (!(obj instanceof Map.Entry) || !getEntrySet().contains(obj)) {
                return false;
            }
            Object key = ((Map.Entry) obj).getKey();
            this.parent.remove(key);
            return true;
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public void clear() {
            this.parent.clear();
        }

        @Override // java.util.AbstractSet, java.util.Collection, java.util.Set
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            return getEntrySet().equals(obj);
        }

        @Override // java.util.AbstractSet, java.util.Collection, java.util.Set
        public int hashCode() {
            return getEntrySet().hashCode();
        }

        @Override // java.util.AbstractCollection
        public String toString() {
            return getEntrySet().toString();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Set
        public Iterator<Map.Entry<K, V>> iterator() {
            return new ListOrderedIterator(this.parent, this.insertOrder);
        }
    }

    static class ListOrderedIterator<K, V> extends AbstractUntypedIteratorDecorator<K, Map.Entry<K, V>> {
        private K last;
        private final ListOrderedMap<K, V> parent;

        ListOrderedIterator(ListOrderedMap<K, V> parent, List<K> insertOrder) {
            super(insertOrder.iterator());
            this.last = null;
            this.parent = parent;
        }

        @Override // java.util.Iterator
        public Map.Entry<K, V> next() {
            this.last = getIterator().next();
            return new ListOrderedMapEntry(this.parent, this.last);
        }

        @Override // org.apache.commons.collections4.iterators.AbstractUntypedIteratorDecorator, java.util.Iterator
        public void remove() {
            super.remove();
            this.parent.decorated().remove(this.last);
        }
    }

    static class ListOrderedMapEntry<K, V> extends AbstractMapEntry<K, V> {
        private final ListOrderedMap<K, V> parent;

        ListOrderedMapEntry(ListOrderedMap<K, V> parent, K key) {
            super(key, null);
            this.parent = parent;
        }

        @Override // org.apache.commons.collections4.keyvalue.AbstractKeyValue, org.apache.commons.collections4.KeyValue
        public V getValue() {
            return this.parent.get(getKey());
        }

        @Override // org.apache.commons.collections4.keyvalue.AbstractMapEntry, org.apache.commons.collections4.keyvalue.AbstractKeyValue, java.util.Map.Entry
        public V setValue(V value) {
            return this.parent.decorated().put(getKey(), value);
        }
    }

    static class ListOrderedMapIterator<K, V> implements OrderedMapIterator<K, V>, ResettableIterator<K> {
        private ListIterator<K> iterator;
        private final ListOrderedMap<K, V> parent;
        private K last = null;
        private boolean readable = false;

        ListOrderedMapIterator(ListOrderedMap<K, V> parent) {
            this.parent = parent;
            this.iterator = ((ListOrderedMap) parent).insertOrder.listIterator();
        }

        @Override // org.apache.commons.collections4.MapIterator, java.util.Iterator
        public boolean hasNext() {
            return this.iterator.hasNext();
        }

        @Override // org.apache.commons.collections4.MapIterator, java.util.Iterator
        public K next() {
            K next = this.iterator.next();
            this.last = next;
            this.readable = true;
            return next;
        }

        @Override // org.apache.commons.collections4.OrderedMapIterator, org.apache.commons.collections4.OrderedIterator
        public boolean hasPrevious() {
            return this.iterator.hasPrevious();
        }

        @Override // org.apache.commons.collections4.OrderedMapIterator, org.apache.commons.collections4.OrderedIterator
        public K previous() {
            K kPrevious = this.iterator.previous();
            this.last = kPrevious;
            this.readable = true;
            return kPrevious;
        }

        @Override // org.apache.commons.collections4.MapIterator, java.util.Iterator
        public void remove() {
            if (!this.readable) {
                throw new IllegalStateException("remove() can only be called once after next()");
            }
            this.iterator.remove();
            this.parent.map.remove(this.last);
            this.readable = false;
        }

        @Override // org.apache.commons.collections4.MapIterator
        public K getKey() {
            if (!this.readable) {
                throw new IllegalStateException("getKey() can only be called after next() and before remove()");
            }
            return this.last;
        }

        @Override // org.apache.commons.collections4.MapIterator
        public V getValue() {
            if (!this.readable) {
                throw new IllegalStateException("getValue() can only be called after next() and before remove()");
            }
            return this.parent.get(this.last);
        }

        @Override // org.apache.commons.collections4.MapIterator
        public V setValue(V value) {
            if (!this.readable) {
                throw new IllegalStateException("setValue() can only be called after next() and before remove()");
            }
            return this.parent.map.put(this.last, value);
        }

        @Override // org.apache.commons.collections4.ResettableIterator
        public void reset() {
            this.iterator = ((ListOrderedMap) this.parent).insertOrder.listIterator();
            this.last = null;
            this.readable = false;
        }

        public String toString() {
            if (this.readable) {
                return "Iterator[" + getKey() + "=" + getValue() + "]";
            }
            return "Iterator[]";
        }
    }
}
