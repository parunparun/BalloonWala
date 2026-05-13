package org.apache.commons.collections4.map;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import org.apache.commons.collections4.IterableMap;
import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.ResettableIterator;
import org.apache.commons.collections4.iterators.EmptyIterator;
import org.apache.commons.collections4.iterators.EmptyMapIterator;

/* JADX INFO: loaded from: classes.dex */
public class Flat3Map<K, V> implements IterableMap<K, V>, Serializable, Cloneable {
    private static final long serialVersionUID = -6701087419741928296L;
    private transient AbstractHashedMap<K, V> delegateMap;
    private transient int hash1;
    private transient int hash2;
    private transient int hash3;
    private transient K key1;
    private transient K key2;
    private transient K key3;
    private transient int size;
    private transient V value1;
    private transient V value2;
    private transient V value3;

    public Flat3Map() {
    }

    public Flat3Map(Map<? extends K, ? extends V> map) {
        putAll(map);
    }

    @Override // java.util.Map, org.apache.commons.collections4.Get
    public V get(Object key) {
        AbstractHashedMap<K, V> abstractHashedMap = this.delegateMap;
        if (abstractHashedMap != null) {
            return abstractHashedMap.get(key);
        }
        if (key == null) {
            int i = this.size;
            if (i != 1) {
                if (i != 2) {
                    if (i == 3) {
                        if (this.key3 == null) {
                            return this.value3;
                        }
                    } else {
                        return null;
                    }
                }
                if (this.key2 == null) {
                    return this.value2;
                }
            }
            if (this.key1 == null) {
                return this.value1;
            }
            return null;
        }
        if (this.size > 0) {
            int hashCode = key.hashCode();
            int i2 = this.size;
            if (i2 != 1) {
                if (i2 != 2) {
                    if (i2 == 3) {
                        if (this.hash3 == hashCode && key.equals(this.key3)) {
                            return this.value3;
                        }
                    } else {
                        return null;
                    }
                }
                if (this.hash2 == hashCode && key.equals(this.key2)) {
                    return this.value2;
                }
            }
            if (this.hash1 == hashCode && key.equals(this.key1)) {
                return this.value1;
            }
            return null;
        }
        return null;
    }

    @Override // java.util.Map, org.apache.commons.collections4.Get
    public int size() {
        AbstractHashedMap<K, V> abstractHashedMap = this.delegateMap;
        if (abstractHashedMap != null) {
            return abstractHashedMap.size();
        }
        return this.size;
    }

    @Override // java.util.Map, org.apache.commons.collections4.Get
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override // java.util.Map, org.apache.commons.collections4.Get
    public boolean containsKey(Object key) {
        AbstractHashedMap<K, V> abstractHashedMap = this.delegateMap;
        if (abstractHashedMap != null) {
            return abstractHashedMap.containsKey(key);
        }
        if (key == null) {
            int i = this.size;
            if (i != 1) {
                if (i != 2) {
                    if (i == 3) {
                        if (this.key3 == null) {
                            return true;
                        }
                    } else {
                        return false;
                    }
                }
                if (this.key2 == null) {
                    return true;
                }
            }
            return this.key1 == null;
        }
        if (this.size > 0) {
            int hashCode = key.hashCode();
            int i2 = this.size;
            if (i2 != 1) {
                if (i2 != 2) {
                    if (i2 == 3) {
                        if (this.hash3 == hashCode && key.equals(this.key3)) {
                            return true;
                        }
                    } else {
                        return false;
                    }
                }
                if (this.hash2 == hashCode && key.equals(this.key2)) {
                    return true;
                }
            }
            return this.hash1 == hashCode && key.equals(this.key1);
        }
        return false;
    }

