package org.apache.commons.collections4.map;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import org.apache.commons.collections4.OrderedIterator;
import org.apache.commons.collections4.OrderedMap;
import org.apache.commons.collections4.OrderedMapIterator;
import org.apache.commons.collections4.ResettableIterator;
import org.apache.commons.collections4.iterators.EmptyOrderedIterator;
import org.apache.commons.collections4.iterators.EmptyOrderedMapIterator;
import org.apache.commons.collections4.map.AbstractHashedMap;

/* JADX INFO: loaded from: classes.dex */
public abstract class AbstractLinkedMap<K, V> extends AbstractHashedMap<K, V> implements OrderedMap<K, V> {
    transient LinkEntry<K, V> header;

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.apache.commons.collections4.map.AbstractHashedMap
    protected /* bridge */ /* synthetic */ AbstractHashedMap.HashEntry createEntry(AbstractHashedMap.HashEntry hashEntry, int i, Object obj, Object obj2) {
        return createEntry((AbstractHashedMap.HashEntry<Object, Object>) hashEntry, i, obj, obj2);
    }

    protected AbstractLinkedMap() {
    }

    protected AbstractLinkedMap(int initialCapacity, float loadFactor, int threshold) {
        super(initialCapacity, loadFactor, threshold);
    }

    protected AbstractLinkedMap(int initialCapacity) {
        super(initialCapacity);
    }

    protected AbstractLinkedMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    protected AbstractLinkedMap(Map<? extends K, ? extends V> map) {
        super(map);
    }

    @Override // org.apache.commons.collections4.map.AbstractHashedMap
    protected void init() {
        LinkEntry<K, V> linkEntryCreateEntry = createEntry((AbstractHashedMap.HashEntry) null, -1, (Object) null, (Object) null);
        this.header = linkEntryCreateEntry;
        linkEntryCreateEntry.after = linkEntryCreateEntry;
        linkEntryCreateEntry.before = linkEntryCreateEntry;
    }

    @Override // org.apache.commons.collections4.map.AbstractHashedMap, java.util.AbstractMap, java.util.Map, org.apache.commons.collections4.Get
    public boolean containsValue(Object value) {
        if (value == null) {
            for (LinkEntry<K, V> entry = this.header.after; entry != this.header; entry = entry.after) {
                if (entry.getValue() == null) {
                    return true;
                }
            }
            return false;
        }
        for (LinkEntry<K, V> entry2 = this.header.after; entry2 != this.header; entry2 = entry2.after) {
            if (isEqualValue(value, entry2.getValue())) {
                return true;
            }
        }
        return false;
    }

    @Override // org.apache.commons.collections4.map.AbstractHashedMap, java.util.AbstractMap, java.util.Map, org.apache.commons.collections4.Put
    public void clear() {
        super.clear();
        LinkEntry<K, V> linkEntry = this.header;
        linkEntry.after = linkEntry;
        linkEntry.before = linkEntry;
    }

    @Override // org.apache.commons.collections4.OrderedMap
    public K firstKey() {
        if (this.size == 0) {
            throw new NoSuchElementException("Map is empty");
        }
        return this.header.after.getKey();
    }

    @Override // org.apache.commons.collections4.OrderedMap
    public K lastKey() {
        if (this.size == 0) {
            throw new NoSuchElementException("Map is empty");
        }
        return this.header.before.getKey();
    }

