package org.apache.commons.collections4.list;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.util.AbstractList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import org.apache.commons.collections4.OrderedIterator;

/* JADX INFO: loaded from: classes.dex */
public abstract class AbstractLinkedList<E> implements List<E> {
    transient Node<E> header;
    transient int modCount;
    transient int size;

    protected AbstractLinkedList() {
    }

    protected AbstractLinkedList(Collection<? extends E> coll) {
        init();
        addAll(coll);
    }

    protected void init() {
        this.header = createHeaderNode();
    }

    @Override // java.util.List, java.util.Collection
    public int size() {
        return this.size;
    }

    @Override // java.util.List, java.util.Collection
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override // java.util.List
    public E get(int index) {
        Node<E> node = getNode(index, false);
        return node.getValue();
    }

    @Override // java.util.List, java.util.Collection, java.lang.Iterable
    public Iterator<E> iterator() {
        return listIterator();
    }

    @Override // java.util.List
    public ListIterator<E> listIterator() {
        return new LinkedListIterator(this, 0);
    }

    @Override // java.util.List
    public ListIterator<E> listIterator(int fromIndex) {
        return new LinkedListIterator(this, fromIndex);
    }

    @Override // java.util.List
    public int indexOf(Object value) {
        int i = 0;
        for (Node<E> node = this.header.next; node != this.header; node = node.next) {
            if (isEqualValue(node.getValue(), value)) {
                return i;
            }
            i++;
        }
        return -1;
    }

    @Override // java.util.List
    public int lastIndexOf(Object value) {
        int i = this.size - 1;
        for (Node<E> node = this.header.previous; node != this.header; node = node.previous) {
            if (isEqualValue(node.getValue(), value)) {
                return i;
            }
            i--;
        }
        return -1;
    }

    @Override // java.util.List, java.util.Collection
    public boolean contains(Object value) {
        return indexOf(value) != -1;
    }

    @Override // java.util.List, java.util.Collection
    public boolean containsAll(Collection<?> coll) {
        for (Object o : coll) {
            if (!contains(o)) {
                return false;
            }
        }
        return true;
    }

    @Override // java.util.List, java.util.Collection
    public Object[] toArray() {
        return toArray(new Object[this.size]);
    }

    @Override // java.util.List, java.util.Collection
    public <T> T[] toArray(T[] tArr) {
        if (tArr.length < this.size) {
            tArr = (T[]) ((Object[]) Array.newInstance(tArr.getClass().getComponentType(), this.size));
        }
        int i = 0;
        Node<E> node = this.header.next;
        while (node != this.header) {
            tArr[i] = node.getValue();
            node = node.next;
            i++;
        }
        int length = tArr.length;
        int i2 = this.size;
        if (length > i2) {
            tArr[i2] = null;
        }
        return tArr;
    }

    @Override // java.util.List
    public List<E> subList(int fromIndexInclusive, int toIndexExclusive) {
        return new LinkedSubList(this, fromIndexInclusive, toIndexExclusive);
    }

    @Override // java.util.List, java.util.Collection
    public boolean add(E value) {
        addLast(value);
        return true;
    }

    @Override // java.util.List
    public void add(int index, E value) {
        Node<E> node = getNode(index, true);
        addNodeBefore(node, value);
    }

    @Override // java.util.List, java.util.Collection
    public boolean addAll(Collection<? extends E> coll) {
        return addAll(this.size, coll);
    }

    @Override // java.util.List
    public boolean addAll(int index, Collection<? extends E> coll) {
        Node<E> node = getNode(index, true);
        for (E e : coll) {
            addNodeBefore(node, e);
        }
        return true;
    }

    @Override // java.util.List
    public E remove(int index) {
        Node<E> node = getNode(index, false);
        E oldValue = node.getValue();
        removeNode(node);
        return oldValue;
    }

