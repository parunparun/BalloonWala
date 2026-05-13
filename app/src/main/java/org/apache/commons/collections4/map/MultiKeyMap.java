package org.apache.commons.collections4.map;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.keyvalue.MultiKey;
import org.apache.commons.collections4.map.AbstractHashedMap;

/* JADX INFO: loaded from: classes.dex */
public class MultiKeyMap<K, V> extends AbstractMapDecorator<MultiKey<? extends K>, V> implements Serializable, Cloneable {
    private static final long serialVersionUID = -1788199231038721040L;

    public static <K, V> MultiKeyMap<K, V> multiKeyMap(AbstractHashedMap<MultiKey<? extends K>, V> map) {
        if (map == null) {
            throw new NullPointerException("Map must not be null");
        }
        if (map.size() > 0) {
            throw new IllegalArgumentException("Map must be empty");
        }
        return new MultiKeyMap<>(map);
    }

    public MultiKeyMap() {
        this(new HashedMap());
    }

    protected MultiKeyMap(AbstractHashedMap<MultiKey<? extends K>, V> map) {
        super(map);
        this.map = map;
    }

    public V get(Object key1, Object key2) {
        int hashCode = hash(key1, key2);
        for (AbstractHashedMap.HashEntry<K, V> hashEntry = decorated().data[decorated().hashIndex(hashCode, decorated().data.length)]; hashEntry != null; hashEntry = hashEntry.next) {
            if (hashEntry.hashCode == hashCode && isEqualKey(hashEntry, key1, key2)) {
                return hashEntry.getValue();
            }
        }
        return null;
    }

    public boolean containsKey(Object key1, Object key2) {
        int hashCode = hash(key1, key2);
        for (AbstractHashedMap.HashEntry<K, V> hashEntry = decorated().data[decorated().hashIndex(hashCode, decorated().data.length)]; hashEntry != null; hashEntry = hashEntry.next) {
            if (hashEntry.hashCode == hashCode && isEqualKey(hashEntry, key1, key2)) {
                return true;
            }
        }
        return false;
    }

    public V put(K key1, K key2, V value) {
        int hashCode = hash(key1, key2);
        int index = decorated().hashIndex(hashCode, decorated().data.length);
        for (AbstractHashedMap.HashEntry<K, V> hashEntry = decorated().data[index]; hashEntry != null; hashEntry = hashEntry.next) {
            if (hashEntry.hashCode == hashCode && isEqualKey(hashEntry, key1, key2)) {
                V oldValue = hashEntry.getValue();
                decorated().updateEntry(hashEntry, value);
                return oldValue;
            }
        }
        decorated().addMapping(index, hashCode, new MultiKey<>(key1, key2), value);
        return null;
    }

    public V removeMultiKey(Object key1, Object key2) {
        int hashCode = hash(key1, key2);
        int index = decorated().hashIndex(hashCode, decorated().data.length);
        AbstractHashedMap.HashEntry<K, V> hashEntry = null;
        for (AbstractHashedMap.HashEntry<K, V> hashEntry2 = decorated().data[index]; hashEntry2 != null; hashEntry2 = hashEntry2.next) {
            if (hashEntry2.hashCode == hashCode && isEqualKey(hashEntry2, key1, key2)) {
                V oldValue = hashEntry2.getValue();
                decorated().removeMapping(hashEntry2, index, hashEntry);
                return oldValue;
            }
            hashEntry = hashEntry2;
        }
        return null;
    }

    protected int hash(Object key1, Object key2) {
        int h = key1 != null ? 0 ^ key1.hashCode() : 0;
        if (key2 != null) {
            h ^= key2.hashCode();
        }
        int h2 = h + (~(h << 9));
        int h3 = h2 ^ (h2 >>> 14);
        int h4 = h3 + (h3 << 4);
        return h4 ^ (h4 >>> 10);
    }