    @Override // org.apache.commons.collections4.OrderedMap
    public K nextKey(Object key) {
        LinkEntry<K, V> entry = getEntry(key);
        if (entry == null || entry.after == this.header) {
            return null;
        }
        return entry.after.getKey();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.commons.collections4.map.AbstractHashedMap
    public LinkEntry<K, V> getEntry(Object key) {
        return (LinkEntry) super.getEntry(key);
    }

    @Override // org.apache.commons.collections4.OrderedMap
    public K previousKey(Object key) {
        LinkEntry<K, V> entry = getEntry(key);
        if (entry == null || entry.before == this.header) {
            return null;
        }
        return entry.before.getKey();
    }

    protected LinkEntry<K, V> getEntry(int index) {
        LinkEntry<K, V> entry;
        if (index < 0) {
            throw new IndexOutOfBoundsException("Index " + index + " is less than zero");
        }
        if (index >= this.size) {
            throw new IndexOutOfBoundsException("Index " + index + " is invalid for size " + this.size);
        }
        if (index < this.size / 2) {
            entry = this.header.after;
            for (int currentIndex = 0; currentIndex < index; currentIndex++) {
                entry = entry.after;
            }
        } else {
            entry = this.header;
            for (int currentIndex2 = this.size; currentIndex2 > index; currentIndex2--) {
                entry = entry.before;
            }
        }
        return entry;
    }

    @Override // org.apache.commons.collections4.map.AbstractHashedMap
    protected void addEntry(AbstractHashedMap.HashEntry<K, V> entry, int hashIndex) {
        LinkEntry<K, V> link = (LinkEntry) entry;
        link.after = this.header;
        link.before = this.header.before;
        this.header.before.after = link;
        this.header.before = link;
        this.data[hashIndex] = link;
    }

    @Override // org.apache.commons.collections4.map.AbstractHashedMap
    protected LinkEntry<K, V> createEntry(AbstractHashedMap.HashEntry<K, V> next, int hashCode, K key, V value) {
        return new LinkEntry<>(next, hashCode, convertKey(key), value);
    }

    @Override // org.apache.commons.collections4.map.AbstractHashedMap
    protected void removeEntry(AbstractHashedMap.HashEntry<K, V> entry, int hashIndex, AbstractHashedMap.HashEntry<K, V> previous) {
        LinkEntry<K, V> link = (LinkEntry) entry;
        link.before.after = link.after;
        link.after.before = link.before;
        link.after = null;
        link.before = null;
        super.removeEntry(entry, hashIndex, previous);
    }

    protected LinkEntry<K, V> entryBefore(LinkEntry<K, V> entry) {
        return entry.before;
    }

    protected LinkEntry<K, V> entryAfter(LinkEntry<K, V> entry) {
        return entry.after;
    }

    @Override // org.apache.commons.collections4.map.AbstractHashedMap, org.apache.commons.collections4.IterableGet
    public OrderedMapIterator<K, V> mapIterator() {
        if (this.size == 0) {
            return EmptyOrderedMapIterator.emptyOrderedMapIterator();
        }
        return new LinkMapIterator(this);
    }

    protected static class LinkMapIterator<K, V> extends LinkIterator<K, V> implements OrderedMapIterator<K, V>, ResettableIterator<K> {
        protected LinkMapIterator(AbstractLinkedMap<K, V> parent) {
            super(parent);
        }

        @Override // org.apache.commons.collections4.MapIterator, java.util.Iterator
        public K next() {
            return super.nextEntry().getKey();
        }

        @Override // org.apache.commons.collections4.OrderedMapIterator, org.apache.commons.collections4.OrderedIterator
        public K previous() {
            return super.previousEntry().getKey();
        }

        @Override // org.apache.commons.collections4.MapIterator
        public K getKey() {
            LinkEntry<K, V> current = currentEntry();
            if (current == null) {
                throw new IllegalStateException("getKey() can only be called after next() and before remove()");
            }
            return current.getKey();
        }

        @Override // org.apache.commons.collections4.MapIterator
        public V getValue() {
            LinkEntry<K, V> current = currentEntry();
            if (current == null) {
                throw new IllegalStateException("getValue() can only be called after next() and before remove()");
            }
            return current.getValue();
        }

        @Override // org.apache.commons.collections4.MapIterator
        public V setValue(V value) {
            LinkEntry<K, V> current = currentEntry();
            if (current == null) {
                throw new IllegalStateException("setValue() can only be called after next() and before remove()");
            }
            return current.setValue(value);
        }
    }

    @Override // org.apache.commons.collections4.map.AbstractHashedMap
    protected Iterator<Map.Entry<K, V>> createEntrySetIterator() {
        if (size() == 0) {
            return EmptyOrderedIterator.emptyOrderedIterator();
        }
        return new EntrySetIterator(this);
    }

    protected static class EntrySetIterator<K, V> extends LinkIterator<K, V> implements OrderedIterator<Map.Entry<K, V>>, ResettableIterator<Map.Entry<K, V>> {
        protected EntrySetIterator(AbstractLinkedMap<K, V> parent) {
            super(parent);
        }

        @Override // java.util.Iterator
        public Map.Entry<K, V> next() {
            return super.nextEntry();
        }

        @Override // org.apache.commons.collections4.OrderedIterator
        public Map.Entry<K, V> previous() {
            return super.previousEntry();
        }
    }

    @Override // org.apache.commons.collections4.map.AbstractHashedMap
    protected Iterator<K> createKeySetIterator() {
        if (size() == 0) {
            return EmptyOrderedIterator.emptyOrderedIterator();
        }
        return new KeySetIterator(this);
    }

    protected static class KeySetIterator<K> extends LinkIterator<K, Object> implements OrderedIterator<K>, ResettableIterator<K> {
        protected KeySetIterator(AbstractLinkedMap<K, ?> parent) {
            super(parent);
        }

        @Override // java.util.Iterator
        public K next() {
            return super.nextEntry().getKey();
        }

        @Override // org.apache.commons.collections4.OrderedIterator
        public K previous() {
            return super.previousEntry().getKey();
        }
    }

    @Override // org.apache.commons.collections4.map.AbstractHashedMap
    protected Iterator<V> createValuesIterator() {
        if (size() == 0) {
            return EmptyOrderedIterator.emptyOrderedIterator();
        }
        return new ValuesIterator(this);
    }

    protected static class ValuesIterator<V> extends LinkIterator<Object, V> implements OrderedIterator<V>, ResettableIterator<V> {
        protected ValuesIterator(AbstractLinkedMap<?, V> parent) {
            super(parent);
        }

        @Override // java.util.Iterator
        public V next() {
            return super.nextEntry().getValue();
        }

        @Override // org.apache.commons.collections4.OrderedIterator
        public V previous() {
            return super.previousEntry().getValue();
        }
    }

    protected static class LinkEntry<K, V> extends AbstractHashedMap.HashEntry<K, V> {
        protected LinkEntry<K, V> after;
        protected LinkEntry<K, V> before;

        protected LinkEntry(AbstractHashedMap.HashEntry<K, V> next, int hashCode, Object key, V value) {
            super(next, hashCode, key, value);
        }
    }

    protected static abstract class LinkIterator<K, V> {
        protected int expectedModCount;
        protected LinkEntry<K, V> last;
        protected LinkEntry<K, V> next;
        protected final AbstractLinkedMap<K, V> parent;

        protected LinkIterator(AbstractLinkedMap<K, V> parent) {
            this.parent = parent;
            this.next = parent.header.after;
            this.expectedModCount = parent.modCount;
        }

        public boolean hasNext() {
            return this.next != this.parent.header;
        }

        public boolean hasPrevious() {
            return this.next.before != this.parent.header;
        }

        protected LinkEntry<K, V> nextEntry() {
            if (this.parent.modCount != this.expectedModCount) {
                throw new ConcurrentModificationException();
            }
            if (this.next == this.parent.header) {
                throw new NoSuchElementException("No next() entry in the iteration");
            }
            LinkEntry<K, V> linkEntry = this.next;
            this.last = linkEntry;
            this.next = linkEntry.after;
            return this.last;
        }

        protected LinkEntry<K, V> previousEntry() {
            if (this.parent.modCount != this.expectedModCount) {
                throw new ConcurrentModificationException();
            }
            LinkEntry<K, V> previous = this.next.before;
            if (previous == this.parent.header) {
                throw new NoSuchElementException("No previous() entry in the iteration");
            }
            this.next = previous;
            this.last = previous;
            return previous;
        }

        protected LinkEntry<K, V> currentEntry() {
            return this.last;
        }

        public void remove() {
            if (this.last == null) {
                throw new IllegalStateException("remove() can only be called once after next()");
            }
            if (this.parent.modCount != this.expectedModCount) {
                throw new ConcurrentModificationException();
            }
            this.parent.remove(this.last.getKey());
            this.last = null;
            this.expectedModCount = this.parent.modCount;
        }

        public void reset() {
            this.last = null;
            this.next = this.parent.header.after;
        }

        public String toString() {
            if (this.last != null) {
                return "Iterator[" + this.last.getKey() + "=" + this.last.getValue() + "]";
            }
            return "Iterator[]";
        }
    }
}
