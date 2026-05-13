package org.apache.commons.collections4.iterators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class PermutationIterator<E> implements Iterator<List<E>> {
    private final boolean[] direction;
    private final int[] keys;
    private List<E> nextPermutation;
    private final Map<Integer, E> objectMap;

    public PermutationIterator(Collection<? extends E> coll) {
        if (coll == null) {
            throw new NullPointerException("The collection must not be null");
        }
        this.keys = new int[coll.size()];
        boolean[] zArr = new boolean[coll.size()];
        this.direction = zArr;
        Arrays.fill(zArr, false);
        int value = 1;
        this.objectMap = new HashMap();
        for (E e : coll) {
            this.objectMap.put(Integer.valueOf(value), e);
            this.keys[value - 1] = value;
            value++;
        }
        this.nextPermutation = new ArrayList(coll);
    }

    @Override // java.util.Iterator
    public boolean hasNext() {
        return this.nextPermutation != null;
    }

    /* JADX WARN: Removed duplicated region for block: B:18:0x0033  */
    @Override // java.util.Iterator
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public java.util.List<E> next() {
        /*
            r10 = this;
            boolean r0 = r10.hasNext()
            if (r0 == 0) goto L9a
            r0 = -1
            r1 = -1
            r2 = 0
        L9:
            int[] r3 = r10.keys
            int r4 = r3.length
            r5 = 1
            if (r2 >= r4) goto L3f
            boolean[] r4 = r10.direction
            boolean r4 = r4[r2]
            if (r4 == 0) goto L21
            int r4 = r3.length
            int r4 = r4 - r5
            if (r2 >= r4) goto L21
            r4 = r3[r2]
            int r5 = r2 + 1
            r3 = r3[r5]
            if (r4 > r3) goto L33
        L21:
            boolean[] r3 = r10.direction
            boolean r3 = r3[r2]
            if (r3 != 0) goto L3c
            if (r2 <= 0) goto L3c
            int[] r3 = r10.keys
            r4 = r3[r2]
            int r5 = r2 + (-1)
            r3 = r3[r5]
            if (r4 <= r3) goto L3c
        L33:
            int[] r3 = r10.keys
            r4 = r3[r2]
            if (r4 <= r1) goto L3c
            r1 = r3[r2]
            r0 = r2
        L3c:
            int r2 = r2 + 1
            goto L9
        L3f:
            r2 = -1
            if (r1 != r2) goto L48
            java.util.List<E> r2 = r10.nextPermutation
            r3 = 0
            r10.nextPermutation = r3
            return r2
        L48:
            boolean[] r3 = r10.direction
            boolean r3 = r3[r0]
            if (r3 == 0) goto L4f
            r2 = r5
        L4f:
            int[] r3 = r10.keys
            r4 = r3[r0]
            int r6 = r0 + r2
            r6 = r3[r6]
            r3[r0] = r6
            int r6 = r0 + r2
            r3[r6] = r4
            boolean[] r3 = r10.direction
            boolean r6 = r3[r0]
            int r7 = r0 + r2
            boolean r7 = r3[r7]
            r3[r0] = r7
            int r7 = r0 + r2
            r3[r7] = r6
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>()
            r7 = 0
        L71:
            int[] r8 = r10.keys
            int r9 = r8.length
            if (r7 >= r9) goto L95
            r8 = r8[r7]
            if (r8 <= r1) goto L81
            boolean[] r8 = r10.direction
            boolean r9 = r8[r7]
            r9 = r9 ^ r5
            r8[r7] = r9
        L81:
            java.util.Map<java.lang.Integer, E> r8 = r10.objectMap
            int[] r9 = r10.keys
            r9 = r9[r7]
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.Object r8 = r8.get(r9)
            r3.add(r8)
            int r7 = r7 + 1
            goto L71
        L95:
            java.util.List<E> r5 = r10.nextPermutation
            r10.nextPermutation = r3
            return r5
        L9a:
            java.util.NoSuchElementException r0 = new java.util.NoSuchElementException
            r0.<init>()
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.collections4.iterators.PermutationIterator.next():java.util.List");
    }

    @Override // java.util.Iterator
    public void remove() {
        throw new UnsupportedOperationException("remove() is not supported");
    }
}
