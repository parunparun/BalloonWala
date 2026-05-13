package org.apache.commons.collections4.multimap;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import org.apache.commons.collections4.MultiValuedMap;

/* JADX INFO: loaded from: classes.dex */
public class HashSetValuedHashMap<K, V> extends AbstractSetValuedMap<K, V> implements Serializable {
    private static final int DEFAULT_INITIAL_MAP_CAPACITY = 16;
    private static final int DEFAULT_INITIAL_SET_CAPACITY = 3;
    private static final long serialVersionUID = 20151118;
    private final int initialSetCapacity;

    public HashSetValuedHashMap() {
        this(16, 3);
    }

    public HashSetValuedHashMap(int initialSetCapacity) {
        this(16, initialSetCapacity);
    }

    public HashSetValuedHashMap(int initialMapCapacity, int initialSetCapacity) {
        super(new HashMap(initialMapCapacity));
        this.initialSetCapacity = initialSetCapacity;
    }

    public HashSetValuedHashMap(MultiValuedMap<? extends K, ? extends V> map) {
        this(map.size(), 3);
        super.putAll(map);
    }

    public HashSetValuedHashMap(Map<? extends K, ? extends V> map) {
        this(map.size(), 3);
        super.putAll(map);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.commons.collections4.multimap.AbstractSetValuedMap, org.apache.commons.collections4.multimap.AbstractMultiValuedMap
    public HashSet<V> createCollection() {
        return new HashSet<>(this.initialSetCapacity);
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
        doWriteObject(oos);
    }

    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        ois.defaultReadObject();
        setMap(new HashMap());
        doReadObject(ois);
    }
}
