package org.apache.commons.collections4.properties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import org.apache.commons.collections4.iterators.IteratorEnumeration;

/* JADX INFO: loaded from: classes.dex */
public class SortedProperties extends Properties {
    private static final long serialVersionUID = 1;

    @Override // java.util.Hashtable, java.util.Dictionary
    public synchronized Enumeration<Object> keys() {
        List<String> keys;
        Set<Object> keySet = keySet();
        keys = new ArrayList<>(keySet.size());
        for (Object key : keySet) {
            keys.add(key.toString());
        }
        Collections.sort(keys);
        return new IteratorEnumeration(keys.iterator());
    }
}
