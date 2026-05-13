package org.apache.commons.collections4.map;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import org.apache.commons.collections4.Transformer;

/* JADX INFO: loaded from: classes.dex */
public class TransformedMap<K, V> extends AbstractInputCheckedMapDecorator<K, V> implements Serializable {
    private static final long serialVersionUID = 7023152376788900464L;
    protected final Transformer<? super K, ? extends K> keyTransformer;
    protected final Transformer<? super V, ? extends V> valueTransformer;

    @Override // org.apache.commons.collections4.map.AbstractInputCheckedMapDecorator, org.apache.commons.collections4.map.AbstractMapDecorator, java.util.Map, org.apache.commons.collections4.Get
    public /* bridge */ /* synthetic */ Set entrySet() {
        return super.entrySet();
    }

    public static <K, V> TransformedMap<K, V> transformingMap(Map<K, V> map, Transformer<? super K, ? extends K> keyTransformer, Transformer<? super V, ? extends V> valueTransformer) {
        return new TransformedMap<>(map, keyTransformer, valueTransformer);
    }

    public static <K, V> TransformedMap<K, V> transformedMap(Map<K, V> map, Transformer<? super K, ? extends K> keyTransformer, Transformer<? super V, ? extends V> valueTransformer) {
        TransformedMap<K, V> decorated = new TransformedMap<>(map, keyTransformer, valueTransformer);
        if (map.size() > 0) {
            Map<K, V> transformed = decorated.transformMap(map);
            decorated.clear();
            decorated.decorated().putAll(transformed);
        }
        return decorated;
    }

    protected TransformedMap(Map<K, V> map, Transformer<? super K, ? extends K> keyTransformer, Transformer<? super V, ? extends V> valueTransformer) {
        super(map);
        this.keyTransformer = keyTransformer;
        this.valueTransformer = valueTransformer;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(this.map);
    }

    private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
        in.defaultReadObject();
        this.map = (Map) in.readObject();
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

    /* JADX WARN: Multi-variable type inference failed */
    protected Map<K, V> transformMap(Map<? extends K, ? extends V> map) {
        if (map.isEmpty()) {
            return map;
        }
        LinkedMap linkedMap = new LinkedMap(map.size());
        for (Map.Entry entry : map.entrySet()) {
            linkedMap.put(transformKey(entry.getKey()), transformValue(entry.getValue()));
        }
        return linkedMap;
    }

    @Override // org.apache.commons.collections4.map.AbstractInputCheckedMapDecorator
    protected V checkSetValue(V value) {
        return this.valueTransformer.transform(value);
    }

    @Override // org.apache.commons.collections4.map.AbstractInputCheckedMapDecorator
    protected boolean isSetValueChecking() {
        return this.valueTransformer != null;
    }

    @Override // org.apache.commons.collections4.map.AbstractMapDecorator, java.util.Map, org.apache.commons.collections4.Put
    public V put(K key, V value) {
        return decorated().put(transformKey(key), transformValue(value));
    }

    @Override // org.apache.commons.collections4.map.AbstractMapDecorator, java.util.Map, org.apache.commons.collections4.Put
    public void putAll(Map<? extends K, ? extends V> mapToCopy) {
        decorated().putAll(transformMap(mapToCopy));
    }
}