    protected boolean isEqualKey(AbstractHashedMap.HashEntry<MultiKey<? extends K>, V> entry, Object key1, Object key2) {
        MultiKey<? extends K> multi = entry.getKey();
        if (multi.size() == 2 && (key1 == multi.getKey(0) || (key1 != null && key1.equals(multi.getKey(0))))) {
            if (key2 == multi.getKey(1)) {
                return true;
            }
            if (key2 != null && key2.equals(multi.getKey(1))) {
                return true;
            }
        }
        return false;
    }

    public V get(Object key1, Object key2, Object key3) {
        int hashCode = hash(key1, key2, key3);
        for (AbstractHashedMap.HashEntry<K, V> hashEntry = decorated().data[decorated().hashIndex(hashCode, decorated().data.length)]; hashEntry != null; hashEntry = hashEntry.next) {
            if (hashEntry.hashCode == hashCode && isEqualKey(hashEntry, key1, key2, key3)) {
                return hashEntry.getValue();
            }
        }
        return null;
    }

    public boolean containsKey(Object key1, Object key2, Object key3) {
        int hashCode = hash(key1, key2, key3);
        for (AbstractHashedMap.HashEntry<K, V> hashEntry = decorated().data[decorated().hashIndex(hashCode, decorated().data.length)]; hashEntry != null; hashEntry = hashEntry.next) {
            if (hashEntry.hashCode == hashCode && isEqualKey(hashEntry, key1, key2, key3)) {
                return true;
            }
        }
        return false;
    }

    public V put(K key1, K key2, K key3, V value) {
        int hashCode = hash(key1, key2, key3);
        int index = decorated().hashIndex(hashCode, decorated().data.length);
        for (AbstractHashedMap.HashEntry<K, V> hashEntry = decorated().data[index]; hashEntry != null; hashEntry = hashEntry.next) {
            if (hashEntry.hashCode == hashCode && isEqualKey(hashEntry, key1, key2, key3)) {
                V oldValue = hashEntry.getValue();
                decorated().updateEntry(hashEntry, value);
                return oldValue;
            }
        }
        decorated().addMapping(index, hashCode, new MultiKey<>(key1, key2, key3), value);
        return null;
    }

    public V removeMultiKey(Object key1, Object key2, Object key3) {
        int hashCode = hash(key1, key2, key3);
        int index = decorated().hashIndex(hashCode, decorated().data.length);
        AbstractHashedMap.HashEntry<K, V> hashEntry = null;
        for (AbstractHashedMap.HashEntry<K, V> hashEntry2 = decorated().data[index]; hashEntry2 != null; hashEntry2 = hashEntry2.next) {
            if (hashEntry2.hashCode == hashCode && isEqualKey(hashEntry2, key1, key2, key3)) {
                V oldValue = hashEntry2.getValue();
                decorated().removeMapping(hashEntry2, index, hashEntry);
                return oldValue;
            }
            hashEntry = hashEntry2;
        }
        return null;
    }

    protected int hash(Object key1, Object key2, Object key3) {
        int h = key1 != null ? 0 ^ key1.hashCode() : 0;
        if (key2 != null) {
            h ^= key2.hashCode();
        }
        if (key3 != null) {
            h ^= key3.hashCode();
        }
        int h2 = h + (~(h << 9));
        int h3 = h2 ^ (h2 >>> 14);
        int h4 = h3 + (h3 << 4);
        return h4 ^ (h4 >>> 10);
    }

    protected boolean isEqualKey(AbstractHashedMap.HashEntry<MultiKey<? extends K>, V> entry, Object key1, Object key2, Object key3) {
        MultiKey<? extends K> multi = entry.getKey();
        if (multi.size() == 3 && ((key1 == multi.getKey(0) || (key1 != null && key1.equals(multi.getKey(0)))) && (key2 == multi.getKey(1) || (key2 != null && key2.equals(multi.getKey(1)))))) {
            if (key3 == multi.getKey(2)) {
                return true;
            }
            if (key3 != null && key3.equals(multi.getKey(2))) {
                return true;
            }
        }
        return false;
    }

