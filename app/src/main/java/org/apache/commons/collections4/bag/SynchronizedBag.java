package org.apache.commons.collections4.bag;

import java.util.Set;
import org.apache.commons.collections4.Bag;
import org.apache.commons.collections4.collection.SynchronizedCollection;

/* JADX INFO: loaded from: classes.dex */
public class SynchronizedBag<E> extends SynchronizedCollection<E> implements Bag<E> {
    private static final long serialVersionUID = 8084674570753837109L;

    public static <E> SynchronizedBag<E> synchronizedBag(Bag<E> bag) {
        return new SynchronizedBag<>(bag);
    }

    protected SynchronizedBag(Bag<E> bag) {
        super(bag);
    }

    protected SynchronizedBag(Bag<E> bag, Object lock) {
        super(bag, lock);
    }

    protected Bag<E> getBag() {
        return (Bag) decorated();
    }

    @Override // org.apache.commons.collections4.collection.SynchronizedCollection, java.util.Collection
    public boolean equals(Object object) {
        boolean zEquals;
        if (object == this) {
            return true;
        }
        synchronized (this.lock) {
            zEquals = getBag().equals(object);
        }
        return zEquals;
    }

    @Override // org.apache.commons.collections4.collection.SynchronizedCollection, java.util.Collection
    public int hashCode() {
        int iHashCode;
        synchronized (this.lock) {
            iHashCode = getBag().hashCode();
        }
        return iHashCode;
    }

    @Override // org.apache.commons.collections4.Bag
    public boolean add(E object, int count) {
        boolean zAdd;
        synchronized (this.lock) {
            zAdd = getBag().add(object, count);
        }
        return zAdd;
    }

    @Override // org.apache.commons.collections4.Bag
    public boolean remove(Object object, int count) {
        boolean zRemove;
        synchronized (this.lock) {
            zRemove = getBag().remove(object, count);
        }
        return zRemove;
    }

    @Override // org.apache.commons.collections4.Bag
    public Set<E> uniqueSet() {
        SynchronizedBagSet synchronizedBagSet;
        synchronized (this.lock) {
            Set<E> set = getBag().uniqueSet();
            synchronizedBagSet = new SynchronizedBagSet(set, this.lock);
        }
        return synchronizedBagSet;
    }

    @Override // org.apache.commons.collections4.Bag
    public int getCount(Object object) {
        int count;
        synchronized (this.lock) {
            count = getBag().getCount(object);
        }
        return count;
    }

    class SynchronizedBagSet extends SynchronizedCollection<E> implements Set<E> {
        private static final long serialVersionUID = 2990565892366827855L;

        SynchronizedBagSet(Set<E> set, Object lock) {
            super(set, lock);
        }
    }
}
