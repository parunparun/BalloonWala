package org.apache.commons.collections4.map;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.ref.Reference;
import org.apache.commons.collections4.map.AbstractReferenceMap;

/* JADX INFO: loaded from: classes.dex */
public class ReferenceIdentityMap<K, V> extends AbstractReferenceMap<K, V> implements Serializable {
    private static final long serialVersionUID = -1266190134568365852L;

    public ReferenceIdentityMap() {
        super(AbstractReferenceMap.ReferenceStrength.HARD, AbstractReferenceMap.ReferenceStrength.SOFT, 16, 0.75f, false);
    }

    public ReferenceIdentityMap(AbstractReferenceMap.ReferenceStrength keyType, AbstractReferenceMap.ReferenceStrength valueType) {
        super(keyType, valueType, 16, 0.75f, false);
    }

    public ReferenceIdentityMap(AbstractReferenceMap.ReferenceStrength keyType, AbstractReferenceMap.ReferenceStrength valueType, boolean purgeValues) {
        super(keyType, valueType, 16, 0.75f, purgeValues);
    }

    public ReferenceIdentityMap(AbstractReferenceMap.ReferenceStrength keyType, AbstractReferenceMap.ReferenceStrength valueType, int capacity, float loadFactor) {
        super(keyType, valueType, capacity, loadFactor, false);
    }

    public ReferenceIdentityMap(AbstractReferenceMap.ReferenceStrength keyType, AbstractReferenceMap.ReferenceStrength valueType, int capacity, float loadFactor, boolean purgeValues) {
        super(keyType, valueType, capacity, loadFactor, purgeValues);
    }

    @Override // org.apache.commons.collections4.map.AbstractHashedMap
    protected int hash(Object key) {
        return System.identityHashCode(key);
    }

    @Override // org.apache.commons.collections4.map.AbstractReferenceMap
    protected int hashEntry(Object key, Object value) {
        return System.identityHashCode(key) ^ System.identityHashCode(value);
    }

    @Override // org.apache.commons.collections4.map.AbstractReferenceMap, org.apache.commons.collections4.map.AbstractHashedMap
    protected boolean isEqualKey(Object key1, Object key2) {
        return key1 == (isKeyType(AbstractReferenceMap.ReferenceStrength.HARD) ? key2 : ((Reference) key2).get());
    }

    @Override // org.apache.commons.collections4.map.AbstractHashedMap
    protected boolean isEqualValue(Object value1, Object value2) {
        return value1 == value2;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        doWriteObject(out);
    }

    private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
        in.defaultReadObject();
        doReadObject(in);
    }
}
