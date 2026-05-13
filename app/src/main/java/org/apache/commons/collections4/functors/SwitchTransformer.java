package org.apache.commons.collections4.functors;

import java.io.Serializable;
import java.util.Map;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.Transformer;

/* JADX INFO: loaded from: classes.dex */
public class SwitchTransformer<I, O> implements Transformer<I, O>, Serializable {
    private static final long serialVersionUID = -6404460890903469332L;
    private final Transformer<? super I, ? extends O> iDefault;
    private final Predicate<? super I>[] iPredicates;
    private final Transformer<? super I, ? extends O>[] iTransformers;

    /* JADX WARN: Multi-variable type inference failed */
    public static <I, O> Transformer<I, O> switchTransformer(Predicate<? super I>[] predicates, Transformer<? super I, ? extends O>[] transformers, Transformer<? super I, ? extends O> transformer) {
        FunctorUtils.validate(predicates);
        FunctorUtils.validate(transformers);
        if (predicates.length != transformers.length) {
            throw new IllegalArgumentException("The predicate and transformer arrays must be the same size");
        }
        if (predicates.length == 0) {
            return transformer == 0 ? ConstantTransformer.nullTransformer() : transformer;
        }
        return new SwitchTransformer(predicates, transformers, transformer);
    }

    public static <I, O> Transformer<I, O> switchTransformer(Map<? extends Predicate<? super I>, ? extends Transformer<? super I, ? extends O>> map) {
        if (map == null) {
            throw new NullPointerException("The predicate and transformer map must not be null");
        }
        if (map.size() == 0) {
            return ConstantTransformer.nullTransformer();
        }
        Transformer<? super I, ? extends O> transformerRemove = map.remove(null);
        int size = map.size();
        if (size == 0) {
            return transformerRemove == null ? ConstantTransformer.nullTransformer() : transformerRemove;
        }
        Transformer[] transformerArr = new Transformer[size];
        Predicate[] predicateArr = new Predicate[size];
        int i = 0;
        for (Map.Entry<? extends Predicate<? super I>, ? extends Transformer<? super I, ? extends O>> entry : map.entrySet()) {
            predicateArr[i] = entry.getKey();
            transformerArr[i] = entry.getValue();
            i++;
        }
        return new SwitchTransformer(false, predicateArr, transformerArr, transformerRemove);
    }

    private SwitchTransformer(boolean clone, Predicate<? super I>[] predicates, Transformer<? super I, ? extends O>[] transformers, Transformer<? super I, ? extends O> defaultTransformer) {
        this.iPredicates = clone ? FunctorUtils.copy(predicates) : predicates;
        this.iTransformers = clone ? FunctorUtils.copy(transformers) : transformers;
        this.iDefault = defaultTransformer == null ? ConstantTransformer.nullTransformer() : defaultTransformer;
    }

    public SwitchTransformer(Predicate<? super I>[] predicates, Transformer<? super I, ? extends O>[] transformers, Transformer<? super I, ? extends O> defaultTransformer) {
        this(true, predicates, transformers, defaultTransformer);
    }

    @Override // org.apache.commons.collections4.Transformer
    public O transform(I input) {
        int i = 0;
        while (true) {
            Predicate<? super I>[] predicateArr = this.iPredicates;
            if (i < predicateArr.length) {
                if (!predicateArr[i].evaluate(input)) {
                    i++;
                } else {
                    return this.iTransformers[i].transform(input);
                }
            } else {
                return this.iDefault.transform(input);
            }
        }
    }

    public Predicate<? super I>[] getPredicates() {
        return FunctorUtils.copy(this.iPredicates);
    }

    public Transformer<? super I, ? extends O>[] getTransformers() {
        return FunctorUtils.copy(this.iTransformers);
    }

    public Transformer<? super I, ? extends O> getDefaultTransformer() {
        return this.iDefault;
    }
}
