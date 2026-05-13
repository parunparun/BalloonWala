package org.apache.commons.collections4.functors;

import java.io.Serializable;
import org.apache.commons.collections4.Transformer;

/* JADX INFO: loaded from: classes.dex */
public class ConstantTransformer<I, O> implements Transformer<I, O>, Serializable {
    public static final Transformer NULL_INSTANCE = new ConstantTransformer(null);
    private static final long serialVersionUID = 6374440726369055124L;
    private final O iConstant;

    public static <I, O> Transformer<I, O> nullTransformer() {
        return NULL_INSTANCE;
    }

    public static <I, O> Transformer<I, O> constantTransformer(O constantToReturn) {
        if (constantToReturn == null) {
            return nullTransformer();
        }
        return new ConstantTransformer(constantToReturn);
    }

    public ConstantTransformer(O constantToReturn) {
        this.iConstant = constantToReturn;
    }

    @Override // org.apache.commons.collections4.Transformer
    public O transform(I input) {
        return this.iConstant;
    }

    public O getConstant() {
        return this.iConstant;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ConstantTransformer)) {
            return false;
        }
        Object otherConstant = ((ConstantTransformer) obj).getConstant();
        if (otherConstant != getConstant()) {
            return otherConstant != null && otherConstant.equals(getConstant());
        }
        return true;
    }

    public int hashCode() {
        int result = "ConstantTransformer".hashCode() << 2;
        if (getConstant() != null) {
            return result | getConstant().hashCode();
        }
        return result;
    }
}
