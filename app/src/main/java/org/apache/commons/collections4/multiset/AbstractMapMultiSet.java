package org.apache.commons.collections4.multiset;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import org.apache.commons.collections4.MultiSet;
import org.apache.commons.collections4.iterators.AbstractIteratorDecorator;
import org.apache.commons.collections4.multiset.AbstractMultiSet;

/* JADX INFO: loaded from: classes.dex */
public abstract class AbstractMapMultiSet<E> extends AbstractMultiSet<E> {
    private transient Map<E, MutableInteger> map;
    private transient int modCount;
    private transient int size;

    static /* synthetic */ int access$210(AbstractMapMultiSet x0) {
        int i = x0.size;
        x0.size = i - 1;
        return i;
    }

    protected AbstractMapMultiSet() {
    }

    protected AbstractMapMultiSet(Map<E, MutableInteger> map) {
        this.map = map;
    }

    protected Map<E, MutableInteger> getMap() {
        return this.map;
    }

    protected void setMap(Map<E, MutableInteger> map) {
        this.map = map;
    }

    @Override // org.apache.commons.collections4.multiset.AbstractMultiSet, java.util.AbstractCollection, java.util.Collection, org.apache.commons.collections4.MultiSet
    public int size() {
        return this.size;
    }

    @Override // java.util.AbstractCollection, java.util.Collection
    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    @Override // org.apache.commons.collections4.multiset.AbstractMultiSet, org.apache.commons.collections4.MultiSet
    public int getCount(Object object) {
        MutableInteger count = this.map.get(object);
        if (count != null) {
            return count.value;
        }
        return 0;
    }

    @Override // org.apache.commons.collections4.multiset.AbstractMultiSet, java.util.AbstractCollection, java.util.Collection
    public boolean contains(Object object) {
        return this.map.containsKey(object);
    }

    @Override // org.apache.commons.collections4.multiset.AbstractMultiSet, java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, org.apache.commons.collections4.MultiSet
    public Iterator<E> iterator() {
        return new MapBasedMultiSetIterator(this);
    }

    private static class MapBasedMultiSetIterator<E> implements Iterator<E> {
        private final Iterator<Map.Entry<E, MutableInteger>> entryIterator;
        private int itemCount;
        private final int mods;
        private final AbstractMapMultiSet<E> parent;
        private Map.Entry<E, MutableInteger> current = null;
        private boolean canRemove = false;

        public MapBasedMultiSetIterator(AbstractMapMultiSet<E> parent) {
            this.parent = parent;
            this.entryIterator = ((AbstractMapMultiSet) parent).map.entrySet().iterator();
            this.mods = ((AbstractMapMultiSet) parent).modCount;
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return this.itemCount > 0 || this.entryIterator.hasNext();
        }

