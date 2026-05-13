package org.apache.commons.collections4.bag;

import java.util.Comparator;
import org.apache.commons.collections4.Bag;
import org.apache.commons.collections4.SortedBag;

/* JADX INFO: loaded from: classes.dex */
public class SynchronizedSortedBag<E> extends SynchronizedBag<E> implements SortedBag<E> {
    private static final long serialVersionUID = 722374056718497858L;

    public static <E> SynchronizedSortedBag<E> synchronizedSortedBag(SortedBag<E> bag) {
        return new SynchronizedSortedBag<>(bag);
    }

    protected SynchronizedSortedBag(SortedBag<E> bag) {
        super(bag);
    }

    protected SynchronizedSortedBag(Bag<E> bag, Object lock) {
        super(bag, lock);
    }

    protected SortedBag<E> getSortedBag() {
        return (SortedBag) decorated();
    }

    @Override // org.apache.commons.collections4.SortedBag
    public synchronized E first() {
        E eFirst;
        synchronized (this.lock) {
            try {
                eFirst = getSortedBag().first();
            } finally {
                th = th;
                while (true) {
                    try {
                    } catch (Throwable th) {
                        th = th;
                    }
                }
            }
        }
        return eFirst;
    }

    @Override // org.apache.commons.collections4.SortedBag
    public synchronized E last() {
        E eLast;
        synchronized (this.lock) {
            try {
                eLast = getSortedBag().last();
            } finally {
                th = th;
                while (true) {
                    try {
                    } catch (Throwable th) {
                        th = th;
                    }
                }
            }
        }
        return eLast;
    }

    @Override // org.apache.commons.collections4.SortedBag
    public synchronized Comparator<? super E> comparator() {
        Comparator<? super E> comparator;
        synchronized (this.lock) {
            try {
                comparator = getSortedBag().comparator();
            } finally {
                th = th;
                while (true) {
                    try {
                    } catch (Throwable th) {
                        th = th;
                    }
                }
            }
        }
        return comparator;
    }
}
