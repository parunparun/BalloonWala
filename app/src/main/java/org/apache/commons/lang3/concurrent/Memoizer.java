package org.apache.commons.lang3.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/* JADX INFO: loaded from: classes.dex */
public class Memoizer<I, O> implements Computable<I, O> {
    private final ConcurrentMap<I, Future<O>> cache;
    private final Computable<I, O> computable;
    private final boolean recalculate;

    public Memoizer(Computable<I, O> computable) {
        this(computable, false);
    }

    public Memoizer(Computable<I, O> computable, boolean recalculate) {
        this.cache = new ConcurrentHashMap();
        this.computable = computable;
        this.recalculate = recalculate;
    }

    @Override // org.apache.commons.lang3.concurrent.Computable
    public O compute(final I arg) throws InterruptedException {
        while (true) {
            Future<O> future = this.cache.get(arg);
            if (future == null) {
                Callable<O> eval = new Callable() { // from class: org.apache.commons.lang3.concurrent.-$$Lambda$Memoizer$Lgw-7srJ6CU1pB81eJL7ZXEZOeo
                    @Override // java.util.concurrent.Callable
                    public final Object call() {
                        return this.f$0.lambda$compute$0$Memoizer(arg);
                    }
                };
                FutureTask<O> futureTask = new FutureTask<>(eval);
                future = this.cache.putIfAbsent(arg, futureTask);
                if (future == null) {
                    future = futureTask;
                    futureTask.run();
                }
            }
            try {
                return future.get();
            } catch (CancellationException e) {
                this.cache.remove(arg, future);
            } catch (ExecutionException e2) {
                if (this.recalculate) {
                    this.cache.remove(arg, future);
                }
                throw launderException(e2.getCause());
            }
        }
    }

    public /* synthetic */ Object lambda$compute$0$Memoizer(Object arg) throws Exception {
        return this.computable.compute(arg);
    }

    private RuntimeException launderException(Throwable throwable) {
        if (throwable instanceof RuntimeException) {
            return (RuntimeException) throwable;
        }
        if (throwable instanceof Error) {
            throw ((Error) throwable);
        }
        throw new IllegalStateException("Unchecked exception", throwable);
    }
}
