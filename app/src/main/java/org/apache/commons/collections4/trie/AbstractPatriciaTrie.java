package org.apache.commons.collections4.trie;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;
import org.apache.commons.collections4.OrderedMapIterator;
import org.apache.commons.collections4.trie.AbstractBitwiseTrie;

/* JADX INFO: loaded from: classes.dex */
abstract class AbstractPatriciaTrie<K, V> extends AbstractBitwiseTrie<K, V> {
    private static final long serialVersionUID = 5155253417231339498L;
    private volatile transient Set<Map.Entry<K, V>> entrySet;
    private volatile transient Set<K> keySet;
    protected transient int modCount;
    private transient TrieEntry<K, V> root;
    private transient int size;
    private volatile transient Collection<V> values;

    protected AbstractPatriciaTrie(KeyAnalyzer<? super K> keyAnalyzer) {
        super(keyAnalyzer);
        this.root = new TrieEntry<>(null, null, -1);
        this.size = 0;
        this.modCount = 0;
    }

    protected AbstractPatriciaTrie(KeyAnalyzer<? super K> keyAnalyzer, Map<? extends K, ? extends V> map) {
        super(keyAnalyzer);
        this.root = new TrieEntry<>(null, null, -1);
        this.size = 0;
        this.modCount = 0;
        putAll(map);
    }

    @Override // java.util.AbstractMap, java.util.Map, org.apache.commons.collections4.Put
    public void clear() {
        this.root.key = null;
        this.root.bitIndex = -1;
        this.root.value = null;
        this.root.parent = null;
        TrieEntry<K, V> trieEntry = this.root;
        trieEntry.left = trieEntry;
        this.root.right = null;
        TrieEntry<K, V> trieEntry2 = this.root;
        trieEntry2.predecessor = trieEntry2;
        this.size = 0;
        incrementModCount();
    }

    @Override // java.util.AbstractMap, java.util.Map, org.apache.commons.collections4.Get
    public int size() {
        return this.size;
    }

    void incrementSize() {
        this.size++;
        incrementModCount();
    }

    void decrementSize() {
        this.size--;
        incrementModCount();
    }

    private void incrementModCount() {
        this.modCount++;
    }

    @Override // java.util.AbstractMap, java.util.Map, org.apache.commons.collections4.Put
    public V put(K key, V value) {
        if (key == null) {
            throw new NullPointerException("Key cannot be null");
        }
        int lengthInBits = lengthInBits(key);
        if (lengthInBits == 0) {
            if (this.root.isEmpty()) {
                incrementSize();
            } else {
                incrementModCount();
            }
            return this.root.setKeyValue(key, value);
        }
        TrieEntry<K, V> found = getNearestEntryForKey(key, lengthInBits);
        if (compareKeys(key, found.key)) {
            if (found.isEmpty()) {
                incrementSize();
            } else {
                incrementModCount();
            }
            return found.setKeyValue(key, value);
        }
        int bitIndex = bitIndex(key, found.key);
        if (!KeyAnalyzer.isOutOfBoundsIndex(bitIndex)) {
            if (KeyAnalyzer.isValidBitIndex(bitIndex)) {
                TrieEntry<K, V> t = new TrieEntry<>(key, value, bitIndex);
                addEntry(t, lengthInBits);
                incrementSize();
                return null;
            }
            if (KeyAnalyzer.isNullBitKey(bitIndex)) {
                if (this.root.isEmpty()) {
                    incrementSize();
                } else {
                    incrementModCount();
                }
                return this.root.setKeyValue(key, value);
            }
            if (KeyAnalyzer.isEqualBitKey(bitIndex) && found != this.root) {
                incrementModCount();
                return found.setKeyValue(key, value);
            }
        }
        throw new IllegalArgumentException("Failed to put: " + key + " -> " + value + ", " + bitIndex);
    }

    TrieEntry<K, V> addEntry(TrieEntry<K, V> entry, int lengthInBits) {
        TrieEntry<K, V> current = this.root.left;
        TrieEntry<K, V> path = this.root;
        while (current.bitIndex < entry.bitIndex && current.bitIndex > path.bitIndex) {
            path = current;
            if (!isBitSet(entry.key, current.bitIndex, lengthInBits)) {
                current = current.left;
            } else {
                current = current.right;
            }
        }
        entry.predecessor = entry;
        if (!isBitSet(entry.key, entry.bitIndex, lengthInBits)) {
            entry.left = entry;
            entry.right = current;
        } else {
            entry.left = current;
            entry.right = entry;
        }
        entry.parent = path;
        if (current.bitIndex >= entry.bitIndex) {
            current.parent = entry;
        }
        if (current.bitIndex <= path.bitIndex) {
            current.predecessor = entry;
        }
        if (path == this.root || !isBitSet(entry.key, path.bitIndex, lengthInBits)) {
            path.left = entry;
        } else {
            path.right = entry;
        }
        return entry;
    }

    @Override // java.util.AbstractMap, java.util.Map, org.apache.commons.collections4.Get
    public V get(Object k) {
        TrieEntry<K, V> entry = getEntry(k);
        if (entry != null) {
            return entry.getValue();
        }
        return null;
    }

    TrieEntry<K, V> getEntry(Object k) {
        K key = castKey(k);
        if (key == null) {
            return null;
        }
        int lengthInBits = lengthInBits(key);
        TrieEntry<K, V> entry = getNearestEntryForKey(key, lengthInBits);
        if (entry.isEmpty() || !compareKeys(key, entry.key)) {
            return null;
        }
        return entry;
    }

    public Map.Entry<K, V> select(K key) {
        int lengthInBits = lengthInBits(key);
        Reference<Map.Entry<K, V>> reference = new Reference<>();
        if (selectR(this.root.left, -1, key, lengthInBits, reference)) {
            return null;
        }
        return reference.get();
    }

    public K selectKey(K key) {
        Map.Entry<K, V> entry = select(key);
        if (entry == null) {
            return null;
        }
        return entry.getKey();
    }

    public V selectValue(K key) {
        Map.Entry<K, V> entry = select(key);
        if (entry == null) {
            return null;
        }
        return entry.getValue();
    }

    private boolean selectR(TrieEntry<K, V> h, int bitIndex, K key, int lengthInBits, Reference<Map.Entry<K, V>> reference) {
        if (h.bitIndex <= bitIndex) {
            if (!h.isEmpty()) {
                reference.set(h);
                return false;
            }
            return true;
        }
        if (!isBitSet(key, h.bitIndex, lengthInBits)) {
            if (selectR(h.left, h.bitIndex, key, lengthInBits, reference)) {
                return selectR(h.right, h.bitIndex, key, lengthInBits, reference);
            }
        } else if (selectR(h.right, h.bitIndex, key, lengthInBits, reference)) {
            return selectR(h.left, h.bitIndex, key, lengthInBits, reference);
        }
        return false;
    }

