package org.apache.commons.collections4.bag;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.apache.commons.collections4.Bag;
import org.apache.commons.collections4.set.UnmodifiableSet;

/* JADX INFO: loaded from: classes.dex */
public abstract class AbstractMapBag<E> implements Bag<E> {
    private transient Map<E, MutableInteger> map;
    private transient int modCount;
    private int size;
    private transient Set<E> uniqueSet;

    static /* synthetic */ int access$210(AbstractMapBag x0) {
        int i = x0.size;
        x0.size = i - 1;
        return i;
    }

    protected AbstractMapBag() {
    }

    protected AbstractMapBag(Map<E, MutableInteger> map) {
        this.map = map;
    }

    protected Map<E, MutableInteger> getMap() {
        return this.map;
    }

    @Override // org.apache.commons.collections4.Bag, java.util.Collection
    public int size() {
        return this.size;
    }

    @Override // java.util.Collection
    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    @Override // org.apache.commons.collections4.Bag
    public int getCount(Object object) {
        MutableInteger count = this.map.get(object);
        if (count != null) {
            return count.value;
        }
        return 0;
    }

    @Override // java.util.Collection
    public boolean contains(Object object) {
        return this.map.containsKey(object);
    }

    @Override // org.apache.commons.collections4.Bag, java.util.Collection
    public boolean containsAll(Collection<?> coll) {
        if (coll instanceof Bag) {
            return containsAll((Bag<?>) coll);
        }
        return containsAll((Bag<?>) new HashBag(coll));
    }

    boolean containsAll(Bag<?> other) {
        for (Object current : other.uniqueSet()) {
            if (getCount(current) < other.getCount(current)) {
                return false;
            }
        }
        return true;
    }

    @Override // org.apache.commons.collections4.Bag, java.util.Collection, java.lang.Iterable
    public Iterator<E> iterator() {
        return new BagIterator(this);
    }

    static class BagIterator<E> implements Iterator<E> {
        private final Iterator<Map.Entry<E, MutableInteger>> entryIterator;
        private int itemCount;
        private final int mods;
        private final AbstractMapBag<E> parent;
        private Map.Entry<E, MutableInteger> current = null;
        private boolean canRemove = false;

        public BagIterator(AbstractMapBag<E> parent) {
            this.parent = parent;
            this.entryIterator = ((AbstractMapBag) parent).map.entrySet().iterator();
            this.mods = ((AbstractMapBag) parent).modCount;
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return this.itemCount > 0 || this.entryIterator.hasNext();
        }

        @Override // java.util.Iterator
        public E next() {
            if (((AbstractMapBag) this.parent).modCount != this.mods) {
                throw new ConcurrentModificationException();
            }
            if (this.itemCount == 0) {
                Map.Entry<E, MutableInteger> next = this.entryIterator.next();
                this.current = next;
                this.itemCount = next.getValue().value;
            }
            this.canRemove = true;
            this.itemCount--;
            return this.current.getKey();
        }

        @Override // java.util.Iterator
        public void remove() {
            if (((AbstractMapBag) this.parent).modCount != this.mods) {
                throw new ConcurrentModificationException();
            }
            if (!this.canRemove) {
                throw new IllegalStateException();
            }
            MutableInteger mut = this.current.getValue();
            if (mut.value > 1) {
                mut.value--;
            } else {
                this.entryIterator.remove();
            }
            AbstractMapBag.access$210(this.parent);
            this.canRemove = false;
        }
    }

    @Override // org.apache.commons.collections4.Bag, java.util.Collection
    public boolean add(E object) {
        return add(object, 1);
    }

    @Override // org.apache.commons.collections4.Bag
    public boolean add(E object, int nCopies) {
        this.modCount++;
        if (nCopies <= 0) {
            return false;
        }
        MutableInteger mut = this.map.get(object);
        this.size += nCopies;
        if (mut == null) {
            this.map.put(object, new MutableInteger(nCopies));
            return true;
        }
        mut.value += nCopies;
        return false;
    }

    @Override // java.util.Collection
    public boolean addAll(Collection<? extends E> coll) {
        boolean changed = false;
        Iterator<? extends E> i = coll.iterator();
        while (i.hasNext()) {
            boolean added = add(i.next());
            changed = changed || added;
        }
        return changed;
    }

    @Override // java.util.Collection
    public void clear() {
        this.modCount++;
        this.map.clear();
        this.size = 0;
    }

    @Override // org.apache.commons.collections4.Bag, java.util.Collection
    public boolean remove(Object object) {
        MutableInteger mut = this.map.get(object);
        if (mut == null) {
            return false;
        }
        this.modCount++;
        this.map.remove(object);
        this.size -= mut.value;
        return true;
    }

