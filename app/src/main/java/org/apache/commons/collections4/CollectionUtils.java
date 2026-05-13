package org.apache.commons.collections4;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import org.apache.commons.collections4.bag.HashBag;
import org.apache.commons.collections4.collection.PredicatedCollection;
import org.apache.commons.collections4.collection.SynchronizedCollection;
import org.apache.commons.collections4.collection.TransformedCollection;
import org.apache.commons.collections4.collection.UnmodifiableBoundedCollection;
import org.apache.commons.collections4.collection.UnmodifiableCollection;
import org.apache.commons.collections4.functors.TruePredicate;
import org.apache.commons.collections4.iterators.CollatingIterator;
import org.apache.commons.collections4.iterators.PermutationIterator;

/* JADX INFO: loaded from: classes.dex */
public class CollectionUtils {
    public static final Collection EMPTY_COLLECTION = Collections.emptyList();

    private static class CardinalityHelper<O> {
        final Map<O, Integer> cardinalityA;
        final Map<O, Integer> cardinalityB;

        public CardinalityHelper(Iterable<? extends O> a, Iterable<? extends O> b) {
            this.cardinalityA = CollectionUtils.getCardinalityMap(a);
            this.cardinalityB = CollectionUtils.getCardinalityMap(b);
        }

        public final int max(Object obj) {
            return Math.max(freqA(obj), freqB(obj));
        }

        public final int min(Object obj) {
            return Math.min(freqA(obj), freqB(obj));
        }

        public int freqA(Object obj) {
            return getFreq(obj, this.cardinalityA);
        }

        public int freqB(Object obj) {
            return getFreq(obj, this.cardinalityB);
        }

        private int getFreq(Object obj, Map<?, Integer> freqMap) {
            Integer count = freqMap.get(obj);
            if (count != null) {
                return count.intValue();
            }
            return 0;
        }
    }

    private static class SetOperationCardinalityHelper<O> extends CardinalityHelper<O> implements Iterable<O> {
        private final Set<O> elements;
        private final List<O> newList;

        public SetOperationCardinalityHelper(Iterable<? extends O> a, Iterable<? extends O> b) {
            super(a, b);
            HashSet hashSet = new HashSet();
            this.elements = hashSet;
            CollectionUtils.addAll(hashSet, a);
            CollectionUtils.addAll(this.elements, b);
            this.newList = new ArrayList(this.elements.size());
        }

        @Override // java.lang.Iterable
        public Iterator<O> iterator() {
            return this.elements.iterator();
        }

        public void setCardinality(O obj, int count) {
            for (int i = 0; i < count; i++) {
                this.newList.add(obj);
            }
        }

        public Collection<O> list() {
            return this.newList;
        }
    }

    private CollectionUtils() {
    }

    public static <T> Collection<T> emptyCollection() {
        return EMPTY_COLLECTION;
    }

    public static <T> Collection<T> emptyIfNull(Collection<T> collection) {
        return collection == null ? emptyCollection() : collection;
    }

    public static <O> Collection<O> union(Iterable<? extends O> a, Iterable<? extends O> b) {
        SetOperationCardinalityHelper<O> helper = new SetOperationCardinalityHelper<>(a, b);
        for (O obj : helper) {
            helper.setCardinality(obj, helper.max(obj));
        }
        return helper.list();
    }

    public static <O> Collection<O> intersection(Iterable<? extends O> a, Iterable<? extends O> b) {
        SetOperationCardinalityHelper<O> helper = new SetOperationCardinalityHelper<>(a, b);
        for (O obj : helper) {
            helper.setCardinality(obj, helper.min(obj));
        }
        return helper.list();
    }

    public static <O> Collection<O> disjunction(Iterable<? extends O> a, Iterable<? extends O> b) {
        SetOperationCardinalityHelper<O> helper = new SetOperationCardinalityHelper<>(a, b);
        for (O obj : helper) {
            helper.setCardinality(obj, helper.max(obj) - helper.min(obj));
        }
        return helper.list();
    }

    public static <O> Collection<O> subtract(Iterable<? extends O> a, Iterable<? extends O> b) {
        Predicate<O> p = TruePredicate.truePredicate();
        return subtract(a, b, p);
    }