    @Override // java.util.AbstractMap, java.util.Map, org.apache.commons.collections4.Get
    public boolean containsKey(Object k) {
        if (k == null) {
            return false;
        }
        K key = castKey(k);
        int lengthInBits = lengthInBits(key);
        TrieEntry<K, V> entry = getNearestEntryForKey(key, lengthInBits);
        return !entry.isEmpty() && compareKeys(key, entry.key);
    }

    @Override // java.util.AbstractMap, java.util.Map, java.util.SortedMap, org.apache.commons.collections4.Get
    public Set<Map.Entry<K, V>> entrySet() {
        if (this.entrySet == null) {
            this.entrySet = new EntrySet();
        }
        return this.entrySet;
    }

    @Override // java.util.AbstractMap, java.util.Map, java.util.SortedMap, org.apache.commons.collections4.Get
    public Set<K> keySet() {
        if (this.keySet == null) {
            this.keySet = new KeySet();
        }
        return this.keySet;
    }

    @Override // java.util.AbstractMap, java.util.Map, java.util.SortedMap, org.apache.commons.collections4.Get
    public Collection<V> values() {
        if (this.values == null) {
            this.values = new Values();
        }
        return this.values;
    }

    @Override // java.util.AbstractMap, java.util.Map, org.apache.commons.collections4.Get
    public V remove(Object k) {
        if (k == null) {
            return null;
        }
        K key = castKey(k);
        int lengthInBits = lengthInBits(key);
        TrieEntry<K, V> current = this.root.left;
        TrieEntry<K, V> path = this.root;
        while (current.bitIndex > path.bitIndex) {
            path = current;
            if (!isBitSet(key, current.bitIndex, lengthInBits)) {
                current = current.left;
            } else {
                current = current.right;
            }
        }
        if (current.isEmpty() || !compareKeys(key, current.key)) {
            return null;
        }
        return removeEntry(current);
    }

    TrieEntry<K, V> getNearestEntryForKey(K key, int lengthInBits) {
        TrieEntry<K, V> current = this.root.left;
        TrieEntry<K, V> path = this.root;
        while (current.bitIndex > path.bitIndex) {
            path = current;
            if (!isBitSet(key, current.bitIndex, lengthInBits)) {
                current = current.left;
            } else {
                current = current.right;
            }
        }
        return current;
    }

    V removeEntry(TrieEntry<K, V> h) {
        if (h != this.root) {
            if (h.isInternalNode()) {
                removeInternalEntry(h);
            } else {
                removeExternalEntry(h);
            }
        }
        decrementSize();
        return h.setKeyValue(null, null);
    }

    private void removeExternalEntry(TrieEntry<K, V> h) {
        if (h == this.root) {
            throw new IllegalArgumentException("Cannot delete root Entry!");
        }
        if (!h.isExternalNode()) {
            throw new IllegalArgumentException(h + " is not an external Entry!");
        }
        TrieEntry<K, V> parent = h.parent;
        TrieEntry<K, V> child = h.left == h ? h.right : h.left;
        if (parent.left == h) {
            parent.left = child;
        } else {
            parent.right = child;
        }
        if (child.bitIndex > parent.bitIndex) {
            child.parent = parent;
        } else {
            child.predecessor = parent;
        }
    }

    private void removeInternalEntry(TrieEntry<K, V> h) {
        if (h == this.root) {
            throw new IllegalArgumentException("Cannot delete root Entry!");
        }
        if (!h.isInternalNode()) {
            throw new IllegalArgumentException(h + " is not an internal Entry!");
        }
        TrieEntry<K, V> p = h.predecessor;
        p.bitIndex = h.bitIndex;
        TrieEntry<K, V> parent = p.parent;
        TrieEntry<K, V> child = p.left == h ? p.right : p.left;
        if (p.predecessor == p && p.parent != h) {
            p.predecessor = p.parent;
        }
        if (parent.left == p) {
            parent.left = child;
        } else {
            parent.right = child;
        }
        if (child.bitIndex > parent.bitIndex) {
            child.parent = parent;
        }
        if (h.left.parent == h) {
            h.left.parent = p;
        }
        if (h.right.parent == h) {
            h.right.parent = p;
        }
        if (h.parent.left == h) {
            h.parent.left = p;
        } else {
            h.parent.right = p;
        }
        p.parent = h.parent;
        p.left = h.left;
        p.right = h.right;
        if (isValidUplink(p.left, p)) {
            p.left.predecessor = p;
        }
        if (isValidUplink(p.right, p)) {
            p.right.predecessor = p;
        }
    }

    TrieEntry<K, V> nextEntry(TrieEntry<K, V> node) {
        if (node == null) {
            return firstEntry();
        }
        return nextEntryImpl(node.predecessor, node, null);
    }

    TrieEntry<K, V> nextEntryImpl(TrieEntry<K, V> start, TrieEntry<K, V> previous, TrieEntry<K, V> tree) {
        TrieEntry<K, V> current = start;
        if (previous == null || start != previous.predecessor) {
            while (!current.left.isEmpty() && previous != current.left) {
                if (isValidUplink(current.left, current)) {
                    return current.left;
                }
                current = current.left;
            }
        }
        if (current.isEmpty() || current.right == null) {
            return null;
        }
        if (previous != current.right) {
            if (isValidUplink(current.right, current)) {
                return current.right;
            }
            return nextEntryImpl(current.right, previous, tree);
        }
        while (current == current.parent.right) {
            if (current == tree) {
                return null;
            }
            current = current.parent;
        }
        if (current == tree || current.parent.right == null) {
            return null;
        }
        if (previous != current.parent.right && isValidUplink(current.parent.right, current.parent)) {
            return current.parent.right;
        }
        if (current.parent.right == current.parent) {
            return null;
        }
        return nextEntryImpl(current.parent.right, previous, tree);
    }

    TrieEntry<K, V> firstEntry() {
        if (isEmpty()) {
            return null;
        }
        return followLeft(this.root);
    }

    TrieEntry<K, V> followLeft(TrieEntry<K, V> node) {
        while (true) {
            TrieEntry<K, V> child = node.left;
            if (child.isEmpty()) {
                child = node.right;
            }
            if (child.bitIndex <= node.bitIndex) {
                return child;
            }
            node = child;
        }
    }

    @Override // java.util.SortedMap
    public Comparator<? super K> comparator() {
        return getKeyAnalyzer();
    }

    @Override // java.util.SortedMap, org.apache.commons.collections4.OrderedMap
    public K firstKey() {
        if (size() == 0) {
            throw new NoSuchElementException();
        }
        return firstEntry().getKey();
    }

    @Override // java.util.SortedMap, org.apache.commons.collections4.OrderedMap
    public K lastKey() {
        TrieEntry<K, V> entry = lastEntry();
        if (entry != null) {
            return entry.getKey();
        }
        throw new NoSuchElementException();
    }

    @Override // org.apache.commons.collections4.OrderedMap
    public K nextKey(K key) {
        TrieEntry<K, V> nextEntry;
        if (key == null) {
            throw null;
        }
        TrieEntry<K, V> entry = getEntry(key);
        if (entry == null || (nextEntry = nextEntry(entry)) == null) {
            return null;
        }
        return nextEntry.getKey();
    }

