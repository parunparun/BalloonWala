package org.apache.commons.collections4.bidimap;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.OrderedBidiMap;
import org.apache.commons.collections4.OrderedMap;
import org.apache.commons.collections4.OrderedMapIterator;
import org.apache.commons.collections4.ResettableIterator;
import org.apache.commons.collections4.SortedBidiMap;
import org.apache.commons.collections4.map.AbstractSortedMapDecorator;

/* JADX INFO: loaded from: classes.dex */
public class DualTreeBidiMap<K, V> extends AbstractDualBidiMap<K, V> implements SortedBidiMap<K, V>, Serializable {
    private static final long serialVersionUID = 721969328361809L;
    private final Comparator<? super K> comparator;
    private final Comparator<? super V> valueComparator;

    public DualTreeBidiMap() {
        super(new TreeMap(), new TreeMap());
        this.comparator = null;
        this.valueComparator = null;
    }

    public DualTreeBidiMap(Map<? extends K, ? extends V> map) {
        super(new TreeMap(), new TreeMap());
        putAll(map);
        this.comparator = null;
        this.valueComparator = null;
    }

    public DualTreeBidiMap(Comparator<? super K> keyComparator, Comparator<? super V> valueComparator) {
        super(new TreeMap(keyComparator), new TreeMap(valueComparator));
        this.comparator = keyComparator;
        this.valueComparator = valueComparator;
    }