    public static <O> Collection<O> subtract(Iterable<? extends O> a, Iterable<? extends O> b, Predicate<O> p) {
        ArrayList<O> list = new ArrayList<>();
        HashBag<O> bag = new HashBag<>();
        for (O element : b) {
            if (p.evaluate(element)) {
                bag.add(element);
            }
        }
        for (O element2 : a) {
            if (!bag.remove(element2, 1)) {
                list.add(element2);
            }
        }
        return list;
    }

    public static boolean containsAll(Collection<?> coll1, Collection<?> coll2) {
        if (coll2.isEmpty()) {
            return true;
        }
        Set<Object> elementsAlreadySeen = new HashSet<>();
        for (Object nextElement : coll2) {
            if (!elementsAlreadySeen.contains(nextElement)) {
                boolean foundCurrentElement = false;
                for (Object p : coll1) {
                    elementsAlreadySeen.add(p);
                    if (nextElement != null) {
                        if (nextElement.equals(p)) {
                            foundCurrentElement = true;
                            break;
                        }
                    } else {
                        if (p == null) {
                            foundCurrentElement = true;
                            break;
                        }
                    }
                }
                if (!foundCurrentElement) {
                    return false;
                }
            }
        }
        return true;
    }

    public static <T> boolean containsAny(Collection<?> coll1, T... coll2) {
        if (coll1.size() < coll2.length) {
            for (Object aColl1 : coll1) {
                if (ArrayUtils.contains(coll2, aColl1)) {
                    return true;
                }
            }
        } else {
            for (T t : coll2) {
                if (coll1.contains(t)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean containsAny(Collection<?> coll1, Collection<?> coll2) {
        if (coll1.size() < coll2.size()) {
            for (Object aColl1 : coll1) {
                if (coll2.contains(aColl1)) {
                    return true;
                }
            }
            return false;
        }
        for (Object aColl2 : coll2) {
            if (coll1.contains(aColl2)) {
                return true;
            }
        }
        return false;
    }

    public static <O> Map<O, Integer> getCardinalityMap(Iterable<? extends O> coll) {
        Map<O, Integer> count = new HashMap<>();
        for (O obj : coll) {
            Integer c = count.get(obj);
            if (c != null) {
                count.put(obj, Integer.valueOf(c.intValue() + 1));
            } else {
                count.put(obj, 1);
            }
        }
        return count;
    }

    public static boolean isSubCollection(Collection<?> a, Collection<?> b) {
        CardinalityHelper<Object> helper = new CardinalityHelper<>(a, b);
        for (Object obj : a) {
            if (helper.freqA(obj) > helper.freqB(obj)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isProperSubCollection(Collection<?> a, Collection<?> b) {
        return a.size() < b.size() && isSubCollection(a, b);
    }

    public static boolean isEqualCollection(Collection<?> a, Collection<?> b) {
        if (a.size() != b.size()) {
            return false;
        }
        CardinalityHelper<Object> helper = new CardinalityHelper<>(a, b);
        if (helper.cardinalityA.size() != helper.cardinalityB.size()) {
            return false;
        }
        for (Object obj : helper.cardinalityA.keySet()) {
            if (helper.freqA(obj) != helper.freqB(obj)) {
                return false;
            }
        }
        return true;
    }

    public static <E> boolean isEqualCollection(Collection<? extends E> a, Collection<? extends E> b, final Equator<? super E> equator) {
        if (equator == null) {
            throw new NullPointerException("Equator must not be null.");
        }
        if (a.size() != b.size()) {
            return false;
        }
        Transformer<E, ?> transformer = new Transformer() { // from class: org.apache.commons.collections4.CollectionUtils.1
            @Override // org.apache.commons.collections4.Transformer
            public EquatorWrapper<?> transform(Object input) {
                return new EquatorWrapper<>(equator, input);
            }
        };
        return isEqualCollection(collect(a, transformer), collect(b, transformer));
    }

    private static class EquatorWrapper<O> {
        private final Equator<? super O> equator;
        private final O object;

        public EquatorWrapper(Equator<? super O> equator, O object) {
            this.equator = equator;
            this.object = object;
        }

        public O getObject() {
            return this.object;
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof EquatorWrapper)) {
                return false;
            }
            return this.equator.equate(this.object, (O) ((EquatorWrapper) obj).getObject());
        }

        public int hashCode() {
            return this.equator.hash(this.object);
        }
    }

    @Deprecated
    public static <O> int cardinality(O obj, Iterable<? super O> coll) {
        if (coll == null) {
            throw new NullPointerException("coll must not be null.");
        }
        return IterableUtils.frequency(coll, obj);
    }

    @Deprecated
    public static <T> T find(Iterable<T> iterable, Predicate<? super T> predicate) {
        if (predicate != null) {
            return (T) IterableUtils.find(iterable, predicate);
        }
        return null;
    }

    @Deprecated
    public static <T, C extends Closure<? super T>> C forAllDo(Iterable<T> collection, C closure) {
        if (closure != null) {
            IterableUtils.forEach(collection, closure);
        }
        return closure;
    }

    @Deprecated
    public static <T, C extends Closure<? super T>> C forAllDo(Iterator<T> iterator, C closure) {
        if (closure != null) {
            IteratorUtils.forEach(iterator, closure);
        }
        return closure;
    }

    @Deprecated
    public static <T, C extends Closure<? super T>> T forAllButLastDo(Iterable<T> iterable, C c) {
        if (c != null) {
            return (T) IterableUtils.forEachButLast(iterable, c);
        }
        return null;
    }

    @Deprecated
    public static <T, C extends Closure<? super T>> T forAllButLastDo(Iterator<T> it, C c) {
        if (c != null) {
            return (T) IteratorUtils.forEachButLast(it, c);
        }
        return null;
    }

    public static <T> boolean filter(Iterable<T> collection, Predicate<? super T> predicate) {
        boolean result = false;
        if (collection != null && predicate != null) {
            Iterator<T> it = collection.iterator();
            while (it.hasNext()) {
                if (!predicate.evaluate(it.next())) {
                    it.remove();
                    result = true;
                }
            }
        }
        return result;
    }

    public static <T> boolean filterInverse(Iterable<T> collection, Predicate<? super T> predicate) {
        return filter(collection, predicate == null ? null : PredicateUtils.notPredicate(predicate));
    }

    public static <C> void transform(Collection<C> collection, Transformer<? super C, ? extends C> transformer) {
        if (collection != null && transformer != null) {
            if (collection instanceof List) {
                ListIterator listIterator = ((List) collection).listIterator();
                while (listIterator.hasNext()) {
                    listIterator.set(transformer.transform((C) listIterator.next()));
                }
            } else {
                Collection<? extends C> collectionCollect = collect(collection, transformer);
                collection.clear();
                collection.addAll(collectionCollect);
            }
        }
    }

    @Deprecated
    public static <C> int countMatches(Iterable<C> input, Predicate<? super C> predicate) {
        if (predicate == null) {
            return 0;
        }
        return (int) IterableUtils.countMatches(input, predicate);
    }

    @Deprecated
    public static <C> boolean exists(Iterable<C> input, Predicate<? super C> predicate) {
        return predicate != null && IterableUtils.matchesAny(input, predicate);
    }

    @Deprecated
    public static <C> boolean matchesAll(Iterable<C> input, Predicate<? super C> predicate) {
        return predicate != null && IterableUtils.matchesAll(input, predicate);
    }

    public static <O> Collection<O> select(Iterable<? extends O> inputCollection, Predicate<? super O> predicate) {
        Collection<O> answer = inputCollection instanceof Collection ? new ArrayList<>(((Collection) inputCollection).size()) : new ArrayList<>();
        return select(inputCollection, predicate, answer);
    }

    public static <O, R extends Collection<? super O>> R select(Iterable<? extends O> iterable, Predicate<? super O> predicate, R r) {
        if (iterable != null && predicate != null) {
            for (O o : iterable) {
                if (predicate.evaluate(o)) {
                    r.add(o);
                }
            }
        }
        return r;
    }

    public static <O, R extends Collection<? super O>> R select(Iterable<? extends O> iterable, Predicate<? super O> predicate, R r, R r2) {
        if (iterable != null && predicate != null) {
            for (O o : iterable) {
                if (predicate.evaluate(o)) {
                    r.add(o);
                } else {
                    r2.add(o);
                }
            }
        }
        return r;
    }

    public static <O> Collection<O> selectRejected(Iterable<? extends O> inputCollection, Predicate<? super O> predicate) {
        Collection<O> answer = inputCollection instanceof Collection ? new ArrayList<>(((Collection) inputCollection).size()) : new ArrayList<>();
        return selectRejected(inputCollection, predicate, answer);
    }

    public static <O, R extends Collection<? super O>> R selectRejected(Iterable<? extends O> iterable, Predicate<? super O> predicate, R r) {
        if (iterable != null && predicate != null) {
            for (O o : iterable) {
                if (!predicate.evaluate(o)) {
                    r.add(o);
                }
            }
        }
        return r;
    }

    public static <I, O> Collection<O> collect(Iterable<I> inputCollection, Transformer<? super I, ? extends O> transformer) {
        Collection<O> answer = inputCollection instanceof Collection ? new ArrayList<>(((Collection) inputCollection).size()) : new ArrayList<>();
        return collect(inputCollection, transformer, answer);
    }

    public static <I, O> Collection<O> collect(Iterator<I> inputIterator, Transformer<? super I, ? extends O> transformer) {
        return collect(inputIterator, transformer, new ArrayList());
    }

    public static <I, O, R extends Collection<? super O>> R collect(Iterable<? extends I> iterable, Transformer<? super I, ? extends O> transformer, R r) {
        if (iterable != null) {
            return (R) collect(iterable.iterator(), transformer, r);
        }
        return r;
    }

    public static <I, O, R extends Collection<? super O>> R collect(Iterator<? extends I> it, Transformer<? super I, ? extends O> transformer, R r) {
        if (it != null && transformer != null) {
            while (it.hasNext()) {
                r.add(transformer.transform(it.next()));
            }
        }
        return r;
    }

    public static <T> boolean addIgnoreNull(Collection<T> collection, T object) {
        if (collection != null) {
            return object != null && collection.add(object);
        }
        throw new NullPointerException("The collection must not be null");
    }

    public static <C> boolean addAll(Collection<C> collection, Iterable<? extends C> iterable) {
        if (iterable instanceof Collection) {
            return collection.addAll((Collection) iterable);
        }
        return addAll(collection, iterable.iterator());
    }

    public static <C> boolean addAll(Collection<C> collection, Iterator<? extends C> iterator) {
        boolean changed = false;
        while (iterator.hasNext()) {
            changed |= collection.add(iterator.next());
        }
        return changed;
    }

    public static <C> boolean addAll(Collection<C> collection, Enumeration<? extends C> enumeration) {
        boolean changed = false;
        while (enumeration.hasMoreElements()) {
            changed |= collection.add(enumeration.nextElement());
        }
        return changed;
    }

    public static <C> boolean addAll(Collection<C> collection, C... elements) {
        boolean changed = false;
        for (C element : elements) {
            changed |= collection.add(element);
        }
        return changed;
    }

    @Deprecated
    public static <T> T get(Iterator<T> it, int i) {
        return (T) IteratorUtils.get(it, i);
    }

    static void checkIndexBounds(int index) {
        if (index < 0) {
            throw new IndexOutOfBoundsException("Index cannot be negative: " + index);
        }
    }

    @Deprecated
    public static <T> T get(Iterable<T> iterable, int i) {
        return (T) IterableUtils.get(iterable, i);
    }

    public static Object get(Object object, int index) {
        if (index < 0) {
            throw new IndexOutOfBoundsException("Index cannot be negative: " + index);
        }
        if (object instanceof Map) {
            Map<?, ?> map = (Map) object;
            Iterator<?> iterator = map.entrySet().iterator();
            return IteratorUtils.get(iterator, index);
        }
        if (object instanceof Object[]) {
            return ((Object[]) object)[index];
        }
        if (object instanceof Iterator) {
            Iterator<?> it = (Iterator) object;
            return IteratorUtils.get(it, index);
        }
        if (object instanceof Iterable) {
            Iterable<?> iterable = (Iterable) object;
            return IterableUtils.get(iterable, index);
        }
        if (object instanceof Enumeration) {
            Enumeration<?> it2 = (Enumeration) object;
            return EnumerationUtils.get(it2, index);
        }
        if (object == null) {
            throw new IllegalArgumentException("Unsupported object type: null");
        }
        try {
            return Array.get(object, index);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unsupported object type: " + object.getClass().getName());
        }
    }

    public static <K, V> Map.Entry<K, V> get(Map<K, V> map, int index) {
        checkIndexBounds(index);
        return (Map.Entry) get((Iterable) map.entrySet(), index);
    }

    public static int size(Object object) {
        if (object == null) {
            return 0;
        }
        int total = 0;
        if (object instanceof Map) {
            int total2 = ((Map) object).size();
            return total2;
        }
        if (object instanceof Collection) {
            int total3 = ((Collection) object).size();
            return total3;
        }
        if (object instanceof Iterable) {
            int total4 = IterableUtils.size((Iterable) object);
            return total4;
        }
        if (object instanceof Object[]) {
            int total5 = ((Object[]) object).length;
            return total5;
        }
        if (object instanceof Iterator) {
            int total6 = IteratorUtils.size((Iterator) object);
            return total6;
        }
        if (object instanceof Enumeration) {
            Enumeration<?> it = (Enumeration) object;
            while (it.hasMoreElements()) {
                total++;
                it.nextElement();
            }
            return total;
        }
        try {
            int total7 = Array.getLength(object);
            return total7;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unsupported object type: " + object.getClass().getName());
        }
    }

    public static boolean sizeIsEmpty(Object object) {
        if (object == null) {
            return true;
        }
        if (object instanceof Collection) {
            return ((Collection) object).isEmpty();
        }
        if (object instanceof Iterable) {
            return IterableUtils.isEmpty((Iterable) object);
        }
        if (object instanceof Map) {
            return ((Map) object).isEmpty();
        }
        if (object instanceof Object[]) {
            return ((Object[]) object).length == 0;
        }
        if (object instanceof Iterator) {
            return true ^ ((Iterator) object).hasNext();
        }
        if (object instanceof Enumeration) {
            return true ^ ((Enumeration) object).hasMoreElements();
        }
        try {
            return Array.getLength(object) == 0;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unsupported object type: " + object.getClass().getName());
        }
    }

    public static boolean isEmpty(Collection<?> coll) {
        return coll == null || coll.isEmpty();
    }

    public static boolean isNotEmpty(Collection<?> coll) {
        return !isEmpty(coll);
    }

    public static void reverseArray(Object[] array) {
        int j = array.length - 1;
        for (int i = 0; j > i; i++) {
            Object tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
            j--;
        }
    }

    public static boolean isFull(Collection<? extends Object> coll) {
        if (coll == null) {
            throw new NullPointerException("The collection must not be null");
        }
        if (coll instanceof BoundedCollection) {
            return ((BoundedCollection) coll).isFull();
        }
        try {
            BoundedCollection<?> bcoll = UnmodifiableBoundedCollection.unmodifiableBoundedCollection(coll);
            return bcoll.isFull();
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static int maxSize(Collection<? extends Object> coll) {
        if (coll == null) {
            throw new NullPointerException("The collection must not be null");
        }
        if (coll instanceof BoundedCollection) {
            return ((BoundedCollection) coll).maxSize();
        }
        try {
            BoundedCollection<?> bcoll = UnmodifiableBoundedCollection.unmodifiableBoundedCollection(coll);
            return bcoll.maxSize();
        } catch (IllegalArgumentException e) {
            return -1;
        }
    }

    public static <O extends Comparable<? super O>> List<O> collate(Iterable<? extends O> a, Iterable<? extends O> b) {
        return collate(a, b, ComparatorUtils.naturalComparator(), true);
    }

    public static <O extends Comparable<? super O>> List<O> collate(Iterable<? extends O> a, Iterable<? extends O> b, boolean includeDuplicates) {
        return collate(a, b, ComparatorUtils.naturalComparator(), includeDuplicates);
    }

    public static <O> List<O> collate(Iterable<? extends O> a, Iterable<? extends O> b, Comparator<? super O> c) {
        return collate(a, b, c, true);
    }

    public static <O> List<O> collate(Iterable<? extends O> a, Iterable<? extends O> b, Comparator<? super O> c, boolean includeDuplicates) {
        if (a == null || b == null) {
            throw new NullPointerException("The collections must not be null");
        }
        if (c == null) {
            throw new NullPointerException("The comparator must not be null");
        }
        int totalSize = ((a instanceof Collection) && (b instanceof Collection)) ? Math.max(1, ((Collection) a).size() + ((Collection) b).size()) : 10;
        Iterator<O> iterator = new CollatingIterator<>(c, a.iterator(), b.iterator());
        if (includeDuplicates) {
            return IteratorUtils.toList(iterator, totalSize);
        }
        ArrayList<O> mergedList = new ArrayList<>(totalSize);
        O lastItem = null;
        while (iterator.hasNext()) {
            O item = iterator.next();
            if (lastItem == null || !lastItem.equals(item)) {
                mergedList.add(item);
            }
            lastItem = item;
        }
        mergedList.trimToSize();
        return mergedList;
    }

    public static <E> Collection<List<E>> permutations(Collection<E> collection) {
        PermutationIterator<E> it = new PermutationIterator<>(collection);
        Collection<List<E>> result = new ArrayList<>();
        while (it.hasNext()) {
            result.add(it.next());
        }
        return result;
    }

    public static <C> Collection<C> retainAll(Collection<C> collection, Collection<?> retain) {
        return ListUtils.retainAll(collection, retain);
    }

    public static <E> Collection<E> retainAll(Iterable<E> collection, Iterable<? extends E> retain, final Equator<? super E> equator) {
        Transformer<E, EquatorWrapper<E>> transformer = new Transformer<E, EquatorWrapper<E>>() { // from class: org.apache.commons.collections4.CollectionUtils.2
            @Override // org.apache.commons.collections4.Transformer
            public EquatorWrapper<E> transform(E input) {
                return new EquatorWrapper<>(equator, input);
            }
        };
        Set<EquatorWrapper<E>> retainSet = (Set) collect(retain, transformer, new HashSet());
        List<E> list = new ArrayList<>();
        for (E element : collection) {
            if (retainSet.contains(new EquatorWrapper(equator, element))) {
                list.add(element);
            }
        }
        return list;
    }

    public static <E> Collection<E> removeAll(Collection<E> collection, Collection<?> remove) {
        return ListUtils.removeAll(collection, remove);
    }

    public static <E> Collection<E> removeAll(Iterable<E> collection, Iterable<? extends E> remove, final Equator<? super E> equator) {
        Transformer<E, EquatorWrapper<E>> transformer = new Transformer<E, EquatorWrapper<E>>() { // from class: org.apache.commons.collections4.CollectionUtils.3
            @Override // org.apache.commons.collections4.Transformer
            public EquatorWrapper<E> transform(E input) {
                return new EquatorWrapper<>(equator, input);
            }
        };
        Set<EquatorWrapper<E>> removeSet = (Set) collect(remove, transformer, new HashSet());
        List<E> list = new ArrayList<>();
        for (E element : collection) {
            if (!removeSet.contains(new EquatorWrapper(equator, element))) {
                list.add(element);
            }
        }
        return list;
    }

    @Deprecated
    public static <C> Collection<C> synchronizedCollection(Collection<C> collection) {
        return SynchronizedCollection.synchronizedCollection(collection);
    }

    @Deprecated
    public static <C> Collection<C> unmodifiableCollection(Collection<? extends C> collection) {
        return UnmodifiableCollection.unmodifiableCollection(collection);
    }

    public static <C> Collection<C> predicatedCollection(Collection<C> collection, Predicate<? super C> predicate) {
        return PredicatedCollection.predicatedCollection(collection, predicate);
    }

    public static <E> Collection<E> transformingCollection(Collection<E> collection, Transformer<? super E, ? extends E> transformer) {
        return TransformedCollection.transformingCollection(collection, transformer);
    }

    public static <E> E extractSingleton(Collection<E> collection) {
        if (collection == null) {
            throw new NullPointerException("Collection must not be null.");
        }
        if (collection.size() != 1) {
            throw new IllegalArgumentException("Can extract singleton only when collection size == 1");
        }
        return collection.iterator().next();
    }
}
