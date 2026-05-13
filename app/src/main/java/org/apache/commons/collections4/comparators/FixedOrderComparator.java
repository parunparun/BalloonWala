package org.apache.commons.collections4.comparators;

import java.io.Serializable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class FixedOrderComparator<T> implements Comparator<T>, Serializable {
    private static final long serialVersionUID = 82794675842863201L;
    private final Map<T, Integer> map = new HashMap();
    private int counter = 0;
    private boolean isLocked = false;
    private UnknownObjectBehavior unknownObjectBehavior = UnknownObjectBehavior.EXCEPTION;

    public enum UnknownObjectBehavior {
        BEFORE,
        AFTER,
        EXCEPTION
    }

    public FixedOrderComparator() {
    }

    public FixedOrderComparator(T... items) {
        if (items == null) {
            throw new NullPointerException("The list of items must not be null");
        }
        for (T item : items) {
            add(item);
        }
    }

    public FixedOrderComparator(List<T> items) {
        if (items == null) {
            throw new NullPointerException("The list of items must not be null");
        }
        for (T t : items) {
            add(t);
        }
    }

    public boolean isLocked() {
        return this.isLocked;
    }

    protected void checkLocked() {
        if (isLocked()) {
            throw new UnsupportedOperationException("Cannot modify a FixedOrderComparator after a comparison");
        }
    }

    public UnknownObjectBehavior getUnknownObjectBehavior() {
        return this.unknownObjectBehavior;
    }

    public void setUnknownObjectBehavior(UnknownObjectBehavior unknownObjectBehavior) {
        checkLocked();
        if (unknownObjectBehavior == null) {
            throw new NullPointerException("Unknown object behavior must not be null");
        }
        this.unknownObjectBehavior = unknownObjectBehavior;
    }

    public boolean add(T obj) {
        checkLocked();
        Map<T, Integer> map = this.map;
        int i = this.counter;
        this.counter = i + 1;
        Integer position = map.put(obj, Integer.valueOf(i));
        return position == null;
    }

    public boolean addAsEqual(T existingObj, T newObj) {
        checkLocked();
        Integer position = this.map.get(existingObj);
        if (position == null) {
            throw new IllegalArgumentException(existingObj + " not known to " + this);
        }
        Integer result = this.map.put(newObj, position);
        return result == null;
    }

    @Override // java.util.Comparator
    public int compare(T obj1, T obj2) {
        this.isLocked = true;
        Integer position1 = this.map.get(obj1);
        Integer position2 = this.map.get(obj2);
        if (position1 == null || position2 == null) {
            int i = AnonymousClass1.$SwitchMap$org$apache$commons$collections4$comparators$FixedOrderComparator$UnknownObjectBehavior[this.unknownObjectBehavior.ordinal()];
            if (i == 1) {
                if (position1 == null) {
                    return position2 == null ? 0 : -1;
                }
                return 1;
            }
            if (i == 2) {
                if (position1 == null) {
                    return position2 == null ? 0 : 1;
                }
                return -1;
            }
            if (i == 3) {
                Object unknownObj = position1 == null ? obj1 : obj2;
                throw new IllegalArgumentException("Attempting to compare unknown object " + unknownObj);
            }
            throw new UnsupportedOperationException("Unknown unknownObjectBehavior: " + this.unknownObjectBehavior);
        }
        return position1.compareTo(position2);
    }

    /* JADX INFO: renamed from: org.apache.commons.collections4.comparators.FixedOrderComparator$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$apache$commons$collections4$comparators$FixedOrderComparator$UnknownObjectBehavior;

        static {
            int[] iArr = new int[UnknownObjectBehavior.values().length];
            $SwitchMap$org$apache$commons$collections4$comparators$FixedOrderComparator$UnknownObjectBehavior = iArr;
            try {
                iArr[UnknownObjectBehavior.BEFORE.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$apache$commons$collections4$comparators$FixedOrderComparator$UnknownObjectBehavior[UnknownObjectBehavior.AFTER.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$apache$commons$collections4$comparators$FixedOrderComparator$UnknownObjectBehavior[UnknownObjectBehavior.EXCEPTION.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    public int hashCode() {
        int iHashCode = ((17 * 37) + this.map.hashCode()) * 37;
        UnknownObjectBehavior unknownObjectBehavior = this.unknownObjectBehavior;
        return ((((iHashCode + (unknownObjectBehavior == null ? 0 : unknownObjectBehavior.hashCode())) * 37) + this.counter) * 37) + (!this.isLocked ? 1 : 0);
    }

    @Override // java.util.Comparator
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || !object.getClass().equals(getClass())) {
            return false;
        }
        FixedOrderComparator<?> comp = (FixedOrderComparator) object;
        Map<T, Integer> map = this.map;
        if (map != null ? map.equals(comp.map) : comp.map == null) {
            UnknownObjectBehavior unknownObjectBehavior = this.unknownObjectBehavior;
            if (unknownObjectBehavior != null) {
                UnknownObjectBehavior unknownObjectBehavior2 = comp.unknownObjectBehavior;
                if (unknownObjectBehavior == unknownObjectBehavior2 && this.counter == comp.counter && this.isLocked == comp.isLocked && unknownObjectBehavior == unknownObjectBehavior2) {
                    return true;
                }
            } else if (comp.unknownObjectBehavior == null) {
                return true;
            }
        }
        return false;
    }
}