    protected DualTreeBidiMap(Map<K, V> normalMap, Map<V, K> reverseMap, BidiMap<V, K> inverseBidiMap) {
        super(normalMap, reverseMap, inverseBidiMap);
        this.comparator = ((SortedMap) normalMap).comparator();
        this.valueComparator = ((SortedMap) reverseMap).comparator();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.commons.collections4.bidimap.AbstractDualBidiMap
    public DualTreeBidiMap<V, K> createBidiMap(Map<V, K> normalMap, Map<K, V> reverseMap, BidiMap<K, V> inverseMap) {
        return new DualTreeBidiMap<>(normalMap, reverseMap, inverseMap);
    }

    @Override // java.util.SortedMap
    public Comparator<? super K> comparator() {
        return ((SortedMap) this.normalMap).comparator();
    }

    @Override // org.apache.commons.collections4.SortedBidiMap
    public Comparator<? super V> valueComparator() {
        return ((SortedMap) this.reverseMap).comparator();
    }

    @Override // org.apache.commons.collections4.OrderedMap
    public K firstKey() {
        return (K) ((SortedMap) this.normalMap).firstKey();
    }

    @Override // org.apache.commons.collections4.OrderedMap
    public K lastKey() {
        return (K) ((SortedMap) this.normalMap).lastKey();
    }

    @Override // org.apache.commons.collections4.OrderedMap
    public K nextKey(K k) {
        if (isEmpty()) {
            return null;
        }
        if (this.normalMap instanceof OrderedMap) {
            return (K) ((OrderedMap) this.normalMap).nextKey(k);
        }
        Iterator<K> it = ((SortedMap) this.normalMap).tailMap(k).keySet().iterator();
        it.next();
        if (it.hasNext()) {
            return it.next();
        }
        return null;
    }

    @Override // org.apache.commons.collections4.OrderedMap
    public K previousKey(K k) {
        if (isEmpty()) {
            return null;
        }
        if (this.normalMap instanceof OrderedMap) {
            return (K) ((OrderedMap) this.normalMap).previousKey(k);
        }
        SortedMap<K, V> sortedMapHeadMap = ((SortedMap) this.normalMap).headMap(k);
        if (sortedMapHeadMap.isEmpty()) {
            return null;
        }
        return sortedMapHeadMap.lastKey();
    }

    @Override // org.apache.commons.collections4.bidimap.AbstractDualBidiMap, org.apache.commons.collections4.IterableGet
    public OrderedMapIterator<K, V> mapIterator() {
        return new BidiOrderedMapIterator(this);
    }

    public SortedBidiMap<V, K> inverseSortedBidiMap() {
        return inverseBidiMap();
    }

    public OrderedBidiMap<V, K> inverseOrderedBidiMap() {
        return inverseBidiMap();
    }

    @Override // java.util.SortedMap
    public SortedMap<K, V> headMap(K toKey) {
        SortedMap<K, V> sub = ((SortedMap) this.normalMap).headMap(toKey);
        return new ViewMap(this, sub);
    }

    @Override // java.util.SortedMap
    public SortedMap<K, V> tailMap(K fromKey) {
        SortedMap<K, V> sub = ((SortedMap) this.normalMap).tailMap(fromKey);
        return new ViewMap(this, sub);
    }

    @Override // java.util.SortedMap
    public SortedMap<K, V> subMap(K fromKey, K toKey) {
        SortedMap<K, V> sub = ((SortedMap) this.normalMap).subMap(fromKey, toKey);
        return new ViewMap(this, sub);
    }

    @Override // org.apache.commons.collections4.bidimap.AbstractDualBidiMap, org.apache.commons.collections4.BidiMap
    public SortedBidiMap<V, K> inverseBidiMap() {
        return (SortedBidiMap) super.inverseBidiMap();
    }

    protected static class ViewMap<K, V> extends AbstractSortedMapDecorator<K, V> {
        protected ViewMap(DualTreeBidiMap<K, V> bidi, SortedMap<K, V> sm) {
            super(new DualTreeBidiMap(sm, bidi.reverseMap, bidi.inverseBidiMap));
        }

        @Override // org.apache.commons.collections4.map.AbstractMapDecorator, java.util.Map, org.apache.commons.collections4.Get
        public boolean containsValue(Object value) {
            return decorated().normalMap.containsValue(value);
        }

        @Override // org.apache.commons.collections4.map.AbstractMapDecorator, java.util.Map, org.apache.commons.collections4.Put
        public void clear() {
            Iterator<K> it = keySet().iterator();
            while (it.hasNext()) {
                it.next();
                it.remove();
            }
        }

        @Override // org.apache.commons.collections4.map.AbstractSortedMapDecorator, java.util.SortedMap
        public SortedMap<K, V> headMap(K toKey) {
            return new ViewMap(decorated(), super.headMap(toKey));
        }

        @Override // org.apache.commons.collections4.map.AbstractSortedMapDecorator, java.util.SortedMap
        public SortedMap<K, V> tailMap(K fromKey) {
            return new ViewMap(decorated(), super.tailMap(fromKey));
        }

        @Override // org.apache.commons.collections4.map.AbstractSortedMapDecorator, java.util.SortedMap
        public SortedMap<K, V> subMap(K fromKey, K toKey) {
            return new ViewMap(decorated(), super.subMap(fromKey, toKey));
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.apache.commons.collections4.map.AbstractSortedMapDecorator, org.apache.commons.collections4.map.AbstractMapDecorator
        public DualTreeBidiMap<K, V> decorated() {
            return (DualTreeBidiMap) super.decorated();
        }

        @Override // org.apache.commons.collections4.map.AbstractSortedMapDecorator, org.apache.commons.collections4.OrderedMap
        public K previousKey(K key) {
            return decorated().previousKey(key);
        }

        @Override // org.apache.commons.collections4.map.AbstractSortedMapDecorator, org.apache.commons.collections4.OrderedMap
        public K nextKey(K key) {
            return decorated().nextKey(key);
        }
    }

    protected static class BidiOrderedMapIterator<K, V> implements OrderedMapIterator<K, V>, ResettableIterator<K> {
        private ListIterator<Map.Entry<K, V>> iterator;
        private Map.Entry<K, V> last = null;
        private final AbstractDualBidiMap<K, V> parent;

        protected BidiOrderedMapIterator(AbstractDualBidiMap<K, V> parent) {
            this.parent = parent;
            this.iterator = new ArrayList(parent.entrySet()).listIterator();
        }

        @Override // org.apache.commons.collections4.MapIterator, java.util.Iterator
        public boolean hasNext() {
            return this.iterator.hasNext();
        }

        @Override // org.apache.commons.collections4.MapIterator, java.util.Iterator
        public K next() {
            Map.Entry<K, V> next = this.iterator.next();
            this.last = next;
            return next.getKey();
        }

        @Override // org.apache.commons.collections4.OrderedMapIterator, org.apache.commons.collections4.OrderedIterator
        public boolean hasPrevious() {
            return this.iterator.hasPrevious();
        }

        @Override // org.apache.commons.collections4.OrderedMapIterator, org.apache.commons.collections4.OrderedIterator
        public K previous() {
            Map.Entry<K, V> entryPrevious = this.iterator.previous();
            this.last = entryPrevious;
            return entryPrevious.getKey();
        }

        @Override // org.apache.commons.collections4.MapIterator, java.util.Iterator
        public void remove() {
            this.iterator.remove();
            this.parent.remove(this.last.getKey());
            this.last = null;
        }

        @Override // org.apache.commons.collections4.MapIterator
        public K getKey() {
            Map.Entry<K, V> entry = this.last;
            if (entry == null) {
                throw new IllegalStateException("Iterator getKey() can only be called after next() and before remove()");
            }
            return entry.getKey();
        }

        @Override // org.apache.commons.collections4.MapIterator
        public V getValue() {
            Map.Entry<K, V> entry = this.last;
            if (entry == null) {
                throw new IllegalStateException("Iterator getValue() can only be called after next() and before remove()");
            }
            return entry.getValue();
        }

        @Override // org.apache.commons.collections4.MapIterator
        public V setValue(V v) {
            if (this.last == null) {
                throw new IllegalStateException("Iterator setValue() can only be called after next() and before remove()");
            }
            if (this.parent.reverseMap.containsKey(v) && this.parent.reverseMap.get(v) != this.last.getKey()) {
                throw new IllegalArgumentException("Cannot use setValue() when the object being set is already in the map");
            }
            V v2 = (V) this.parent.put(this.last.getKey(), v);
            this.last.setValue(v);
            return v2;
        }

        @Override // org.apache.commons.collections4.ResettableIterator
        public void reset() {
            this.iterator = new ArrayList(this.parent.entrySet()).listIterator();
            this.last = null;
        }

        public String toString() {
            if (this.last != null) {
                return "MapIterator[" + getKey() + "=" + getValue() + "]";
            }
            return "MapIterator[]";
        }
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(this.normalMap);
    }

    private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
        in.defaultReadObject();
        this.normalMap = new TreeMap(this.comparator);
        this.reverseMap = new TreeMap(this.valueComparator);
        putAll((Map) in.readObject());
    }
}