    @Override // java.util.List, java.util.Collection
    public boolean remove(Object value) {
        for (Node<E> node = this.header.next; node != this.header; node = node.next) {
            if (isEqualValue(node.getValue(), value)) {
                removeNode(node);
                return true;
            }
        }
        return false;
    }

    @Override // java.util.List, java.util.Collection
    public boolean removeAll(Collection<?> coll) {
        boolean modified = false;
        Iterator<E> it = iterator();
        while (it.hasNext()) {
            if (coll.contains(it.next())) {
                it.remove();
                modified = true;
            }
        }
        return modified;
    }

    @Override // java.util.List, java.util.Collection
    public boolean retainAll(Collection<?> coll) {
        boolean modified = false;
        Iterator<E> it = iterator();
        while (it.hasNext()) {
            if (!coll.contains(it.next())) {
                it.remove();
                modified = true;
            }
        }
        return modified;
    }

    @Override // java.util.List
    public E set(int index, E value) {
        Node<E> node = getNode(index, false);
        E oldValue = node.getValue();
        updateNode(node, value);
        return oldValue;
    }

    @Override // java.util.List, java.util.Collection
    public void clear() {
        removeAllNodes();
    }

    public E getFirst() {
        Node<E> node = this.header.next;
        if (node == this.header) {
            throw new NoSuchElementException();
        }
        return node.getValue();
    }

    public E getLast() {
        Node<E> node = this.header.previous;
        if (node == this.header) {
            throw new NoSuchElementException();
        }
        return node.getValue();
    }

    public boolean addFirst(E o) {
        addNodeAfter(this.header, o);
        return true;
    }

    public boolean addLast(E o) {
        addNodeBefore(this.header, o);
        return true;
    }

    public E removeFirst() {
        Node<E> node = this.header.next;
        if (node == this.header) {
            throw new NoSuchElementException();
        }
        E oldValue = node.getValue();
        removeNode(node);
        return oldValue;
    }

    public E removeLast() {
        Node<E> node = this.header.previous;
        if (node == this.header) {
            throw new NoSuchElementException();
        }
        E oldValue = node.getValue();
        removeNode(node);
        return oldValue;
    }

