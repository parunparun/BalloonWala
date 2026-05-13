package org.apache.commons.collections4.map;

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import org.apache.commons.collections4.KeyValue;

/* JADX INFO: loaded from: classes.dex */
public final class StaticBucketMap<K, V> extends AbstractIterableMap<K, V> {
    private static final int DEFAULT_BUCKETS = 255;
    private final Node<K, V>[] buckets;
    private final Lock[] locks;

    public StaticBucketMap() {
        this(255);
    }

    public StaticBucketMap(int numBuckets) {
        int size = Math.max(17, numBuckets);
        size = size % 2 == 0 ? size - 1 : size;
        this.buckets = new Node[size];
        this.locks = new Lock[size];
        for (int i = 0; i < size; i++) {
            this.locks[i] = new Lock();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getHash(Object key) {
        if (key == null) {
            return 0;
        }
        int hash = key.hashCode();
        int hash2 = hash + (~(hash << 15));
        int hash3 = hash2 ^ (hash2 >>> 10);
        int hash4 = hash3 + (hash3 << 3);
        int hash5 = hash4 ^ (hash4 >>> 6);
        int hash6 = hash5 + (~(hash5 << 11));
        int hash7 = (hash6 ^ (hash6 >>> 16)) % this.buckets.length;
        return hash7 < 0 ? hash7 * (-1) : hash7;
    }

    @Override // java.util.Map, org.apache.commons.collections4.Get
    public int size() {
        int cnt = 0;
        for (int i = 0; i < this.buckets.length; i++) {
            synchronized (this.locks[i]) {
                cnt += this.locks[i].size;
            }
        }
        return cnt;
    }

    @Override // java.util.Map, org.apache.commons.collections4.Get
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override // java.util.Map, org.apache.commons.collections4.Get
    public V get(Object key) {
        int hash = getHash(key);
        synchronized (this.locks[hash]) {
            for (Node<K, V> n = this.buckets[hash]; n != null; n = n.next) {
                if (n.key != key && (n.key == null || !n.key.equals(key))) {
                }
                return n.value;
            }
            return null;
        }
    }

    @Override // java.util.Map, org.apache.commons.collections4.Get
    public boolean containsKey(Object key) {
        int hash = getHash(key);
        synchronized (this.locks[hash]) {
            for (Node<K, V> n = this.buckets[hash]; n != null; n = n.next) {
                if (n.key != key && (n.key == null || !n.key.equals(key))) {
                }
                return true;
            }
            return false;
        }
    }

    @Override // java.util.Map, org.apache.commons.collections4.Get
    public boolean containsValue(Object value) {
        for (int i = 0; i < this.buckets.length; i++) {
            synchronized (this.locks[i]) {
                for (Node<K, V> n = this.buckets[i]; n != null; n = n.next) {
                    if (n.value != value && (n.value == null || !n.value.equals(value))) {
                    }
                    return true;
                }
            }
        }
        return false;
    }

    @Override // java.util.Map, org.apache.commons.collections4.Put
    public V put(K key, V value) {
        int hash = getHash(key);
        synchronized (this.locks[hash]) {
            Node<K, V> n = this.buckets[hash];
            if (n == null) {
                Node<K, V> n2 = new Node<>();
                n2.key = key;
                n2.value = value;
                this.buckets[hash] = n2;
                this.locks[hash].size++;
                return null;
            }
            for (Node<K, V> next = n; next != null; next = next.next) {
                n = next;
                if (n.key != key && (n.key == null || !n.key.equals(key))) {
                }
                V returnVal = n.value;
                n.value = value;
                return returnVal;
            }
            Node<K, V> newNode = new Node<>();
            newNode.key = key;
            newNode.value = value;
            n.next = newNode;
            this.locks[hash].size++;
            return null;
        }
    }

    @Override // java.util.Map, org.apache.commons.collections4.Get
    public V remove(Object key) {
        int hash = getHash(key);
        synchronized (this.locks[hash]) {
            Node<K, V> prev = null;
            for (Node<K, V> n = this.buckets[hash]; n != null; n = n.next) {
                if (n.key != key && (n.key == null || !n.key.equals(key))) {
                    prev = n;
                }
                if (prev == null) {
                    this.buckets[hash] = n.next;
                } else {
                    prev.next = n.next;
                }
                Lock lock = this.locks[hash];
                lock.size--;
                return n.value;
            }
            return null;
        }
    }

    @Override // java.util.Map, org.apache.commons.collections4.Get
    public Set<K> keySet() {
        return new KeySet();
    }

    @Override // java.util.Map, org.apache.commons.collections4.Get
    public Collection<V> values() {
        return new Values();
    }

    @Override // java.util.Map, org.apache.commons.collections4.Get
    public Set<Map.Entry<K, V>> entrySet() {
        return new EntrySet();
    }

    @Override // java.util.Map, org.apache.commons.collections4.Put
    public void putAll(Map<? extends K, ? extends V> map) {
        for (Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override // java.util.Map, org.apache.commons.collections4.Put
    public void clear() {
        for (int i = 0; i < this.buckets.length; i++) {
            Lock lock = this.locks[i];
            synchronized (lock) {
                this.buckets[i] = null;
                lock.size = 0;
            }
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
        return entrySet().equals(other.entrySet());
    }

    @Override // java.util.Map
    public int hashCode() {
        int hashCode = 0;
        for (int i = 0; i < this.buckets.length; i++) {
            synchronized (this.locks[i]) {
                for (Node<K, V> n = this.buckets[i]; n != null; n = n.next) {
                    hashCode += n.hashCode();
                }
            }
        }
        return hashCode;
    }

    private static final class Node<K, V> implements Map.Entry<K, V>, KeyValue<K, V> {
        protected K key;
        protected Node<K, V> next;
        protected V value;

        private Node() {
        }

        @Override // java.util.Map.Entry, org.apache.commons.collections4.KeyValue
        public K getKey() {
            return this.key;
        }

        @Override // java.util.Map.Entry, org.apache.commons.collections4.KeyValue
        public V getValue() {
            return this.value;
        }

        @Override // java.util.Map.Entry
        public int hashCode() {
            K k = this.key;
            int iHashCode = k == null ? 0 : k.hashCode();
            V v = this.value;
            return iHashCode ^ (v != null ? v.hashCode() : 0);
        }

        @Override // java.util.Map.Entry
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof Map.Entry)) {
                return false;
            }
            Map.Entry<?, ?> e2 = (Map.Entry) obj;
            K k = this.key;
            if (k != null ? k.equals(e2.getKey()) : e2.getKey() == null) {
                V v = this.value;
                if (v == null) {
                    if (e2.getValue() == null) {
                        return true;
                    }
                } else if (v.equals(e2.getValue())) {
                    return true;
                }
            }
            return false;
        }

        @Override // java.util.Map.Entry
        public V setValue(V obj) {
            V retVal = this.value;
            this.value = obj;
            return retVal;
        }
    }

    private static final class Lock {
        public int size;

        private Lock() {
        }
    }

    private class BaseIterator {
        private int bucket;
        private final ArrayList<Map.Entry<K, V>> current;
        private Map.Entry<K, V> last;

        private BaseIterator() {
            this.current = new ArrayList<>();
        }

        public boolean hasNext() {
            if (this.current.size() > 0) {
                return true;
            }
            while (this.bucket < StaticBucketMap.this.buckets.length) {
                synchronized (StaticBucketMap.this.locks[this.bucket]) {
                    for (Node<K, V> n = StaticBucketMap.this.buckets[this.bucket]; n != null; n = n.next) {
                        this.current.add(n);
                    }
                    this.bucket++;
                    if (this.current.size() > 0) {
                        return true;
                    }
                }
            }
            return false;
        }

        protected Map.Entry<K, V> nextEntry() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            Map.Entry<K, V> entryRemove = this.current.remove(r0.size() - 1);
            this.last = entryRemove;
            return entryRemove;
        }

        public void remove() {
            Map.Entry<K, V> entry = this.last;
            if (entry == null) {
                throw new IllegalStateException();
            }
            StaticBucketMap.this.remove(entry.getKey());
            this.last = null;
        }
    }

    private class EntryIterator extends StaticBucketMap<K, V>.BaseIterator implements Iterator<Map.Entry<K, V>> {
        private EntryIterator() {
            super();
        }

        @Override // java.util.Iterator
        public Map.Entry<K, V> next() {
            return nextEntry();
        }
    }

    private class ValueIterator extends StaticBucketMap<K, V>.BaseIterator implements Iterator<V> {
        private ValueIterator() {
            super();
        }

        @Override // java.util.Iterator
        public V next() {
            return nextEntry().getValue();
        }
    }

    private class KeyIterator extends StaticBucketMap<K, V>.BaseIterator implements Iterator<K> {
        private KeyIterator() {
            super();
        }

        @Override // java.util.Iterator
        public K next() {
            return nextEntry().getKey();
        }
    }

    private class EntrySet extends AbstractSet<Map.Entry<K, V>> {
        private EntrySet() {
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public int size() {
            return StaticBucketMap.this.size();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public void clear() {
            StaticBucketMap.this.clear();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Set
        public Iterator<Map.Entry<K, V>> iterator() {
            return new EntryIterator();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public boolean contains(Object obj) {
            Map.Entry<?, ?> entry = (Map.Entry) obj;
            int hash = StaticBucketMap.this.getHash(entry.getKey());
            synchronized (StaticBucketMap.this.locks[hash]) {
                for (Node<K, V> n = StaticBucketMap.this.buckets[hash]; n != null; n = n.next) {
                    if (n.equals(entry)) {
                        return true;
                    }
                }
                return false;
            }
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public boolean remove(Object obj) {
            if (!(obj instanceof Map.Entry)) {
                return false;
            }
            Map.Entry<?, ?> entry = (Map.Entry) obj;
            int hash = StaticBucketMap.this.getHash(entry.getKey());
            synchronized (StaticBucketMap.this.locks[hash]) {
                for (Node<K, V> n = StaticBucketMap.this.buckets[hash]; n != null; n = n.next) {
                    if (n.equals(entry)) {
                        StaticBucketMap.this.remove(n.getKey());
                        return true;
                    }
                }
                return false;
            }
        }
    }

    private class KeySet extends AbstractSet<K> {
        private KeySet() {
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public int size() {
            return StaticBucketMap.this.size();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public void clear() {
            StaticBucketMap.this.clear();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Set
        public Iterator<K> iterator() {
            return new KeyIterator();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public boolean contains(Object obj) {
            return StaticBucketMap.this.containsKey(obj);
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public boolean remove(Object obj) {
            int hash = StaticBucketMap.this.getHash(obj);
            synchronized (StaticBucketMap.this.locks[hash]) {
                for (Node<K, V> n = StaticBucketMap.this.buckets[hash]; n != null; n = n.next) {
                    Object k = n.getKey();
                    if (k != obj && (k == null || !k.equals(obj))) {
                    }
                    StaticBucketMap.this.remove(k);
                    return true;
                }
                return false;
            }
        }
    }

    private class Values extends AbstractCollection<V> {
        private Values() {
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public int size() {
            return StaticBucketMap.this.size();
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public void clear() {
            StaticBucketMap.this.clear();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable
        public Iterator<V> iterator() {
            return new ValueIterator();
        }
    }

    public void atomic(Runnable r) {
        if (r == null) {
            throw null;
        }
        atomic(r, 0);
    }

    private void atomic(Runnable r, int bucket) {
        if (bucket >= this.buckets.length) {
            r.run();
            return;
        }
        synchronized (this.locks[bucket]) {
            atomic(r, bucket + 1);
        }
    }
}
