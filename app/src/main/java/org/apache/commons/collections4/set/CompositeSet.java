package org.apache.commons.collections4.set;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.iterators.EmptyIterator;
import org.apache.commons.collections4.iterators.IteratorChain;
import org.apache.commons.collections4.list.UnmodifiableList;

/* JADX INFO: loaded from: classes.dex */
public class CompositeSet<E> implements Set<E>, Serializable {
    private static final long serialVersionUID = 5185069727540378940L;
    private final List<Set<E>> all = new ArrayList();
    private SetMutator<E> mutator;

    public interface SetMutator<E> extends Serializable {
        boolean add(CompositeSet<E> compositeSet, List<Set<E>> list, E e);

        boolean addAll(CompositeSet<E> compositeSet, List<Set<E>> list, Collection<? extends E> collection);

        void resolveCollision(CompositeSet<E> compositeSet, Set<E> set, Set<E> set2, Collection<E> collection);
    }

    public CompositeSet() {
    }

    public CompositeSet(Set<E> set) {
        addComposited(set);
    }

    public CompositeSet(Set<E>... sets) {
        addComposited(sets);
    }

    @Override // java.util.Set, java.util.Collection
    public int size() {
        int size = 0;
        for (Set<E> item : this.all) {
            size += item.size();
        }
        return size;
    }

    @Override // java.util.Set, java.util.Collection
    public boolean isEmpty() {
        for (Set<E> item : this.all) {
            if (!item.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override // java.util.Set, java.util.Collection
    public boolean contains(Object obj) {
        for (Set<E> item : this.all) {
            if (item.contains(obj)) {
                return true;
            }
        }
        return false;
    }

    @Override // java.util.Set, java.util.Collection, java.lang.Iterable
    public Iterator<E> iterator() {
        if (this.all.isEmpty()) {
            return EmptyIterator.emptyIterator();
        }
        IteratorChain<E> chain = new IteratorChain<>();
        for (Set<E> item : this.all) {
            chain.addIterator(item.iterator());
        }
        return chain;
    }

    @Override // java.util.Set, java.util.Collection
    public Object[] toArray() {
        Object[] result = new Object[size()];
        int i = 0;
        Iterator<E> it = iterator();
        while (it.hasNext()) {
            result[i] = it.next();
            i++;
        }
        return result;
    }

    @Override // java.util.Set, java.util.Collection
    public <T> T[] toArray(T[] tArr) {
        Object[] objArr;
        int size = size();
        if (tArr.length >= size) {
            objArr = tArr;
        } else {
            objArr = (Object[]) Array.newInstance(tArr.getClass().getComponentType(), size);
        }
        int i = 0;
        Iterator<Set<E>> it = this.all.iterator();
        while (it.hasNext()) {
            Iterator<E> it2 = it.next().iterator();
            while (it2.hasNext()) {
                objArr[i] = it2.next();
                i++;
            }
        }
        if (objArr.length > size) {
            objArr[size] = null;
        }
        return (T[]) objArr;
    }

    @Override // java.util.Set, java.util.Collection
    public boolean add(E obj) {
        SetMutator<E> setMutator = this.mutator;
        if (setMutator == null) {
            throw new UnsupportedOperationException("add() is not supported on CompositeSet without a SetMutator strategy");
        }
        return setMutator.add(this, this.all, obj);
    }

    @Override // java.util.Set, java.util.Collection
    public boolean remove(Object obj) {
        for (Set<E> set : getSets()) {
            if (set.contains(obj)) {
                return set.remove(obj);
            }
        }
        return false;
    }

    @Override // java.util.Set, java.util.Collection
    public boolean containsAll(Collection<?> coll) {
        if (coll == null) {
            return false;
        }
        for (Object item : coll) {
            if (!contains(item)) {
                return false;
            }
        }
        return true;
    }

    @Override // java.util.Set, java.util.Collection
    public boolean addAll(Collection<? extends E> coll) {
        SetMutator<E> setMutator = this.mutator;
        if (setMutator == null) {
            throw new UnsupportedOperationException("addAll() is not supported on CompositeSet without a SetMutator strategy");
        }
        return setMutator.addAll(this, this.all, coll);
    }

    @Override // java.util.Collection
    public boolean removeIf(Predicate<? super E> filter) {
        if (Objects.isNull(filter)) {
            return false;
        }
        boolean changed = false;
        for (Collection<E> item : this.all) {
            changed |= item.removeIf(filter);
        }
        return changed;
    }

    @Override // java.util.Set, java.util.Collection
    public boolean removeAll(Collection<?> coll) {
        if (CollectionUtils.isEmpty(coll)) {
            return false;
        }
        boolean changed = false;
        for (Collection<E> item : this.all) {
            changed |= item.removeAll(coll);
        }
        return changed;
    }

    @Override // java.util.Set, java.util.Collection
    public boolean retainAll(Collection<?> coll) {
        boolean changed = false;
        for (Collection<E> item : this.all) {
            changed |= item.retainAll(coll);
        }
        return changed;
    }

    @Override // java.util.Set, java.util.Collection
    public void clear() {
        for (Collection<E> coll : this.all) {
            coll.clear();
        }
    }

    public void setMutator(SetMutator<E> mutator) {
        this.mutator = mutator;
    }

    public synchronized void addComposited(Set<E> set) {
        if (set != null) {
            for (Set<E> existingSet : getSets()) {
                Collection<E> intersects = CollectionUtils.intersection(existingSet, set);
                if (intersects.size() > 0) {
                    if (this.mutator == null) {
                        throw new UnsupportedOperationException("Collision adding composited set with no SetMutator set");
                    }
                    getMutator().resolveCollision(this, existingSet, set, intersects);
                    if (CollectionUtils.intersection(existingSet, set).size() > 0) {
                        throw new IllegalArgumentException("Attempt to add illegal entry unresolved by SetMutator.resolveCollision()");
                    }
                }
            }
            this.all.add(set);
        }
    }

    public void addComposited(Set<E> set1, Set<E> set2) {
        addComposited(set1);
        addComposited(set2);
    }

    public void addComposited(Set<E>... sets) {
        if (sets != null) {
            for (Set<E> set : sets) {
                addComposited(set);
            }
        }
    }

    public void removeComposited(Set<E> set) {
        this.all.remove(set);
    }

    public Set<E> toSet() {
        return new HashSet(this);
    }

    public List<Set<E>> getSets() {
        return UnmodifiableList.unmodifiableList(this.all);
    }

    protected SetMutator<E> getMutator() {
        return this.mutator;
    }

    @Override // java.util.Set, java.util.Collection
    public boolean equals(Object obj) {
        if (!(obj instanceof Set)) {
            return false;
        }
        Set<?> set = (Set) obj;
        return set.size() == size() && set.containsAll(this);
    }

    @Override // java.util.Set, java.util.Collection
    public int hashCode() {
        int code = 0;
        Iterator<E> it = iterator();
        while (it.hasNext()) {
            E e = it.next();
            code += e == null ? 0 : e.hashCode();
        }
        return code;
    }
}