    @Override // org.apache.commons.collections4.OrderedMap
    public K previousKey(K key) {
        TrieEntry<K, V> prevEntry;
        if (key == null) {
            throw null;
        }
        TrieEntry<K, V> entry = getEntry(key);
        if (entry == null || (prevEntry = previousEntry(entry)) == null) {
            return null;
        }
        return prevEntry.getKey();
    }

    @Override // org.apache.commons.collections4.OrderedMap, org.apache.commons.collections4.IterableGet
    public OrderedMapIterator<K, V> mapIterator() {
        return new TrieMapIterator();
    }

    @Override // org.apache.commons.collections4.Trie
    public SortedMap<K, V> prefixMap(K key) {
        return getPrefixMapByBits(key, 0, lengthInBits(key));
    }

    private SortedMap<K, V> getPrefixMapByBits(K key, int offsetInBits, int lengthInBits) {
        int offsetLength = offsetInBits + lengthInBits;
        if (offsetLength > lengthInBits(key)) {
            throw new IllegalArgumentException(offsetInBits + " + " + lengthInBits + " > " + lengthInBits(key));
        }
        if (offsetLength == 0) {
            return this;
        }
        return new PrefixRangeMap(key, offsetInBits, lengthInBits);
    }

    @Override // java.util.SortedMap
    public SortedMap<K, V> headMap(K toKey) {
        return new RangeEntryMap(this, null, toKey);
    }

    @Override // java.util.SortedMap
    public SortedMap<K, V> subMap(K fromKey, K toKey) {
        return new RangeEntryMap(this, fromKey, toKey);
    }

    @Override // java.util.SortedMap
    public SortedMap<K, V> tailMap(K fromKey) {
        return new RangeEntryMap(this, fromKey, null);
    }

    TrieEntry<K, V> higherEntry(K key) {
        int lengthInBits = lengthInBits(key);
        if (lengthInBits == 0) {
            if (!this.root.isEmpty()) {
                if (size() > 1) {
                    return nextEntry(this.root);
                }
                return null;
            }
            return firstEntry();
        }
        TrieEntry<K, V> found = getNearestEntryForKey(key, lengthInBits);
        if (compareKeys(key, found.key)) {
            return nextEntry(found);
        }
        int bitIndex = bitIndex(key, found.key);
        if (KeyAnalyzer.isValidBitIndex(bitIndex)) {
            TrieEntry<K, V> added = new TrieEntry<>(key, null, bitIndex);
            addEntry(added, lengthInBits);
            incrementSize();
            TrieEntry<K, V> ceil = nextEntry(added);
            removeEntry(added);
            this.modCount -= 2;
            return ceil;
        }
        if (KeyAnalyzer.isNullBitKey(bitIndex)) {
            if (this.root.isEmpty()) {
                if (size() > 1) {
                    return nextEntry(firstEntry());
                }
                return null;
            }
            return firstEntry();
        }
        if (KeyAnalyzer.isEqualBitKey(bitIndex)) {
            return nextEntry(found);
        }
        throw new IllegalStateException("invalid lookup: " + key);
    }

    TrieEntry<K, V> ceilingEntry(K key) {
        int lengthInBits = lengthInBits(key);
        if (lengthInBits == 0) {
            if (!this.root.isEmpty()) {
                return this.root;
            }
            return firstEntry();
        }
        TrieEntry<K, V> found = getNearestEntryForKey(key, lengthInBits);
        if (compareKeys(key, found.key)) {
            return found;
        }
        int bitIndex = bitIndex(key, found.key);
        if (KeyAnalyzer.isValidBitIndex(bitIndex)) {
            TrieEntry<K, V> added = new TrieEntry<>(key, null, bitIndex);
            addEntry(added, lengthInBits);
            incrementSize();
            TrieEntry<K, V> ceil = nextEntry(added);
            removeEntry(added);
            this.modCount -= 2;
            return ceil;
        }
        if (KeyAnalyzer.isNullBitKey(bitIndex)) {
            if (!this.root.isEmpty()) {
                return this.root;
            }
            return firstEntry();
        }
        if (KeyAnalyzer.isEqualBitKey(bitIndex)) {
            return found;
        }
        throw new IllegalStateException("invalid lookup: " + key);
    }

    TrieEntry<K, V> lowerEntry(K key) {
        int lengthInBits = lengthInBits(key);
        if (lengthInBits == 0) {
            return null;
        }
        TrieEntry<K, V> found = getNearestEntryForKey(key, lengthInBits);
        if (compareKeys(key, found.key)) {
            return previousEntry(found);
        }
        int bitIndex = bitIndex(key, found.key);
        if (KeyAnalyzer.isValidBitIndex(bitIndex)) {
            TrieEntry<K, V> added = new TrieEntry<>(key, null, bitIndex);
            addEntry(added, lengthInBits);
            incrementSize();
            TrieEntry<K, V> prior = previousEntry(added);
            removeEntry(added);
            this.modCount -= 2;
            return prior;
        }
        if (KeyAnalyzer.isNullBitKey(bitIndex)) {
            return null;
        }
        if (KeyAnalyzer.isEqualBitKey(bitIndex)) {
            return previousEntry(found);
        }
        throw new IllegalStateException("invalid lookup: " + key);
    }

    TrieEntry<K, V> floorEntry(K key) {
        int lengthInBits = lengthInBits(key);
        if (lengthInBits == 0) {
            if (this.root.isEmpty()) {
                return null;
            }
            return this.root;
        }
        TrieEntry<K, V> found = getNearestEntryForKey(key, lengthInBits);
        if (compareKeys(key, found.key)) {
            return found;
        }
        int bitIndex = bitIndex(key, found.key);
        if (KeyAnalyzer.isValidBitIndex(bitIndex)) {
            TrieEntry<K, V> added = new TrieEntry<>(key, null, bitIndex);
            addEntry(added, lengthInBits);
            incrementSize();
            TrieEntry<K, V> floor = previousEntry(added);
            removeEntry(added);
            this.modCount -= 2;
            return floor;
        }
        if (KeyAnalyzer.isNullBitKey(bitIndex)) {
            if (this.root.isEmpty()) {
                return null;
            }
            return this.root;
        }
        if (KeyAnalyzer.isEqualBitKey(bitIndex)) {
            return found;
        }
        throw new IllegalStateException("invalid lookup: " + key);
    }

