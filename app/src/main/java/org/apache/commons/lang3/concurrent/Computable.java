package org.apache.commons.lang3.concurrent;

/* JADX INFO: loaded from: classes.dex */
public interface Computable<I, O> {
    O compute(I i) throws InterruptedException;
}
