package org.apache.commons.collections4;

/* JADX INFO: loaded from: classes.dex */
public interface OrderedMapIterator<K, V> extends MapIterator<K, V>, OrderedIterator<K> {
    @Override // org.apache.commons.collections4.OrderedIterator
    boolean hasPrevious();

    @Override // org.apache.commons.collections4.OrderedIterator
    K previous();
}
