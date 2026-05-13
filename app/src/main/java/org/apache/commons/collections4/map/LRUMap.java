package org.apache.commons.collections4.map;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import org.apache.commons.collections4.BoundedMap;
import org.apache.commons.collections4.map.AbstractHashedMap;
import org.apache.commons.collections4.map.AbstractLinkedMap;

/* JADX INFO: loaded from: classes.dex */
public class LRUMap<K, V> extends AbstractLinkedMap<K, V> implements BoundedMap<K, V>, Serializable, Cloneable {
    protected static final int DEFAULT_MAX_SIZE = 100;
    private static final long serialVersionUID = -612114643488955218L;
    private transient int maxSize;
    private boolean scanUntilRemovable;

    public LRUMap() {
        this(100, 0.75f, false);
    }

    public LRUMap(int maxSize) {
        this(maxSize, 0.75f);
    }

    public LRUMap(int maxSize, int initialSize) {
        this(maxSize, initialSize, 0.75f);
    }

    public LRUMap(int maxSize, boolean scanUntilRemovable) {
        this(maxSize, 0.75f, scanUntilRemovable);
    }

    public LRUMap(int maxSize, float loadFactor) {
        this(maxSize, loadFactor, false);
    }

    public LRUMap(int maxSize, int initialSize, float loadFactor) {
        this(maxSize, initialSize, loadFactor, false);
    }

    public LRUMap(int maxSize, float loadFactor, boolean scanUntilRemovable) {
        this(maxSize, maxSize, loadFactor, scanUntilRemovable);
    }

    public LRUMap(int maxSize, int initialSize, float loadFactor, boolean scanUntilRemovable) {
        super(initialSize, loadFactor);
        if (maxSize < 1) {
            throw new IllegalArgumentException("LRUMap max size must be greater than 0");
        }
        if (initialSize > maxSize) {
            throw new IllegalArgumentException("LRUMap initial size must not be greather than max size");
        }
        this.maxSize = maxSize;
        this.scanUntilRemovable = scanUntilRemovable;
    }

    public LRUMap(Map<? extends K, ? extends V> map) {
        this((Map) map, false);
    }

    public LRUMap(Map<? extends K, ? extends V> map, boolean scanUntilRemovable) {
        this(map.size(), 0.75f, scanUntilRemovable);
        putAll(map);
    }

    @Override // org.apache.commons.collections4.map.AbstractHashedMap, java.util.AbstractMap, java.util.Map, org.apache.commons.collections4.Get
    public V get(Object key) {
        return get(key, true);
    }

    public V get(Object key, boolean updateToMRU) {
        AbstractLinkedMap.LinkEntry<K, V> entry = getEntry(key);
        if (entry == null) {
            return null;
        }
        if (updateToMRU) {
            moveToMRU(entry);
        }
        return entry.getValue();
    }

    protected void moveToMRU(AbstractLinkedMap.LinkEntry<K, V> entry) {
        if (entry.after != this.header) {
            this.modCount++;
            if (entry.before == null) {
                throw new IllegalStateException("Entry.before is null. This should not occur if your keys are immutable, and you have used synchronization properly.");
            }
            entry.before.after = entry.after;
            entry.after.before = entry.before;
            entry.after = this.header;
            entry.before = this.header.before;
            this.header.before.after = entry;
            this.header.before = entry;
            return;
        }
        if (entry == this.header) {
            throw new IllegalStateException("Can't move header to MRU This should not occur if your keys are immutable, and you have used synchronization properly.");
        }
    }

    @Override // org.apache.commons.collections4.map.AbstractHashedMap
    protected void updateEntry(AbstractHashedMap.HashEntry<K, V> entry, V newValue) {
        moveToMRU((AbstractLinkedMap.LinkEntry) entry);
        entry.setValue(newValue);
    }

