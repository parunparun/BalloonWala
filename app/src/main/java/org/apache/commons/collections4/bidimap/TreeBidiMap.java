package org.apache.commons.collections4.bidimap;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.Comparable;
import java.util.AbstractSet;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import org.apache.commons.collections4.KeyValue;
import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.OrderedBidiMap;
import org.apache.commons.collections4.OrderedIterator;
import org.apache.commons.collections4.OrderedMapIterator;
import org.apache.commons.collections4.iterators.EmptyOrderedMapIterator;
import org.apache.commons.collections4.keyvalue.UnmodifiableMapEntry;

/* JADX INFO: loaded from: classes.dex */
public class TreeBidiMap<K extends Comparable<K>, V extends Comparable<V>> implements OrderedBidiMap<K, V>, Serializable {
    private static final long serialVersionUID = 721969328361807L;
    private transient Set<Map.Entry<K, V>> entrySet;
    private transient TreeBidiMap<K, V>.Inverse inverse;
    private transient Set<K> keySet;
    private transient int modifications;
    private transient int nodeCount;
    private transient Node<K, V>[] rootNode;
    private transient Set<V> valuesSet;

    enum DataElement {
        KEY("key"),
        VALUE("value");

        private final String description;

        DataElement(String description) {
            this.description = description;
        }

        @Override // java.lang.Enum
        public String toString() {
            return this.description;
        }
    }

    public TreeBidiMap() {
        this.nodeCount = 0;
        this.modifications = 0;
        this.inverse = null;
        this.rootNode = new Node[2];
    }

    public TreeBidiMap(Map<? extends K, ? extends V> map) {
        this();
        putAll(map);
    }

    @Override // java.util.Map, org.apache.commons.collections4.Get
    public int size() {
        return this.nodeCount;
    }

    @Override // java.util.Map, org.apache.commons.collections4.Get
    public boolean isEmpty() {
        return this.nodeCount == 0;
    }

    @Override // java.util.Map, org.apache.commons.collections4.Get
    public boolean containsKey(Object key) {
        checkKey(key);
        return lookupKey(key) != null;
    }

    @Override // java.util.Map, org.apache.commons.collections4.Get
    public boolean containsValue(Object value) {
        checkValue(value);
        return lookupValue(value) != null;
    }

    @Override // java.util.Map, org.apache.commons.collections4.Get
    public V get(Object obj) {
        checkKey(obj);
        Node<K, V> nodeLookupKey = lookupKey(obj);
        if (nodeLookupKey == null) {
            return null;
        }
        return (V) nodeLookupKey.getValue();
    }

    @Override // org.apache.commons.collections4.BidiMap, java.util.Map, org.apache.commons.collections4.Put
    public V put(K k, V v) {
        V v2 = (V) get((Object) k);
        doPut(k, v);
        return v2;
    }

    @Override // java.util.Map, org.apache.commons.collections4.Put
    public void putAll(Map<? extends K, ? extends V> map) {
        for (Map.Entry<? extends K, ? extends V> e : map.entrySet()) {
            put((Comparable) e.getKey(), (Comparable) e.getValue());
        }
    }

    @Override // java.util.Map, org.apache.commons.collections4.Get
    public V remove(Object obj) {
        return (V) doRemoveKey(obj);
    }

    @Override // java.util.Map, org.apache.commons.collections4.Put
    public void clear() {
        modify();
        this.nodeCount = 0;
        this.rootNode[DataElement.KEY.ordinal()] = null;
        this.rootNode[DataElement.VALUE.ordinal()] = null;
    }

    @Override // org.apache.commons.collections4.BidiMap
    public K getKey(Object obj) {
        checkValue(obj);
        Node<K, V> nodeLookupValue = lookupValue(obj);
        if (nodeLookupValue == null) {
            return null;
        }
        return (K) nodeLookupValue.getKey();
    }

    @Override // org.apache.commons.collections4.BidiMap
    public K removeValue(Object obj) {
        return (K) doRemoveValue(obj);
    }

    @Override // org.apache.commons.collections4.OrderedMap
    public K firstKey() {
        if (this.nodeCount == 0) {
            throw new NoSuchElementException("Map is empty");
        }
        return (K) leastNode(this.rootNode[DataElement.KEY.ordinal()], DataElement.KEY).getKey();
    }

    @Override // org.apache.commons.collections4.OrderedMap
    public K lastKey() {
        if (this.nodeCount == 0) {
            throw new NoSuchElementException("Map is empty");
        }
        return (K) greatestNode(this.rootNode[DataElement.KEY.ordinal()], DataElement.KEY).getKey();
    }

    @Override // org.apache.commons.collections4.OrderedMap
    public K nextKey(K k) {
        checkKey(k);
        Node<K, V> nodeNextGreater = nextGreater(lookupKey(k), DataElement.KEY);
        if (nodeNextGreater == null) {
            return null;
        }
        return (K) nodeNextGreater.getKey();
    }

    @Override // org.apache.commons.collections4.OrderedMap
    public K previousKey(K k) {
        checkKey(k);
        Node<K, V> nodeNextSmaller = nextSmaller(lookupKey(k), DataElement.KEY);
        if (nodeNextSmaller == null) {
            return null;
        }
        return (K) nodeNextSmaller.getKey();
    }

    @Override // java.util.Map, org.apache.commons.collections4.Get
    public Set<K> keySet() {
        if (this.keySet == null) {
            this.keySet = new KeyView(DataElement.KEY);
        }
        return this.keySet;
    }

    @Override // org.apache.commons.collections4.BidiMap, java.util.Map, org.apache.commons.collections4.Get
    public Set<V> values() {
        if (this.valuesSet == null) {
            this.valuesSet = new ValueView(DataElement.KEY);
        }
        return this.valuesSet;
    }

    @Override // java.util.Map, org.apache.commons.collections4.Get
    public Set<Map.Entry<K, V>> entrySet() {
        if (this.entrySet == null) {
            this.entrySet = new EntryView();
        }
        return this.entrySet;
    }

    @Override // org.apache.commons.collections4.IterableGet
    public OrderedMapIterator<K, V> mapIterator() {
        if (isEmpty()) {
            return EmptyOrderedMapIterator.emptyOrderedMapIterator();
        }
        return new ViewMapIterator(DataElement.KEY);
    }