    public V get(Object key1, Object key2, Object key3, Object key4) {
        int hashCode = hash(key1, key2, key3, key4);
        for (AbstractHashedMap.HashEntry<K, V> hashEntry = decorated().data[decorated().hashIndex(hashCode, decorated().data.length)]; hashEntry != null; hashEntry = hashEntry.next) {
            if (hashEntry.hashCode == hashCode && isEqualKey(hashEntry, key1, key2, key3, key4)) {
                return hashEntry.getValue();
            }
        }
        return null;
    }

    public boolean containsKey(Object key1, Object key2, Object key3, Object key4) {
        int hashCode = hash(key1, key2, key3, key4);
        for (AbstractHashedMap.HashEntry<K, V> hashEntry = decorated().data[decorated().hashIndex(hashCode, decorated().data.length)]; hashEntry != null; hashEntry = hashEntry.next) {
            if (hashEntry.hashCode == hashCode && isEqualKey(hashEntry, key1, key2, key3, key4)) {
                return true;
            }
        }
        return false;
    }

    public V put(K key1, K key2, K key3, K key4, V value) {
        int hashCode = hash(key1, key2, key3, key4);
        int index = decorated().hashIndex(hashCode, decorated().data.length);
        for (AbstractHashedMap.HashEntry<K, V> hashEntry = decorated().data[index]; hashEntry != null; hashEntry = hashEntry.next) {
            if (hashEntry.hashCode == hashCode && isEqualKey(hashEntry, key1, key2, key3, key4)) {
                V oldValue = hashEntry.getValue();
                decorated().updateEntry(hashEntry, value);
                return oldValue;
            }
        }
        decorated().addMapping(index, hashCode, new MultiKey<>(key1, key2, key3, key4), value);
        return null;
    }

    public V removeMultiKey(Object key1, Object key2, Object key3, Object key4) {
        int hashCode = hash(key1, key2, key3, key4);
        int index = decorated().hashIndex(hashCode, decorated().data.length);
        AbstractHashedMap.HashEntry<K, V> hashEntry = null;
        for (AbstractHashedMap.HashEntry<K, V> hashEntry2 = decorated().data[index]; hashEntry2 != null; hashEntry2 = hashEntry2.next) {
            if (hashEntry2.hashCode == hashCode && isEqualKey(hashEntry2, key1, key2, key3, key4)) {
                V oldValue = hashEntry2.getValue();
                decorated().removeMapping(hashEntry2, index, hashEntry);
                return oldValue;
            }
            hashEntry = hashEntry2;
        }
        return null;
    }

    protected int hash(Object key1, Object key2, Object key3, Object key4) {
        int h = key1 != null ? 0 ^ key1.hashCode() : 0;
        if (key2 != null) {
            h ^= key2.hashCode();
        }
        if (key3 != null) {
            h ^= key3.hashCode();
        }
        if (key4 != null) {
            h ^= key4.hashCode();
        }
        int h2 = h + (~(h << 9));
        int h3 = h2 ^ (h2 >>> 14);
        int h4 = h3 + (h3 << 4);
        return h4 ^ (h4 >>> 10);
    }

    protected boolean isEqualKey(AbstractHashedMap.HashEntry<MultiKey<? extends K>, V> entry, Object key1, Object key2, Object key3, Object key4) {
        MultiKey<? extends K> multi = entry.getKey();
        if (multi.size() == 4 && ((key1 == multi.getKey(0) || (key1 != null && key1.equals(multi.getKey(0)))) && ((key2 == multi.getKey(1) || (key2 != null && key2.equals(multi.getKey(1)))) && (key3 == multi.getKey(2) || (key3 != null && key3.equals(multi.getKey(2))))))) {
            if (key4 == multi.getKey(3)) {
                return true;
            }
            if (key4 != null && key4.equals(multi.getKey(3))) {
                return true;
            }
        }
        return false;
    }

