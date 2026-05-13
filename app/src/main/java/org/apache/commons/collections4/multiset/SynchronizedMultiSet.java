package org.apache.commons.collections4.multiset;

import java.util.Set;
import org.apache.commons.collections4.MultiSet;
import org.apache.commons.collections4.collection.SynchronizedCollection;

/* JADX INFO: loaded from: classes.dex */
public class SynchronizedMultiSet<E> extends SynchronizedCollection<E> implements MultiSet<E> {
    private static final long serialVersionUID = 20150629;

    public static <E> SynchronizedMultiSet<E> synchronizedMultiSet(MultiSet<E> multiset) {
        return new SynchronizedMultiSet<>(multiset);
    }

    protected SynchronizedMultiSet(MultiSet<E> multiset) {
        super(multiset);
    }

    protected SynchronizedMultiSet(MultiSet<E> multiset, Object lock) {
        super(multiset, lock);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.commons.collections4.collection.SynchronizedCollection
    public MultiSet<E> decorated() {
        return (MultiSet) super.decorated();
    }

    @Override // org.apache.commons.collections4.collection.SynchronizedCollection, java.util.Collection
    public boolean equals(Object object) {
        boolean zEquals;
        if (object == this) {
            return true;
        }
        synchronized (this.lock) {
            zEquals = decorated().equals(object);
        }
        return zEquals;
    }

    @Override // org.apache.commons.collections4.collection.SynchronizedCollection, java.util.Collection
    public int hashCode() {
        int iHashCode;
        synchronized (this.lock) {
            iHashCode = decorated().hashCode();
        }
        return iHashCode;
    }

    @Override // org.apache.commons.collections4.MultiSet
    public int add(E object, int count) {
        int iAdd;
        synchronized (this.lock) {
            iAdd = decorated().add(object, count);
        }
        return iAdd;
    }

    @Override // org.apache.commons.collections4.MultiSet
    public int remove(Object object, int count) {
        int iRemove;
        synchronized (this.lock) {
            iRemove = decorated().remove(object, count);
        }
        return iRemove;
    }

    @Override // org.apache.commons.collections4.MultiSet
    public int getCount(Object object) {
        int count;
        synchronized (this.lock) {
            count = decorated().getCount(object);
        }
        return count;
    }

    @Override // org.apache.commons.collections4.MultiSet
    public int setCount(E object, int count) {
        int count2;
        synchronized (this.lock) {
            count2 = decorated().setCount(object, count);
        }
        return count2;
    }

    @Override // org.apache.commons.collections4.MultiSet
    public Set<E> uniqueSet() {
        SynchronizedSet synchronizedSet;
        synchronized (this.lock) {
            Set<E> set = decorated().uniqueSet();
            synchronizedSet = new SynchronizedSet(set, this.lock);
        }
        return synchronizedSet;
    }

    @Override // org.apache.commons.collections4.MultiSet
    public Set<MultiSet.Entry<E>> entrySet() {
        SynchronizedSet synchronizedSet;
        synchronized (this.lock) {
            Set<MultiSet.Entry<E>> set = decorated().entrySet();
            synchronizedSet = new SynchronizedSet(set, this.lock);
        }
        return synchronizedSet;
    }

    static class SynchronizedSet<T> extends SynchronizedCollection<T> implements Set<T> {
        private static final long serialVersionUID = 20150629;

        SynchronizedSet(Set<T> set, Object lock) {
            super(set, lock);
        }
    }
}
