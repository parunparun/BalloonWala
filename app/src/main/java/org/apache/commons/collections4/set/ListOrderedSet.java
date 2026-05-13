package org.apache.commons.collections4.set;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.OrderedIterator;
import org.apache.commons.collections4.functors.UniquePredicate;
import org.apache.commons.collections4.iterators.AbstractIteratorDecorator;
import org.apache.commons.collections4.list.UnmodifiableList;

/* JADX INFO: loaded from: classes.dex */
public class ListOrderedSet<E> extends AbstractSerializableSetDecorator<E> {
    private static final long serialVersionUID = -228664372470420141L;
    private final List<E> setOrder;

    public static <E> ListOrderedSet<E> listOrderedSet(Set<E> set, List<E> list) {
        if (set == null) {
            throw new NullPointerException("Set must not be null");
        }
        if (list == null) {
            throw new NullPointerException("List must not be null");
        }
        if (set.size() > 0 || list.size() > 0) {
            throw new IllegalArgumentException("Set and List must be empty");
        }
        return new ListOrderedSet<>(set, list);
    }

    public static <E> ListOrderedSet<E> listOrderedSet(Set<E> set) {
        return new ListOrderedSet<>(set);
    }

    public static <E> ListOrderedSet<E> listOrderedSet(List<E> list) {
        if (list == null) {
            throw new NullPointerException("List must not be null");
        }
        CollectionUtils.filter(list, UniquePredicate.uniquePredicate());
        Set<E> set = new HashSet<>(list);
        return new ListOrderedSet<>(set, list);
    }

    public ListOrderedSet() {
        super(new HashSet());
        this.setOrder = new ArrayList();
    }

    protected ListOrderedSet(Set<E> set) {
        super(set);
        this.setOrder = new ArrayList(set);
    }

    protected ListOrderedSet(Set<E> set, List<E> list) {
        super(set);
        if (list == null) {
            throw new NullPointerException("List must not be null");
        }
        this.setOrder = list;
    }

    public List<E> asList() {
        return UnmodifiableList.unmodifiableList(this.setOrder);
    }

    @Override // org.apache.commons.collections4.collection.AbstractCollectionDecorator, java.util.Collection
    public void clear() {
        decorated().clear();
        this.setOrder.clear();
    }

    @Override // org.apache.commons.collections4.collection.AbstractCollectionDecorator, java.util.Collection, java.lang.Iterable, org.apache.commons.collections4.Bag
    public OrderedIterator<E> iterator() {
        return new OrderedSetIterator(this.setOrder.listIterator(), decorated());
    }

    @Override // org.apache.commons.collections4.collection.AbstractCollectionDecorator, java.util.Collection, org.apache.commons.collections4.Bag
    public boolean add(E object) {
        if (decorated().add(object)) {
            this.setOrder.add(object);
            return true;
        }
        return false;
    }

    @Override // org.apache.commons.collections4.collection.AbstractCollectionDecorator, java.util.Collection
    public boolean addAll(Collection<? extends E> coll) {
        boolean result = false;
        for (E e : coll) {
            result |= add(e);
        }
        return result;
    }

    @Override // org.apache.commons.collections4.collection.AbstractCollectionDecorator, java.util.Collection, org.apache.commons.collections4.Bag
    public boolean remove(Object object) {
        boolean result = decorated().remove(object);
        if (result) {
            this.setOrder.remove(object);
        }
        return result;
    }

    @Override // org.apache.commons.collections4.collection.AbstractCollectionDecorator, java.util.Collection
    public boolean removeIf(Predicate<? super E> filter) {
        if (Objects.isNull(filter)) {
            return false;
        }
        boolean result = decorated().removeIf(filter);
        if (result) {
            this.setOrder.removeIf(filter);
        }
        return result;
    }

    @Override // org.apache.commons.collections4.collection.AbstractCollectionDecorator, java.util.Collection, org.apache.commons.collections4.Bag
    public boolean removeAll(Collection<?> coll) {
        boolean result = false;
        for (Object name : coll) {
            result |= remove(name);
        }
        return result;
    }

    @Override // org.apache.commons.collections4.collection.AbstractCollectionDecorator, java.util.Collection, org.apache.commons.collections4.Bag
    public boolean retainAll(Collection<?> coll) {
        boolean result = decorated().retainAll(coll);
        if (!result) {
            return false;
        }
        if (decorated().size() == 0) {
            this.setOrder.clear();
        } else {
            Iterator<E> it = this.setOrder.iterator();
            while (it.hasNext()) {
                if (!decorated().contains(it.next())) {
                    it.remove();
                }
            }
        }
        return result;
    }

    @Override // org.apache.commons.collections4.collection.AbstractCollectionDecorator, java.util.Collection
    public Object[] toArray() {
        return this.setOrder.toArray();
    }

    @Override // org.apache.commons.collections4.collection.AbstractCollectionDecorator, java.util.Collection
    public <T> T[] toArray(T[] tArr) {
        return (T[]) this.setOrder.toArray(tArr);
    }

    public E get(int index) {
        return this.setOrder.get(index);
    }

    public int indexOf(Object object) {
        return this.setOrder.indexOf(object);
    }

    public void add(int index, E object) {
        if (!contains(object)) {
            decorated().add(object);
            this.setOrder.add(index, object);
        }
    }

    public boolean addAll(int index, Collection<? extends E> coll) {
        boolean changed = false;
        List<E> toAdd = new ArrayList<>();
        for (E e : coll) {
            if (!contains(e)) {
                decorated().add(e);
                toAdd.add(e);
                changed = true;
            }
        }
        if (changed) {
            this.setOrder.addAll(index, toAdd);
        }
        return changed;
    }

    public E remove(int index) {
        E obj = this.setOrder.remove(index);
        remove(obj);
        return obj;
    }

    @Override // org.apache.commons.collections4.collection.AbstractCollectionDecorator
    public String toString() {
        return this.setOrder.toString();
    }

    static class OrderedSetIterator<E> extends AbstractIteratorDecorator<E> implements OrderedIterator<E> {
        private E last;
        private final Collection<E> set;

        private OrderedSetIterator(ListIterator<E> iterator, Collection<E> set) {
            super(iterator);
            this.set = set;
        }

        @Override // org.apache.commons.collections4.iterators.AbstractIteratorDecorator, java.util.Iterator
        public E next() {
            E next = getIterator().next();
            this.last = next;
            return next;
        }

        @Override // org.apache.commons.collections4.iterators.AbstractUntypedIteratorDecorator, java.util.Iterator
        public void remove() {
            this.set.remove(this.last);
            getIterator().remove();
            this.last = null;
        }

        @Override // org.apache.commons.collections4.OrderedIterator
        public boolean hasPrevious() {
            return ((ListIterator) getIterator()).hasPrevious();
        }

        @Override // org.apache.commons.collections4.OrderedIterator
        public E previous() {
            E e = (E) ((ListIterator) getIterator()).previous();
            this.last = e;
            return e;
        }
    }
}