    public V get(Object key1, Object key2, Object key3, Object key4, Object key5) {
        int hashCode = hash(key1, key2, key3, key4, key5);
        for (AbstractHashedMap.HashEntry<K, V> hashEntry = decorated().data[decorated().hashIndex(hashCode, decorated().data.length)]; hashEntry != null; hashEntry = hashEntry.next) {
            if (hashEntry.hashCode == hashCode && isEqualKey(hashEntry, key1, key2, key3, key4, key5)) {
                return hashEntry.getValue();
            }
        }
        return null;
    }

    public boolean containsKey(Object key1, Object key2, Object key3, Object key4, Object key5) {
        int hashCode = hash(key1, key2, key3, key4, key5);
        for (AbstractHashedMap.HashEntry<K, V> hashEntry = decorated().data[decorated().hashIndex(hashCode, decorated().data.length)]; hashEntry != null; hashEntry = hashEntry.next) {
            if (hashEntry.hashCode == hashCode && isEqualKey(hashEntry, key1, key2, key3, key4, key5)) {
                return true;
            }
        }
        return false;
    }

    public V put(K key1, K key2, K key3, K key4, K key5, V value) {
        int hashCode = hash(key1, key2, key3, key4, key5);
        int index = decorated().hashIndex(hashCode, decorated().data.length);
        for (AbstractHashedMap.HashEntry<K, V> hashEntry = decorated().data[index]; hashEntry != null; hashEntry = hashEntry.next) {
            if (hashEntry.hashCode == hashCode && isEqualKey(hashEntry, key1, key2, key3, key4, key5)) {
                V oldValue = hashEntry.getValue();
                decorated().updateEntry(hashEntry, value);
                return oldValue;
            }
        }
        decorated().addMapping(index, hashCode, new MultiKey<>(key1, key2, key3, key4, key5), value);
        return null;
    }

    public V removeMultiKey(Object key1, Object key2, Object key3, Object key4, Object key5) {
        int hashCode = hash(key1, key2, key3, key4, key5);
        int index = decorated().hashIndex(hashCode, decorated().data.length);
        AbstractHashedMap.HashEntry<K, V> hashEntry = null;
        for (AbstractHashedMap.HashEntry<K, V> hashEntry2 = decorated().data[index]; hashEntry2 != null; hashEntry2 = hashEntry2.next) {
            if (hashEntry2.hashCode == hashCode && isEqualKey(hashEntry2, key1, key2, key3, key4, key5)) {
                V oldValue = hashEntry2.getValue();
                decorated().removeMapping(hashEntry2, index, hashEntry);
                return oldValue;
            }
            hashEntry = hashEntry2;
        }
        return null;
    }

    protected int hash(Object key1, Object key2, Object key3, Object key4, Object key5) {
        int h = key1 != null ? 0 ^ key1.hashCode() : 0;
        if (key2 != null) {
            h ^= key2.hashCode();
        }
        if (key3 != null) {
            h ^= key3.hashCode();
        }
        if (key4 != null) {
            h ^= key4.hashCode();
        }
        if (key5 != null) {
            h ^= key5.hashCode();
        }
        int h2 = h + (~(h << 9));
        int h3 = h2 ^ (h2 >>> 14);
        int h4 = h3 + (h3 << 4);
        return h4 ^ (h4 >>> 10);
    }

    protected boolean isEqualKey(AbstractHashedMap.HashEntry<MultiKey<? extends K>, V> entry, Object key1, Object key2, Object key3, Object key4, Object key5) {
        MultiKey<? extends K> multi = entry.getKey();
        if (multi.size() == 5 && ((key1 == multi.getKey(0) || (key1 != null && key1.equals(multi.getKey(0)))) && ((key2 == multi.getKey(1) || (key2 != null && key2.equals(multi.getKey(1)))) && ((key3 == multi.getKey(2) || (key3 != null && key3.equals(multi.getKey(2)))) && (key4 == multi.getKey(3) || (key4 != null && key4.equals(multi.getKey(3)))))))) {
            if (key5 == multi.getKey(4)) {
                return true;
            }
            if (key5 != null && key5.equals(multi.getKey(4))) {
                return true;
            }
        }
        return false;
    }

