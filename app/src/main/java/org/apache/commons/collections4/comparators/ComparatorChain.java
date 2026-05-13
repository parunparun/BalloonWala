package org.apache.commons.collections4.comparators;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class ComparatorChain<E> implements Comparator<E>, Serializable {
    private static final long serialVersionUID = -721644942746081630L;
    private final List<Comparator<E>> comparatorChain;
    private boolean isLocked;
    private BitSet orderingBits;

    public ComparatorChain() {
        this(new ArrayList(), new BitSet());
    }

    public ComparatorChain(Comparator<E> comparator) {
        this((Comparator) comparator, false);
    }

    public ComparatorChain(Comparator<E> comparator, boolean reverse) {
        this.orderingBits = null;
        this.isLocked = false;
        ArrayList arrayList = new ArrayList(1);
        this.comparatorChain = arrayList;
        arrayList.add(comparator);
        BitSet bitSet = new BitSet(1);
        this.orderingBits = bitSet;
        if (reverse) {
            bitSet.set(0);
        }
    }

    public ComparatorChain(List<Comparator<E>> list) {
        this(list, new BitSet(list.size()));
    }

    public ComparatorChain(List<Comparator<E>> list, BitSet bits) {
        this.orderingBits = null;
        this.isLocked = false;
        this.comparatorChain = list;
        this.orderingBits = bits;
    }

    public void addComparator(Comparator<E> comparator) {
        addComparator(comparator, false);
    }

    public void addComparator(Comparator<E> comparator, boolean reverse) {
        checkLocked();
        this.comparatorChain.add(comparator);
        if (reverse) {
            this.orderingBits.set(this.comparatorChain.size() - 1);
        }
    }

    public void setComparator(int index, Comparator<E> comparator) throws IndexOutOfBoundsException {
        setComparator(index, comparator, false);
    }

    public void setComparator(int index, Comparator<E> comparator, boolean reverse) {
        checkLocked();
        this.comparatorChain.set(index, comparator);
        if (reverse) {
            this.orderingBits.set(index);
        } else {
            this.orderingBits.clear(index);
        }
    }

    public void setForwardSort(int index) {
        checkLocked();
        this.orderingBits.clear(index);
    }

    public void setReverseSort(int index) {
        checkLocked();
        this.orderingBits.set(index);
    }

    public int size() {
        return this.comparatorChain.size();
    }

    public boolean isLocked() {
        return this.isLocked;
    }

    private void checkLocked() {
        if (this.isLocked) {
            throw new UnsupportedOperationException("Comparator ordering cannot be changed after the first comparison is performed");
        }
    }

    private void checkChainIntegrity() {
        if (this.comparatorChain.size() == 0) {
            throw new UnsupportedOperationException("ComparatorChains must contain at least one Comparator");
        }
    }

    @Override // java.util.Comparator
    public int compare(E o1, E o2) throws UnsupportedOperationException {
        if (!this.isLocked) {
            checkChainIntegrity();
            this.isLocked = true;
        }
        Iterator<Comparator<E>> comparators = this.comparatorChain.iterator();
        int comparatorIndex = 0;
        while (comparators.hasNext()) {
            int retval = comparators.next().compare(o1, o2);
            if (retval == 0) {
                comparatorIndex++;
            } else {
                if (this.orderingBits.get(comparatorIndex)) {
                    if (retval > 0) {
                        return -1;
                    }
                    return 1;
                }
                return retval;
            }
        }
        return 0;
    }

    public int hashCode() {
        List<Comparator<E>> list = this.comparatorChain;
        int hash = list != null ? 0 ^ list.hashCode() : 0;
        BitSet bitSet = this.orderingBits;
        if (bitSet != null) {
            return hash ^ bitSet.hashCode();
        }
        return hash;
    }

    @Override // java.util.Comparator
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || !object.getClass().equals(getClass())) {
            return false;
        }
        ComparatorChain<?> chain = (ComparatorChain) object;
        BitSet bitSet = this.orderingBits;
        if (bitSet != null ? bitSet.equals(chain.orderingBits) : chain.orderingBits == null) {
            List<Comparator<E>> list = this.comparatorChain;
            if (list == null) {
                if (chain.comparatorChain == null) {
                    return true;
                }
            } else if (list.equals(chain.comparatorChain)) {
                return true;
            }
        }
        return false;
    }
}