    TrieEntry<K, V> subtree(K prefix, int offsetInBits, int lengthInBits) {
        TrieEntry<K, V> current = this.root.left;
        TrieEntry<K, V> current2 = current;
        TrieEntry<K, V> path = this.root;
        while (current2.bitIndex > path.bitIndex && lengthInBits > current2.bitIndex) {
            path = current2;
            if (!isBitSet(prefix, offsetInBits + current2.bitIndex, offsetInBits + lengthInBits)) {
                current2 = current2.left;
            } else {
                current2 = current2.right;
            }
        }
        TrieEntry<K, V> entry = current2.isEmpty() ? path : current2;
        if (entry.isEmpty()) {
            return null;
        }
        int endIndexInBits = offsetInBits + lengthInBits;
        if ((entry == this.root && lengthInBits(entry.getKey()) < endIndexInBits) || isBitSet(prefix, endIndexInBits - 1, endIndexInBits) != isBitSet(entry.key, lengthInBits - 1, lengthInBits(entry.key))) {
            return null;
        }
        int bitIndex = getKeyAnalyzer().bitIndex(prefix, offsetInBits, lengthInBits, entry.key, 0, lengthInBits(entry.getKey()));
        if (bitIndex < 0 || bitIndex >= lengthInBits) {
            return entry;
        }
        return null;
    }

    TrieEntry<K, V> lastEntry() {
        return followRight(this.root.left);
    }

    TrieEntry<K, V> followRight(TrieEntry<K, V> node) {
        if (node.right == null) {
            return null;
        }
        while (node.right.bitIndex > node.bitIndex) {
            node = node.right;
        }
        return node.right;
    }

    TrieEntry<K, V> previousEntry(TrieEntry<K, V> start) {
        if (start.predecessor == null) {
            throw new IllegalArgumentException("must have come from somewhere!");
        }
        if (start.predecessor.right == start) {
            if (isValidUplink(start.predecessor.left, start.predecessor)) {
                return start.predecessor.left;
            }
            return followRight(start.predecessor.left);
        }
        TrieEntry<K, V> node = start.predecessor;
        while (node.parent != null && node == node.parent.left) {
            node = node.parent;
        }
        if (node.parent == null) {
            return null;
        }
        if (isValidUplink(node.parent.left, node.parent)) {
            TrieEntry<K, V> trieEntry = node.parent.left;
            TrieEntry<K, V> trieEntry2 = this.root;
            if (trieEntry == trieEntry2) {
                if (trieEntry2.isEmpty()) {
                    return null;
                }
                return this.root;
            }
            return node.parent.left;
        }
        return followRight(node.parent.left);
    }

    TrieEntry<K, V> nextEntryInSubtree(TrieEntry<K, V> node, TrieEntry<K, V> parentOfSubtree) {
        if (node == null) {
            return firstEntry();
        }
        return nextEntryImpl(node.predecessor, node, parentOfSubtree);
    }

    static boolean isValidUplink(TrieEntry<?, ?> next, TrieEntry<?, ?> from) {
        return (next == null || next.bitIndex > from.bitIndex || next.isEmpty()) ? false : true;
    }

    private static class Reference<E> {
        private E item;

        private Reference() {
        }

        public void set(E item) {
            this.item = item;
        }

        public E get() {
            return this.item;
        }
    }

    protected static class TrieEntry<K, V> extends AbstractBitwiseTrie.BasicEntry<K, V> {
        private static final long serialVersionUID = 4596023148184140013L;
        protected int bitIndex;
        protected TrieEntry<K, V> left;
        protected TrieEntry<K, V> parent;
        protected TrieEntry<K, V> predecessor;
        protected TrieEntry<K, V> right;

        public TrieEntry(K key, V value, int bitIndex) {
            super(key, value);
            this.bitIndex = bitIndex;
            this.parent = null;
            this.left = this;
            this.right = null;
            this.predecessor = this;
        }

        public boolean isEmpty() {
            return this.key == null;
        }

        public boolean isInternalNode() {
            return (this.left == this || this.right == this) ? false : true;
        }

        public boolean isExternalNode() {
            return !isInternalNode();
        }

        @Override // org.apache.commons.collections4.trie.AbstractBitwiseTrie.BasicEntry
        public String toString() {
            StringBuilder buffer = new StringBuilder();
            if (this.bitIndex == -1) {
                buffer.append("RootEntry(");
            } else {
                buffer.append("Entry(");
            }
            buffer.append("key=");
            buffer.append(getKey());
            buffer.append(" [");
            buffer.append(this.bitIndex);
            buffer.append("], ");
            buffer.append("value=");
            buffer.append(getValue());
            buffer.append(", ");
            TrieEntry<K, V> trieEntry = this.parent;
            if (trieEntry != null) {
                if (trieEntry.bitIndex == -1) {
                    buffer.append("parent=");
                    buffer.append("ROOT");
                } else {
                    buffer.append("parent=");
                    buffer.append(this.parent.getKey());
                    buffer.append(" [");
                    buffer.append(this.parent.bitIndex);
                    buffer.append("]");
                }
            } else {
                buffer.append("parent=");
                buffer.append("null");
            }
            buffer.append(", ");
            TrieEntry<K, V> trieEntry2 = this.left;
            if (trieEntry2 != null) {
                if (trieEntry2.bitIndex == -1) {
                    buffer.append("left=");
                    buffer.append("ROOT");
                } else {
                    buffer.append("left=");
                    buffer.append(this.left.getKey());
                    buffer.append(" [");
                    buffer.append(this.left.bitIndex);
                    buffer.append("]");
                }
            } else {
                buffer.append("left=");
                buffer.append("null");
            }
            buffer.append(", ");
            TrieEntry<K, V> trieEntry3 = this.right;
            if (trieEntry3 != null) {
                if (trieEntry3.bitIndex == -1) {
                    buffer.append("right=");
                    buffer.append("ROOT");
                } else {
                    buffer.append("right=");
                    buffer.append(this.right.getKey());
                    buffer.append(" [");
                    buffer.append(this.right.bitIndex);
                    buffer.append("]");
                }
            } else {
                buffer.append("right=");
                buffer.append("null");
            }
            buffer.append(", ");
            TrieEntry<K, V> trieEntry4 = this.predecessor;
            if (trieEntry4 != null) {
                if (trieEntry4.bitIndex == -1) {
                    buffer.append("predecessor=");
                    buffer.append("ROOT");
                } else {
                    buffer.append("predecessor=");
                    buffer.append(this.predecessor.getKey());
                    buffer.append(" [");
                    buffer.append(this.predecessor.bitIndex);
                    buffer.append("]");
                }
            }
            buffer.append(")");
            return buffer.toString();
        }
    }