    public boolean removeAll(Object key1) {
        boolean modified = false;
        MapIterator<MultiKey<? extends K>, V> it = mapIterator();
        while (it.hasNext()) {
            MultiKey<? extends K> multi = it.next();
            if (multi.size() >= 1) {
                if (key1 == null) {
                    if (multi.getKey(0) == null) {
                        it.remove();
                        modified = true;
                    }
                } else if (key1.equals(multi.getKey(0))) {
                    it.remove();
                    modified = true;
                }
            }
        }
        return modified;
    }

    /* JADX WARN: Removed duplicated region for block: B:28:0x0036 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:30:0x002f A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public boolean removeAll(java.lang.Object r6, java.lang.Object r7) {
        /*
            r5 = this;
            r0 = 0
            org.apache.commons.collections4.MapIterator r1 = r5.mapIterator()
        L5:
            boolean r2 = r1.hasNext()
            if (r2 == 0) goto L45
            java.lang.Object r2 = r1.next()
            org.apache.commons.collections4.keyvalue.MultiKey r2 = (org.apache.commons.collections4.keyvalue.MultiKey) r2
            int r3 = r2.size()
            r4 = 2
            if (r3 < r4) goto L44
            r3 = 0
            if (r6 != 0) goto L22
            java.lang.Object r3 = r2.getKey(r3)
            if (r3 != 0) goto L44
            goto L2c
        L22:
            java.lang.Object r3 = r2.getKey(r3)
            boolean r3 = r6.equals(r3)
            if (r3 == 0) goto L44
        L2c:
            r3 = 1
            if (r7 != 0) goto L36
            java.lang.Object r3 = r2.getKey(r3)
            if (r3 != 0) goto L44
            goto L40
        L36:
            java.lang.Object r3 = r2.getKey(r3)
            boolean r3 = r7.equals(r3)
            if (r3 == 0) goto L44
        L40:
            r1.remove()
            r0 = 1
        L44:
            goto L5
        L45:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.collections4.map.MultiKeyMap.removeAll(java.lang.Object, java.lang.Object):boolean");
    }

    /* JADX WARN: Removed duplicated region for block: B:35:0x0036 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:37:0x004a A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:39:0x0043 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:41:0x002f A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public boolean removeAll(java.lang.Object r6, java.lang.Object r7, java.lang.Object r8) {
        /*
            r5 = this;
            r0 = 0
            org.apache.commons.collections4.MapIterator r1 = r5.mapIterator()
        L5:
            boolean r2 = r1.hasNext()
            if (r2 == 0) goto L59
            java.lang.Object r2 = r1.next()
            org.apache.commons.collections4.keyvalue.MultiKey r2 = (org.apache.commons.collections4.keyvalue.MultiKey) r2
            int r3 = r2.size()
            r4 = 3
            if (r3 < r4) goto L58
            r3 = 0
            if (r6 != 0) goto L22
            java.lang.Object r3 = r2.getKey(r3)
            if (r3 != 0) goto L58
            goto L2c
        L22:
            java.lang.Object r3 = r2.getKey(r3)
            boolean r3 = r6.equals(r3)
            if (r3 == 0) goto L58
        L2c:
            r3 = 1
            if (r7 != 0) goto L36
            java.lang.Object r3 = r2.getKey(r3)
            if (r3 != 0) goto L58
            goto L40
        L36:
            java.lang.Object r3 = r2.getKey(r3)
            boolean r3 = r7.equals(r3)
            if (r3 == 0) goto L58
        L40:
            r3 = 2
            if (r8 != 0) goto L4a
            java.lang.Object r3 = r2.getKey(r3)
            if (r3 != 0) goto L58
            goto L54
        L4a:
            java.lang.Object r3 = r2.getKey(r3)
            boolean r3 = r8.equals(r3)
            if (r3 == 0) goto L58
        L54:
            r1.remove()
            r0 = 1
        L58:
            goto L5
        L59:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.collections4.map.MultiKeyMap.removeAll(java.lang.Object, java.lang.Object, java.lang.Object):boolean");
    }

    /* JADX WARN: Removed duplicated region for block: B:42:0x0036 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:44:0x004a A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:46:0x005e A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:48:0x0057 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:50:0x0043 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:52:0x002f A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public boolean removeAll(java.lang.Object r6, java.lang.Object r7, java.lang.Object r8, java.lang.Object r9) {
        /*
            r5 = this;
            r0 = 0
            org.apache.commons.collections4.MapIterator r1 = r5.mapIterator()
        L5:
            boolean r2 = r1.hasNext()
            if (r2 == 0) goto L6d
            java.lang.Object r2 = r1.next()
            org.apache.commons.collections4.keyvalue.MultiKey r2 = (org.apache.commons.collections4.keyvalue.MultiKey) r2
            int r3 = r2.size()
            r4 = 4
            if (r3 < r4) goto L6c
            r3 = 0
            if (r6 != 0) goto L22
            java.lang.Object r3 = r2.getKey(r3)
            if (r3 != 0) goto L6c
            goto L2c
        L22:
            java.lang.Object r3 = r2.getKey(r3)
            boolean r3 = r6.equals(r3)
            if (r3 == 0) goto L6c
        L2c:
            r3 = 1
            if (r7 != 0) goto L36
            java.lang.Object r3 = r2.getKey(r3)
            if (r3 != 0) goto L6c
            goto L40
        L36:
            java.lang.Object r3 = r2.getKey(r3)
            boolean r3 = r7.equals(r3)
            if (r3 == 0) goto L6c
        L40:
            r3 = 2
            if (r8 != 0) goto L4a
            java.lang.Object r3 = r2.getKey(r3)
            if (r3 != 0) goto L6c
            goto L54
        L4a:
            java.lang.Object r3 = r2.getKey(r3)
            boolean r3 = r8.equals(r3)
            if (r3 == 0) goto L6c
        L54:
            r3 = 3
            if (r9 != 0) goto L5e
            java.lang.Object r3 = r2.getKey(r3)
            if (r3 != 0) goto L6c
            goto L68
        L5e:
            java.lang.Object r3 = r2.getKey(r3)
            boolean r3 = r9.equals(r3)
            if (r3 == 0) goto L6c
        L68:
            r1.remove()
            r0 = 1
        L6c:
            goto L5
        L6d:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.collections4.map.MultiKeyMap.removeAll(java.lang.Object, java.lang.Object, java.lang.Object, java.lang.Object):boolean");
    }

    protected void checkKey(MultiKey<?> key) {
        if (key == null) {
            throw new NullPointerException("Key must not be null");
        }
    }

    public MultiKeyMap<K, V> clone() {
        try {
            return (MultiKeyMap) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.apache.commons.collections4.map.AbstractMapDecorator, java.util.Map, org.apache.commons.collections4.Put
    public V put(MultiKey<? extends K> multiKey, V v) {
        checkKey(multiKey);
        return (V) super.put(multiKey, v);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.apache.commons.collections4.map.AbstractMapDecorator, java.util.Map, org.apache.commons.collections4.Put
    public void putAll(Map<? extends MultiKey<? extends K>, ? extends V> map) {
        for (MultiKey<? extends K> key : map.keySet()) {
            checkKey(key);
        }
        super.putAll(map);
    }

    @Override // org.apache.commons.collections4.map.AbstractIterableMap, org.apache.commons.collections4.IterableGet
    public MapIterator<MultiKey<? extends K>, V> mapIterator() {
        return decorated().mapIterator();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.commons.collections4.map.AbstractMapDecorator
    public AbstractHashedMap<MultiKey<? extends K>, V> decorated() {
        return (AbstractHashedMap) super.decorated();
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(this.map);
    }

    private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
        in.defaultReadObject();
        this.map = (Map) in.readObject();
    }
}
