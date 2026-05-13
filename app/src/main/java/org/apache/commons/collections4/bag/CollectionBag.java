package org.apache.commons.collections4.bag;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Iterator;
import org.apache.commons.collections4.Bag;

/* JADX INFO: loaded from: classes.dex */
public final class CollectionBag<E> extends AbstractBagDecorator<E> {
    private static final long serialVersionUID = -2560033712679053143L;

    public static <E> Bag<E> collectionBag(Bag<E> bag) {
        return new CollectionBag(bag);
    }

    public CollectionBag(Bag<E> bag) {
        super(bag);
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(decorated());
    }

    private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
        in.defaultReadObject();
        setCollection((Collection) in.readObject());
    }

    @Override // org.apache.commons.collections4.collection.AbstractCollectionDecorator, java.util.Collection, org.apache.commons.collections4.Bag
    public boolean containsAll(Collection<?> coll) {
        Iterator<?> e = coll.iterator();
        while (e.hasNext()) {
            if (!contains(e.next())) {
                return false;
            }
        }
        return true;
    }

    @Override // org.apache.commons.collections4.collection.AbstractCollectionDecorator, java.util.Collection, org.apache.commons.collections4.Bag
    public boolean add(E object) {
        return add(object, 1);
    }

    @Override // org.apache.commons.collections4.collection.AbstractCollectionDecorator, java.util.Collection
    public boolean addAll(Collection<? extends E> coll) {
        boolean changed = false;
        Iterator<? extends E> i = coll.iterator();
        while (i.hasNext()) {
            boolean z = true;
            boolean added = add(i.next(), 1);
            if (!changed && !added) {
                z = false;
            }
            changed = z;
        }
        return changed;
    }

    @Override // org.apache.commons.collections4.collection.AbstractCollectionDecorator, java.util.Collection, org.apache.commons.collections4.Bag
    public boolean remove(Object object) {
        return remove(object, 1);
    }

    @Override // org.apache.commons.collections4.collection.AbstractCollectionDecorator, java.util.Collection, org.apache.commons.collections4.Bag
    public boolean removeAll(Collection<?> coll) {
        if (coll != null) {
            boolean result = false;
            for (Object obj : coll) {
                boolean changed = remove(obj, getCount(obj));
                result = result || changed;
            }
            return result;
        }
        return decorated().removeAll(null);
    }

    @Override // org.apache.commons.collections4.collection.AbstractCollectionDecorator, java.util.Collection, org.apache.commons.collections4.Bag
    public boolean retainAll(Collection<?> coll) {
        if (coll != null) {
            boolean modified = false;
            Iterator<E> e = iterator();
            while (e.hasNext()) {
                if (!coll.contains(e.next())) {
                    e.remove();
                    modified = true;
                }
            }
            return modified;
        }
        return decorated().retainAll(null);
    }

    @Override // org.apache.commons.collections4.bag.AbstractBagDecorator, org.apache.commons.collections4.Bag
    public boolean add(E object, int count) {
        decorated().add(object, count);
        return true;
    }
}