    @Override // java.util.Map, org.apache.commons.collections4.Get
    public boolean containsValue(Object value) {
        AbstractHashedMap<K, V> abstractHashedMap = this.delegateMap;
        if (abstractHashedMap != null) {
            return abstractHashedMap.containsValue(value);
        }
        if (value == null) {
            int i = this.size;
            if (i != 1) {
                if (i != 2) {
                    if (i == 3) {
                        if (this.value3 == null) {
                            return true;
                        }
                    } else {
                        return false;
                    }
                }
                if (this.value2 == null) {
                    return true;
                }
            }
            return this.value1 == null;
        }
        int i2 = this.size;
        if (i2 != 1) {
            if (i2 != 2) {
                if (i2 == 3) {
                    if (value.equals(this.value3)) {
                        return true;
                    }
                } else {
                    return false;
                }
            }
            if (value.equals(this.value2)) {
                return true;
            }
        }
        return value.equals(this.value1);
    }

    /* JADX WARN: Removed duplicated region for block: B:23:0x002d  */
    @Override // java.util.Map, org.apache.commons.collections4.Put
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public V put(K r6, V r7) {
        /*
            r5 = this;
            org.apache.commons.collections4.map.AbstractHashedMap<K, V> r0 = r5.delegateMap
            if (r0 == 0) goto L9
            java.lang.Object r0 = r0.put(r6, r7)
            return r0
        L9:
            r0 = 3
            r1 = 2
            r2 = 1
            if (r6 != 0) goto L33
            int r3 = r5.size
            if (r3 == r2) goto L29
            if (r3 == r1) goto L20
            if (r3 == r0) goto L17
            goto L32
        L17:
            K r0 = r5.key3
            if (r0 != 0) goto L20
            V r0 = r5.value3
            r5.value3 = r7
            return r0
        L20:
            K r0 = r5.key2
            if (r0 != 0) goto L29
            V r0 = r5.value2
            r5.value2 = r7
            return r0
        L29:
            K r0 = r5.key1
            if (r0 != 0) goto L32
            V r0 = r5.value1
            r5.value1 = r7
            return r0
        L32:
            goto L77
        L33:
            int r3 = r5.size
            if (r3 <= 0) goto L77
            int r3 = r6.hashCode()
            int r4 = r5.size
            if (r4 == r2) goto L66
            if (r4 == r1) goto L55
            if (r4 == r0) goto L44
            goto L77
        L44:
            int r0 = r5.hash3
            if (r0 != r3) goto L55
            K r0 = r5.key3
            boolean r0 = r6.equals(r0)
            if (r0 == 0) goto L55
            V r0 = r5.value3
            r5.value3 = r7
            return r0
        L55:
            int r0 = r5.hash2
            if (r0 != r3) goto L66
            K r0 = r5.key2
            boolean r0 = r6.equals(r0)
            if (r0 == 0) goto L66
            V r0 = r5.value2
            r5.value2 = r7
            return r0
        L66:
            int r0 = r5.hash1
            if (r0 != r3) goto L77
            K r0 = r5.key1
            boolean r0 = r6.equals(r0)
            if (r0 == 0) goto L77
            V r0 = r5.value1
            r5.value1 = r7
            return r0
        L77:
            int r0 = r5.size
            r3 = 0
            r4 = 0
            if (r0 == 0) goto La6
            if (r0 == r2) goto L98
            if (r0 == r1) goto L8a
            r5.convertToMap()
            org.apache.commons.collections4.map.AbstractHashedMap<K, V> r0 = r5.delegateMap
            r0.put(r6, r7)
            return r3
        L8a:
            if (r6 != 0) goto L8d
            goto L91
        L8d:
            int r4 = r6.hashCode()
        L91:
            r5.hash3 = r4
            r5.key3 = r6
            r5.value3 = r7
            goto Lb3
        L98:
            if (r6 != 0) goto L9b
            goto L9f
        L9b:
            int r4 = r6.hashCode()
        L9f:
            r5.hash2 = r4
            r5.key2 = r6
            r5.value2 = r7
            goto Lb3
        La6:
            if (r6 != 0) goto La9
            goto Lad
        La9:
            int r4 = r6.hashCode()
        Lad:
            r5.hash1 = r4
            r5.key1 = r6
            r5.value1 = r7
        Lb3:
            int r0 = r5.size
            int r0 = r0 + r2
            r5.size = r0
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.collections4.map.Flat3Map.put(java.lang.Object, java.lang.Object):java.lang.Object");
    }

    @Override // java.util.Map, org.apache.commons.collections4.Put
    public void putAll(Map<? extends K, ? extends V> map) {
        int size = map.size();
        if (size == 0) {
            return;
        }
        AbstractHashedMap<K, V> abstractHashedMap = this.delegateMap;
        if (abstractHashedMap != null) {
            abstractHashedMap.putAll(map);
            return;
        }
        if (size < 4) {
            for (Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
                put(entry.getKey(), entry.getValue());
            }
            return;
        }
        convertToMap();
        this.delegateMap.putAll(map);
    }

    private void convertToMap() {
        AbstractHashedMap<K, V> abstractHashedMapCreateDelegateMap = createDelegateMap();
        this.delegateMap = abstractHashedMapCreateDelegateMap;
        int i = this.size;
        if (i != 0) {
            if (i != 1) {
                if (i != 2) {
                    if (i == 3) {
                        abstractHashedMapCreateDelegateMap.put(this.key3, this.value3);
                    } else {
                        throw new IllegalStateException("Invalid map index: " + this.size);
                    }
                }
                this.delegateMap.put(this.key2, this.value2);
            }
            this.delegateMap.put(this.key1, this.value1);
        }
        this.size = 0;
        this.hash3 = 0;
        this.hash2 = 0;
        this.hash1 = 0;
        this.key3 = null;
        this.key2 = null;
        this.key1 = null;
        this.value3 = null;
        this.value2 = null;
        this.value1 = null;
    }

    protected AbstractHashedMap<K, V> createDelegateMap() {
        return new HashedMap();
    }

    @Override // java.util.Map, org.apache.commons.collections4.Get
    public V remove(Object key) {
        AbstractHashedMap<K, V> abstractHashedMap = this.delegateMap;
        if (abstractHashedMap != null) {
            return abstractHashedMap.remove(key);
        }
        int i = this.size;
        if (i == 0) {
            return null;
        }
        if (key == null) {
            if (i != 1) {
                if (i != 2) {
                    if (i == 3) {
                        K k = this.key3;
                        if (k == null) {
                            V old = this.value3;
                            this.hash3 = 0;
                            this.key3 = null;
                            this.value3 = null;
                            this.size = 2;
                            return old;
                        }
                        if (this.key2 == null) {
                            V old2 = this.value2;
                            this.hash2 = this.hash3;
                            this.key2 = k;
                            this.value2 = this.value3;
                            this.hash3 = 0;
                            this.key3 = null;
                            this.value3 = null;
                            this.size = 2;
                            return old2;
                        }
                        if (this.key1 != null) {
                            return null;
                        }
                        V old3 = this.value1;
                        this.hash1 = this.hash3;
                        this.key1 = k;
                        this.value1 = this.value3;
                        this.hash3 = 0;
                        this.key3 = null;
                        this.value3 = null;
                        this.size = 2;
                        return old3;
                    }
                } else {
                    K k2 = this.key2;
                    if (k2 == null) {
                        V old4 = this.value2;
                        this.hash2 = 0;
                        this.key2 = null;
                        this.value2 = null;
                        this.size = 1;
                        return old4;
                    }
                    if (this.key1 != null) {
                        return null;
                    }
                    V old5 = this.value1;
                    this.hash1 = this.hash2;
                    this.key1 = k2;
                    this.value1 = this.value2;
                    this.hash2 = 0;
                    this.key2 = null;
                    this.value2 = null;
                    this.size = 1;
                    return old5;
                }
            } else if (this.key1 == null) {
                V old6 = this.value1;
                this.hash1 = 0;
                this.key1 = null;
                this.value1 = null;
                this.size = 0;
                return old6;
            }
        } else if (i > 0) {
            int hashCode = key.hashCode();
            int i2 = this.size;
            if (i2 != 1) {
                if (i2 != 2) {
                    if (i2 == 3) {
                        if (this.hash3 == hashCode && key.equals(this.key3)) {
                            V old7 = this.value3;
                            this.hash3 = 0;
                            this.key3 = null;
                            this.value3 = null;
                            this.size = 2;
                            return old7;
                        }
                        if (this.hash2 == hashCode && key.equals(this.key2)) {
                            V old8 = this.value2;
                            this.hash2 = this.hash3;
                            this.key2 = this.key3;
                            this.value2 = this.value3;
                            this.hash3 = 0;
                            this.key3 = null;
                            this.value3 = null;
                            this.size = 2;
                            return old8;
                        }
                        if (this.hash1 != hashCode || !key.equals(this.key1)) {
                            return null;
                        }
                        V old9 = this.value1;
                        this.hash1 = this.hash3;
                        this.key1 = this.key3;
                        this.value1 = this.value3;
                        this.hash3 = 0;
                        this.key3 = null;
                        this.value3 = null;
                        this.size = 2;
                        return old9;
                    }
                } else {
                    if (this.hash2 == hashCode && key.equals(this.key2)) {
                        V old10 = this.value2;
                        this.hash2 = 0;
                        this.key2 = null;
                        this.value2 = null;
                        this.size = 1;
                        return old10;
                    }
                    if (this.hash1 != hashCode || !key.equals(this.key1)) {
                        return null;
                    }
                    V old11 = this.value1;
                    this.hash1 = this.hash2;
                    this.key1 = this.key2;
                    this.value1 = this.value2;
                    this.hash2 = 0;
                    this.key2 = null;
                    this.value2 = null;
                    this.size = 1;
                    return old11;
                }
            } else if (this.hash1 == hashCode && key.equals(this.key1)) {
                V old12 = this.value1;
                this.hash1 = 0;
                this.key1 = null;
                this.value1 = null;
                this.size = 0;
                return old12;
            }
        }
        return null;
    }

    @Override // java.util.Map, org.apache.commons.collections4.Put
    public void clear() {
        AbstractHashedMap<K, V> abstractHashedMap = this.delegateMap;
        if (abstractHashedMap != null) {
            abstractHashedMap.clear();
            this.delegateMap = null;
            return;
        }
        this.size = 0;
        this.hash3 = 0;
        this.hash2 = 0;
        this.hash1 = 0;
        this.key3 = null;
        this.key2 = null;
        this.key1 = null;
        this.value3 = null;
        this.value2 = null;
        this.value1 = null;
    }

    @Override // org.apache.commons.collections4.IterableGet
    public MapIterator<K, V> mapIterator() {
        AbstractHashedMap<K, V> abstractHashedMap = this.delegateMap;
        if (abstractHashedMap != null) {
            return abstractHashedMap.mapIterator();
        }
        if (this.size == 0) {
            return EmptyMapIterator.emptyMapIterator();
        }
        return new FlatMapIterator(this);
    }

    static class FlatMapIterator<K, V> implements MapIterator<K, V>, ResettableIterator<K> {
        private final Flat3Map<K, V> parent;
        private int nextIndex = 0;
        private boolean canRemove = false;

        FlatMapIterator(Flat3Map<K, V> parent) {
            this.parent = parent;
        }

        @Override // org.apache.commons.collections4.MapIterator, java.util.Iterator
        public boolean hasNext() {
            return this.nextIndex < ((Flat3Map) this.parent).size;
        }

        @Override // org.apache.commons.collections4.MapIterator, java.util.Iterator
        public K next() {
            if (!hasNext()) {
                throw new NoSuchElementException("No next() entry in the iteration");
            }
            this.canRemove = true;
            this.nextIndex++;
            return getKey();
        }

        @Override // org.apache.commons.collections4.MapIterator, java.util.Iterator
        public void remove() {
            if (!this.canRemove) {
                throw new IllegalStateException("remove() can only be called once after next()");
            }
            this.parent.remove(getKey());
            this.nextIndex--;
            this.canRemove = false;
        }

        @Override // org.apache.commons.collections4.MapIterator
        public K getKey() {
            if (!this.canRemove) {
                throw new IllegalStateException("getKey() can only be called after next() and before remove()");
            }
            int i = this.nextIndex;
            if (i == 1) {
                return (K) ((Flat3Map) this.parent).key1;
            }
            if (i == 2) {
                return (K) ((Flat3Map) this.parent).key2;
            }
            if (i == 3) {
                return (K) ((Flat3Map) this.parent).key3;
            }
            throw new IllegalStateException("Invalid map index: " + this.nextIndex);
        }

        @Override // org.apache.commons.collections4.MapIterator
        public V getValue() {
            if (!this.canRemove) {
                throw new IllegalStateException("getValue() can only be called after next() and before remove()");
            }
            int i = this.nextIndex;
            if (i == 1) {
                return (V) ((Flat3Map) this.parent).value1;
            }
            if (i == 2) {
                return (V) ((Flat3Map) this.parent).value2;
            }
            if (i == 3) {
                return (V) ((Flat3Map) this.parent).value3;
            }
            throw new IllegalStateException("Invalid map index: " + this.nextIndex);
        }

        @Override // org.apache.commons.collections4.MapIterator
        public V setValue(V value) {
            if (!this.canRemove) {
                throw new IllegalStateException("setValue() can only be called after next() and before remove()");
            }
            V old = getValue();
            int i = this.nextIndex;
            if (i == 1) {
                ((Flat3Map) this.parent).value1 = value;
            } else if (i == 2) {
                ((Flat3Map) this.parent).value2 = value;
            } else if (i == 3) {
                ((Flat3Map) this.parent).value3 = value;
            } else {
                throw new IllegalStateException("Invalid map index: " + this.nextIndex);
            }
            return old;
        }

        @Override // org.apache.commons.collections4.ResettableIterator
        public void reset() {
            this.nextIndex = 0;
            this.canRemove = false;
        }

        public String toString() {
            if (this.canRemove) {
                return "Iterator[" + getKey() + "=" + getValue() + "]";
            }
            return "Iterator[]";
        }
    }

    @Override // java.util.Map, org.apache.commons.collections4.Get
    public Set<Map.Entry<K, V>> entrySet() {
        AbstractHashedMap<K, V> abstractHashedMap = this.delegateMap;
        if (abstractHashedMap != null) {
            return abstractHashedMap.entrySet();
        }
        return new EntrySet(this);
    }

    static class EntrySet<K, V> extends AbstractSet<Map.Entry<K, V>> {
        private final Flat3Map<K, V> parent;

        EntrySet(Flat3Map<K, V> parent) {
            this.parent = parent;
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public int size() {
            return this.parent.size();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public void clear() {
            this.parent.clear();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public boolean remove(Object obj) {
            if (!(obj instanceof Map.Entry)) {
                return false;
            }
            Map.Entry<?, ?> entry = (Map.Entry) obj;
            Object key = entry.getKey();
            boolean result = this.parent.containsKey(key);
            this.parent.remove(key);
            return result;
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Set
        public Iterator<Map.Entry<K, V>> iterator() {
            if (((Flat3Map) this.parent).delegateMap != null) {
                return ((Flat3Map) this.parent).delegateMap.entrySet().iterator();
            }
            if (this.parent.size() == 0) {
                return EmptyIterator.emptyIterator();
            }
            return new EntrySetIterator(this.parent);
        }
    }

    static class FlatMapEntry<K, V> implements Map.Entry<K, V> {
        private final int index;
        private final Flat3Map<K, V> parent;
        private volatile boolean removed = false;

        public FlatMapEntry(Flat3Map<K, V> parent, int index) {
            this.parent = parent;
            this.index = index;
        }

        void setRemoved(boolean flag) {
            this.removed = flag;
        }

        @Override // java.util.Map.Entry
        public K getKey() {
            if (this.removed) {
                throw new IllegalStateException("getKey() can only be called after next() and before remove()");
            }
            int i = this.index;
            if (i == 1) {
                return (K) ((Flat3Map) this.parent).key1;
            }
            if (i == 2) {
                return (K) ((Flat3Map) this.parent).key2;
            }
            if (i == 3) {
                return (K) ((Flat3Map) this.parent).key3;
            }
            throw new IllegalStateException("Invalid map index: " + this.index);
        }

        @Override // java.util.Map.Entry
        public V getValue() {
            if (this.removed) {
                throw new IllegalStateException("getValue() can only be called after next() and before remove()");
            }
            int i = this.index;
            if (i == 1) {
                return (V) ((Flat3Map) this.parent).value1;
            }
            if (i == 2) {
                return (V) ((Flat3Map) this.parent).value2;
            }
            if (i == 3) {
                return (V) ((Flat3Map) this.parent).value3;
            }
            throw new IllegalStateException("Invalid map index: " + this.index);
        }

        @Override // java.util.Map.Entry
        public V setValue(V value) {
            if (this.removed) {
                throw new IllegalStateException("setValue() can only be called after next() and before remove()");
            }
            V old = getValue();
            int i = this.index;
            if (i == 1) {
                ((Flat3Map) this.parent).value1 = value;
            } else if (i == 2) {
                ((Flat3Map) this.parent).value2 = value;
            } else if (i == 3) {
                ((Flat3Map) this.parent).value3 = value;
            } else {
                throw new IllegalStateException("Invalid map index: " + this.index);
            }
            return old;
        }

        @Override // java.util.Map.Entry
        public boolean equals(Object obj) {
            if (this.removed || !(obj instanceof Map.Entry)) {
                return false;
            }
            Map.Entry<?, ?> other = (Map.Entry) obj;
            Object key = getKey();
            Object value = getValue();
            Object key2 = other.getKey();
            if (key == null) {
                if (key2 != null) {
                    return false;
                }
            } else if (!key.equals(key2)) {
                return false;
            }
            if (value == null) {
                if (other.getValue() != null) {
                    return false;
                }
            } else if (!value.equals(other.getValue())) {
                return false;
            }
            return true;
        }

        @Override // java.util.Map.Entry
        public int hashCode() {
            if (this.removed) {
                return 0;
            }
            Object key = getKey();
            Object value = getValue();
            return (value != null ? value.hashCode() : 0) ^ (key == null ? 0 : key.hashCode());
        }

        public String toString() {
            if (!this.removed) {
                return getKey() + "=" + getValue();
            }
            return "";
        }
    }

    static abstract class EntryIterator<K, V> {
        private final Flat3Map<K, V> parent;
        private int nextIndex = 0;
        private FlatMapEntry<K, V> currentEntry = null;

        public EntryIterator(Flat3Map<K, V> parent) {
            this.parent = parent;
        }

        public boolean hasNext() {
            return this.nextIndex < ((Flat3Map) this.parent).size;
        }

        public Map.Entry<K, V> nextEntry() {
            if (!hasNext()) {
                throw new NoSuchElementException("No next() entry in the iteration");
            }
            Flat3Map<K, V> flat3Map = this.parent;
            int i = this.nextIndex + 1;
            this.nextIndex = i;
            FlatMapEntry<K, V> flatMapEntry = new FlatMapEntry<>(flat3Map, i);
            this.currentEntry = flatMapEntry;
            return flatMapEntry;
        }

        public void remove() {
            FlatMapEntry<K, V> flatMapEntry = this.currentEntry;
            if (flatMapEntry == null) {
                throw new IllegalStateException("remove() can only be called once after next()");
            }
            flatMapEntry.setRemoved(true);
            this.parent.remove(this.currentEntry.getKey());
            this.nextIndex--;
            this.currentEntry = null;
        }
    }

    static class EntrySetIterator<K, V> extends EntryIterator<K, V> implements Iterator<Map.Entry<K, V>> {
        EntrySetIterator(Flat3Map<K, V> parent) {
            super(parent);
        }

        @Override // java.util.Iterator
        public Map.Entry<K, V> next() {
            return nextEntry();
        }
    }

    @Override // java.util.Map, org.apache.commons.collections4.Get
    public Set<K> keySet() {
        AbstractHashedMap<K, V> abstractHashedMap = this.delegateMap;
        if (abstractHashedMap != null) {
            return abstractHashedMap.keySet();
        }
        return new KeySet(this);
    }

    static class KeySet<K> extends AbstractSet<K> {
        private final Flat3Map<K, ?> parent;

        KeySet(Flat3Map<K, ?> parent) {
            this.parent = parent;
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public int size() {
            return this.parent.size();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public void clear() {
            this.parent.clear();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public boolean contains(Object key) {
            return this.parent.containsKey(key);
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public boolean remove(Object key) {
            boolean result = this.parent.containsKey(key);
            this.parent.remove(key);
            return result;
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Set
        public Iterator<K> iterator() {
            if (((Flat3Map) this.parent).delegateMap != null) {
                return ((Flat3Map) this.parent).delegateMap.keySet().iterator();
            }
            if (this.parent.size() == 0) {
                return EmptyIterator.emptyIterator();
            }
            return new KeySetIterator(this.parent);
        }
    }

    static class KeySetIterator<K> extends EntryIterator<K, Object> implements Iterator<K> {
        KeySetIterator(Flat3Map<K, ?> parent) {
            super(parent);
        }

        @Override // java.util.Iterator
        public K next() {
            return nextEntry().getKey();
        }
    }

    @Override // java.util.Map, org.apache.commons.collections4.Get
    public Collection<V> values() {
        AbstractHashedMap<K, V> abstractHashedMap = this.delegateMap;
        if (abstractHashedMap != null) {
            return abstractHashedMap.values();
        }
        return new Values(this);
    }

    static class Values<V> extends AbstractCollection<V> {
        private final Flat3Map<?, V> parent;

        Values(Flat3Map<?, V> parent) {
            this.parent = parent;
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public int size() {
            return this.parent.size();
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public void clear() {
            this.parent.clear();
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean contains(Object value) {
            return this.parent.containsValue(value);
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable
        public Iterator<V> iterator() {
            if (((Flat3Map) this.parent).delegateMap != null) {
                return ((Flat3Map) this.parent).delegateMap.values().iterator();
            }
            if (this.parent.size() == 0) {
                return EmptyIterator.emptyIterator();
            }
            return new ValuesIterator(this.parent);
        }
    }

    static class ValuesIterator<V> extends EntryIterator<Object, V> implements Iterator<V> {
        ValuesIterator(Flat3Map<?, V> parent) {
            super(parent);
        }

        @Override // java.util.Iterator
        public V next() {
            return nextEntry().getValue();
        }
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeInt(size());
        MapIterator<K, V> mapIterator = mapIterator();
        while (mapIterator.hasNext()) {
            out.writeObject(mapIterator.next());
            out.writeObject(mapIterator.getValue());
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
        in.defaultReadObject();
        int count = in.readInt();
        if (count > 3) {
            this.delegateMap = createDelegateMap();
        }
        for (int i = count; i > 0; i--) {
            put(in.readObject(), in.readObject());
        }
    }

    public Flat3Map<K, V> clone() {
        try {
            Flat3Map<K, V> cloned = (Flat3Map) super.clone();
            if (cloned.delegateMap != null) {
                cloned.delegateMap = cloned.delegateMap.clone();
            }
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:46:0x006f A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:47:0x0070  */
    @Override // java.util.Map
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public boolean equals(java.lang.Object r7) {
        /*
            r6 = this;
            r0 = 1
            if (r7 != r6) goto L4
            return r0
        L4:
            org.apache.commons.collections4.map.AbstractHashedMap<K, V> r1 = r6.delegateMap
            if (r1 == 0) goto Ld
            boolean r0 = r1.equals(r7)
            return r0
        Ld:
            boolean r1 = r7 instanceof java.util.Map
            r2 = 0
            if (r1 != 0) goto L13
            return r2
        L13:
            r1 = r7
            java.util.Map r1 = (java.util.Map) r1
            int r3 = r6.size
            int r4 = r1.size()
            if (r3 == r4) goto L1f
            return r2
        L1f:
            int r3 = r6.size
            if (r3 <= 0) goto L84
            r4 = 0
            if (r3 == r0) goto L67
            r5 = 2
            if (r3 == r5) goto L4a
            r5 = 3
            if (r3 == r5) goto L2d
            goto L84
        L2d:
            K r3 = r6.key3
            boolean r3 = r1.containsKey(r3)
            if (r3 != 0) goto L36
            return r2
        L36:
            K r3 = r6.key3
            java.lang.Object r4 = r1.get(r3)
            V r3 = r6.value3
            if (r3 != 0) goto L43
            if (r4 == 0) goto L4a
            goto L49
        L43:
            boolean r3 = r3.equals(r4)
            if (r3 != 0) goto L4a
        L49:
            return r2
        L4a:
            K r3 = r6.key2
            boolean r3 = r1.containsKey(r3)
            if (r3 != 0) goto L53
            return r2
        L53:
            K r3 = r6.key2
            java.lang.Object r4 = r1.get(r3)
            V r3 = r6.value2
            if (r3 != 0) goto L60
            if (r4 == 0) goto L67
            goto L66
        L60:
            boolean r3 = r3.equals(r4)
            if (r3 != 0) goto L67
        L66:
            return r2
        L67:
            K r3 = r6.key1
            boolean r3 = r1.containsKey(r3)
            if (r3 != 0) goto L70
            return r2
        L70:
            K r3 = r6.key1
            java.lang.Object r3 = r1.get(r3)
            V r4 = r6.value1
            if (r4 != 0) goto L7d
            if (r3 == 0) goto L84
            goto L83
        L7d:
            boolean r4 = r4.equals(r3)
            if (r4 != 0) goto L84
        L83:
            return r2
        L84:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.collections4.map.Flat3Map.equals(java.lang.Object):boolean");
    }

    @Override // java.util.Map
    public int hashCode() {
        AbstractHashedMap<K, V> abstractHashedMap = this.delegateMap;
        if (abstractHashedMap != null) {
            return abstractHashedMap.hashCode();
        }
        int total = 0;
        int i = this.size;
        if (i == 0) {
            return 0;
        }
        if (i != 1) {
            if (i != 2) {
                if (i == 3) {
                    int i2 = this.hash3;
                    V v = this.value3;
                    total = 0 + (i2 ^ (v == null ? 0 : v.hashCode()));
                } else {
                    throw new IllegalStateException("Invalid map index: " + this.size);
                }
            }
            int i3 = this.hash2;
            V v2 = this.value2;
            total += i3 ^ (v2 == null ? 0 : v2.hashCode());
        }
        int i4 = this.hash1;
        V v3 = this.value1;
        return total + (i4 ^ (v3 != null ? v3.hashCode() : 0));
    }

    public String toString() {
        AbstractHashedMap<K, V> abstractHashedMap = this.delegateMap;
        if (abstractHashedMap != null) {
            return abstractHashedMap.toString();
        }
        if (this.size == 0) {
            return "{}";
        }
        StringBuilder buf = new StringBuilder(128);
        buf.append('{');
        int i = this.size;
        if (i != 1) {
            if (i != 2) {
                if (i == 3) {
                    Object obj = this.key3;
                    if (obj == this) {
                        obj = "(this Map)";
                    }
                    buf.append(obj);
                    buf.append('=');
                    Object obj2 = this.value3;
                    if (obj2 == this) {
                        obj2 = "(this Map)";
                    }
                    buf.append(obj2);
                    buf.append(',');
                } else {
                    throw new IllegalStateException("Invalid map index: " + this.size);
                }
            }
            Object obj3 = this.key2;
            if (obj3 == this) {
                obj3 = "(this Map)";
            }
            buf.append(obj3);
            buf.append('=');
            Object obj4 = this.value2;
            if (obj4 == this) {
                obj4 = "(this Map)";
            }
            buf.append(obj4);
            buf.append(',');
        }
        Object obj5 = this.key1;
        if (obj5 == this) {
            obj5 = "(this Map)";
        }
        buf.append(obj5);
        buf.append('=');
        V v = this.value1;
        buf.append(v != this ? v : "(this Map)");
        buf.append('}');
        return buf.toString();
    }
}
