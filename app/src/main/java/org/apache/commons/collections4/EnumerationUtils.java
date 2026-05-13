package org.apache.commons.collections4;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.StringTokenizer;
import org.apache.commons.collections4.iterators.EnumerationIterator;

/* JADX INFO: loaded from: classes.dex */
public class EnumerationUtils {
    private EnumerationUtils() {
    }

    public static <T> T get(Enumeration<T> e, int index) {
        int i = index;
        CollectionUtils.checkIndexBounds(i);
        while (e.hasMoreElements()) {
            i--;
            if (i == -1) {
                return e.nextElement();
            }
            e.nextElement();
        }
        throw new IndexOutOfBoundsException("Entry does not exist: " + i);
    }

    public static <E> List<E> toList(Enumeration<? extends E> enumeration) {
        return IteratorUtils.toList(new EnumerationIterator(enumeration));
    }

    public static List<String> toList(StringTokenizer stringTokenizer) {
        List<String> result = new ArrayList<>(stringTokenizer.countTokens());
        while (stringTokenizer.hasMoreTokens()) {
            result.add(stringTokenizer.nextToken());
        }
        return result;
    }
}
