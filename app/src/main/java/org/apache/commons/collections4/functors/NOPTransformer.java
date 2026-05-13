package org.apache.commons.collections4.functors;

import java.io.Serializable;
import org.apache.commons.collections4.Transformer;

/* JADX INFO: loaded from: classes.dex */
public class NOPTransformer<T> implements Transformer<T, T>, Serializable {
    public static final Transformer INSTANCE = new NOPTransformer();
    private static final long serialVersionUID = 2133891748318574490L;

    public static <T> Transformer<T, T> nopTransformer() {
        return INSTANCE;
    }

    private NOPTransformer() {
    }

    @Override // org.apache.commons.collections4.Transformer
    public T transform(T input) {
        return input;
    }

    private Object readResolve() {
        return INSTANCE;
    }
}
