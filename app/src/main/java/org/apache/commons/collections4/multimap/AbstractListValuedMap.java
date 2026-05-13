package org.apache.commons.collections4.multimap;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.ListValuedMap;

/* JADX INFO: loaded from: classes.dex */
public abstract class AbstractListValuedMap<K, V> extends AbstractMultiValuedMap<K, V> implements ListValuedMap<K, V> {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.commons.collections4.multimap.AbstractMultiValuedMap
    public abstract List<V> createCollection();

    protected AbstractListValuedMap() {
    }

    protected AbstractListValuedMap(Map<K, ? extends List<V>> map) {
        super(map);
    }

    @Override // org.apache.commons.collections4.multimap.AbstractMultiValuedMap
    protected Map<K, List<V>> getMap() {
        return super.getMap();
    }

    @Override // org.apache.commons.collections4.multimap.AbstractMultiValuedMap, org.apache.commons.collections4.MultiValuedMap
    public List<V> get(K key) {
        return wrappedCollection((Object) key);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.apache.commons.collections4.multimap.AbstractMultiValuedMap
    public List<V> wrappedCollection(K key) {
        return new WrappedList(key);
    }

    @Override // org.apache.commons.collections4.multimap.AbstractMultiValuedMap, org.apache.commons.collections4.MultiValuedMap
    public List<V> remove(Object key) {
        return ListUtils.emptyIfNull(getMap().remove(key));
    }

    private class WrappedList extends AbstractMultiValuedMap<K, V>.WrappedCollection implements List<V> {
        public WrappedList(K key) {
            super(key);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.apache.commons.collections4.multimap.AbstractMultiValuedMap.WrappedCollection
        public List<V> getMapping() {
            return AbstractListValuedMap.this.getMap().get(this.key);
        }

        @Override // java.util.List
        public void add(int index, V value) {
            List<V> list = getMapping();
            if (list == null) {
                list = AbstractListValuedMap.this.createCollection();
                AbstractListValuedMap.this.getMap().put(this.key, list);
            }
            list.add(index, value);
        }

        @Override // java.util.List
        public boolean addAll(int index, Collection<? extends V> c) {
            List<V> list = getMapping();
            if (list == null) {
                List<V> list2 = AbstractListValuedMap.this.createCollection();
                boolean changed = list2.addAll(index, c);
                if (changed) {
                    AbstractListValuedMap.this.getMap().put(this.key, list2);
                }
                return changed;
            }
            return list.addAll(index, c);
        }

        @Override // java.util.List
        public V get(int index) {
            List<V> list = ListUtils.emptyIfNull(getMapping());
            return list.get(index);
        }

        @Override // java.util.List
        public int indexOf(Object o) {
            List<V> list = ListUtils.emptyIfNull(getMapping());
            return list.indexOf(o);
        }

        @Override // java.util.List
        public int lastIndexOf(Object o) {
            List<V> list = ListUtils.emptyIfNull(getMapping());
            return list.lastIndexOf(o);
        }

        @Override // java.util.List
        public ListIterator<V> listIterator() {
            return new ValuesListIterator(this.key);
        }

        @Override // java.util.List
        public ListIterator<V> listIterator(int index) {
            return new ValuesListIterator(this.key, index);
        }

        @Override // java.util.List
        public V remove(int index) {
            List<V> list = ListUtils.emptyIfNull(getMapping());
            V value = list.remove(index);
            if (list.isEmpty()) {
                AbstractListValuedMap.this.remove((Object) this.key);
            }
            return value;
        }

        @Override // java.util.List
        public V set(int index, V value) {
            List<V> list = ListUtils.emptyIfNull(getMapping());
            return list.set(index, value);
        }

        @Override // java.util.List
        public List<V> subList(int fromIndex, int toIndex) {
            List<V> list = ListUtils.emptyIfNull(getMapping());
            return list.subList(fromIndex, toIndex);
        }

        @Override // java.util.Collection, java.util.List
        public boolean equals(Object other) {
            List<V> list = getMapping();
            if (list == null) {
                return Collections.emptyList().equals(other);
            }
            if (!(other instanceof List)) {
                return false;
            }
            List<?> otherList = (List) other;
            return ListUtils.isEqualList(list, otherList);
        }

        @Override // java.util.Collection, java.util.List
        public int hashCode() {
            List<V> list = getMapping();
            return ListUtils.hashCodeForList(list);
        }
    }

    private class ValuesListIterator implements ListIterator<V> {
        private ListIterator<V> iterator;
        private final K key;
        private List<V> values;

        public ValuesListIterator(K key) {
            this.key = key;
            List<V> listEmptyIfNull = ListUtils.emptyIfNull(AbstractListValuedMap.this.getMap().get(key));
            this.values = listEmptyIfNull;
            this.iterator = listEmptyIfNull.listIterator();
        }

        public ValuesListIterator(K key, int index) {
            this.key = key;
            List<V> listEmptyIfNull = ListUtils.emptyIfNull(AbstractListValuedMap.this.getMap().get(key));
            this.values = listEmptyIfNull;
            this.iterator = listEmptyIfNull.listIterator(index);
        }

        @Override // java.util.ListIterator
        public void add(V value) {
            if (AbstractListValuedMap.this.getMap().get(this.key) == null) {
                List<V> list = AbstractListValuedMap.this.createCollection();
                AbstractListValuedMap.this.getMap().put(this.key, list);
                this.values = list;
                this.iterator = list.listIterator();
            }
            this.iterator.add(value);
        }

        @Override // java.util.ListIterator, java.util.Iterator
        public boolean hasNext() {
            return this.iterator.hasNext();
        }

        @Override // java.util.ListIterator
        public boolean hasPrevious() {
            return this.iterator.hasPrevious();
        }

        @Override // java.util.ListIterator, java.util.Iterator
        public V next() {
            return this.iterator.next();
        }

        @Override // java.util.ListIterator
        public int nextIndex() {
            return this.iterator.nextIndex();
        }

        @Override // java.util.ListIterator
        public V previous() {
            return this.iterator.previous();
        }

        @Override // java.util.ListIterator
        public int previousIndex() {
            return this.iterator.previousIndex();
        }

        @Override // java.util.ListIterator, java.util.Iterator
        public void remove() {
            this.iterator.remove();
            if (this.values.isEmpty()) {
                AbstractListValuedMap.this.getMap().remove(this.key);
            }
        }

        @Override // java.util.ListIterator
        public void set(V value) {
            this.iterator.set(value);
        }
    }
}