    @Override // org.apache.commons.collections4.OrderedBidiMap, org.apache.commons.collections4.BidiMap
    public OrderedBidiMap<V, K> inverseBidiMap() {
        if (this.inverse == null) {
            this.inverse = new Inverse();
        }
        return this.inverse;
    }

    @Override // java.util.Map
    public boolean equals(Object obj) {
        return doEquals(obj, DataElement.KEY);
    }

    @Override // java.util.Map
    public int hashCode() {
        return doHashCode(DataElement.KEY);
    }

    public String toString() {
        return doToString(DataElement.KEY);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doPut(K key, V value) {
        checkKeyAndValue(key, value);
        doRemoveKey(key);
        doRemoveValue(value);
        Node<K, V> node = this.rootNode[DataElement.KEY.ordinal()];
        if (node == null) {
            Node<K, V> root = new Node<>(key, value);
            this.rootNode[DataElement.KEY.ordinal()] = root;
            this.rootNode[DataElement.VALUE.ordinal()] = root;
            grow();
            return;
        }
        while (true) {
            int cmp = compare(key, node.getKey());
            if (cmp == 0) {
                throw new IllegalArgumentException("Cannot store a duplicate key (\"" + key + "\") in this Map");
            }
            if (cmp < 0) {
                if (node.getLeft(DataElement.KEY) == null) {
                    Node<K, V> newNode = new Node<>(key, value);
                    insertValue(newNode);
                    node.setLeft(newNode, DataElement.KEY);
                    newNode.setParent(node, DataElement.KEY);
                    doRedBlackInsert(newNode, DataElement.KEY);
                    grow();
                    return;
                }
                node = node.getLeft(DataElement.KEY);
            } else {
                if (node.getRight(DataElement.KEY) == null) {
                    Node<K, V> newNode2 = new Node<>(key, value);
                    insertValue(newNode2);
                    node.setRight(newNode2, DataElement.KEY);
                    newNode2.setParent(node, DataElement.KEY);
                    doRedBlackInsert(newNode2, DataElement.KEY);
                    grow();
                    return;
                }
                node = node.getRight(DataElement.KEY);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public V doRemoveKey(Object obj) {
        Node<K, V> nodeLookupKey = lookupKey(obj);
        if (nodeLookupKey == null) {
            return null;
        }
        doRedBlackDelete(nodeLookupKey);
        return (V) nodeLookupKey.getValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public K doRemoveValue(Object obj) {
        Node<K, V> nodeLookupValue = lookupValue(obj);
        if (nodeLookupValue == null) {
            return null;
        }
        doRedBlackDelete(nodeLookupValue);
        return (K) nodeLookupValue.getKey();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public <T extends Comparable<T>> Node<K, V> lookup(Object data, DataElement dataElement) {
        Node<K, V> node = this.rootNode[dataElement.ordinal()];
        while (node != null) {
            int cmp = compare((Comparable) data, (Comparable) node.getData(dataElement));
            if (cmp == 0) {
                Node<K, V> rval = node;
                return rval;
            }
            node = cmp < 0 ? node.getLeft(dataElement) : node.getRight(dataElement);
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Node<K, V> lookupKey(Object key) {
        return lookup(key, DataElement.KEY);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Node<K, V> lookupValue(Object value) {
        return lookup(value, DataElement.VALUE);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Node<K, V> nextGreater(Node<K, V> node, DataElement dataElement) {
        if (node == null) {
            return null;
        }
        Node<K, V> rval = node.getRight(dataElement);
        if (rval != null) {
            Node<K, V> rval2 = leastNode(node.getRight(dataElement), dataElement);
            return rval2;
        }
        Node<K, V> parent = node.getParent(dataElement);
        Node<K, V> child = node;
        while (parent != null && child == parent.getRight(dataElement)) {
            child = parent;
            parent = parent.getParent(dataElement);
        }
        return parent;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Node<K, V> nextSmaller(Node<K, V> node, DataElement dataElement) {
        if (node == null) {
            return null;
        }
        Node<K, V> rval = node.getLeft(dataElement);
        if (rval != null) {
            Node<K, V> rval2 = greatestNode(node.getLeft(dataElement), dataElement);
            return rval2;
        }
        Node<K, V> parent = node.getParent(dataElement);
        Node<K, V> child = node;
        while (parent != null && child == parent.getLeft(dataElement)) {
            child = parent;
            parent = parent.getParent(dataElement);
        }
        return parent;
    }

    private static <T extends Comparable<T>> int compare(T o1, T o2) {
        return o1.compareTo(o2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Node<K, V> leastNode(Node<K, V> node, DataElement dataElement) {
        Node<K, V> rval = node;
        if (rval != null) {
            while (rval.getLeft(dataElement) != null) {
                rval = rval.getLeft(dataElement);
            }
        }
        return rval;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Node<K, V> greatestNode(Node<K, V> node, DataElement dataElement) {
        Node<K, V> rval = node;
        if (rval != null) {
            while (rval.getRight(dataElement) != null) {
                rval = rval.getRight(dataElement);
            }
        }
        return rval;
    }

    private void copyColor(Node<K, V> from, Node<K, V> to, DataElement dataElement) {
        if (to != null) {
            if (from == null) {
                to.setBlack(dataElement);
            } else {
                to.copyColor(from, dataElement);
            }
        }
    }

    private static boolean isRed(Node<?, ?> node, DataElement dataElement) {
        return node != null && node.isRed(dataElement);
    }

    private static boolean isBlack(Node<?, ?> node, DataElement dataElement) {
        return node == null || node.isBlack(dataElement);
    }

    private static void makeRed(Node<?, ?> node, DataElement dataElement) {
        if (node == null) {
            return;
        }
        node.setRed(dataElement);
    }

    private static void makeBlack(Node<?, ?> node, DataElement dataElement) {
        if (node == null) {
            return;
        }
        node.setBlack(dataElement);
    }

    private Node<K, V> getGrandParent(Node<K, V> node, DataElement dataElement) {
        return getParent(getParent(node, dataElement), dataElement);
    }

    private Node<K, V> getParent(Node<K, V> node, DataElement dataElement) {
        if (node == null) {
            return null;
        }
        return node.getParent(dataElement);
    }

    private Node<K, V> getRightChild(Node<K, V> node, DataElement dataElement) {
        if (node == null) {
            return null;
        }
        return node.getRight(dataElement);
    }

    private Node<K, V> getLeftChild(Node<K, V> node, DataElement dataElement) {
        if (node == null) {
            return null;
        }
        return node.getLeft(dataElement);
    }

    private void rotateLeft(Node<K, V> node, DataElement dataElement) {
        Node<K, V> rightChild = node.getRight(dataElement);
        node.setRight(rightChild.getLeft(dataElement), dataElement);
        if (rightChild.getLeft(dataElement) != null) {
            rightChild.getLeft(dataElement).setParent(node, dataElement);
        }
        rightChild.setParent(node.getParent(dataElement), dataElement);
        if (node.getParent(dataElement) == null) {
            this.rootNode[dataElement.ordinal()] = rightChild;
        } else if (node.getParent(dataElement).getLeft(dataElement) == node) {
            node.getParent(dataElement).setLeft(rightChild, dataElement);
        } else {
            node.getParent(dataElement).setRight(rightChild, dataElement);
        }
        rightChild.setLeft(node, dataElement);
        node.setParent(rightChild, dataElement);
    }

    private void rotateRight(Node<K, V> node, DataElement dataElement) {
        Node<K, V> leftChild = node.getLeft(dataElement);
        node.setLeft(leftChild.getRight(dataElement), dataElement);
        if (leftChild.getRight(dataElement) != null) {
            leftChild.getRight(dataElement).setParent(node, dataElement);
        }
        leftChild.setParent(node.getParent(dataElement), dataElement);
        if (node.getParent(dataElement) == null) {
            this.rootNode[dataElement.ordinal()] = leftChild;
        } else if (node.getParent(dataElement).getRight(dataElement) == node) {
            node.getParent(dataElement).setRight(leftChild, dataElement);
        } else {
            node.getParent(dataElement).setLeft(leftChild, dataElement);
        }
        leftChild.setRight(node, dataElement);
        node.setParent(leftChild, dataElement);
    }

    private void doRedBlackInsert(Node<K, V> insertedNode, DataElement dataElement) {
        Node<K, V> currentNode = insertedNode;
        makeRed(currentNode, dataElement);
        while (currentNode != null && currentNode != this.rootNode[dataElement.ordinal()] && isRed(currentNode.getParent(dataElement), dataElement)) {
            if (currentNode.isLeftChild(dataElement)) {
                Node<K, V> y = getRightChild(getGrandParent(currentNode, dataElement), dataElement);
                if (isRed(y, dataElement)) {
                    makeBlack(getParent(currentNode, dataElement), dataElement);
                    makeBlack(y, dataElement);
                    makeRed(getGrandParent(currentNode, dataElement), dataElement);
                    currentNode = getGrandParent(currentNode, dataElement);
                } else {
                    if (currentNode.isRightChild(dataElement)) {
                        currentNode = getParent(currentNode, dataElement);
                        rotateLeft(currentNode, dataElement);
                    }
                    makeBlack(getParent(currentNode, dataElement), dataElement);
                    makeRed(getGrandParent(currentNode, dataElement), dataElement);
                    if (getGrandParent(currentNode, dataElement) != null) {
                        rotateRight(getGrandParent(currentNode, dataElement), dataElement);
                    }
                }
            } else {
                Node<K, V> y2 = getLeftChild(getGrandParent(currentNode, dataElement), dataElement);
                if (isRed(y2, dataElement)) {
                    makeBlack(getParent(currentNode, dataElement), dataElement);
                    makeBlack(y2, dataElement);
                    makeRed(getGrandParent(currentNode, dataElement), dataElement);
                    currentNode = getGrandParent(currentNode, dataElement);
                } else {
                    if (currentNode.isLeftChild(dataElement)) {
                        currentNode = getParent(currentNode, dataElement);
                        rotateRight(currentNode, dataElement);
                    }
                    makeBlack(getParent(currentNode, dataElement), dataElement);
                    makeRed(getGrandParent(currentNode, dataElement), dataElement);
                    if (getGrandParent(currentNode, dataElement) != null) {
                        rotateLeft(getGrandParent(currentNode, dataElement), dataElement);
                    }
                }
            }
        }
        makeBlack(this.rootNode[dataElement.ordinal()], dataElement);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doRedBlackDelete(Node<K, V> deletedNode) {
        for (DataElement dataElement : DataElement.values()) {
            if (deletedNode.getLeft(dataElement) != null && deletedNode.getRight(dataElement) != null) {
                swapPosition(nextGreater(deletedNode, dataElement), deletedNode, dataElement);
            }
            Node<K, V> replacement = deletedNode.getLeft(dataElement) != null ? deletedNode.getLeft(dataElement) : deletedNode.getRight(dataElement);
            if (replacement != null) {
                replacement.setParent(deletedNode.getParent(dataElement), dataElement);
                if (deletedNode.getParent(dataElement) == null) {
                    this.rootNode[dataElement.ordinal()] = replacement;
                } else if (deletedNode == deletedNode.getParent(dataElement).getLeft(dataElement)) {
                    deletedNode.getParent(dataElement).setLeft(replacement, dataElement);
                } else {
                    deletedNode.getParent(dataElement).setRight(replacement, dataElement);
                }
                deletedNode.setLeft(null, dataElement);
                deletedNode.setRight(null, dataElement);
                deletedNode.setParent(null, dataElement);
                if (isBlack(deletedNode, dataElement)) {
                    doRedBlackDeleteFixup(replacement, dataElement);
                }
            } else if (deletedNode.getParent(dataElement) == null) {
                this.rootNode[dataElement.ordinal()] = null;
            } else {
                if (isBlack(deletedNode, dataElement)) {
                    doRedBlackDeleteFixup(deletedNode, dataElement);
                }
                if (deletedNode.getParent(dataElement) != null) {
                    if (deletedNode == deletedNode.getParent(dataElement).getLeft(dataElement)) {
                        deletedNode.getParent(dataElement).setLeft(null, dataElement);
                    } else {
                        deletedNode.getParent(dataElement).setRight(null, dataElement);
                    }
                    deletedNode.setParent(null, dataElement);
                }
            }
        }
        shrink();
    }

    private void doRedBlackDeleteFixup(Node<K, V> replacementNode, DataElement dataElement) {
        Node<K, V> currentNode = replacementNode;
        while (currentNode != this.rootNode[dataElement.ordinal()] && isBlack(currentNode, dataElement)) {
            if (currentNode.isLeftChild(dataElement)) {
                Node<K, V> siblingNode = getRightChild(getParent(currentNode, dataElement), dataElement);
                if (isRed(siblingNode, dataElement)) {
                    makeBlack(siblingNode, dataElement);
                    makeRed(getParent(currentNode, dataElement), dataElement);
                    rotateLeft(getParent(currentNode, dataElement), dataElement);
                    siblingNode = getRightChild(getParent(currentNode, dataElement), dataElement);
                }
                if (isBlack(getLeftChild(siblingNode, dataElement), dataElement) && isBlack(getRightChild(siblingNode, dataElement), dataElement)) {
                    makeRed(siblingNode, dataElement);
                    currentNode = getParent(currentNode, dataElement);
                } else {
                    if (isBlack(getRightChild(siblingNode, dataElement), dataElement)) {
                        makeBlack(getLeftChild(siblingNode, dataElement), dataElement);
                        makeRed(siblingNode, dataElement);
                        rotateRight(siblingNode, dataElement);
                        siblingNode = getRightChild(getParent(currentNode, dataElement), dataElement);
                    }
                    copyColor(getParent(currentNode, dataElement), siblingNode, dataElement);
                    makeBlack(getParent(currentNode, dataElement), dataElement);
                    makeBlack(getRightChild(siblingNode, dataElement), dataElement);
                    rotateLeft(getParent(currentNode, dataElement), dataElement);
                    currentNode = this.rootNode[dataElement.ordinal()];
                }
            } else {
                Node<K, V> siblingNode2 = getLeftChild(getParent(currentNode, dataElement), dataElement);
                if (isRed(siblingNode2, dataElement)) {
                    makeBlack(siblingNode2, dataElement);
                    makeRed(getParent(currentNode, dataElement), dataElement);
                    rotateRight(getParent(currentNode, dataElement), dataElement);
                    siblingNode2 = getLeftChild(getParent(currentNode, dataElement), dataElement);
                }
                if (isBlack(getRightChild(siblingNode2, dataElement), dataElement) && isBlack(getLeftChild(siblingNode2, dataElement), dataElement)) {
                    makeRed(siblingNode2, dataElement);
                    currentNode = getParent(currentNode, dataElement);
                } else {
                    if (isBlack(getLeftChild(siblingNode2, dataElement), dataElement)) {
                        makeBlack(getRightChild(siblingNode2, dataElement), dataElement);
                        makeRed(siblingNode2, dataElement);
                        rotateLeft(siblingNode2, dataElement);
                        siblingNode2 = getLeftChild(getParent(currentNode, dataElement), dataElement);
                    }
                    copyColor(getParent(currentNode, dataElement), siblingNode2, dataElement);
                    makeBlack(getParent(currentNode, dataElement), dataElement);
                    makeBlack(getLeftChild(siblingNode2, dataElement), dataElement);
                    rotateRight(getParent(currentNode, dataElement), dataElement);
                    currentNode = this.rootNode[dataElement.ordinal()];
                }
            }
        }
        makeBlack(currentNode, dataElement);
    }

    private void swapPosition(Node<K, V> x, Node<K, V> y, DataElement dataElement) {
        Node<K, V> xFormerParent = x.getParent(dataElement);
        Node<K, V> xFormerLeftChild = x.getLeft(dataElement);
        Node<K, V> xFormerRightChild = x.getRight(dataElement);
        Node<K, V> yFormerParent = y.getParent(dataElement);
        Node<K, V> yFormerLeftChild = y.getLeft(dataElement);
        Node<K, V> yFormerRightChild = y.getRight(dataElement);
        boolean xWasLeftChild = x.getParent(dataElement) != null && x == x.getParent(dataElement).getLeft(dataElement);
        boolean yWasLeftChild = y.getParent(dataElement) != null && y == y.getParent(dataElement).getLeft(dataElement);
        if (x == yFormerParent) {
            x.setParent(y, dataElement);
            if (yWasLeftChild) {
                y.setLeft(x, dataElement);
                y.setRight(xFormerRightChild, dataElement);
            } else {
                y.setRight(x, dataElement);
                y.setLeft(xFormerLeftChild, dataElement);
            }
        } else {
            x.setParent(yFormerParent, dataElement);
            if (yFormerParent != null) {
                if (yWasLeftChild) {
                    yFormerParent.setLeft(x, dataElement);
                } else {
                    yFormerParent.setRight(x, dataElement);
                }
            }
            y.setLeft(xFormerLeftChild, dataElement);
            y.setRight(xFormerRightChild, dataElement);
        }
        if (y == xFormerParent) {
            y.setParent(x, dataElement);
            if (xWasLeftChild) {
                x.setLeft(y, dataElement);
                x.setRight(yFormerRightChild, dataElement);
            } else {
                x.setRight(y, dataElement);
                x.setLeft(yFormerLeftChild, dataElement);
            }
        } else {
            y.setParent(xFormerParent, dataElement);
            if (xFormerParent != null) {
                if (xWasLeftChild) {
                    xFormerParent.setLeft(y, dataElement);
                } else {
                    xFormerParent.setRight(y, dataElement);
                }
            }
            x.setLeft(yFormerLeftChild, dataElement);
            x.setRight(yFormerRightChild, dataElement);
        }
        if (x.getLeft(dataElement) != null) {
            x.getLeft(dataElement).setParent(x, dataElement);
        }
        if (x.getRight(dataElement) != null) {
            x.getRight(dataElement).setParent(x, dataElement);
        }
        if (y.getLeft(dataElement) != null) {
            y.getLeft(dataElement).setParent(y, dataElement);
        }
        if (y.getRight(dataElement) != null) {
            y.getRight(dataElement).setParent(y, dataElement);
        }
        x.swapColors(y, dataElement);
        if (this.rootNode[dataElement.ordinal()] == x) {
            this.rootNode[dataElement.ordinal()] = y;
        } else if (this.rootNode[dataElement.ordinal()] == y) {
            this.rootNode[dataElement.ordinal()] = x;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void checkNonNullComparable(Object o, DataElement dataElement) {
        if (o == null) {
            throw new NullPointerException(dataElement + " cannot be null");
        }
        if (!(o instanceof Comparable)) {
            throw new ClassCastException(dataElement + " must be Comparable");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void checkKey(Object key) {
        checkNonNullComparable(key, DataElement.KEY);
    }

    private static void checkValue(Object value) {
        checkNonNullComparable(value, DataElement.VALUE);
    }

    private static void checkKeyAndValue(Object key, Object value) {
        checkKey(key);
        checkValue(value);
    }

    private void modify() {
        this.modifications++;
    }

    private void grow() {
        modify();
        this.nodeCount++;
    }

    private void shrink() {
        modify();
        this.nodeCount--;
    }

    private void insertValue(Node<K, V> newNode) throws IllegalArgumentException {
        Node<K, V> node = this.rootNode[DataElement.VALUE.ordinal()];
        while (true) {
            int cmp = compare(newNode.getValue(), node.getValue());
            if (cmp != 0) {
                if (cmp < 0) {
                    if (node.getLeft(DataElement.VALUE) != null) {
                        node = node.getLeft(DataElement.VALUE);
                    } else {
                        node.setLeft(newNode, DataElement.VALUE);
                        newNode.setParent(node, DataElement.VALUE);
                        doRedBlackInsert(newNode, DataElement.VALUE);
                        return;
                    }
                } else if (node.getRight(DataElement.VALUE) != null) {
                    node = node.getRight(DataElement.VALUE);
                } else {
                    node.setRight(newNode, DataElement.VALUE);
                    newNode.setParent(node, DataElement.VALUE);
                    doRedBlackInsert(newNode, DataElement.VALUE);
                    return;
                }
            } else {
                throw new IllegalArgumentException("Cannot store a duplicate value (\"" + newNode.getData(DataElement.VALUE) + "\") in this Map");
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean doEquals(Object obj, DataElement dataElement) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Map)) {
            return false;
        }
        Map<?, ?> other = (Map) obj;
        if (other.size() != size()) {
            return false;
        }
        if (this.nodeCount > 0) {
            try {
                MapIterator<?, ?> it = getMapIterator(dataElement);
                while (it.hasNext()) {
                    Object key = it.next();
                    Object value = it.getValue();
                    if (!value.equals(other.get(key))) {
                        return false;
                    }
                }
            } catch (ClassCastException e) {
                return false;
            } catch (NullPointerException e2) {
                return false;
            }
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int doHashCode(DataElement dataElement) {
        int total = 0;
        if (this.nodeCount > 0) {
            MapIterator<?, ?> it = getMapIterator(dataElement);
            while (it.hasNext()) {
                Object key = it.next();
                Object value = it.getValue();
                total += key.hashCode() ^ value.hashCode();
            }
        }
        return total;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String doToString(DataElement dataElement) {
        if (this.nodeCount == 0) {
            return "{}";
        }
        StringBuilder buf = new StringBuilder(this.nodeCount * 32);
        buf.append('{');
        MapIterator<?, ?> it = getMapIterator(dataElement);
        boolean hasNext = it.hasNext();
        while (hasNext) {
            Object key = it.next();
            Object value = it.getValue();
            buf.append(key == this ? "(this Map)" : key);
            buf.append('=');
            buf.append(value != this ? value : "(this Map)");
            hasNext = it.hasNext();
            if (hasNext) {
                buf.append(", ");
            }
        }
        buf.append('}');
        return buf.toString();
    }

    /* JADX INFO: renamed from: org.apache.commons.collections4.bidimap.TreeBidiMap$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$apache$commons$collections4$bidimap$TreeBidiMap$DataElement;

        static {
            int[] iArr = new int[DataElement.values().length];
            $SwitchMap$org$apache$commons$collections4$bidimap$TreeBidiMap$DataElement = iArr;
            try {
                iArr[DataElement.KEY.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$apache$commons$collections4$bidimap$TreeBidiMap$DataElement[DataElement.VALUE.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    private MapIterator<?, ?> getMapIterator(DataElement dataElement) {
        int i = AnonymousClass1.$SwitchMap$org$apache$commons$collections4$bidimap$TreeBidiMap$DataElement[dataElement.ordinal()];
        if (i == 1) {
            return new ViewMapIterator(DataElement.KEY);
        }
        if (i == 2) {
            return new InverseViewMapIterator(DataElement.VALUE);
        }
        throw new IllegalArgumentException();
    }

    private void readObject(ObjectInputStream stream) throws ClassNotFoundException, IOException {
        stream.defaultReadObject();
        this.rootNode = new Node[2];
        int size = stream.readInt();
        for (int i = 0; i < size; i++) {
            put((Comparable) stream.readObject(), (Comparable) stream.readObject());
        }
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        stream.writeInt(size());
        for (Map.Entry<K, V> entry : entrySet()) {
            stream.writeObject(entry.getKey());
            stream.writeObject(entry.getValue());
        }
    }

    abstract class View<E> extends AbstractSet<E> {
        final DataElement orderType;

        View(DataElement orderType) {
            this.orderType = orderType;
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public int size() {
            return TreeBidiMap.this.size();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public void clear() {
            TreeBidiMap.this.clear();
        }
    }

    class KeyView extends TreeBidiMap<K, V>.View<K> {
        public KeyView(DataElement orderType) {
            super(orderType);
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Set
        public Iterator<K> iterator() {
            return new ViewMapIterator(this.orderType);
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public boolean contains(Object obj) {
            TreeBidiMap.checkNonNullComparable(obj, DataElement.KEY);
            return TreeBidiMap.this.lookupKey(obj) != null;
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public boolean remove(Object o) {
            return TreeBidiMap.this.doRemoveKey(o) != null;
        }
    }

    class ValueView extends TreeBidiMap<K, V>.View<V> {
        public ValueView(DataElement orderType) {
            super(orderType);
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Set
        public Iterator<V> iterator() {
            return new InverseViewMapIterator(this.orderType);
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public boolean contains(Object obj) {
            TreeBidiMap.checkNonNullComparable(obj, DataElement.VALUE);
            return TreeBidiMap.this.lookupValue(obj) != null;
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public boolean remove(Object o) {
            return TreeBidiMap.this.doRemoveValue(o) != null;
        }
    }

    class EntryView extends TreeBidiMap<K, V>.View<Map.Entry<K, V>> {
        EntryView() {
            super(DataElement.KEY);
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public boolean contains(Object obj) {
            if (!(obj instanceof Map.Entry)) {
                return false;
            }
            Map.Entry<?, ?> entry = (Map.Entry) obj;
            Object value = entry.getValue();
            Node<K, V> node = TreeBidiMap.this.lookupKey(entry.getKey());
            return node != null && node.getValue().equals(value);
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public boolean remove(Object obj) {
            if (!(obj instanceof Map.Entry)) {
                return false;
            }
            Map.Entry<?, ?> entry = (Map.Entry) obj;
            Object value = entry.getValue();
            Node<K, V> node = TreeBidiMap.this.lookupKey(entry.getKey());
            if (node == null || !node.getValue().equals(value)) {
                return false;
            }
            TreeBidiMap.this.doRedBlackDelete(node);
            return true;
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Set
        public Iterator<Map.Entry<K, V>> iterator() {
            return new ViewMapEntryIterator();
        }
    }

    class InverseEntryView extends TreeBidiMap<K, V>.View<Map.Entry<V, K>> {
        InverseEntryView() {
            super(DataElement.VALUE);
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public boolean contains(Object obj) {
            if (!(obj instanceof Map.Entry)) {
                return false;
            }
            Map.Entry<?, ?> entry = (Map.Entry) obj;
            Object value = entry.getValue();
            Node<K, V> node = TreeBidiMap.this.lookupValue(entry.getKey());
            return node != null && node.getKey().equals(value);
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public boolean remove(Object obj) {
            if (!(obj instanceof Map.Entry)) {
                return false;
            }
            Map.Entry<?, ?> entry = (Map.Entry) obj;
            Object value = entry.getValue();
            Node<K, V> node = TreeBidiMap.this.lookupValue(entry.getKey());
            if (node == null || !node.getKey().equals(value)) {
                return false;
            }
            TreeBidiMap.this.doRedBlackDelete(node);
            return true;
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Set
        public Iterator<Map.Entry<V, K>> iterator() {
            return new InverseViewMapEntryIterator();
        }
    }

    abstract class ViewIterator {
        private int expectedModifications;
        private Node<K, V> nextNode;
        private final DataElement orderType;
        Node<K, V> lastReturnedNode = null;
        private Node<K, V> previousNode = null;

        ViewIterator(DataElement orderType) {
            this.orderType = orderType;
            this.expectedModifications = TreeBidiMap.this.modifications;
            this.nextNode = TreeBidiMap.this.leastNode(TreeBidiMap.this.rootNode[orderType.ordinal()], orderType);
        }

        public final boolean hasNext() {
            return this.nextNode != null;
        }

        protected Node<K, V> navigateNext() {
            if (this.nextNode != null) {
                if (TreeBidiMap.this.modifications != this.expectedModifications) {
                    throw new ConcurrentModificationException();
                }
                Node<K, V> node = this.nextNode;
                this.lastReturnedNode = node;
                this.previousNode = node;
                this.nextNode = TreeBidiMap.this.nextGreater(node, this.orderType);
                return this.lastReturnedNode;
            }
            throw new NoSuchElementException();
        }

        public boolean hasPrevious() {
            return this.previousNode != null;
        }

        protected Node<K, V> navigatePrevious() {
            if (this.previousNode != null) {
                if (TreeBidiMap.this.modifications != this.expectedModifications) {
                    throw new ConcurrentModificationException();
                }
                Node<K, V> node = this.lastReturnedNode;
                this.nextNode = node;
                if (node == null) {
                    this.nextNode = TreeBidiMap.this.nextGreater(this.previousNode, this.orderType);
                }
                Node<K, V> node2 = this.previousNode;
                this.lastReturnedNode = node2;
                this.previousNode = TreeBidiMap.this.nextSmaller(node2, this.orderType);
                return this.lastReturnedNode;
            }
            throw new NoSuchElementException();
        }

        public final void remove() {
            if (this.lastReturnedNode != null) {
                if (TreeBidiMap.this.modifications == this.expectedModifications) {
                    TreeBidiMap.this.doRedBlackDelete(this.lastReturnedNode);
                    this.expectedModifications++;
                    this.lastReturnedNode = null;
                    Node<K, V> node = this.nextNode;
                    if (node != null) {
                        this.previousNode = TreeBidiMap.this.nextSmaller(node, this.orderType);
                        return;
                    } else {
                        TreeBidiMap treeBidiMap = TreeBidiMap.this;
                        this.previousNode = treeBidiMap.greatestNode(treeBidiMap.rootNode[this.orderType.ordinal()], this.orderType);
                        return;
                    }
                }
                throw new ConcurrentModificationException();
            }
            throw new IllegalStateException();
        }
    }

    class ViewMapIterator extends TreeBidiMap<K, V>.ViewIterator implements OrderedMapIterator<K, V> {
        ViewMapIterator(DataElement orderType) {
            super(orderType);
        }

        @Override // org.apache.commons.collections4.MapIterator
        public K getKey() {
            if (this.lastReturnedNode == null) {
                throw new IllegalStateException("Iterator getKey() can only be called after next() and before remove()");
            }
            return (K) this.lastReturnedNode.getKey();
        }

        @Override // org.apache.commons.collections4.MapIterator
        public V getValue() {
            if (this.lastReturnedNode == null) {
                throw new IllegalStateException("Iterator getValue() can only be called after next() and before remove()");
            }
            return (V) this.lastReturnedNode.getValue();
        }

        @Override // org.apache.commons.collections4.MapIterator
        public V setValue(V obj) {
            throw new UnsupportedOperationException();
        }

        @Override // org.apache.commons.collections4.MapIterator, java.util.Iterator
        public K next() {
            return (K) navigateNext().getKey();
        }

        @Override // org.apache.commons.collections4.OrderedMapIterator, org.apache.commons.collections4.OrderedIterator
        public K previous() {
            return (K) navigatePrevious().getKey();
        }
    }

    class InverseViewMapIterator extends TreeBidiMap<K, V>.ViewIterator implements OrderedMapIterator<V, K> {
        public InverseViewMapIterator(DataElement orderType) {
            super(orderType);
        }

        @Override // org.apache.commons.collections4.MapIterator
        public V getKey() {
            if (this.lastReturnedNode == null) {
                throw new IllegalStateException("Iterator getKey() can only be called after next() and before remove()");
            }
            return (V) this.lastReturnedNode.getValue();
        }

        @Override // org.apache.commons.collections4.MapIterator
        public K getValue() {
            if (this.lastReturnedNode == null) {
                throw new IllegalStateException("Iterator getValue() can only be called after next() and before remove()");
            }
            return (K) this.lastReturnedNode.getKey();
        }

        @Override // org.apache.commons.collections4.MapIterator
        public K setValue(K obj) {
            throw new UnsupportedOperationException();
        }

        @Override // org.apache.commons.collections4.MapIterator, java.util.Iterator
        public V next() {
            return (V) navigateNext().getValue();
        }

        @Override // org.apache.commons.collections4.OrderedMapIterator, org.apache.commons.collections4.OrderedIterator
        public V previous() {
            return (V) navigatePrevious().getValue();
        }
    }

    class ViewMapEntryIterator extends TreeBidiMap<K, V>.ViewIterator implements OrderedIterator<Map.Entry<K, V>> {
        ViewMapEntryIterator() {
            super(DataElement.KEY);
        }

        @Override // java.util.Iterator
        public Map.Entry<K, V> next() {
            return navigateNext();
        }

        @Override // org.apache.commons.collections4.OrderedIterator
        public Map.Entry<K, V> previous() {
            return navigatePrevious();
        }
    }

    class InverseViewMapEntryIterator extends TreeBidiMap<K, V>.ViewIterator implements OrderedIterator<Map.Entry<V, K>> {
        InverseViewMapEntryIterator() {
            super(DataElement.VALUE);
        }

        @Override // java.util.Iterator
        public Map.Entry<V, K> next() {
            return createEntry(navigateNext());
        }

        @Override // org.apache.commons.collections4.OrderedIterator
        public Map.Entry<V, K> previous() {
            return createEntry(navigatePrevious());
        }

        private Map.Entry<V, K> createEntry(Node<K, V> node) {
            return new UnmodifiableMapEntry(node.getValue(), node.getKey());
        }
    }

    static class Node<K extends Comparable<K>, V extends Comparable<V>> implements Map.Entry<K, V>, KeyValue<K, V> {
        private int hashcodeValue;
        private final K key;
        private final V value;
        private final Node<K, V>[] leftNode = new Node[2];
        private final Node<K, V>[] rightNode = new Node[2];
        private final Node<K, V>[] parentNode = new Node[2];
        private final boolean[] blackColor = {true, true};
        private boolean calculatedHashCode = false;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public Object getData(DataElement dataElement) {
            int i = AnonymousClass1.$SwitchMap$org$apache$commons$collections4$bidimap$TreeBidiMap$DataElement[dataElement.ordinal()];
            if (i == 1) {
                return getKey();
            }
            if (i == 2) {
                return getValue();
            }
            throw new IllegalArgumentException();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setLeft(Node<K, V> node, DataElement dataElement) {
            this.leftNode[dataElement.ordinal()] = node;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public Node<K, V> getLeft(DataElement dataElement) {
            return this.leftNode[dataElement.ordinal()];
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setRight(Node<K, V> node, DataElement dataElement) {
            this.rightNode[dataElement.ordinal()] = node;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public Node<K, V> getRight(DataElement dataElement) {
            return this.rightNode[dataElement.ordinal()];
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setParent(Node<K, V> node, DataElement dataElement) {
            this.parentNode[dataElement.ordinal()] = node;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public Node<K, V> getParent(DataElement dataElement) {
            return this.parentNode[dataElement.ordinal()];
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void swapColors(Node<K, V> node, DataElement dataElement) {
            boolean[] zArr = this.blackColor;
            int iOrdinal = dataElement.ordinal();
            zArr[iOrdinal] = zArr[iOrdinal] ^ node.blackColor[dataElement.ordinal()];
            boolean[] zArr2 = node.blackColor;
            int iOrdinal2 = dataElement.ordinal();
            zArr2[iOrdinal2] = zArr2[iOrdinal2] ^ this.blackColor[dataElement.ordinal()];
            boolean[] zArr3 = this.blackColor;
            int iOrdinal3 = dataElement.ordinal();
            zArr3[iOrdinal3] = zArr3[iOrdinal3] ^ node.blackColor[dataElement.ordinal()];
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean isBlack(DataElement dataElement) {
            return this.blackColor[dataElement.ordinal()];
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean isRed(DataElement dataElement) {
            return !this.blackColor[dataElement.ordinal()];
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setBlack(DataElement dataElement) {
            this.blackColor[dataElement.ordinal()] = true;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setRed(DataElement dataElement) {
            this.blackColor[dataElement.ordinal()] = false;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void copyColor(Node<K, V> node, DataElement dataElement) {
            this.blackColor[dataElement.ordinal()] = node.blackColor[dataElement.ordinal()];
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean isLeftChild(DataElement dataElement) {
            return this.parentNode[dataElement.ordinal()] != null && this.parentNode[dataElement.ordinal()].leftNode[dataElement.ordinal()] == this;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean isRightChild(DataElement dataElement) {
            return this.parentNode[dataElement.ordinal()] != null && this.parentNode[dataElement.ordinal()].rightNode[dataElement.ordinal()] == this;
        }

        @Override // java.util.Map.Entry, org.apache.commons.collections4.KeyValue
        public K getKey() {
            return this.key;
        }

        @Override // java.util.Map.Entry, org.apache.commons.collections4.KeyValue
        public V getValue() {
            return this.value;
        }

        @Override // java.util.Map.Entry
        public V setValue(V ignored) throws UnsupportedOperationException {
            throw new UnsupportedOperationException("Map.Entry.setValue is not supported");
        }

        @Override // java.util.Map.Entry
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof Map.Entry)) {
                return false;
            }
            Map.Entry<?, ?> e = (Map.Entry) obj;
            return getKey().equals(e.getKey()) && getValue().equals(e.getValue());
        }

        @Override // java.util.Map.Entry
        public int hashCode() {
            if (!this.calculatedHashCode) {
                this.hashcodeValue = getKey().hashCode() ^ getValue().hashCode();
                this.calculatedHashCode = true;
            }
            return this.hashcodeValue;
        }
    }

    class Inverse implements OrderedBidiMap<V, K> {
        private Set<Map.Entry<V, K>> inverseEntrySet;
        private Set<V> inverseKeySet;
        private Set<K> inverseValuesSet;

        Inverse() {
        }

        @Override // java.util.Map, org.apache.commons.collections4.Get
        public int size() {
            return TreeBidiMap.this.size();
        }

        @Override // java.util.Map, org.apache.commons.collections4.Get
        public boolean isEmpty() {
            return TreeBidiMap.this.isEmpty();
        }

        @Override // java.util.Map, org.apache.commons.collections4.Get
        public K get(Object obj) {
            return (K) TreeBidiMap.this.getKey(obj);
        }

        @Override // org.apache.commons.collections4.BidiMap
        public V getKey(Object obj) {
            return (V) TreeBidiMap.this.get(obj);
        }

        @Override // java.util.Map, org.apache.commons.collections4.Get
        public boolean containsKey(Object key) {
            return TreeBidiMap.this.containsValue(key);
        }

        @Override // java.util.Map, org.apache.commons.collections4.Get
        public boolean containsValue(Object value) {
            return TreeBidiMap.this.containsKey(value);
        }

        @Override // org.apache.commons.collections4.OrderedMap
        public V firstKey() {
            if (TreeBidiMap.this.nodeCount == 0) {
                throw new NoSuchElementException("Map is empty");
            }
            TreeBidiMap treeBidiMap = TreeBidiMap.this;
            return (V) treeBidiMap.leastNode(treeBidiMap.rootNode[DataElement.VALUE.ordinal()], DataElement.VALUE).getValue();
        }

        @Override // org.apache.commons.collections4.OrderedMap
        public V lastKey() {
            if (TreeBidiMap.this.nodeCount == 0) {
                throw new NoSuchElementException("Map is empty");
            }
            TreeBidiMap treeBidiMap = TreeBidiMap.this;
            return (V) treeBidiMap.greatestNode(treeBidiMap.rootNode[DataElement.VALUE.ordinal()], DataElement.VALUE).getValue();
        }

        @Override // org.apache.commons.collections4.OrderedMap
        public V nextKey(V v) {
            TreeBidiMap.checkKey(v);
            TreeBidiMap treeBidiMap = TreeBidiMap.this;
            Node nodeNextGreater = treeBidiMap.nextGreater(treeBidiMap.lookup(v, DataElement.VALUE), DataElement.VALUE);
            if (nodeNextGreater == null) {
                return null;
            }
            return (V) nodeNextGreater.getValue();
        }

        @Override // org.apache.commons.collections4.OrderedMap
        public V previousKey(V v) {
            TreeBidiMap.checkKey(v);
            TreeBidiMap treeBidiMap = TreeBidiMap.this;
            Node nodeNextSmaller = treeBidiMap.nextSmaller(treeBidiMap.lookup(v, DataElement.VALUE), DataElement.VALUE);
            if (nodeNextSmaller == null) {
                return null;
            }
            return (V) nodeNextSmaller.getValue();
        }

        @Override // org.apache.commons.collections4.BidiMap, java.util.Map, org.apache.commons.collections4.Put
        public K put(V v, K k) {
            K k2 = (K) get((Object) v);
            TreeBidiMap.this.doPut(k, v);
            return k2;
        }

        @Override // java.util.Map, org.apache.commons.collections4.Put
        public void putAll(Map<? extends V, ? extends K> map) {
            for (Map.Entry<? extends V, ? extends K> e : map.entrySet()) {
                put((Comparable) e.getKey(), (Comparable) e.getValue());
            }
        }

        @Override // java.util.Map, org.apache.commons.collections4.Get
        public K remove(Object obj) {
            return (K) TreeBidiMap.this.removeValue(obj);
        }

        @Override // org.apache.commons.collections4.BidiMap
        public V removeValue(Object obj) {
            return (V) TreeBidiMap.this.remove(obj);
        }

        @Override // java.util.Map, org.apache.commons.collections4.Put
        public void clear() {
            TreeBidiMap.this.clear();
        }

        @Override // java.util.Map, org.apache.commons.collections4.Get
        public Set<V> keySet() {
            if (this.inverseKeySet == null) {
                this.inverseKeySet = new ValueView(DataElement.VALUE);
            }
            return this.inverseKeySet;
        }

        @Override // org.apache.commons.collections4.BidiMap, java.util.Map, org.apache.commons.collections4.Get
        public Set<K> values() {
            if (this.inverseValuesSet == null) {
                this.inverseValuesSet = new KeyView(DataElement.VALUE);
            }
            return this.inverseValuesSet;
        }

        @Override // java.util.Map, org.apache.commons.collections4.Get
        public Set<Map.Entry<V, K>> entrySet() {
            if (this.inverseEntrySet == null) {
                this.inverseEntrySet = new InverseEntryView();
            }
            return this.inverseEntrySet;
        }

        @Override // org.apache.commons.collections4.IterableGet
        public OrderedMapIterator<V, K> mapIterator() {
            if (isEmpty()) {
                return EmptyOrderedMapIterator.emptyOrderedMapIterator();
            }
            return new InverseViewMapIterator(DataElement.VALUE);
        }

        @Override // org.apache.commons.collections4.OrderedBidiMap, org.apache.commons.collections4.BidiMap
        public OrderedBidiMap<K, V> inverseBidiMap() {
            return TreeBidiMap.this;
        }

        @Override // java.util.Map
        public boolean equals(Object obj) {
            return TreeBidiMap.this.doEquals(obj, DataElement.VALUE);
        }

        @Override // java.util.Map
        public int hashCode() {
            return TreeBidiMap.this.doHashCode(DataElement.VALUE);
        }

        public String toString() {
            return TreeBidiMap.this.doToString(DataElement.VALUE);
        }
    }
}
