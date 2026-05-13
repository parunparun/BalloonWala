package org.apache.commons.collections4.map;

import java.util.Comparator;
import java.util.SortedMap;
import org.apache.commons.collections4.Predicate;

/* JADX INFO: loaded from: classes.dex */
public class PredicatedSortedMap<K, V> extends PredicatedMap<K, V> implements SortedMap<K, V> {
    private static final long serialVersionUID = 3359846175935304332L;

    public static <K, V> PredicatedSortedMap<K, V> predicatedSortedMap(SortedMap<K, V> map, Predicate<? super K> keyPredicate, Predicate<? super V> valuePredicate) {
        return new PredicatedSortedMap<>(map, keyPredicate, valuePredicate);
    }

    protected PredicatedSortedMap(SortedMap<K, V> map, Predicate<? super K> keyPredicate, Predicate<? super V> valuePredicate) {
        super(map, keyPredicate, valuePredicate);
    }

    protected SortedMap<K, V> getSortedMap() {
        return (SortedMap) this.map;
    }

    @Override // java.util.SortedMap
    public K firstKey() {
        return getSortedMap().firstKey();
    }

    @Override // java.util.SortedMap
    public K lastKey() {
        return getSortedMap().lastKey();
    }

    @Override // java.util.SortedMap
    public Comparator<? super K> comparator() {
        return getSortedMap().comparator();
    }

    @Override // java.util.SortedMap
    public SortedMap<K, V> subMap(K fromKey, K toKey) {
        SortedMap<K, V> map = getSortedMap().subMap(fromKey, toKey);
        return new PredicatedSortedMap(map, this.keyPredicate, this.valuePredicate);
    }

    @Override // java.util.SortedMap
    public SortedMap<K, V> headMap(K toKey) {
        SortedMap<K, V> map = getSortedMap().headMap(toKey);
        return new PredicatedSortedMap(map, this.keyPredicate, this.valuePredicate);
    }

    @Override // java.util.SortedMap
    public SortedMap<K, V> tailMap(K fromKey) {
        SortedMap<K, V> map = getSortedMap().tailMap(fromKey);
        return new PredicatedSortedMap(map, this.keyPredicate, this.valuePredicate);
    }
}