    @Override // org.apache.commons.collections4.map.AbstractHashedMap
    protected void addMapping(int hashIndex, int hashCode, K key, V value) {
        AbstractLinkedMap.LinkEntry<K, V> reuse;
        boolean removeLRUEntry;
        if (isFull()) {
            AbstractLinkedMap.LinkEntry<K, V> reuse2 = this.header.after;
            boolean removeLRUEntry2 = false;
            if (this.scanUntilRemovable) {
                while (true) {
                    if (reuse2 == this.header || reuse2 == null) {
                        break;
                    }
                    if (removeLRU(reuse2)) {
                        removeLRUEntry2 = true;
                        break;
                    }
                    reuse2 = reuse2.after;
                }
                if (reuse2 == null) {
                    throw new IllegalStateException("Entry.after=null, header.after=" + this.header.after + " header.before=" + this.header.before + " key=" + key + " value=" + value + " size=" + this.size + " maxSize=" + this.maxSize + " This should not occur if your keys are immutable, and you have used synchronization properly.");
                }
                reuse = reuse2;
                removeLRUEntry = removeLRUEntry2;
            } else {
                reuse = reuse2;
                removeLRUEntry = removeLRU(reuse2);
            }
            if (removeLRUEntry) {
                if (reuse == null) {
                    throw new IllegalStateException("reuse=null, header.after=" + this.header.after + " header.before=" + this.header.before + " key=" + key + " value=" + value + " size=" + this.size + " maxSize=" + this.maxSize + " This should not occur if your keys are immutable, and you have used synchronization properly.");
                }
                reuseMapping(reuse, hashIndex, hashCode, key, value);
                return;
            }
            super.addMapping(hashIndex, hashCode, key, value);
            return;
        }
        super.addMapping(hashIndex, hashCode, key, value);
    }

    protected void reuseMapping(AbstractLinkedMap.LinkEntry<K, V> entry, int hashIndex, int hashCode, K key, V value) {
        try {
            int removeIndex = hashIndex(entry.hashCode, this.data.length);
            AbstractHashedMap.HashEntry<K, V>[] tmp = this.data;
            AbstractHashedMap.HashEntry<K, V> loop = tmp[removeIndex];
            AbstractHashedMap.HashEntry<K, V> previous = null;
            while (loop != entry && loop != null) {
                previous = loop;
                loop = loop.next;
            }
            if (loop != null) {
                this.modCount++;
                removeEntry(entry, removeIndex, previous);
                reuseEntry(entry, hashIndex, hashCode, key, value);
                addEntry(entry, hashIndex);
                return;
            }
            throw new IllegalStateException("Entry.next=null, data[removeIndex]=" + this.data[removeIndex] + " previous=" + previous + " key=" + key + " value=" + value + " size=" + this.size + " maxSize=" + this.maxSize + " This should not occur if your keys are immutable, and you have used synchronization properly.");
        } catch (NullPointerException e) {
            StringBuilder sb = new StringBuilder();
            sb.append("NPE, entry=");
            sb.append(entry);
            sb.append(" entryIsHeader=");
            sb.append(entry == this.header);
            sb.append(" key=");
            sb.append(key);
            sb.append(" value=");
            sb.append(value);
            sb.append(" size=");
            sb.append(this.size);
            sb.append(" maxSize=");
            sb.append(this.maxSize);
            sb.append(" This should not occur if your keys are immutable, and you have used synchronization properly.");
            throw new IllegalStateException(sb.toString());
        }
    }

    protected boolean removeLRU(AbstractLinkedMap.LinkEntry<K, V> entry) {
        return true;
    }

    @Override // org.apache.commons.collections4.BoundedMap
    public boolean isFull() {
        return this.size >= this.maxSize;
    }

    @Override // org.apache.commons.collections4.BoundedMap
    public int maxSize() {
        return this.maxSize;
    }

    public boolean isScanUntilRemovable() {
        return this.scanUntilRemovable;
    }

    @Override // org.apache.commons.collections4.map.AbstractHashedMap, java.util.AbstractMap
    public LRUMap<K, V> clone() {
        return (LRUMap) super.clone();
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        doWriteObject(out);
    }

    private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
        in.defaultReadObject();
        doReadObject(in);
    }

    @Override // org.apache.commons.collections4.map.AbstractHashedMap
    protected void doWriteObject(ObjectOutputStream out) throws IOException {
        out.writeInt(this.maxSize);
        super.doWriteObject(out);
    }

    @Override // org.apache.commons.collections4.map.AbstractHashedMap
    protected void doReadObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        this.maxSize = in.readInt();
        super.doReadObject(in);
    }
}
