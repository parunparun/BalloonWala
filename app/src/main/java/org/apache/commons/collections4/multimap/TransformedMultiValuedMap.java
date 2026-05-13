package org.apache.commons.collections4.multimap;

import java.util.Iterator;
import java.util.Map;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.FluentIterable;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.Transformer;

/* JADX INFO: loaded from: classes.dex */
public class TransformedMultiValuedMap<K, V> extends AbstractMultiValuedMapDecorator<K, V> {
    private static final long serialVersionUID = 20150612;
    private final Transformer<? super K, ? extends K> keyTransformer;
    private final Transformer<? super V, ? extends V> valueTransformer;

    public static <K, V> TransformedMultiValuedMap<K, V> transformingMap(MultiValuedMap<K, V> map, Transformer<? super K, ? extends K> keyTransformer, Transformer<? super V, ? extends V> valueTransformer) {
        return new TransformedMultiValuedMap<>(map, keyTransformer, valueTransformer);
    }

    public static <K, V> TransformedMultiValuedMap<K, V> transformedMap(MultiValuedMap<K, V> map, Transformer<? super K, ? extends K> keyTransformer, Transformer<? super V, ? extends V> valueTransformer) {
        TransformedMultiValuedMap<K, V> decorated = new TransformedMultiValuedMap<>(map, keyTransformer, valueTransformer);
        if (!map.isEmpty()) {
            ArrayListValuedHashMap arrayListValuedHashMap = new ArrayListValuedHashMap(map);
            decorated.clear();
            decorated.putAll(arrayListValuedHashMap);
        }
        return decorated;
    }

    protected TransformedMultiValuedMap(MultiValuedMap<K, V> map, Transformer<? super K, ? extends K> keyTransformer, Transformer<? super V, ? extends V> valueTransformer) {
        super(map);
        this.keyTransformer = keyTransformer;
        this.valueTransformer = valueTransformer;
    }

    protected K transformKey(K object) {
        Transformer<? super K, ? extends K> transformer = this.keyTransformer;
        if (transformer == null) {
            return object;
        }
        return transformer.transform(object);
    }

    protected V transformValue(V object) {
        Transformer<? super V, ? extends V> transformer = this.valueTransformer;
        if (transformer == null) {
            return object;
        }
        return transformer.transform(object);
    }

    @Override // org.apache.commons.collections4.multimap.AbstractMultiValuedMapDecorator, org.apache.commons.collections4.MultiValuedMap
    public boolean put(K key, V value) {
        return decorated().put(transformKey(key), transformValue(value));
    }

    @Override // org.apache.commons.collections4.multimap.AbstractMultiValuedMapDecorator, org.apache.commons.collections4.MultiValuedMap
    public boolean putAll(K key, Iterable<? extends V> values) {
        if (values == null) {
            throw new NullPointerException("Values must not be null.");
        }
        Iterable<V> transformedValues = FluentIterable.of((Iterable) values).transform(this.valueTransformer);
        Iterator<V> it = transformedValues.iterator();
        return it.hasNext() && CollectionUtils.addAll(decorated().get(transformKey(key)), it);
    }

    @Override // org.apache.commons.collections4.multimap.AbstractMultiValuedMapDecorator, org.apache.commons.collections4.MultiValuedMap
    public boolean putAll(Map<? extends K, ? extends V> map) {
        if (map == null) {
            throw new NullPointerException("Map must not be null.");
        }
        boolean changed = false;
        for (Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
            changed |= put(entry.getKey(), entry.getValue());
        }
        return changed;
    }

    @Override // org.apache.commons.collections4.multimap.AbstractMultiValuedMapDecorator, org.apache.commons.collections4.MultiValuedMap
    public boolean putAll(MultiValuedMap<? extends K, ? extends V> map) {
        if (map == null) {
            throw new NullPointerException("Map must not be null.");
        }
        boolean changed = false;
        for (Map.Entry<? extends K, ? extends V> entry : map.entries()) {
            changed |= put(entry.getKey(), entry.getValue());
        }
        return changed;
    }
}
