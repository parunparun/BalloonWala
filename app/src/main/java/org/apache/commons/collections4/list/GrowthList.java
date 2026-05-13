package org.apache.commons.collections4.list;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class GrowthList<E> extends AbstractSerializableListDecorator<E> {
    private static final long serialVersionUID = -3620001881672L;

    public static <E> GrowthList<E> growthList(List<E> list) {
        return new GrowthList<>(list);
    }

    public GrowthList() {
        super(new ArrayList());
    }

    public GrowthList(int initialSize) {
        super(new ArrayList(initialSize));
    }

    protected GrowthList(List<E> list) {
        super(list);
    }

    @Override // org.apache.commons.collections4.list.AbstractListDecorator, java.util.List
    public void add(int index, E element) {
        int size = decorated().size();
        if (index > size) {
            decorated().addAll(Collections.nCopies(index - size, null));
        }
        decorated().add(index, element);
    }

    @Override // org.apache.commons.collections4.list.AbstractListDecorator, java.util.List
    public boolean addAll(int index, Collection<? extends E> coll) {
        int size = decorated().size();
        boolean result = false;
        if (index > size) {
            decorated().addAll(Collections.nCopies(index - size, null));
            result = true;
        }
        return decorated().addAll(index, coll) || result;
    }

    @Override // org.apache.commons.collections4.list.AbstractListDecorator, java.util.List
    public E set(int i, E e) {
        int size = decorated().size();
        if (i >= size) {
            decorated().addAll(Collections.nCopies((i - size) + 1, null));
        }
        return (E) decorated().set(i, e);
    }
}
