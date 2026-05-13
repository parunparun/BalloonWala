package org.apache.commons.collections4.map;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import org.apache.commons.collections4.Predicate;

/* JADX INFO: loaded from: classes.dex */
public class PredicatedMap<K, V> extends AbstractInputCheckedMapDecorator<K, V> implements Serializable {
    private static final long serialVersionUID = 7412622456128415156L;
    protected final Predicate<? super K> keyPredicate;
    protected final Predicate<? super V> valuePredicate;

    @Override // org.apache.commons.collections4.map.AbstractInputCheckedMapDecorator, org.apache.commons.collections4.map.AbstractMapDecorator, java.util.Map, org.apache.commons.collections4.Get
    public /* bridge */ /* synthetic */ Set entrySet() {
        return super.entrySet();
    }

    public static <K, V> PredicatedMap<K, V> predicatedMap(Map<K, V> map, Predicate<? super K> keyPredicate, Predicate<? super V> valuePredicate) {
        return new PredicatedMap<>(map, keyPredicate, valuePredicate);
    }

    protected PredicatedMap(Map<K, V> map, Predicate<? super K> keyPredicate, Predicate<? super V> valuePredicate) {
        super(map);
        this.keyPredicate = keyPredicate;
        this.valuePredicate = valuePredicate;
        for (Map.Entry<K, V> entry : map.entrySet()) {
            validate(entry.getKey(), entry.getValue());
        }
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(this.map);
    }

    private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
        in.defaultReadObject();
        this.map = (Map) in.readObject();
    }

    protected void validate(K key, V value) {
        Predicate<? super K> predicate = this.keyPredicate;
        if (predicate != null && !predicate.evaluate(key)) {
            throw new IllegalArgumentException("Cannot add key - Predicate rejected it");
        }
        Predicate<? super V> predicate2 = this.valuePredicate;
        if (predicate2 != null && !predicate2.evaluate(value)) {
            throw new IllegalArgumentException("Cannot add value - Predicate rejected it");
        }
    }

    @Override // org.apache.commons.collections4.map.AbstractInputCheckedMapDecorator
    protected V checkSetValue(V value) {
        if (!this.valuePredicate.evaluate(value)) {
            throw new IllegalArgumentException("Cannot set value - Predicate rejected it");
        }
        return value;
    }

    @Override // org.apache.commons.collections4.map.AbstractInputCheckedMapDecorator
    protected boolean isSetValueChecking() {
        return this.valuePredicate != null;
    }

    @Override // org.apache.commons.collections4.map.AbstractMapDecorator, java.util.Map, org.apache.commons.collections4.Put
    public V put(K key, V value) {
        validate(key, value);
        return this.map.put(key, value);
    }

    @Override // org.apache.commons.collections4.map.AbstractMapDecorator, java.util.Map, org.apache.commons.collections4.Put
    public void putAll(Map<? extends K, ? extends V> mapToCopy) {
        for (Map.Entry<? extends K, ? extends V> entry : mapToCopy.entrySet()) {
            validate(entry.getKey(), entry.getValue());
        }
        super.putAll(mapToCopy);
    }
}
