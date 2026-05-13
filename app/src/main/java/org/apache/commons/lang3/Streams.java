package org.apache.commons.lang3;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;
import org.apache.commons.lang3.Functions;
import org.apache.commons.lang3.Streams;

/* JADX INFO: loaded from: classes.dex */
public class Streams {

    public static class FailableStream<O> {
        private Stream<O> stream;
        private boolean terminated;

        public FailableStream(Stream<O> stream) {
            this.stream = stream;
        }

        protected void assertNotTerminated() {
            if (this.terminated) {
                throw new IllegalStateException("This stream is already terminated.");
            }
        }

        protected void makeTerminated() {
            assertNotTerminated();
            this.terminated = true;
        }

        public FailableStream<O> filter(Functions.FailablePredicate<O, ?> predicate) {
            assertNotTerminated();
            this.stream = this.stream.filter(Functions.asPredicate(predicate));
            return this;
        }

        public void forEach(Functions.FailableConsumer<O, ?> action) {
            makeTerminated();
            stream().forEach(Functions.asConsumer(action));
        }

        public <A, R> R collect(Collector<? super O, A, R> collector) {
            makeTerminated();
            return (R) stream().collect(collector);
        }

        public <A, R> R collect(Supplier<R> supplier, BiConsumer<R, ? super O> biConsumer, BiConsumer<R, R> biConsumer2) {
            makeTerminated();
            return (R) stream().collect(supplier, biConsumer, biConsumer2);
        }

        public O reduce(O identity, BinaryOperator<O> accumulator) {
            makeTerminated();
            return stream().reduce(identity, accumulator);
        }

        public <R> FailableStream<R> map(Functions.FailableFunction<O, R, ?> mapper) {
            assertNotTerminated();
            return new FailableStream<>(this.stream.map(Functions.asFunction(mapper)));
        }

        public Stream<O> stream() {
            return this.stream;
        }

        public boolean allMatch(Functions.FailablePredicate<O, ?> predicate) {
            assertNotTerminated();
            return stream().allMatch(Functions.asPredicate(predicate));
        }

        public boolean anyMatch(Functions.FailablePredicate<O, ?> predicate) {
            assertNotTerminated();
            return stream().anyMatch(Functions.asPredicate(predicate));
        }
    }

    public static <O> FailableStream<O> stream(Stream<O> stream) {
        return new FailableStream<>(stream);
    }

    public static <O> FailableStream<O> stream(Collection<O> stream) {
        return stream(stream.stream());
    }

    public static class ArrayCollector<O> implements Collector<O, List<O>, O[]> {
        private static final Set<Collector.Characteristics> characteristics = Collections.emptySet();
        private final Class<O> elementType;

        public ArrayCollector(Class<O> elementType) {
            this.elementType = elementType;
        }

        static /* synthetic */ List lambda$supplier$0() {
            return new ArrayList();
        }

        @Override // java.util.stream.Collector
        public Supplier<List<O>> supplier() {
            return new Supplier() { // from class: org.apache.commons.lang3.-$$Lambda$Streams$ArrayCollector$8RphERm94gwAWdw4oNiJuv--onk
                @Override // java.util.function.Supplier
                public final Object get() {
                    return Streams.ArrayCollector.lambda$supplier$0();
                }
            };
        }

        @Override // java.util.stream.Collector
        public BiConsumer<List<O>, O> accumulator() {
            return new BiConsumer() { // from class: org.apache.commons.lang3.-$$Lambda$Streams$ArrayCollector$kn9mtQXN9Etvi3pZgFoJ6_h7FCw
                @Override // java.util.function.BiConsumer
                public final void accept(Object obj, Object obj2) {
                    ((List) obj).add(obj2);
                }
            };
        }

        @Override // java.util.stream.Collector
        public BinaryOperator<List<O>> combiner() {
            return new BinaryOperator() { // from class: org.apache.commons.lang3.-$$Lambda$Streams$ArrayCollector$pSlEShHveVQwA9CnMwGV6zuKM-Q
                @Override // java.util.function.BiFunction
                public final Object apply(Object obj, Object obj2) {
                    return Streams.ArrayCollector.lambda$combiner$2((List) obj, (List) obj2);
                }
            };
        }

        static /* synthetic */ List lambda$combiner$2(List left, List right) {
            left.addAll(right);
            return left;
        }

        @Override // java.util.stream.Collector
        public Function<List<O>, O[]> finisher() {
            return new Function() { // from class: org.apache.commons.lang3.-$$Lambda$Streams$ArrayCollector$cDaowiG0v8MFzWYOzPH8hWyr7YI
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    return this.f$0.lambda$finisher$3$Streams$ArrayCollector((List) obj);
                }
            };
        }

        public /* synthetic */ Object[] lambda$finisher$3$Streams$ArrayCollector(List list) {
            return list.toArray((Object[]) Array.newInstance((Class<?>) this.elementType, list.size()));
        }

        @Override // java.util.stream.Collector
        public Set<Collector.Characteristics> characteristics() {
            return characteristics;
        }
    }

    public static <O> Collector<O, ?, O[]> toArray(Class<O> pElementType) {
        return new ArrayCollector(pElementType);
    }
}
