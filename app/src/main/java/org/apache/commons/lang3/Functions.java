package org.apache.commons.lang3;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.apache.commons.lang3.Streams;

/* JADX INFO: loaded from: classes.dex */
public class Functions {

    @FunctionalInterface
    public interface FailableBiConsumer<O1, O2, T extends Throwable> {
        void accept(O1 o1, O2 o2) throws Throwable;
    }

    @FunctionalInterface
    public interface FailableBiFunction<I1, I2, O, T extends Throwable> {
        O apply(I1 i1, I2 i2) throws Throwable;
    }

    @FunctionalInterface
    public interface FailableBiPredicate<O1, O2, T extends Throwable> {
        boolean test(O1 o1, O2 o2) throws Throwable;
    }

    @FunctionalInterface
    public interface FailableCallable<O, T extends Throwable> {
        O call() throws Throwable;
    }

    @FunctionalInterface
    public interface FailableConsumer<O, T extends Throwable> {
        void accept(O o) throws Throwable;
    }

    @FunctionalInterface
    public interface FailableFunction<I, O, T extends Throwable> {
        O apply(I i) throws Throwable;
    }

    @FunctionalInterface
    public interface FailablePredicate<O, T extends Throwable> {
        boolean test(O o) throws Throwable;
    }

    @FunctionalInterface
    public interface FailableRunnable<T extends Throwable> {
        void run() throws Throwable;
    }

    @FunctionalInterface
    public interface FailableSupplier<O, T extends Throwable> {
        O get() throws Throwable;
    }

    public static Runnable asRunnable(final FailableRunnable<?> runnable) {
        return new Runnable() { // from class: org.apache.commons.lang3.-$$Lambda$Functions$jhJiYKiu4B2R0Hhd4HGFBrLQrGM
            @Override // java.lang.Runnable
            public final void run() {
                Functions.run(runnable);
            }
        };
    }

