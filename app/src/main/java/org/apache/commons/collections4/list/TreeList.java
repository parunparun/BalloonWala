package org.apache.commons.collections4.list;

import java.util.AbstractList;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Deque;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import org.apache.commons.collections4.OrderedIterator;

/* JADX INFO: loaded from: classes.dex */
public class TreeList<E> extends AbstractList<E> {
    private AVLNode<E> root;
    private int size;

    public TreeList() {
    }

    public TreeList(Collection<? extends E> coll) {
        if (!coll.isEmpty()) {
            this.root = new AVLNode<>(coll);
            this.size = coll.size();
        }
    }

    @Override // java.util.AbstractList, java.util.List
    public E get(int index) {
        checkInterval(index, 0, size() - 1);
        return this.root.get(index).getValue();
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public int size() {
        return this.size;
    }

    @Override // java.util.AbstractList, java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.List
    public Iterator<E> iterator() {
        return listIterator(0);
    }

    @Override // java.util.AbstractList, java.util.List
    public ListIterator<E> listIterator() {
        return listIterator(0);
    }

    @Override // java.util.AbstractList, java.util.List
    public ListIterator<E> listIterator(int fromIndex) {
        checkInterval(fromIndex, 0, size());
        return new TreeListIterator(this, fromIndex);
    }

    @Override // java.util.AbstractList, java.util.List
    public int indexOf(Object object) {
        AVLNode<E> aVLNode = this.root;
        if (aVLNode == null) {
            return -1;
        }
        return aVLNode.indexOf(object, ((AVLNode) aVLNode).relativePosition);
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public boolean contains(Object object) {
        return indexOf(object) >= 0;
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public Object[] toArray() {
        Object[] array = new Object[size()];
        AVLNode<E> aVLNode = this.root;
        if (aVLNode != null) {
            aVLNode.toArray(array, ((AVLNode) aVLNode).relativePosition);
        }
        return array;
    }

    @Override // java.util.AbstractList, java.util.List
    public void add(int index, E obj) {
        this.modCount++;
        checkInterval(index, 0, size());
        AVLNode<E> aVLNode = this.root;
        if (aVLNode == null) {
            this.root = new AVLNode<>(index, obj, null, null);
        } else {
            this.root = aVLNode.insert(index, obj);
        }
        this.size++;
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public boolean addAll(Collection<? extends E> c) {
        if (c.isEmpty()) {
            return false;
        }
        this.modCount += c.size();
        AVLNode<E> cTree = new AVLNode<>(c);
        AVLNode<E> aVLNode = this.root;
        this.root = aVLNode == null ? cTree : aVLNode.addAll(cTree, this.size);
        this.size += c.size();
        return true;
    }

    @Override // java.util.AbstractList, java.util.List
    public E set(int i, E e) {
        checkInterval(i, 0, size() - 1);
        AVLNode<E> aVLNode = this.root.get(i);
        E e2 = (E) ((AVLNode) aVLNode).value;
        aVLNode.setValue(e);
        return e2;
    }

    @Override // java.util.AbstractList, java.util.List
    public E remove(int index) {
        this.modCount++;
        checkInterval(index, 0, size() - 1);
        E result = get(index);
        this.root = this.root.remove(index);
        this.size--;
        return result;
    }

    @Override // java.util.AbstractList, java.util.AbstractCollection, java.util.Collection, java.util.List
    public void clear() {
        this.modCount++;
        this.root = null;
        this.size = 0;
    }

    private void checkInterval(int index, int startIndex, int endIndex) {
        if (index < startIndex || index > endIndex) {
            throw new IndexOutOfBoundsException("Invalid index:" + index + ", size=" + size());
        }
    }

    static class AVLNode<E> {
        private int height;
        private AVLNode<E> left;
        private boolean leftIsPrevious;
        private int relativePosition;
        private AVLNode<E> right;
        private boolean rightIsNext;
        private E value;

        private AVLNode(int relativePosition, E obj, AVLNode<E> rightFollower, AVLNode<E> leftFollower) {
            this.relativePosition = relativePosition;
            this.value = obj;
            this.rightIsNext = true;
            this.leftIsPrevious = true;
            this.right = rightFollower;
            this.left = leftFollower;
        }

        private AVLNode(Collection<? extends E> coll) {
            this(coll.iterator(), 0, coll.size() - 1, 0, null, null);
        }

        private AVLNode(Iterator<? extends E> iterator, int start, int end, int absolutePositionOfParent, AVLNode<E> prev, AVLNode<E> next) {
            int mid = start + ((end - start) / 2);
            if (start < mid) {
                this.left = new AVLNode<>(iterator, start, mid - 1, mid, prev, this);
            } else {
                this.leftIsPrevious = true;
                this.left = prev;
            }
            this.value = iterator.next();
            this.relativePosition = mid - absolutePositionOfParent;
            if (mid < end) {
                this.right = new AVLNode<>(iterator, mid + 1, end, mid, this, next);
            } else {
                this.rightIsNext = true;
                this.right = next;
            }
            recalcHeight();
        }

        E getValue() {
            return this.value;
        }

        void setValue(E obj) {
            this.value = obj;
        }

        AVLNode<E> get(int index) {
            int indexRelativeToMe = index - this.relativePosition;
            if (indexRelativeToMe == 0) {
                return this;
            }
            AVLNode<E> nextNode = indexRelativeToMe < 0 ? getLeftSubTree() : getRightSubTree();
            if (nextNode == null) {
                return null;
            }
            return nextNode.get(indexRelativeToMe);
        }

        int indexOf(Object object, int index) {
            if (getLeftSubTree() != null) {
                AVLNode<E> aVLNode = this.left;
                int result = aVLNode.indexOf(object, aVLNode.relativePosition + index);
                if (result != -1) {
                    return result;
                }
            }
            E e = this.value;
            if (e != null ? e.equals(object) : e == object) {
                return index;
            }
            if (getRightSubTree() == null) {
                return -1;
            }
            AVLNode<E> aVLNode2 = this.right;
            return aVLNode2.indexOf(object, aVLNode2.relativePosition + index);
        }

        void toArray(Object[] array, int index) {
            array[index] = this.value;
            if (getLeftSubTree() != null) {
                AVLNode<E> aVLNode = this.left;
                aVLNode.toArray(array, aVLNode.relativePosition + index);
            }
            if (getRightSubTree() != null) {
                AVLNode<E> aVLNode2 = this.right;
                aVLNode2.toArray(array, aVLNode2.relativePosition + index);
            }
        }

        AVLNode<E> next() {
            AVLNode<E> aVLNode;
            if (this.rightIsNext || (aVLNode = this.right) == null) {
                return this.right;
            }
            return aVLNode.min();
        }

        AVLNode<E> previous() {
            AVLNode<E> aVLNode;
            if (this.leftIsPrevious || (aVLNode = this.left) == null) {
                return this.left;
            }
            return aVLNode.max();
        }

        AVLNode<E> insert(int index, E obj) {
            int indexRelativeToMe = index - this.relativePosition;
            if (indexRelativeToMe <= 0) {
                return insertOnLeft(indexRelativeToMe, obj);
            }
            return insertOnRight(indexRelativeToMe, obj);
        }

        private AVLNode<E> insertOnLeft(int indexRelativeToMe, E obj) {
            if (getLeftSubTree() == null) {
                setLeft(new AVLNode<>(-1, obj, this, this.left), null);
            } else {
                setLeft(this.left.insert(indexRelativeToMe, obj), null);
            }
            int i = this.relativePosition;
            if (i >= 0) {
                this.relativePosition = i + 1;
            }
            AVLNode<E> ret = balance();
            recalcHeight();
            return ret;
        }

        private AVLNode<E> insertOnRight(int indexRelativeToMe, E obj) {
            if (getRightSubTree() == null) {
                setRight(new AVLNode<>(1, obj, this.right, this), null);
            } else {
                setRight(this.right.insert(indexRelativeToMe, obj), null);
            }
            int i = this.relativePosition;
            if (i < 0) {
                this.relativePosition = i - 1;
            }
            AVLNode<E> ret = balance();
            recalcHeight();
            return ret;
        }

        private AVLNode<E> getLeftSubTree() {
            if (this.leftIsPrevious) {
                return null;
            }
            return this.left;
        }

        private AVLNode<E> getRightSubTree() {
            if (this.rightIsNext) {
                return null;
            }
            return this.right;
        }

        private AVLNode<E> max() {
            return getRightSubTree() == null ? this : this.right.max();
        }

        private AVLNode<E> min() {
            return getLeftSubTree() == null ? this : this.left.min();
        }

        AVLNode<E> remove(int index) {
            int indexRelativeToMe = index - this.relativePosition;
            if (indexRelativeToMe == 0) {
                return removeSelf();
            }
            if (indexRelativeToMe > 0) {
                setRight(this.right.remove(indexRelativeToMe), this.right.right);
                int i = this.relativePosition;
                if (i < 0) {
                    this.relativePosition = i + 1;
                }
            } else {
                setLeft(this.left.remove(indexRelativeToMe), this.left.left);
                int i2 = this.relativePosition;
                if (i2 > 0) {
                    this.relativePosition = i2 - 1;
                }
            }
            recalcHeight();
            return balance();
        }

        private AVLNode<E> removeMax() {
            if (getRightSubTree() == null) {
                return removeSelf();
            }
            setRight(this.right.removeMax(), this.right.right);
            int i = this.relativePosition;
            if (i < 0) {
                this.relativePosition = i + 1;
            }
            recalcHeight();
            return balance();
        }

        private AVLNode<E> removeMin() {
            if (getLeftSubTree() == null) {
                return removeSelf();
            }
            setLeft(this.left.removeMin(), this.left.left);
            int i = this.relativePosition;
            if (i > 0) {
                this.relativePosition = i - 1;
            }
            recalcHeight();
            return balance();
        }

        private AVLNode<E> removeSelf() {
            if (getRightSubTree() == null && getLeftSubTree() == null) {
                return null;
            }
            if (getRightSubTree() == null) {
                int i = this.relativePosition;
                if (i > 0) {
                    this.left.relativePosition += i;
                }
                this.left.max().setRight(null, this.right);
                return this.left;
            }
            if (getLeftSubTree() == null) {
                AVLNode<E> aVLNode = this.right;
                int i2 = aVLNode.relativePosition;
                int i3 = this.relativePosition;
                aVLNode.relativePosition = i2 + (i3 - (i3 < 0 ? 0 : 1));
                this.right.min().setLeft(null, this.left);
                return this.right;
            }
            if (heightRightMinusLeft() > 0) {
                AVLNode<E> rightMin = this.right.min();
                this.value = rightMin.value;
                if (this.leftIsPrevious) {
                    this.left = rightMin.left;
                }
                this.right = this.right.removeMin();
                int i4 = this.relativePosition;
                if (i4 < 0) {
                    this.relativePosition = i4 + 1;
                }
            } else {
                AVLNode<E> leftMax = this.left.max();
                this.value = leftMax.value;
                if (this.rightIsNext) {
                    this.right = leftMax.right;
                }
                AVLNode<E> aVLNode2 = this.left;
                AVLNode<E> leftPrevious = aVLNode2.left;
                AVLNode<E> aVLNodeRemoveMax = aVLNode2.removeMax();
                this.left = aVLNodeRemoveMax;
                if (aVLNodeRemoveMax == null) {
                    this.left = leftPrevious;
                    this.leftIsPrevious = true;
                }
                int i5 = this.relativePosition;
                if (i5 > 0) {
                    this.relativePosition = i5 - 1;
                }
            }
            recalcHeight();
            return this;
        }

        private AVLNode<E> balance() {
            int iHeightRightMinusLeft = heightRightMinusLeft();
            if (iHeightRightMinusLeft == -2) {
                if (this.left.heightRightMinusLeft() > 0) {
                    setLeft(this.left.rotateLeft(), null);
                }
                return rotateRight();
            }
            if (iHeightRightMinusLeft == -1 || iHeightRightMinusLeft == 0 || iHeightRightMinusLeft == 1) {
                return this;
            }
            if (iHeightRightMinusLeft == 2) {
                if (this.right.heightRightMinusLeft() < 0) {
                    setRight(this.right.rotateRight(), null);
                }
                return rotateLeft();
            }
            throw new RuntimeException("tree inconsistent!");
        }

        private int getOffset(AVLNode<E> node) {
            if (node == null) {
                return 0;
            }
            return node.relativePosition;
        }

        private int setOffset(AVLNode<E> node, int newOffest) {
            if (node == null) {
                return 0;
            }
            int oldOffset = getOffset(node);
            node.relativePosition = newOffest;
            return oldOffset;
        }

        private void recalcHeight() {
            this.height = Math.max(getLeftSubTree() == null ? -1 : getLeftSubTree().height, getRightSubTree() != null ? getRightSubTree().height : -1) + 1;
        }

        private int getHeight(AVLNode<E> node) {
            if (node == null) {
                return -1;
            }
            return node.height;
        }

        private int heightRightMinusLeft() {
            return getHeight(getRightSubTree()) - getHeight(getLeftSubTree());
        }

        private AVLNode<E> rotateLeft() {
            AVLNode<E> newTop = this.right;
            AVLNode<E> movedNode = getRightSubTree().getLeftSubTree();
            int newTopPosition = this.relativePosition + getOffset(newTop);
            int myNewPosition = -newTop.relativePosition;
            int movedPosition = getOffset(newTop) + getOffset(movedNode);
            setRight(movedNode, newTop);
            newTop.setLeft(this, null);
            setOffset(newTop, newTopPosition);
            setOffset(this, myNewPosition);
            setOffset(movedNode, movedPosition);
            return newTop;
        }

        private AVLNode<E> rotateRight() {
            AVLNode<E> newTop = this.left;
            AVLNode<E> movedNode = getLeftSubTree().getRightSubTree();
            int newTopPosition = this.relativePosition + getOffset(newTop);
            int myNewPosition = -newTop.relativePosition;
            int movedPosition = getOffset(newTop) + getOffset(movedNode);
            setLeft(movedNode, newTop);
            newTop.setRight(this, null);
            setOffset(newTop, newTopPosition);
            setOffset(this, myNewPosition);
            setOffset(movedNode, movedPosition);
            return newTop;
        }

        private void setLeft(AVLNode<E> node, AVLNode<E> previous) {
            boolean z = node == null;
            this.leftIsPrevious = z;
            this.left = z ? previous : node;
            recalcHeight();
        }

        private void setRight(AVLNode<E> node, AVLNode<E> next) {
            boolean z = node == null;
            this.rightIsNext = z;
            this.right = z ? next : node;
            recalcHeight();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public AVLNode<E> addAll(AVLNode<E> otherTree, int currentSize) {
            AVLNode<E> maxNode = max();
            AVLNode<E> otherTreeMin = otherTree.min();
            if (otherTree.height > this.height) {
                AVLNode<E> leftSubTree = removeMax();
                Deque<AVLNode<E>> sAncestors = new ArrayDeque<>();
                AVLNode<E> s = otherTree;
                int sAbsolutePosition = s.relativePosition + currentSize;
                int sParentAbsolutePosition = 0;
                while (s != null && s.height > getHeight(leftSubTree)) {
                    sParentAbsolutePosition = sAbsolutePosition;
                    sAncestors.push(s);
                    s = s.left;
                    if (s != null) {
                        sAbsolutePosition += s.relativePosition;
                    }
                }
                maxNode.setLeft(leftSubTree, null);
                maxNode.setRight(s, otherTreeMin);
                if (leftSubTree != null) {
                    leftSubTree.max().setRight(null, maxNode);
                    leftSubTree.relativePosition -= currentSize - 1;
                }
                if (s != null) {
                    s.min().setLeft(null, maxNode);
                    s.relativePosition = (sAbsolutePosition - currentSize) + 1;
                }
                maxNode.relativePosition = (currentSize - 1) - sParentAbsolutePosition;
                otherTree.relativePosition += currentSize;
                AVLNode<E> s2 = maxNode;
                while (!sAncestors.isEmpty()) {
                    AVLNode<E> sAncestor = sAncestors.pop();
                    sAncestor.setLeft(s2, null);
                    s2 = sAncestor.balance();
                }
                return s2;
            }
            AVLNode<E> otherTree2 = otherTree.removeMin();
            Deque<AVLNode<E>> sAncestors2 = new ArrayDeque<>();
            AVLNode<E> s3 = this;
            int sAbsolutePosition2 = s3.relativePosition;
            int sParentAbsolutePosition2 = 0;
            while (s3 != null && s3.height > getHeight(otherTree2)) {
                sParentAbsolutePosition2 = sAbsolutePosition2;
                sAncestors2.push(s3);
                s3 = s3.right;
                if (s3 != null) {
                    sAbsolutePosition2 += s3.relativePosition;
                }
            }
            otherTreeMin.setRight(otherTree2, null);
            otherTreeMin.setLeft(s3, maxNode);
            if (otherTree2 != null) {
                otherTree2.min().setLeft(null, otherTreeMin);
                otherTree2.relativePosition++;
            }
            if (s3 != null) {
                s3.max().setRight(null, otherTreeMin);
                s3.relativePosition = sAbsolutePosition2 - currentSize;
            }
            otherTreeMin.relativePosition = currentSize - sParentAbsolutePosition2;
            AVLNode<E> s4 = otherTreeMin;
            while (!sAncestors2.isEmpty()) {
                AVLNode<E> sAncestor2 = sAncestors2.pop();
                sAncestor2.setRight(s4, null);
                s4 = sAncestor2.balance();
            }
            return s4;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("AVLNode(");
            sb.append(this.relativePosition);
            sb.append(',');
            sb.append(this.left != null);
            sb.append(',');
            sb.append(this.value);
            sb.append(',');
            sb.append(getRightSubTree() != null);
            sb.append(", faedelung ");
            sb.append(this.rightIsNext);
            sb.append(" )");
            return sb.toString();
        }
    }

    static class TreeListIterator<E> implements ListIterator<E>, OrderedIterator<E> {
        private AVLNode<E> current;
        private int currentIndex;
        private int expectedModCount;
        private AVLNode<E> next;
        private int nextIndex;
        private final TreeList<E> parent;

        protected TreeListIterator(TreeList<E> parent, int fromIndex) throws IndexOutOfBoundsException {
            this.parent = parent;
            this.expectedModCount = parent.modCount;
            this.next = ((TreeList) parent).root == null ? null : ((TreeList) parent).root.get(fromIndex);
            this.nextIndex = fromIndex;
            this.currentIndex = -1;
        }

        protected void checkModCount() {
            if (this.parent.modCount != this.expectedModCount) {
                throw new ConcurrentModificationException();
            }
        }

        @Override // java.util.ListIterator, java.util.Iterator
        public boolean hasNext() {
            return this.nextIndex < this.parent.size();
        }

        @Override // java.util.ListIterator, java.util.Iterator
        public E next() {
            checkModCount();
            if (!hasNext()) {
                throw new NoSuchElementException("No element at index " + this.nextIndex + ".");
            }
            if (this.next == null) {
                this.next = ((TreeList) this.parent).root.get(this.nextIndex);
            }
            E value = this.next.getValue();
            AVLNode<E> aVLNode = this.next;
            this.current = aVLNode;
            int i = this.nextIndex;
            this.nextIndex = i + 1;
            this.currentIndex = i;
            this.next = aVLNode.next();
            return value;
        }

        @Override // java.util.ListIterator, org.apache.commons.collections4.OrderedIterator
        public boolean hasPrevious() {
            return this.nextIndex > 0;
        }

        @Override // java.util.ListIterator, org.apache.commons.collections4.OrderedIterator
        public E previous() {
            checkModCount();
            if (!hasPrevious()) {
                throw new NoSuchElementException("Already at start of list.");
            }
            AVLNode<E> aVLNode = this.next;
            if (aVLNode == null) {
                this.next = ((TreeList) this.parent).root.get(this.nextIndex - 1);
            } else {
                this.next = aVLNode.previous();
            }
            E value = this.next.getValue();
            this.current = this.next;
            int i = this.nextIndex - 1;
            this.nextIndex = i;
            this.currentIndex = i;
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
            int i = this.currentIndex;
            if (i == -1) {
                throw new IllegalStateException();
            }
            this.parent.remove(i);
            int i2 = this.nextIndex;
            if (i2 != this.currentIndex) {
                this.nextIndex = i2 - 1;
            }
            this.next = null;
            this.current = null;
            this.currentIndex = -1;
            this.expectedModCount++;
        }

        @Override // java.util.ListIterator
        public void set(E obj) {
            checkModCount();
            AVLNode<E> aVLNode = this.current;
            if (aVLNode == null) {
                throw new IllegalStateException();
            }
            aVLNode.setValue(obj);
        }

        @Override // java.util.ListIterator
        public void add(E obj) {
            checkModCount();
            this.parent.add(this.nextIndex, obj);
            this.current = null;
            this.currentIndex = -1;
            this.nextIndex++;
            this.expectedModCount++;
        }
    }
}
