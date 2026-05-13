package org.apache.commons.collections4.functors;

import org.apache.commons.collections4.Closure;
import org.apache.commons.collections4.FunctorException;

/* JADX INFO: loaded from: classes.dex */
public abstract class CatchAndRethrowClosure<E> implements Closure<E> {
    protected abstract void executeAndThrow(E e) throws Throwable;

    @Override // org.apache.commons.collections4.Closure
    public void execute(E input) {
        try {
            executeAndThrow(input);
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Throwable t) {
            throw new FunctorException(t);
        }
    }
}
