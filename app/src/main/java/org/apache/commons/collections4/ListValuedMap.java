package org.apache.commons.collections4;

import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public interface ListValuedMap<K, V> extends MultiValuedMap<K, V> {
    @Override // org.apache.commons.collections4.MultiValuedMap
    List<V> get(K k);

    @Override // org.apache.commons.collections4.MultiValuedMap
    List<V> remove(Object obj);
}