    public static <I> Consumer<I> asConsumer(final FailableConsumer<I, ?> consumer) {
        return new Consumer() { // from class: org.apache.commons.lang3.-$$Lambda$Functions$BxA7Nthp-qoGnFAIJmM3sbDk8hU
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                Functions.accept(consumer, obj);
            }
        };
    }

    public static <O> Callable<O> asCallable(final FailableCallable<O, ?> callable) {
        return new Callable() { // from class: org.apache.commons.lang3.-$$Lambda$Functions$BrGUJz5Go0xesdr3cfYY84a8Jtw
            @Override // java.util.concurrent.Callable
            public final Object call() {
                return Functions.call(callable);
            }
        };
    }

    public static <I1, I2> BiConsumer<I1, I2> asBiConsumer(final FailableBiConsumer<I1, I2, ?> consumer) {
        return new BiConsumer() { // from class: org.apache.commons.lang3.-$$Lambda$Functions$C75AqpwW5ku0oxgUTb2UjJh2xtY
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                Functions.accept(consumer, obj, obj2);
            }
        };
    }

    public static <I, O> Function<I, O> asFunction(final FailableFunction<I, O, ?> function) {
        return new Function() { // from class: org.apache.commons.lang3.-$$Lambda$Functions$2U0ZeizdEVEQ81cHaw5pTefXUG0
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return Functions.apply(function, obj);
            }
        };
    }

    public static <I1, I2, O> BiFunction<I1, I2, O> asBiFunction(final FailableBiFunction<I1, I2, O, ?> function) {
        return new BiFunction() { // from class: org.apache.commons.lang3.-$$Lambda$Functions$dqoFauEx8ARbg7cdzle1gu2pgww
            @Override // java.util.function.BiFunction
            public final Object apply(Object obj, Object obj2) {
                return Functions.apply(function, obj, obj2);
            }
        };
    }

    public static <I> Predicate<I> asPredicate(final FailablePredicate<I, ?> predicate) {
        return new Predicate() { // from class: org.apache.commons.lang3.-$$Lambda$Functions$bAPSIN5AEoA404hjvQARQ-WFoEU
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return Functions.test(predicate, obj);
            }
        };
    }

    public static <I1, I2> BiPredicate<I1, I2> asBiPredicate(final FailableBiPredicate<I1, I2, ?> predicate) {
        return new BiPredicate() { // from class: org.apache.commons.lang3.-$$Lambda$Functions$8h9gPpQdwk09nPw3nNdbqFeDbvU
            @Override // java.util.function.BiPredicate
            public final boolean test(Object obj, Object obj2) {
                return Functions.test(predicate, obj, obj2);
            }
        };
    }

    public static <O> Supplier<O> asSupplier(final FailableSupplier<O, ?> supplier) {
        return new Supplier() { // from class: org.apache.commons.lang3.-$$Lambda$Functions$AeHrzpqp4ARnmG38BgtD0hYOSY8
            @Override // java.util.function.Supplier
            public final Object get() {
                return Functions.get(supplier);
            }
        };
    }

    public static <T extends Throwable> void run(FailableRunnable<T> runnable) {
        try {
            runnable.run();
        } catch (Throwable t) {
            throw rethrow(t);
        }
    }

    public static <O, T extends Throwable> O call(final FailableCallable<O, T> failableCallable) {
        failableCallable.getClass();
        return (O) get(new FailableSupplier() { // from class: org.apache.commons.lang3.-$$Lambda$R1ZuMUi0QBiWw1m72KMff07gyJw
            @Override // org.apache.commons.lang3.Functions.FailableSupplier
            public final Object get() {
                return failableCallable.call();
            }
        });
    }

    public static <O, T extends Throwable> void accept(final FailableConsumer<O, T> consumer, final O object) {
        run(new FailableRunnable() { // from class: org.apache.commons.lang3.-$$Lambda$Functions$fYKoLP7Vb2NuSepizWmNhhDChyY
            @Override // org.apache.commons.lang3.Functions.FailableRunnable
            public final void run() throws Throwable {
                consumer.accept(object);
            }
        });
    }

    public static <O1, O2, T extends Throwable> void accept(final FailableBiConsumer<O1, O2, T> consumer, final O1 object1, final O2 object2) {
        run(new FailableRunnable() { // from class: org.apache.commons.lang3.-$$Lambda$Functions$2Jpj3b-Os5Gayf8DW--emrav0V0
            @Override // org.apache.commons.lang3.Functions.FailableRunnable
            public final void run() throws Throwable {
                consumer.accept(object1, object2);
            }
        });
    }

    public static <I, O, T extends Throwable> O apply(final FailableFunction<I, O, T> failableFunction, final I i) {
        return (O) get(new FailableSupplier() { // from class: org.apache.commons.lang3.-$$Lambda$Functions$LIBwkgGXLCjV_gr3Edf7uLfjr2Y
            @Override // org.apache.commons.lang3.Functions.FailableSupplier
            public final Object get() {
                return failableFunction.apply(i);
            }
        });
    }

    public static <I1, I2, O, T extends Throwable> O apply(final FailableBiFunction<I1, I2, O, T> failableBiFunction, final I1 i1, final I2 i2) {
        return (O) get(new FailableSupplier() { // from class: org.apache.commons.lang3.-$$Lambda$Functions$53wbaiM0TUadSC46EomEmNRhvqE
            @Override // org.apache.commons.lang3.Functions.FailableSupplier
            public final Object get() {
                return failableBiFunction.apply(i1, i2);
            }
        });
    }

    public static <O, T extends Throwable> boolean test(final FailablePredicate<O, T> predicate, final O object) {
        return ((Boolean) get(new FailableSupplier() { // from class: org.apache.commons.lang3.-$$Lambda$Functions$esHb76I2fpY6EDx0S3e22Kn0jcE
            @Override // org.apache.commons.lang3.Functions.FailableSupplier
            public final Object get() {
                return Boolean.valueOf(predicate.test(object));
            }
        })).booleanValue();
    }

    public static <O1, O2, T extends Throwable> boolean test(final FailableBiPredicate<O1, O2, T> predicate, final O1 object1, final O2 object2) {
        return ((Boolean) get(new FailableSupplier() { // from class: org.apache.commons.lang3.-$$Lambda$Functions$G2XqwI5-Fv5BL9jRmLpXsZKy0xQ
            @Override // org.apache.commons.lang3.Functions.FailableSupplier
            public final Object get() {
                return Boolean.valueOf(predicate.test(object1, object2));
            }
        })).booleanValue();
    }

    public static <O, T extends Throwable> O get(FailableSupplier<O, T> supplier) {
        try {
            return supplier.get();
        } catch (Throwable t) {
            throw rethrow(t);
        }
    }

    public static <O> Streams.FailableStream<O> stream(Stream<O> stream) {
        return new Streams.FailableStream<>(stream);
    }

    public static <O> Streams.FailableStream<O> stream(Collection<O> collection) {
        return new Streams.FailableStream<>(collection.stream());
    }

    @SafeVarargs
    public static void tryWithResources(FailableRunnable<? extends Throwable> action, FailableConsumer<Throwable, ? extends Throwable> errorHandler, FailableRunnable<? extends Throwable>... resources) {
        FailableConsumer<Throwable, ? extends Throwable> actualErrorHandler;
        if (errorHandler == null) {
            actualErrorHandler = new FailableConsumer() { // from class: org.apache.commons.lang3.-$$Lambda$obb9qhpA_PbEmx4ld6DraxJI-s8
                @Override // org.apache.commons.lang3.Functions.FailableConsumer
                public final void accept(Object obj) {
                    Functions.rethrow((Throwable) obj);
                }
            };
        } else {
            actualErrorHandler = errorHandler;
        }
        if (resources != null) {
            for (FailableRunnable<? extends Throwable> failableRunnable : resources) {
                Objects.requireNonNull(failableRunnable, "runnable");
            }
        }
        Throwable th = null;
        try {
            action.run();
        } catch (Throwable t) {
            th = t;
        }
        if (resources != null) {
            for (FailableRunnable<? extends Throwable> failableRunnable2 : resources) {
                try {
                    failableRunnable2.run();
                } catch (Throwable t2) {
                    if (th == null) {
                        th = t2;
                    }
                }
            }
        }
        if (th != null) {
            try {
                actualErrorHandler.accept(th);
            } catch (Throwable t3) {
                throw rethrow(t3);
            }
        }
    }

    @SafeVarargs
    public static void tryWithResources(FailableRunnable<? extends Throwable> action, FailableRunnable<? extends Throwable>... resources) {
        tryWithResources(action, null, resources);
    }

    public static RuntimeException rethrow(Throwable throwable) {
        Objects.requireNonNull(throwable, "throwable");
        if (throwable instanceof RuntimeException) {
            throw ((RuntimeException) throwable);
        }
        if (throwable instanceof Error) {
            throw ((Error) throwable);
        }
        if (throwable instanceof IOException) {
            throw new UncheckedIOException((IOException) throwable);
        }
        throw new UndeclaredThrowableException(throwable);
    }
}
