package org.apache.commons.collections4.functors;

import java.io.Serializable;
import java.util.Collection;
import org.apache.commons.collections4.Transformer;

/* JADX INFO: loaded from: classes.dex */
public class ChainedTransformer<T> implements Transformer<T, T>, Serializable {
    private static final long serialVersionUID = 3514945074733160196L;
    private final Transformer<? super T, ? extends T>[] iTransformers;

    public static <T> Transformer<T, T> chainedTransformer(Transformer<? super T, ? extends T>... transformers) {
        FunctorUtils.validate(transformers);
        if (transformers.length == 0) {
            return NOPTransformer.nopTransformer();
        }
        return new ChainedTransformer(transformers);
    }

    public static <T> Transformer<T, T> chainedTransformer(Collection<? extends Transformer<? super T, ? extends T>> transformers) {
        if (transformers == null) {
            throw new NullPointerException("Transformer collection must not be null");
        }
        if (transformers.size() == 0) {
            return NOPTransformer.nopTransformer();
        }
        Transformer<T, T>[] cmds = (Transformer[]) transformers.toArray(new Transformer[transformers.size()]);
        FunctorUtils.validate((Transformer<?, ?>[]) cmds);
        return new ChainedTransformer(false, cmds);
    }

    private ChainedTransformer(boolean clone, Transformer<? super T, ? extends T>[] transformers) {
        this.iTransformers = clone ? FunctorUtils.copy(transformers) : transformers;
    }

    public ChainedTransformer(Transformer<? super T, ? extends T>... transformers) {
        this(true, transformers);
    }

    @Override // org.apache.commons.collections4.Transformer
    public T transform(T t) {
        for (Transformer<? super T, ? extends T> transformer : this.iTransformers) {
            t = transformer.transform(t);
        }
        return t;
    }

    public Transformer<? super T, ? extends T>[] getTransformers() {
        return FunctorUtils.copy(this.iTransformers);
    }
}
