package org.apache.commons.collections4.functors;

import java.lang.reflect.InvocationTargetException;
import org.apache.commons.collections4.FunctorException;
import org.apache.commons.collections4.Transformer;

/* JADX INFO: loaded from: classes.dex */
public class InvokerTransformer<I, O> implements Transformer<I, O> {
    private final Object[] iArgs;
    private final String iMethodName;
    private final Class<?>[] iParamTypes;

    public static <I, O> Transformer<I, O> invokerTransformer(String methodName) {
        if (methodName == null) {
            throw new NullPointerException("The method to invoke must not be null");
        }
        return new InvokerTransformer(methodName);
    }

    public static <I, O> Transformer<I, O> invokerTransformer(String methodName, Class<?>[] paramTypes, Object[] args) {
        if (methodName == null) {
            throw new NullPointerException("The method to invoke must not be null");
        }
        if ((paramTypes == null && args != null) || ((paramTypes != null && args == null) || (paramTypes != null && args != null && paramTypes.length != args.length))) {
            throw new IllegalArgumentException("The parameter types must match the arguments");
        }
        if (paramTypes == null || paramTypes.length == 0) {
            return new InvokerTransformer(methodName);
        }
        return new InvokerTransformer(methodName, paramTypes, args);
    }

    private InvokerTransformer(String methodName) {
        this.iMethodName = methodName;
        this.iParamTypes = null;
        this.iArgs = null;
    }

    public InvokerTransformer(String methodName, Class<?>[] paramTypes, Object[] args) {
        this.iMethodName = methodName;
        this.iParamTypes = paramTypes != null ? (Class[]) paramTypes.clone() : null;
        this.iArgs = args != null ? (Object[]) args.clone() : null;
    }

    @Override // org.apache.commons.collections4.Transformer
    public O transform(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return (O) obj.getClass().getMethod(this.iMethodName, this.iParamTypes).invoke(obj, this.iArgs);
        } catch (IllegalAccessException e) {
            throw new FunctorException("InvokerTransformer: The method '" + this.iMethodName + "' on '" + obj.getClass() + "' cannot be accessed");
        } catch (NoSuchMethodException e2) {
            throw new FunctorException("InvokerTransformer: The method '" + this.iMethodName + "' on '" + obj.getClass() + "' does not exist");
        } catch (InvocationTargetException e3) {
            throw new FunctorException("InvokerTransformer: The method '" + this.iMethodName + "' on '" + obj.getClass() + "' threw an exception", e3);
        }
    }
}
