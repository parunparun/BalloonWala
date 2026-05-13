package org.apache.commons.collections4;

import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import org.apache.commons.collections4.set.ListOrderedSet;
import org.apache.commons.collections4.set.PredicatedNavigableSet;
import org.apache.commons.collections4.set.PredicatedSet;
import org.apache.commons.collections4.set.PredicatedSortedSet;
import org.apache.commons.collections4.set.TransformedNavigableSet;
import org.apache.commons.collections4.set.TransformedSet;
import org.apache.commons.collections4.set.TransformedSortedSet;
import org.apache.commons.collections4.set.UnmodifiableNavigableSet;
import org.apache.commons.collections4.set.UnmodifiableSet;
import org.apache.commons.collections4.set.UnmodifiableSortedSet;

/* JADX INFO: loaded from: classes.dex */
public class SetUtils {
    public static final SortedSet EMPTY_SORTED_SET = UnmodifiableSortedSet.unmodifiableSortedSet(new TreeSet());

    public static abstract class SetView<E> extends AbstractSet<E> {
        protected abstract Iterator<E> createIterator();

        public <S extends Set<E>> void copyInto(S set) {
            CollectionUtils.addAll(set, this);
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Set
        public Iterator<E> iterator() {
            return IteratorUtils.unmodifiableIterator(createIterator());
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public int size() {
            return IteratorUtils.size(iterator());
        }

        public Set<E> toSet() {
            HashSet hashSet = new HashSet(size());
            copyInto(hashSet);
            return hashSet;
        }
    }

    public static <E> SetView<E> difference(final Set<? extends E> a, final Set<? extends E> b) {
        if (a == null || b == null) {
            throw new NullPointerException("Sets must not be null.");
        }
        final Predicate<E> notContainedInB = new Predicate<E>() { // from class: org.apache.commons.collections4.SetUtils.1
            @Override // org.apache.commons.collections4.Predicate
            public boolean evaluate(E object) {
                return !b.contains(object);
            }
        };
        return new SetView<E>() { // from class: org.apache.commons.collections4.SetUtils.2
            @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
            public boolean contains(Object o) {
                return a.contains(o) && !b.contains(o);
            }

            @Override // org.apache.commons.collections4.SetUtils.SetView
            public Iterator<E> createIterator() {
                return IteratorUtils.filteredIterator(a.iterator(), notContainedInB);
            }
        };
    }

    public static <E> SetView<E> disjunction(final Set<? extends E> a, final Set<? extends E> b) {
        if (a == null || b == null) {
            throw new NullPointerException("Sets must not be null.");
        }
        final SetView<E> aMinusB = difference(a, b);
        final SetView<E> bMinusA = difference(b, a);
        return new SetView<E>() { // from class: org.apache.commons.collections4.SetUtils.3
            @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
            public boolean contains(Object o) {
                return a.contains(o) ^ b.contains(o);
            }

            @Override // org.apache.commons.collections4.SetUtils.SetView
            public Iterator<E> createIterator() {
                return IteratorUtils.chainedIterator(aMinusB.iterator(), bMinusA.iterator());
            }

            @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
            public boolean isEmpty() {
                return aMinusB.isEmpty() && bMinusA.isEmpty();
            }

            @Override // org.apache.commons.collections4.SetUtils.SetView, java.util.AbstractCollection, java.util.Collection, java.util.Set
            public int size() {
                return aMinusB.size() + bMinusA.size();
            }
        };
    }

    public static <T> Set<T> emptyIfNull(Set<T> set) {
        return set == null ? Collections.emptySet() : set;
    }

    public static <E> Set<E> emptySet() {
        return Collections.emptySet();
    }

    public static <E> SortedSet<E> emptySortedSet() {
        return EMPTY_SORTED_SET;
    }

    public static <T> int hashCodeForSet(Collection<T> set) {
        if (set == null) {
            return 0;
        }
        int hashCode = 0;
        for (T obj : set) {
            if (obj != null) {
                hashCode += obj.hashCode();
            }
        }
        return hashCode;
    }

    public static <E> HashSet<E> hashSet(E... items) {
        if (items == null) {
            return null;
        }
        return new HashSet<>(Arrays.asList(items));
    }

    public static <E> SetView<E> intersection(final Set<? extends E> a, final Set<? extends E> b) {
        if (a == null || b == null) {
            throw new NullPointerException("Sets must not be null.");
        }
        final Predicate<E> containedInB = new Predicate<E>() { // from class: org.apache.commons.collections4.SetUtils.4
            @Override // org.apache.commons.collections4.Predicate
            public boolean evaluate(E object) {
                return b.contains(object);
            }
        };
        return new SetView<E>() { // from class: org.apache.commons.collections4.SetUtils.5
            @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
            public boolean contains(Object o) {
                return a.contains(o) && b.contains(o);
            }

            @Override // org.apache.commons.collections4.SetUtils.SetView
            public Iterator<E> createIterator() {
                return IteratorUtils.filteredIterator(a.iterator(), containedInB);
            }
        };
    }

    public static boolean isEqualSet(Collection<?> set1, Collection<?> set2) {
        if (set1 == set2) {
            return true;
        }
        if (set1 == null || set2 == null || set1.size() != set2.size()) {
            return false;
        }
        return set1.containsAll(set2);
    }

    public static <E> Set<E> newIdentityHashSet() {
        return Collections.newSetFromMap(new IdentityHashMap());
    }

    public static <E> Set<E> orderedSet(Set<E> set) {
        return ListOrderedSet.listOrderedSet(set);
    }

    public static <E> SortedSet<E> predicatedNavigableSet(NavigableSet<E> set, Predicate<? super E> predicate) {
        return PredicatedNavigableSet.predicatedNavigableSet(set, predicate);
    }

    public static <E> Set<E> predicatedSet(Set<E> set, Predicate<? super E> predicate) {
        return PredicatedSet.predicatedSet(set, predicate);
    }

    public static <E> SortedSet<E> predicatedSortedSet(SortedSet<E> set, Predicate<? super E> predicate) {
        return PredicatedSortedSet.predicatedSortedSet(set, predicate);
    }

    public static <E> Set<E> synchronizedSet(Set<E> set) {
        return Collections.synchronizedSet(set);
    }

    public static <E> SortedSet<E> synchronizedSortedSet(SortedSet<E> set) {
        return Collections.synchronizedSortedSet(set);
    }

    public static <E> SortedSet<E> transformedNavigableSet(NavigableSet<E> set, Transformer<? super E, ? extends E> transformer) {
        return TransformedNavigableSet.transformingNavigableSet(set, transformer);
    }

    public static <E> Set<E> transformedSet(Set<E> set, Transformer<? super E, ? extends E> transformer) {
        return TransformedSet.transformingSet(set, transformer);
    }

    public static <E> SortedSet<E> transformedSortedSet(SortedSet<E> set, Transformer<? super E, ? extends E> transformer) {
        return TransformedSortedSet.transformingSortedSet(set, transformer);
    }

    public static <E> SetView<E> union(final Set<? extends E> a, final Set<? extends E> b) {
        if (a == null || b == null) {
            throw new NullPointerException("Sets must not be null.");
        }
        final SetView<E> bMinusA = difference(b, a);
        return new SetView<E>() { // from class: org.apache.commons.collections4.SetUtils.6
            @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
            public boolean contains(Object o) {
                return a.contains(o) || b.contains(o);
            }

            @Override // org.apache.commons.collections4.SetUtils.SetView
            public Iterator<E> createIterator() {
                return IteratorUtils.chainedIterator(a.iterator(), bMinusA.iterator());
            }

            @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
            public boolean isEmpty() {
                return a.isEmpty() && b.isEmpty();
            }

            @Override // org.apache.commons.collections4.SetUtils.SetView, java.util.AbstractCollection, java.util.Collection, java.util.Set
            public int size() {
                return a.size() + bMinusA.size();
            }
        };
    }

    public static <E> SortedSet<E> unmodifiableNavigableSet(NavigableSet<E> set) {
        return UnmodifiableNavigableSet.unmodifiableNavigableSet(set);
    }

    public static <E> Set<E> unmodifiableSet(E... items) {
        if (items == null) {
            return null;
        }
        return UnmodifiableSet.unmodifiableSet(hashSet(items));
    }

    public static <E> Set<E> unmodifiableSet(Set<? extends E> set) {
        return UnmodifiableSet.unmodifiableSet(set);
    }

    public static <E> SortedSet<E> unmodifiableSortedSet(SortedSet<E> set) {
        return UnmodifiableSortedSet.unmodifiableSortedSet(set);
    }

    private SetUtils() {
    }
}