    private class EntrySet extends AbstractSet<Map.Entry<K, V>> {
        private EntrySet() {
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Set
        public Iterator<Map.Entry<K, V>> iterator() {
            return new EntryIterator();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public boolean contains(Object o) {
            TrieEntry<K, V> candidate;
            return (o instanceof Map.Entry) && (candidate = AbstractPatriciaTrie.this.getEntry(((Map.Entry) o).getKey())) != null && candidate.equals(o);
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public boolean remove(Object obj) {
            if (!(obj instanceof Map.Entry) || !contains(obj)) {
                return false;
            }
            Map.Entry<?, ?> entry = (Map.Entry) obj;
            AbstractPatriciaTrie.this.remove(entry.getKey());
            return true;
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public int size() {
            return AbstractPatriciaTrie.this.size();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public void clear() {
            AbstractPatriciaTrie.this.clear();
        }

        private class EntryIterator extends AbstractPatriciaTrie<K, V>.TrieIterator<Map.Entry<K, V>> {
            private EntryIterator() {
                super();
            }

            @Override // java.util.Iterator
            public Map.Entry<K, V> next() {
                return nextEntry();
            }
        }
    }

    private class KeySet extends AbstractSet<K> {
        private KeySet() {
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Set
        public Iterator<K> iterator() {
            return new KeyIterator();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public int size() {
            return AbstractPatriciaTrie.this.size();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public boolean contains(Object o) {
            return AbstractPatriciaTrie.this.containsKey(o);
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public boolean remove(Object o) {
            int size = size();
            AbstractPatriciaTrie.this.remove(o);
            return size != size();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public void clear() {
            AbstractPatriciaTrie.this.clear();
        }

        private class KeyIterator extends AbstractPatriciaTrie<K, V>.TrieIterator<K> {
            private KeyIterator() {
                super();
            }

            @Override // java.util.Iterator
            public K next() {
                return nextEntry().getKey();
            }
        }
    }

    private class Values extends AbstractCollection<V> {
        private Values() {
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable
        public Iterator<V> iterator() {
            return new ValueIterator();
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public int size() {
            return AbstractPatriciaTrie.this.size();
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean contains(Object o) {
            return AbstractPatriciaTrie.this.containsValue(o);
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public void clear() {
            AbstractPatriciaTrie.this.clear();
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean remove(Object o) {
            Iterator<V> it = iterator();
            while (it.hasNext()) {
                V value = it.next();
                if (AbstractBitwiseTrie.compare(value, o)) {
                    it.remove();
                    return true;
                }
            }
            return false;
        }

        private class ValueIterator extends AbstractPatriciaTrie<K, V>.TrieIterator<V> {
            private ValueIterator() {
                super();
            }

            @Override // java.util.Iterator
            public V next() {
                return nextEntry().getValue();
            }
        }
    }

    abstract class TrieIterator<E> implements Iterator<E> {
        protected TrieEntry<K, V> current;
        protected int expectedModCount;
        protected TrieEntry<K, V> next;

        protected TrieIterator() {
            this.expectedModCount = AbstractPatriciaTrie.this.modCount;
            this.next = AbstractPatriciaTrie.this.nextEntry(null);
        }

        protected TrieIterator(TrieEntry<K, V> firstEntry) {
            this.expectedModCount = AbstractPatriciaTrie.this.modCount;
            this.next = firstEntry;
        }

        protected TrieEntry<K, V> nextEntry() {
            if (this.expectedModCount != AbstractPatriciaTrie.this.modCount) {
                throw new ConcurrentModificationException();
            }
            TrieEntry<K, V> e = this.next;
            if (e == null) {
                throw new NoSuchElementException();
            }
            this.next = findNext(e);
            this.current = e;
            return e;
        }

        protected TrieEntry<K, V> findNext(TrieEntry<K, V> prior) {
            return AbstractPatriciaTrie.this.nextEntry(prior);
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return this.next != null;
        }

        @Override // java.util.Iterator
        public void remove() {
            if (this.current == null) {
                throw new IllegalStateException();
            }
            if (this.expectedModCount != AbstractPatriciaTrie.this.modCount) {
                throw new ConcurrentModificationException();
            }
            TrieEntry<K, V> node = this.current;
            this.current = null;
            AbstractPatriciaTrie.this.removeEntry(node);
            this.expectedModCount = AbstractPatriciaTrie.this.modCount;
        }
    }

    private class TrieMapIterator extends AbstractPatriciaTrie<K, V>.TrieIterator<K> implements OrderedMapIterator<K, V> {
        protected TrieEntry<K, V> previous;

        private TrieMapIterator() {
            super();
        }

        @Override // java.util.Iterator, org.apache.commons.collections4.MapIterator
        public K next() {
            return nextEntry().getKey();
        }

        @Override // org.apache.commons.collections4.MapIterator
        public K getKey() {
            if (this.current == null) {
                throw new IllegalStateException();
            }
            return this.current.getKey();
        }

        @Override // org.apache.commons.collections4.MapIterator
        public V getValue() {
            if (this.current == null) {
                throw new IllegalStateException();
            }
            return this.current.getValue();
        }

        @Override // org.apache.commons.collections4.MapIterator
        public V setValue(V value) {
            if (this.current == null) {
                throw new IllegalStateException();
            }
            return this.current.setValue(value);
        }

        @Override // org.apache.commons.collections4.OrderedMapIterator, org.apache.commons.collections4.OrderedIterator
        public boolean hasPrevious() {
            return this.previous != null;
        }

        @Override // org.apache.commons.collections4.OrderedMapIterator, org.apache.commons.collections4.OrderedIterator
        public K previous() {
            return previousEntry().getKey();
        }

        @Override // org.apache.commons.collections4.trie.AbstractPatriciaTrie.TrieIterator
        protected TrieEntry<K, V> nextEntry() {
            TrieEntry<K, V> nextEntry = super.nextEntry();
            this.previous = nextEntry;
            return nextEntry;
        }

        protected TrieEntry<K, V> previousEntry() {
            if (this.expectedModCount != AbstractPatriciaTrie.this.modCount) {
                throw new ConcurrentModificationException();
            }
            TrieEntry<K, V> e = this.previous;
            if (e == null) {
                throw new NoSuchElementException();
            }
            this.previous = AbstractPatriciaTrie.this.previousEntry(e);
            this.next = this.current;
            this.current = e;
            return this.current;
        }
    }

    private abstract class RangeMap extends AbstractMap<K, V> implements SortedMap<K, V> {
        private volatile transient Set<Map.Entry<K, V>> entrySet;

        protected abstract Set<Map.Entry<K, V>> createEntrySet();

        protected abstract SortedMap<K, V> createRangeMap(K k, boolean z, K k2, boolean z2);

        protected abstract K getFromKey();

        protected abstract K getToKey();

        protected abstract boolean isFromInclusive();

        protected abstract boolean isToInclusive();

        private RangeMap() {
        }

        @Override // java.util.SortedMap
        public Comparator<? super K> comparator() {
            return AbstractPatriciaTrie.this.comparator();
        }

        @Override // java.util.AbstractMap, java.util.Map
        public boolean containsKey(Object key) {
            if (!inRange(AbstractPatriciaTrie.this.castKey(key))) {
                return false;
            }
            return AbstractPatriciaTrie.this.containsKey(key);
        }

        @Override // java.util.AbstractMap, java.util.Map
        public V remove(Object obj) {
            if (!inRange(AbstractPatriciaTrie.this.castKey(obj))) {
                return null;
            }
            return (V) AbstractPatriciaTrie.this.remove(obj);
        }

        @Override // java.util.AbstractMap, java.util.Map
        public V get(Object obj) {
            if (!inRange(AbstractPatriciaTrie.this.castKey(obj))) {
                return null;
            }
            return (V) AbstractPatriciaTrie.this.get(obj);
        }

        @Override // java.util.AbstractMap, java.util.Map
        public V put(K k, V v) {
            if (!inRange(k)) {
                throw new IllegalArgumentException("Key is out of range: " + k);
            }
            return (V) AbstractPatriciaTrie.this.put(k, v);
        }

        @Override // java.util.AbstractMap, java.util.Map, java.util.SortedMap
        public Set<Map.Entry<K, V>> entrySet() {
            if (this.entrySet == null) {
                this.entrySet = createEntrySet();
            }
            return this.entrySet;
        }

        @Override // java.util.SortedMap
        public SortedMap<K, V> subMap(K fromKey, K toKey) {
            if (!inRange2(fromKey)) {
                throw new IllegalArgumentException("FromKey is out of range: " + fromKey);
            }
            if (!inRange2(toKey)) {
                throw new IllegalArgumentException("ToKey is out of range: " + toKey);
            }
            return createRangeMap(fromKey, isFromInclusive(), toKey, isToInclusive());
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // java.util.SortedMap
        public SortedMap<K, V> headMap(K toKey) {
            if (!inRange2(toKey)) {
                throw new IllegalArgumentException("ToKey is out of range: " + toKey);
            }
            return createRangeMap(getFromKey(), isFromInclusive(), toKey, isToInclusive());
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // java.util.SortedMap
        public SortedMap<K, V> tailMap(K fromKey) {
            if (!inRange2(fromKey)) {
                throw new IllegalArgumentException("FromKey is out of range: " + fromKey);
            }
            return createRangeMap(fromKey, isFromInclusive(), getToKey(), isToInclusive());
        }

        protected boolean inRange(K key) {
            Object fromKey = getFromKey();
            Object toKey = getToKey();
            if (fromKey == null || inFromRange(key, false)) {
                return toKey == null || inToRange(key, false);
            }
            return false;
        }

        protected boolean inRange2(K key) {
            return (getFromKey() == null || inFromRange(key, false)) && (getToKey() == null || inToRange(key, true));
        }

        protected boolean inFromRange(K key, boolean forceInclusive) {
            Object fromKey = getFromKey();
            boolean fromInclusive = isFromInclusive();
            int ret = AbstractPatriciaTrie.this.getKeyAnalyzer().compare(key, fromKey);
            return (fromInclusive || forceInclusive) ? ret >= 0 : ret > 0;
        }

        protected boolean inToRange(K key, boolean forceInclusive) {
            Object toKey = getToKey();
            boolean toInclusive = isToInclusive();
            int ret = AbstractPatriciaTrie.this.getKeyAnalyzer().compare(key, toKey);
            return (toInclusive || forceInclusive) ? ret <= 0 : ret < 0;
        }
    }

    private class RangeEntryMap extends AbstractPatriciaTrie<K, V>.RangeMap {
        private final boolean fromInclusive;
        private final K fromKey;
        private final boolean toInclusive;
        private final K toKey;

        protected RangeEntryMap(AbstractPatriciaTrie abstractPatriciaTrie, K fromKey, K toKey) {
            this(fromKey, true, toKey, false);
        }

        /* JADX WARN: Multi-variable type inference failed */
        protected RangeEntryMap(K k, boolean fromInclusive, K k2, boolean toInclusive) {
            super();
            if (k == 0 && k2 == 0) {
                throw new IllegalArgumentException("must have a from or to!");
            }
            if (k != 0 && k2 != 0 && AbstractPatriciaTrie.this.getKeyAnalyzer().compare(k, k2) > 0) {
                throw new IllegalArgumentException("fromKey > toKey");
            }
            this.fromKey = k;
            this.fromInclusive = fromInclusive;
            this.toKey = k2;
            this.toInclusive = toInclusive;
        }

        @Override // java.util.SortedMap
        public K firstKey() {
            Map.Entry<K, V> e;
            K k = this.fromKey;
            if (k == null) {
                e = AbstractPatriciaTrie.this.firstEntry();
            } else if (this.fromInclusive) {
                e = AbstractPatriciaTrie.this.ceilingEntry(k);
            } else {
                e = AbstractPatriciaTrie.this.higherEntry(k);
            }
            K first = e != null ? e.getKey() : null;
            if (e == null || (this.toKey != null && !inToRange(first, false))) {
                throw new NoSuchElementException();
            }
            return first;
        }

        @Override // java.util.SortedMap
        public K lastKey() {
            Map.Entry<K, V> e;
            K k = this.toKey;
            if (k == null) {
                e = AbstractPatriciaTrie.this.lastEntry();
            } else if (this.toInclusive) {
                e = AbstractPatriciaTrie.this.floorEntry(k);
            } else {
                e = AbstractPatriciaTrie.this.lowerEntry(k);
            }
            K last = e != null ? e.getKey() : null;
            if (e == null || (this.fromKey != null && !inFromRange(last, false))) {
                throw new NoSuchElementException();
            }
            return last;
        }

        @Override // org.apache.commons.collections4.trie.AbstractPatriciaTrie.RangeMap
        protected Set<Map.Entry<K, V>> createEntrySet() {
            return new RangeEntrySet(this);
        }

        @Override // org.apache.commons.collections4.trie.AbstractPatriciaTrie.RangeMap
        public K getFromKey() {
            return this.fromKey;
        }

        @Override // org.apache.commons.collections4.trie.AbstractPatriciaTrie.RangeMap
        public K getToKey() {
            return this.toKey;
        }

        @Override // org.apache.commons.collections4.trie.AbstractPatriciaTrie.RangeMap
        public boolean isFromInclusive() {
            return this.fromInclusive;
        }

        @Override // org.apache.commons.collections4.trie.AbstractPatriciaTrie.RangeMap
        public boolean isToInclusive() {
            return this.toInclusive;
        }

        @Override // org.apache.commons.collections4.trie.AbstractPatriciaTrie.RangeMap
        protected SortedMap<K, V> createRangeMap(K fromKey, boolean fromInclusive, K toKey, boolean toInclusive) {
            return new RangeEntryMap(fromKey, fromInclusive, toKey, toInclusive);
        }
    }

    private class RangeEntrySet extends AbstractSet<Map.Entry<K, V>> {
        private final AbstractPatriciaTrie<K, V>.RangeMap delegate;
        private transient int expectedModCount;
        private transient int size = -1;

        public RangeEntrySet(AbstractPatriciaTrie<K, V>.RangeMap delegate) {
            if (delegate == null) {
                throw new NullPointerException("delegate");
            }
            this.delegate = delegate;
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Set
        public Iterator<Map.Entry<K, V>> iterator() {
            TrieEntry<K, V> first;
            K fromKey = this.delegate.getFromKey();
            K toKey = this.delegate.getToKey();
            if (fromKey == null) {
                first = AbstractPatriciaTrie.this.firstEntry();
            } else {
                first = AbstractPatriciaTrie.this.ceilingEntry(fromKey);
            }
            TrieEntry<K, V> last = null;
            if (toKey != null) {
                last = AbstractPatriciaTrie.this.ceilingEntry(toKey);
            }
            return new EntryIterator(first, last);
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public int size() {
            if (this.size == -1 || this.expectedModCount != AbstractPatriciaTrie.this.modCount) {
                this.size = 0;
                Iterator<?> it = iterator();
                while (it.hasNext()) {
                    this.size++;
                    it.next();
                }
                this.expectedModCount = AbstractPatriciaTrie.this.modCount;
            }
            return this.size;
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public boolean isEmpty() {
            return !iterator().hasNext();
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public boolean contains(Object obj) {
            TrieEntry<K, V> entry;
            if (!(obj instanceof Map.Entry)) {
                return false;
            }
            Map.Entry entry2 = (Map.Entry) obj;
            Object key = entry2.getKey();
            return this.delegate.inRange(key) && (entry = AbstractPatriciaTrie.this.getEntry(key)) != null && AbstractBitwiseTrie.compare(entry.getValue(), entry2.getValue());
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public boolean remove(Object obj) {
            TrieEntry<K, V> entry;
            if (!(obj instanceof Map.Entry)) {
                return false;
            }
            Map.Entry entry2 = (Map.Entry) obj;
            Object key = entry2.getKey();
            if (!this.delegate.inRange(key) || (entry = AbstractPatriciaTrie.this.getEntry(key)) == null || !AbstractBitwiseTrie.compare(entry.getValue(), entry2.getValue())) {
                return false;
            }
            AbstractPatriciaTrie.this.removeEntry(entry);
            return true;
        }

        private final class EntryIterator extends AbstractPatriciaTrie<K, V>.TrieIterator<Map.Entry<K, V>> {
            private final K excludedKey;

            private EntryIterator(TrieEntry<K, V> first, TrieEntry<K, V> last) {
                super(first);
                this.excludedKey = last != null ? last.getKey() : null;
            }

            @Override // org.apache.commons.collections4.trie.AbstractPatriciaTrie.TrieIterator, java.util.Iterator
            public boolean hasNext() {
                return (this.next == null || AbstractBitwiseTrie.compare(this.next.key, this.excludedKey)) ? false : true;
            }

            @Override // java.util.Iterator
            public Map.Entry<K, V> next() {
                if (this.next == null || AbstractBitwiseTrie.compare(this.next.key, this.excludedKey)) {
                    throw new NoSuchElementException();
                }
                return nextEntry();
            }
        }
    }

    private class PrefixRangeMap extends AbstractPatriciaTrie<K, V>.RangeMap {
        private transient int expectedModCount;
        private K fromKey;
        private final int lengthInBits;
        private final int offsetInBits;
        private final K prefix;
        private int size;
        private K toKey;

        private PrefixRangeMap(K prefix, int offsetInBits, int lengthInBits) {
            super();
            this.fromKey = null;
            this.toKey = null;
            this.expectedModCount = 0;
            this.size = -1;
            this.prefix = prefix;
            this.offsetInBits = offsetInBits;
            this.lengthInBits = lengthInBits;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public int fixup() {
            if (this.size == -1 || AbstractPatriciaTrie.this.modCount != this.expectedModCount) {
                Iterator<Map.Entry<K, V>> it = super.entrySet().iterator();
                this.size = 0;
                Map.Entry<K, V> entry = null;
                if (it.hasNext()) {
                    Map.Entry<K, V> entry2 = it.next();
                    entry = entry2;
                    this.size = 1;
                }
                K key = entry == null ? null : entry.getKey();
                this.fromKey = key;
                if (key != null) {
                    TrieEntry<K, V> prior = AbstractPatriciaTrie.this.previousEntry((TrieEntry) entry);
                    this.fromKey = prior == null ? null : prior.getKey();
                }
                this.toKey = this.fromKey;
                while (it.hasNext()) {
                    this.size++;
                    Map.Entry<K, V> entry3 = it.next();
                    entry = entry3;
                }
                K key2 = entry == null ? null : entry.getKey();
                this.toKey = key2;
                if (key2 != null) {
                    Map.Entry<K, V> entry4 = AbstractPatriciaTrie.this.nextEntry((TrieEntry) entry);
                    this.toKey = entry4 != null ? entry4.getKey() : null;
                }
                this.expectedModCount = AbstractPatriciaTrie.this.modCount;
            }
            return this.size;
        }

        @Override // java.util.SortedMap
        public K firstKey() {
            TrieEntry<K, V> trieEntryHigherEntry;
            fixup();
            K k = this.fromKey;
            if (k == null) {
                trieEntryHigherEntry = AbstractPatriciaTrie.this.firstEntry();
            } else {
                trieEntryHigherEntry = AbstractPatriciaTrie.this.higherEntry(k);
            }
            K key = trieEntryHigherEntry != null ? trieEntryHigherEntry.getKey() : null;
            if (trieEntryHigherEntry == null || !AbstractPatriciaTrie.this.getKeyAnalyzer().isPrefix(this.prefix, this.offsetInBits, this.lengthInBits, key)) {
                throw new NoSuchElementException();
            }
            return key;
        }

        @Override // java.util.SortedMap
        public K lastKey() {
            TrieEntry<K, V> trieEntryLowerEntry;
            fixup();
            K k = this.toKey;
            if (k == null) {
                trieEntryLowerEntry = AbstractPatriciaTrie.this.lastEntry();
            } else {
                trieEntryLowerEntry = AbstractPatriciaTrie.this.lowerEntry(k);
            }
            K key = trieEntryLowerEntry != null ? trieEntryLowerEntry.getKey() : null;
            if (trieEntryLowerEntry == null || !AbstractPatriciaTrie.this.getKeyAnalyzer().isPrefix(this.prefix, this.offsetInBits, this.lengthInBits, key)) {
                throw new NoSuchElementException();
            }
            return key;
        }

        @Override // org.apache.commons.collections4.trie.AbstractPatriciaTrie.RangeMap
        protected boolean inRange(K k) {
            return AbstractPatriciaTrie.this.getKeyAnalyzer().isPrefix(this.prefix, this.offsetInBits, this.lengthInBits, k);
        }

        @Override // org.apache.commons.collections4.trie.AbstractPatriciaTrie.RangeMap
        protected boolean inRange2(K key) {
            return inRange(key);
        }

        @Override // org.apache.commons.collections4.trie.AbstractPatriciaTrie.RangeMap
        protected boolean inFromRange(K k, boolean z) {
            return AbstractPatriciaTrie.this.getKeyAnalyzer().isPrefix(this.prefix, this.offsetInBits, this.lengthInBits, k);
        }

        @Override // org.apache.commons.collections4.trie.AbstractPatriciaTrie.RangeMap
        protected boolean inToRange(K k, boolean z) {
            return AbstractPatriciaTrie.this.getKeyAnalyzer().isPrefix(this.prefix, this.offsetInBits, this.lengthInBits, k);
        }

        @Override // org.apache.commons.collections4.trie.AbstractPatriciaTrie.RangeMap
        protected Set<Map.Entry<K, V>> createEntrySet() {
            return new PrefixRangeEntrySet(this);
        }

        @Override // org.apache.commons.collections4.trie.AbstractPatriciaTrie.RangeMap
        public K getFromKey() {
            return this.fromKey;
        }

        @Override // org.apache.commons.collections4.trie.AbstractPatriciaTrie.RangeMap
        public K getToKey() {
            return this.toKey;
        }

        @Override // org.apache.commons.collections4.trie.AbstractPatriciaTrie.RangeMap
        public boolean isFromInclusive() {
            return false;
        }

        @Override // org.apache.commons.collections4.trie.AbstractPatriciaTrie.RangeMap
        public boolean isToInclusive() {
            return false;
        }

        @Override // org.apache.commons.collections4.trie.AbstractPatriciaTrie.RangeMap
        protected SortedMap<K, V> createRangeMap(K fromKey, boolean fromInclusive, K toKey, boolean toInclusive) {
            return new RangeEntryMap(fromKey, fromInclusive, toKey, toInclusive);
        }

        @Override // java.util.AbstractMap, java.util.Map
        public void clear() {
            Iterator<Map.Entry<K, V>> it = AbstractPatriciaTrie.this.entrySet().iterator();
            Set<K> currentKeys = keySet();
            while (it.hasNext()) {
                if (currentKeys.contains(it.next().getKey())) {
                    it.remove();
                }
            }
        }
    }

    private final class PrefixRangeEntrySet extends AbstractPatriciaTrie<K, V>.RangeEntrySet {
        private final AbstractPatriciaTrie<K, V>.PrefixRangeMap delegate;
        private int expectedModCount;
        private TrieEntry<K, V> prefixStart;

        public PrefixRangeEntrySet(AbstractPatriciaTrie<K, V>.PrefixRangeMap delegate) {
            super(delegate);
            this.expectedModCount = 0;
            this.delegate = delegate;
        }

        @Override // org.apache.commons.collections4.trie.AbstractPatriciaTrie.RangeEntrySet, java.util.AbstractCollection, java.util.Collection, java.util.Set
        public int size() {
            return this.delegate.fixup();
        }

        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Type inference fix 'apply assigned field type' failed
        java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$PrimitiveArg
        	at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:593)
        	at jadx.core.dex.attributes.nodes.ClassTypeVarsAttr.getTypeVarsMapFor(ClassTypeVarsAttr.java:35)
        	at jadx.core.dex.nodes.utils.TypeUtils.replaceClassGenerics(TypeUtils.java:177)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.insertExplicitUseCast(FixTypesVisitor.java:397)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryFieldTypeWithNewCasts(FixTypesVisitor.java:359)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.applyFieldType(FixTypesVisitor.java:309)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
         */
        @Override // org.apache.commons.collections4.trie.AbstractPatriciaTrie.RangeEntrySet, java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Set
        public Iterator<Map.Entry<K, V>> iterator() {
            if (AbstractPatriciaTrie.this.modCount != this.expectedModCount) {
                this.prefixStart = AbstractPatriciaTrie.this.subtree(((PrefixRangeMap) this.delegate).prefix, ((PrefixRangeMap) this.delegate).offsetInBits, ((PrefixRangeMap) this.delegate).lengthInBits);
                this.expectedModCount = AbstractPatriciaTrie.this.modCount;
            }
            if (this.prefixStart != null) {
                if (((PrefixRangeMap) this.delegate).lengthInBits > this.prefixStart.bitIndex) {
                    return new SingletonIterator(this.prefixStart);
                }
                return new EntryIterator(this.prefixStart, ((PrefixRangeMap) this.delegate).prefix, ((PrefixRangeMap) this.delegate).offsetInBits, ((PrefixRangeMap) this.delegate).lengthInBits);
            }
            Set<Map.Entry<K, V>> empty = Collections.emptySet();
            return empty.iterator();
        }

        private final class SingletonIterator implements Iterator<Map.Entry<K, V>> {
            private final TrieEntry<K, V> entry;
            private int hit = 0;

            public SingletonIterator(TrieEntry<K, V> entry) {
                this.entry = entry;
            }

            @Override // java.util.Iterator
            public boolean hasNext() {
                return this.hit == 0;
            }

            @Override // java.util.Iterator
            public Map.Entry<K, V> next() {
                int i = this.hit;
                if (i != 0) {
                    throw new NoSuchElementException();
                }
                this.hit = i + 1;
                return this.entry;
            }

            @Override // java.util.Iterator
            public void remove() {
                int i = this.hit;
                if (i != 1) {
                    throw new IllegalStateException();
                }
                this.hit = i + 1;
                AbstractPatriciaTrie.this.removeEntry(this.entry);
            }
        }

        private final class EntryIterator extends AbstractPatriciaTrie<K, V>.TrieIterator<Map.Entry<K, V>> {
            private boolean lastOne;
            private final int lengthInBits;
            private final int offset;
            private final K prefix;
            private TrieEntry<K, V> subtree;

            EntryIterator(TrieEntry<K, V> startScan, K prefix, int offset, int lengthInBits) {
                super();
                this.subtree = startScan;
                this.next = AbstractPatriciaTrie.this.followLeft(startScan);
                this.prefix = prefix;
                this.offset = offset;
                this.lengthInBits = lengthInBits;
            }

            @Override // java.util.Iterator
            public Map.Entry<K, V> next() {
                Map.Entry<K, V> entry = nextEntry();
                if (this.lastOne) {
                    this.next = null;
                }
                return entry;
            }

            @Override // org.apache.commons.collections4.trie.AbstractPatriciaTrie.TrieIterator
            protected TrieEntry<K, V> findNext(TrieEntry<K, V> prior) {
                return AbstractPatriciaTrie.this.nextEntryInSubtree(prior, this.subtree);
            }

            @Override // org.apache.commons.collections4.trie.AbstractPatriciaTrie.TrieIterator, java.util.Iterator
            public void remove() {
                boolean needsFixing = false;
                int bitIdx = this.subtree.bitIndex;
                if (this.current == this.subtree) {
                    needsFixing = true;
                }
                super.remove();
                if (bitIdx != this.subtree.bitIndex || needsFixing) {
                    this.subtree = AbstractPatriciaTrie.this.subtree(this.prefix, this.offset, this.lengthInBits);
                }
                if (this.lengthInBits >= this.subtree.bitIndex) {
                    this.lastOne = true;
                }
            }
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    private void readObject(ObjectInputStream stream) throws ClassNotFoundException, IOException {
        stream.defaultReadObject();
        this.root = new TrieEntry<>(null, null, -1);
        int size = stream.readInt();
        for (int i = 0; i < size; i++) {
            put(stream.readObject(), stream.readObject());
        }
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        stream.writeInt(size());
        for (Map.Entry<K, V> entry : entrySet()) {
            stream.writeObject(entry.getKey());
            stream.writeObject(entry.getValue());
        }
    }
}
