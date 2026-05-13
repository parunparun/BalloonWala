package org.apache.commons.collections4.multiset;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;

/* JADX INFO: loaded from: classes.dex */
public class HashMultiSet<E> extends AbstractMapMultiSet<E> implements Serializable {
    private static final long serialVersionUID = 20150610;

    public HashMultiSet() {
        super(new HashMap());
    }

    public HashMultiSet(Collection<? extends E> coll) {
        this();
        addAll(coll);
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        super.doWriteObject(out);
    }

    private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
        in.defaultReadObject();
        setMap(new HashMap());
        super.doReadObject(in);
    }
}