        @Override // java.util.Iterator
        public E next() {
            if (((AbstractMapMultiSet) this.parent).modCount != this.mods) {
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
            if (((AbstractMapMultiSet) this.parent).modCount != this.mods) {
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
            AbstractMapMultiSet.access$210(this.parent);
            this.canRemove = false;
        }
    }

    @Override // org.apache.commons.collections4.multiset.AbstractMultiSet, org.apache.commons.collections4.MultiSet
    public int add(E object, int occurrences) {
        if (occurrences < 0) {
            throw new IllegalArgumentException("Occurrences must not be negative.");
        }
        MutableInteger mut = this.map.get(object);
        int oldCount = mut != null ? mut.value : 0;
        if (occurrences > 0) {
            this.modCount++;
            this.size += occurrences;
            if (mut == null) {
                this.map.put(object, new MutableInteger(occurrences));
            } else {
                mut.value += occurrences;
            }
        }
        return oldCount;
    }

    @Override // org.apache.commons.collections4.multiset.AbstractMultiSet, java.util.AbstractCollection, java.util.Collection
    public void clear() {
        this.modCount++;
        this.map.clear();
        this.size = 0;
    }

    @Override // org.apache.commons.collections4.multiset.AbstractMultiSet, org.apache.commons.collections4.MultiSet
    public int remove(Object object, int occurrences) {
        if (occurrences < 0) {
            throw new IllegalArgumentException("Occurrences must not be negative.");
        }
        MutableInteger mut = this.map.get(object);
        if (mut == null) {
            return 0;
        }
        int oldCount = mut.value;
        if (occurrences > 0) {
            this.modCount++;
            if (occurrences < mut.value) {
                mut.value -= occurrences;
                this.size -= occurrences;
            } else {
                this.map.remove(object);
                this.size -= mut.value;
                mut.value = 0;
            }
        }
        return oldCount;
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

    @Override // org.apache.commons.collections4.multiset.AbstractMultiSet
    protected Iterator<E> createUniqueSetIterator() {
        return new UniqueSetIterator(getMap().keySet().iterator(), this);
    }

    @Override // org.apache.commons.collections4.multiset.AbstractMultiSet
    protected int uniqueElements() {
        return this.map.size();
    }

    @Override // org.apache.commons.collections4.multiset.AbstractMultiSet
    protected Iterator<MultiSet.Entry<E>> createEntrySetIterator() {
        return new EntrySetIterator(this.map.entrySet().iterator(), this);
    }

    protected static class UniqueSetIterator<E> extends AbstractIteratorDecorator<E> {
        protected boolean canRemove;
        protected E lastElement;
        protected final AbstractMapMultiSet<E> parent;

        protected UniqueSetIterator(Iterator<E> iterator, AbstractMapMultiSet<E> parent) {
            super(iterator);
            this.lastElement = null;
            this.canRemove = false;
            this.parent = parent;
        }

        @Override // org.apache.commons.collections4.iterators.AbstractIteratorDecorator, java.util.Iterator
        public E next() {
            E e = (E) super.next();
            this.lastElement = e;
            this.canRemove = true;
            return e;
        }

        @Override // org.apache.commons.collections4.iterators.AbstractUntypedIteratorDecorator, java.util.Iterator
        public void remove() {
            if (!this.canRemove) {
                throw new IllegalStateException("Iterator remove() can only be called once after next()");
            }
            int count = this.parent.getCount(this.lastElement);
            super.remove();
            this.parent.remove(this.lastElement, count);
            this.lastElement = null;
            this.canRemove = false;
        }
    }

    protected static class EntrySetIterator<E> implements Iterator<MultiSet.Entry<E>> {
        protected final Iterator<Map.Entry<E, MutableInteger>> decorated;
        protected final AbstractMapMultiSet<E> parent;
        protected MultiSet.Entry<E> last = null;
        protected boolean canRemove = false;

        protected EntrySetIterator(Iterator<Map.Entry<E, MutableInteger>> iterator, AbstractMapMultiSet<E> parent) {
            this.decorated = iterator;
            this.parent = parent;
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return this.decorated.hasNext();
        }

        @Override // java.util.Iterator
        public MultiSet.Entry<E> next() {
            MultiSetEntry multiSetEntry = new MultiSetEntry(this.decorated.next());
            this.last = multiSetEntry;
            this.canRemove = true;
            return multiSetEntry;
        }

        @Override // java.util.Iterator
        public void remove() {
            if (!this.canRemove) {
                throw new IllegalStateException("Iterator remove() can only be called once after next()");
            }
            this.decorated.remove();
            this.last = null;
            this.canRemove = false;
        }
    }

    protected static class MultiSetEntry<E> extends AbstractMultiSet.AbstractEntry<E> {
        protected final Map.Entry<E, MutableInteger> parentEntry;

        protected MultiSetEntry(Map.Entry<E, MutableInteger> parentEntry) {
            this.parentEntry = parentEntry;
        }

        @Override // org.apache.commons.collections4.MultiSet.Entry
        public E getElement() {
            return this.parentEntry.getKey();
        }

        @Override // org.apache.commons.collections4.MultiSet.Entry
        public int getCount() {
            return this.parentEntry.getValue().value;
        }
    }

    @Override // org.apache.commons.collections4.multiset.AbstractMultiSet
    protected void doWriteObject(ObjectOutputStream out) throws IOException {
        out.writeInt(this.map.size());
        for (Map.Entry<E, MutableInteger> entry : this.map.entrySet()) {
            out.writeObject(entry.getKey());
            out.writeInt(entry.getValue().value);
        }
    }

    @Override // org.apache.commons.collections4.multiset.AbstractMultiSet
    protected void doReadObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        int i = objectInputStream.readInt();
        for (int i2 = 0; i2 < i; i2++) {
            Object object = objectInputStream.readObject();
            int i3 = objectInputStream.readInt();
            this.map.put((E) object, new MutableInteger(i3));
            this.size += i3;
        }
    }

    @Override // java.util.AbstractCollection, java.util.Collection
    public Object[] toArray() {
        Object[] result = new Object[size()];
        int i = 0;
        for (Map.Entry<E, MutableInteger> entry : this.map.entrySet()) {
            E current = entry.getKey();
            MutableInteger count = entry.getValue();
            int index = count.value;
            while (index > 0) {
                result[i] = current;
                index--;
                i++;
            }
        }
        return result;
    }

    @Override // java.util.AbstractCollection, java.util.Collection
    public <T> T[] toArray(T[] tArr) {
        int size = size();
        if (tArr.length < size) {
            tArr = (T[]) ((Object[]) Array.newInstance(tArr.getClass().getComponentType(), size));
        }
        int i = 0;
        for (Map.Entry<E, MutableInteger> entry : this.map.entrySet()) {
            E key = entry.getKey();
            int i2 = entry.getValue().value;
            while (i2 > 0) {
                tArr[i] = key;
                i2--;
                i++;
            }
        }
        while (i < tArr.length) {
            tArr[i] = null;
            i++;
        }
        return tArr;
    }

    @Override // org.apache.commons.collections4.multiset.AbstractMultiSet, java.util.Collection, org.apache.commons.collections4.MultiSet
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof MultiSet)) {
            return false;
        }
        MultiSet<?> other = (MultiSet) object;
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

    @Override // org.apache.commons.collections4.multiset.AbstractMultiSet, java.util.Collection, org.apache.commons.collections4.MultiSet
    public int hashCode() {
        int total = 0;
        for (Map.Entry<E, MutableInteger> entry : this.map.entrySet()) {
            E element = entry.getKey();
            MutableInteger count = entry.getValue();
            total += (element == null ? 0 : element.hashCode()) ^ count.value;
        }
        return total;
    }
}
