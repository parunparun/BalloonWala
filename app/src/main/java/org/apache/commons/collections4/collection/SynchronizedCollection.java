package org.apache.commons.collections4.collection;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Predicate;

/* JADX INFO: loaded from: classes.dex */
public class SynchronizedCollection<E> implements Collection<E>, Serializable {
    private static final long serialVersionUID = 2412805092710877986L;
    private final Collection<E> collection;
    protected final Object lock;

    public static <T> SynchronizedCollection<T> synchronizedCollection(Collection<T> coll) {
        return new SynchronizedCollection<>(coll);
    }

    protected SynchronizedCollection(Collection<E> collection) {
        if (collection == null) {
            throw new NullPointerException("Collection must not be null.");
        }
        this.collection = collection;
        this.lock = this;
    }

    protected SynchronizedCollection(Collection<E> collection, Object lock) {
        if (collection == null) {
            throw new NullPointerException("Collection must not be null.");
        }
        if (lock == null) {
            throw new NullPointerException("Lock must not be null.");
        }
        this.collection = collection;
        this.lock = lock;
    }

    protected Collection<E> decorated() {
        return this.collection;
    }

    @Override // java.util.Collection
    public boolean add(E object) {
        boolean zAdd;
        synchronized (this.lock) {
            zAdd = decorated().add(object);
        }
        return zAdd;
    }

    @Override // java.util.Collection
    public boolean addAll(Collection<? extends E> coll) {
        boolean zAddAll;
        synchronized (this.lock) {
            zAddAll = decorated().addAll(coll);
        }
        return zAddAll;
    }

    @Override // java.util.Collection
    public void clear() {
        synchronized (this.lock) {
            decorated().clear();
        }
    }

    @Override // java.util.Collection
    public boolean contains(Object object) {
        boolean zContains;
        synchronized (this.lock) {
            zContains = decorated().contains(object);
        }
        return zContains;
    }

    @Override // java.util.Collection
    public boolean containsAll(Collection<?> coll) {
        boolean zContainsAll;
        synchronized (this.lock) {
            zContainsAll = decorated().containsAll(coll);
        }
        return zContainsAll;
    }

    @Override // java.util.Collection
    public boolean isEmpty() {
        boolean zIsEmpty;
        synchronized (this.lock) {
            zIsEmpty = decorated().isEmpty();
        }
        return zIsEmpty;
    }

    @Override // java.util.Collection, java.lang.Iterable
    public Iterator<E> iterator() {
        return decorated().iterator();
    }

    @Override // java.util.Collection
    public Object[] toArray() {
        Object[] array;
        synchronized (this.lock) {
            array = decorated().toArray();
        }
        return array;
    }

    @Override // java.util.Collection
    public <T> T[] toArray(T[] tArr) {
        T[] tArr2;
        synchronized (this.lock) {
            tArr2 = (T[]) decorated().toArray(tArr);
        }
        return tArr2;
    }

    @Override // java.util.Collection
    public boolean remove(Object object) {
        boolean zRemove;
        synchronized (this.lock) {
            zRemove = decorated().remove(object);
        }
        return zRemove;
    }

    @Override // java.util.Collection
    public boolean removeIf(Predicate<? super E> filter) {
        boolean zRemoveIf;
        synchronized (this.lock) {
            zRemoveIf = decorated().removeIf(filter);
        }
        return zRemoveIf;
    }

    @Override // java.util.Collection
    public boolean removeAll(Collection<?> coll) {
        boolean zRemoveAll;
        synchronized (this.lock) {
            zRemoveAll = decorated().removeAll(coll);
        }
        return zRemoveAll;
    }

    @Override // java.util.Collection
    public boolean retainAll(Collection<?> coll) {
        boolean zRetainAll;
        synchronized (this.lock) {
            zRetainAll = decorated().retainAll(coll);
        }
        return zRetainAll;
    }

    @Override // java.util.Collection
    public int size() {
        int size;
        synchronized (this.lock) {
            size = decorated().size();
        }
        return size;
    }

    @Override // java.util.Collection
    public boolean equals(Object object) {
        synchronized (this.lock) {
            boolean z = true;
            try {
                if (object == this) {
                    return true;
                }
                if (object != this && !decorated().equals(object)) {
                    z = false;
                }
                return z;
            } finally {
            }
        }
    }

    @Override // java.util.Collection
    public int hashCode() {
        int iHashCode;
        synchronized (this.lock) {
            iHashCode = decorated().hashCode();
        }
        return iHashCode;
    }

    public String toString() {
        String string;
        synchronized (this.lock) {
            string = decorated().toString();
        }
        return string;
    }
}
