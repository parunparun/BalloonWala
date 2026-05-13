package org.apache.commons.collections4;

import java.util.Collection;

/* JADX INFO: loaded from: classes.dex */
@Deprecated
public interface MultiMap<K, V> extends IterableMap<K, Object> {
    @Override // java.util.Map, org.apache.commons.collections4.Get
    boolean containsValue(Object obj);

    @Override // java.util.Map, org.apache.commons.collections4.Get
    Object get(Object obj);

    @Override // java.util.Map, org.apache.commons.collections4.Put
    Object put(K k, Object obj);

    @Override // java.util.Map, org.apache.commons.collections4.Get
    Object remove(Object obj);

    boolean removeMapping(K k, V v);

    @Override // java.util.Map, org.apache.commons.collections4.Get
    int size();

    @Override // java.util.Map, org.apache.commons.collections4.Get
    Collection<Object> values();
}