    @Override // org.apache.commons.collections4.Bag
    public boolean remove(Object object, int nCopies) {
        MutableInteger mut = this.map.get(object);
        if (mut == null || nCopies <= 0) {
            return false;
        }
        this.modCount++;
        if (nCopies < mut.value) {
            mut.value -= nCopies;
            this.size -= nCopies;
        } else {
            this.map.remove(object);
            this.size -= mut.value;
        }
        return true;
    }

    @Override // org.apache.commons.collections4.Bag, java.util.Collection
    public boolean removeAll(Collection<?> coll) {
        boolean result = false;
        if (coll != null) {
            Iterator<?> i = coll.iterator();
            while (i.hasNext()) {
                boolean z = true;
                boolean changed = remove(i.next(), 1);
                if (!result && !changed) {
                    z = false;
                }
                result = z;
            }
        }
        return result;
    }

    @Override // org.apache.commons.collections4.Bag, java.util.Collection
    public boolean retainAll(Collection<?> coll) {
        if (coll instanceof Bag) {
            return retainAll((Bag<?>) coll);
        }
        return retainAll((Bag<?>) new HashBag(coll));
    }

    boolean retainAll(Bag<?> other) {
        Bag<E> excess = new HashBag<>();
        for (E current : uniqueSet()) {
            int myCount = getCount(current);
            int otherCount = other.getCount(current);
            if (1 <= otherCount && otherCount <= myCount) {
                excess.add(current, myCount - otherCount);
            } else {
                excess.add(current, myCount);
            }
        }
        if (excess.isEmpty()) {
            return false;
        }
        boolean result = removeAll(excess);
        return result;
    }

    protected static class MutableInteger {
        protected int value;

        MutableInteger(int value) {
            this.value = value;
        }

        public boolean equals(Object obj) {
            return (obj instanceof MutableInteger) && ((MutableInteger) obj).value == this.value;
        }

        public int hashCode() {
            return this.value;
        }
    }

    @Override // java.util.Collection
    public Object[] toArray() {
        Object[] result = new Object[size()];
        int i = 0;
        for (E current : this.map.keySet()) {
            int index = getCount(current);
            while (index > 0) {
                result[i] = current;
                index--;
                i++;
            }
        }
        return result;
    }

    @Override // java.util.Collection
    public <T> T[] toArray(T[] tArr) {
        int size = size();
        if (tArr.length < size) {
            tArr = (T[]) ((Object[]) Array.newInstance(tArr.getClass().getComponentType(), size));
        }
        int i = 0;
        for (E e : this.map.keySet()) {
            int count = getCount(e);
            while (count > 0) {
                tArr[i] = e;
                count--;
                i++;
            }
        }
        while (i < tArr.length) {
            tArr[i] = null;
            i++;
        }
        return tArr;
    }

    @Override // org.apache.commons.collections4.Bag
    public Set<E> uniqueSet() {
        if (this.uniqueSet == null) {
            this.uniqueSet = UnmodifiableSet.unmodifiableSet(this.map.keySet());
        }
        return this.uniqueSet;
    }

    protected void doWriteObject(ObjectOutputStream out) throws IOException {
        out.writeInt(this.map.size());
        for (Map.Entry<E, MutableInteger> entry : this.map.entrySet()) {
            out.writeObject(entry.getKey());
            out.writeInt(entry.getValue().value);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    protected void doReadObject(Map<E, MutableInteger> map, ObjectInputStream in) throws IOException, ClassNotFoundException {
        this.map = map;
        int entrySize = in.readInt();
        for (int i = 0; i < entrySize; i++) {
            Object object = in.readObject();
            int count = in.readInt();
            map.put(object, new MutableInteger(count));
            this.size += count;
        }
    }

    @Override // java.util.Collection
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof Bag)) {
            return false;
        }
        Bag<?> other = (Bag) object;
        if (other.size() != size()) {
            return false;
        }
        for (E element : this.map.keySet()) {
            if (other.getCount(element) != getCount(element)) {
                return false;
            }
        }
        return true;
    }

    @Override // java.util.Collection
    public int hashCode() {
        int total = 0;
        for (Map.Entry<E, MutableInteger> entry : this.map.entrySet()) {
            E element = entry.getKey();
            MutableInteger count = entry.getValue();
            total += (element == null ? 0 : element.hashCode()) ^ count.value;
        }
        return total;
    }

    public String toString() {
        if (size() == 0) {
            return "[]";
        }
        StringBuilder buf = new StringBuilder();
        buf.append('[');
        Iterator<E> it = uniqueSet().iterator();
        while (it.hasNext()) {
            Object current = it.next();
            int count = getCount(current);
            buf.append(count);
            buf.append(':');
            buf.append(current);
            if (it.hasNext()) {
                buf.append(',');
            }
        }
        buf.append(']');
        return buf.toString();
    }
}