    @Override // java.util.List, java.util.Collection
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof List)) {
            return false;
        }
        List<?> other = (List) obj;
        if (other.size() != size()) {
            return false;
        }
        ListIterator<E> listIterator = listIterator();
        ListIterator<E> listIterator2 = other.listIterator();
        while (listIterator.hasNext() && listIterator2.hasNext()) {
            Object o1 = listIterator.next();
            Object o2 = listIterator2.next();
            if (o1 == null) {
                if (o2 != null) {
                    return false;
                }
            } else if (!o1.equals(o2)) {
                return false;
            }
        }
        return (listIterator.hasNext() || listIterator2.hasNext()) ? false : true;
    }

    @Override // java.util.List, java.util.Collection
    public int hashCode() {
        int hashCode = 1;
        Iterator<E> it = iterator();
        while (it.hasNext()) {
            E e = it.next();
            hashCode = (hashCode * 31) + (e == null ? 0 : e.hashCode());
        }
        return hashCode;
    }

    public String toString() {
        if (size() == 0) {
            return "[]";
        }
        StringBuilder buf = new StringBuilder(size() * 16);
        buf.append('[');
        Iterator<E> it = iterator();
        boolean hasNext = it.hasNext();
        while (hasNext) {
            Object value = it.next();
            buf.append(value == this ? "(this Collection)" : value);
            hasNext = it.hasNext();
            if (hasNext) {
                buf.append(", ");
            }
        }
        buf.append(']');
        return buf.toString();
    }

    protected boolean isEqualValue(Object value1, Object value2) {
        return value1 == value2 || (value1 != null && value1.equals(value2));
    }

    protected void updateNode(Node<E> node, E value) {
        node.setValue(value);
    }

    protected Node<E> createHeaderNode() {
        return new Node<>();
    }

    protected Node<E> createNode(E value) {
        return new Node<>(value);
    }

    protected void addNodeBefore(Node<E> node, E value) {
        Node<E> newNode = createNode(value);
        addNode(newNode, node);
    }

    protected void addNodeAfter(Node<E> node, E value) {
        Node<E> newNode = createNode(value);
        addNode(newNode, node.next);
    }

    protected void addNode(Node<E> nodeToInsert, Node<E> insertBeforeNode) {
        nodeToInsert.next = insertBeforeNode;
        nodeToInsert.previous = insertBeforeNode.previous;
        insertBeforeNode.previous.next = nodeToInsert;
        insertBeforeNode.previous = nodeToInsert;
        this.size++;
        this.modCount++;
    }

    protected void removeNode(Node<E> node) {
        node.previous.next = node.next;
        node.next.previous = node.previous;
        this.size--;
        this.modCount++;
    }

    protected void removeAllNodes() {
        Node<E> node = this.header;
        node.next = node;
        Node<E> node2 = this.header;
        node2.previous = node2;
        this.size = 0;
        this.modCount++;
    }

    protected Node<E> getNode(int index, boolean endMarkerAllowed) throws IndexOutOfBoundsException {
        Node<E> node;
        if (index < 0) {
            throw new IndexOutOfBoundsException("Couldn't get the node: index (" + index + ") less than zero.");
        }
        if (!endMarkerAllowed && index == this.size) {
            throw new IndexOutOfBoundsException("Couldn't get the node: index (" + index + ") is the size of the list.");
        }
        int i = this.size;
        if (index > i) {
            throw new IndexOutOfBoundsException("Couldn't get the node: index (" + index + ") greater than the size of the list (" + this.size + ").");
        }
        if (index < i / 2) {
            node = this.header.next;
            for (int currentIndex = 0; currentIndex < index; currentIndex++) {
                node = node.next;
            }
        } else {
            node = this.header;
            for (int currentIndex2 = this.size; currentIndex2 > index; currentIndex2--) {
                node = node.previous;
            }
        }
        return node;
    }

    protected Iterator<E> createSubListIterator(LinkedSubList<E> subList) {
        return createSubListListIterator(subList, 0);
    }

    protected ListIterator<E> createSubListListIterator(LinkedSubList<E> subList, int fromIndex) {
        return new LinkedSubListIterator(subList, fromIndex);
    }

    protected void doWriteObject(ObjectOutputStream outputStream) throws IOException {
        outputStream.writeInt(size());
        for (E e : this) {
            outputStream.writeObject(e);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    protected void doReadObject(ObjectInputStream inputStream) throws IOException, ClassNotFoundException {
        init();
        int size = inputStream.readInt();
        for (int i = 0; i < size; i++) {
            add(inputStream.readObject());
        }
    }

    protected static class Node<E> {
        protected Node<E> next;
        protected Node<E> previous;
        protected E value;

        protected Node() {
            this.previous = this;
            this.next = this;
        }

        protected Node(E value) {
            this.value = value;
        }

        protected Node(Node<E> previous, Node<E> next, E value) {
            this.previous = previous;
            this.next = next;
            this.value = value;
        }

        protected E getValue() {
            return this.value;
        }

        protected void setValue(E value) {
            this.value = value;
        }

        protected Node<E> getPreviousNode() {
            return this.previous;
        }

        protected void setPreviousNode(Node<E> previous) {
            this.previous = previous;
        }

        protected Node<E> getNextNode() {
            return this.next;
        }

        protected void setNextNode(Node<E> next) {
            this.next = next;
        }
    }

    protected static class LinkedListIterator<E> implements ListIterator<E>, OrderedIterator<E> {
        protected Node<E> current;
        protected int expectedModCount;
        protected Node<E> next;
        protected int nextIndex;
        protected final AbstractLinkedList<E> parent;

        protected LinkedListIterator(AbstractLinkedList<E> parent, int fromIndex) throws IndexOutOfBoundsException {
            this.parent = parent;
            this.expectedModCount = parent.modCount;
            this.next = parent.getNode(fromIndex, true);
            this.nextIndex = fromIndex;
        }

        protected void checkModCount() {
            if (this.parent.modCount != this.expectedModCount) {
                throw new ConcurrentModificationException();
            }
        }

        protected Node<E> getLastNodeReturned() throws IllegalStateException {
            Node<E> node = this.current;
            if (node == null) {
                throw new IllegalStateException();
            }
            return node;
        }

        @Override // java.util.ListIterator, java.util.Iterator
        public boolean hasNext() {
            return this.next != this.parent.header;
        }

        @Override // java.util.ListIterator, java.util.Iterator
        public E next() {
            checkModCount();
            if (!hasNext()) {
                throw new NoSuchElementException("No element at index " + this.nextIndex + ".");
            }
            E value = this.next.getValue();
            Node<E> node = this.next;
            this.current = node;
            this.next = node.next;
            this.nextIndex++;
            return value;
        }

        @Override // java.util.ListIterator, org.apache.commons.collections4.OrderedIterator
        public boolean hasPrevious() {
            return this.next.previous != this.parent.header;
        }

        @Override // java.util.ListIterator, org.apache.commons.collections4.OrderedIterator
        public E previous() {
            checkModCount();
            if (!hasPrevious()) {
                throw new NoSuchElementException("Already at start of list.");
            }
            Node<E> node = this.next.previous;
            this.next = node;
            E value = node.getValue();
            this.current = this.next;
            this.nextIndex--;
            return value;
        }

        @Override // java.util.ListIterator
        public int nextIndex() {
            return this.nextIndex;
        }

        @Override // java.util.ListIterator
        public int previousIndex() {
            return nextIndex() - 1;
        }

        @Override // java.util.ListIterator, java.util.Iterator
        public void remove() {
            checkModCount();
            Node<E> node = this.current;
            Node<E> node2 = this.next;
            if (node == node2) {
                this.next = node2.next;
                this.parent.removeNode(getLastNodeReturned());
            } else {
                this.parent.removeNode(getLastNodeReturned());
                this.nextIndex--;
            }
            this.current = null;
            this.expectedModCount++;
        }

        @Override // java.util.ListIterator
        public void set(E obj) {
            checkModCount();
            getLastNodeReturned().setValue(obj);
        }

        @Override // java.util.ListIterator
        public void add(E obj) {
            checkModCount();
            this.parent.addNodeBefore(this.next, obj);
            this.current = null;
            this.nextIndex++;
            this.expectedModCount++;
        }
    }

    protected static class LinkedSubListIterator<E> extends LinkedListIterator<E> {
        protected final LinkedSubList<E> sub;

        protected LinkedSubListIterator(LinkedSubList<E> sub, int startIndex) {
            super(sub.parent, sub.offset + startIndex);
            this.sub = sub;
        }

        @Override // org.apache.commons.collections4.list.AbstractLinkedList.LinkedListIterator, java.util.ListIterator, java.util.Iterator
        public boolean hasNext() {
            return nextIndex() < this.sub.size;
        }

        @Override // org.apache.commons.collections4.list.AbstractLinkedList.LinkedListIterator, java.util.ListIterator, org.apache.commons.collections4.OrderedIterator
        public boolean hasPrevious() {
            return previousIndex() >= 0;
        }

        @Override // org.apache.commons.collections4.list.AbstractLinkedList.LinkedListIterator, java.util.ListIterator
        public int nextIndex() {
            return super.nextIndex() - this.sub.offset;
        }

        @Override // org.apache.commons.collections4.list.AbstractLinkedList.LinkedListIterator, java.util.ListIterator
        public void add(E obj) {
            super.add(obj);
            this.sub.expectedModCount = this.parent.modCount;
            this.sub.size++;
        }

        @Override // org.apache.commons.collections4.list.AbstractLinkedList.LinkedListIterator, java.util.ListIterator, java.util.Iterator
        public void remove() {
            super.remove();
            this.sub.expectedModCount = this.parent.modCount;
            LinkedSubList<E> linkedSubList = this.sub;
            linkedSubList.size--;
        }
    }

    protected static class LinkedSubList<E> extends AbstractList<E> {
        int expectedModCount;
        int offset;
        AbstractLinkedList<E> parent;
        int size;

        protected LinkedSubList(AbstractLinkedList<E> parent, int fromIndex, int toIndex) {
            if (fromIndex < 0) {
                throw new IndexOutOfBoundsException("fromIndex = " + fromIndex);
            }
            if (toIndex > parent.size()) {
                throw new IndexOutOfBoundsException("toIndex = " + toIndex);
            }
            if (fromIndex > toIndex) {
                throw new IllegalArgumentException("fromIndex(" + fromIndex + ") > toIndex(" + toIndex + ")");
            }
            this.parent = parent;
            this.offset = fromIndex;
            this.size = toIndex - fromIndex;
            this.expectedModCount = parent.modCount;
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
        public int size() {
            checkModCount();
            return this.size;
        }

        @Override // java.util.AbstractList, java.util.List
        public E get(int index) {
            rangeCheck(index, this.size);
            checkModCount();
            return this.parent.get(this.offset + index);
        }

        @Override // java.util.AbstractList, java.util.List
        public void add(int index, E obj) {
            rangeCheck(index, this.size + 1);
            checkModCount();
            this.parent.add(this.offset + index, obj);
            this.expectedModCount = this.parent.modCount;
            this.size++;
            this.modCount++;
        }

        @Override // java.util.AbstractList, java.util.List
        public E remove(int index) {
            rangeCheck(index, this.size);
            checkModCount();
            E result = this.parent.remove(this.offset + index);
            this.expectedModCount = this.parent.modCount;
            this.size--;
            this.modCount++;
            return result;
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
        public boolean addAll(Collection<? extends E> coll) {
            return addAll(this.size, coll);
        }

        @Override // java.util.AbstractList, java.util.List
        public boolean addAll(int index, Collection<? extends E> coll) {
            rangeCheck(index, this.size + 1);
            int cSize = coll.size();
            if (cSize == 0) {
                return false;
            }
            checkModCount();
            this.parent.addAll(this.offset + index, coll);
            this.expectedModCount = this.parent.modCount;
            this.size += cSize;
            this.modCount++;
            return true;
        }

        @Override // java.util.AbstractList, java.util.List
        public E set(int index, E obj) {
            rangeCheck(index, this.size);
            checkModCount();
            return this.parent.set(this.offset + index, obj);
        }

        @Override // java.util.AbstractList, java.util.AbstractCollection, java.util.Collection, java.util.List
        public void clear() {
            checkModCount();
            Iterator<E> it = iterator();
            while (it.hasNext()) {
                it.next();
                it.remove();
            }
        }

        @Override // java.util.AbstractList, java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.List
        public Iterator<E> iterator() {
            checkModCount();
            return this.parent.createSubListIterator(this);
        }

        @Override // java.util.AbstractList, java.util.List
        public ListIterator<E> listIterator(int index) {
            rangeCheck(index, this.size + 1);
            checkModCount();
            return this.parent.createSubListListIterator(this, index);
        }

        @Override // java.util.AbstractList, java.util.List
        public List<E> subList(int fromIndexInclusive, int toIndexExclusive) {
            AbstractLinkedList<E> abstractLinkedList = this.parent;
            int i = this.offset;
            return new LinkedSubList(abstractLinkedList, fromIndexInclusive + i, i + toIndexExclusive);
        }

        protected void rangeCheck(int index, int beyond) {
            if (index < 0 || index >= beyond) {
                throw new IndexOutOfBoundsException("Index '" + index + "' out of bounds for size '" + this.size + "'");
            }
        }

        protected void checkModCount() {
            if (this.parent.modCount != this.expectedModCount) {
                throw new ConcurrentModificationException();
            }
        }
    }
}
