package org.apache.commons.collections4.functors;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import org.apache.commons.collections4.Factory;
import org.apache.commons.collections4.FunctorException;

/* JADX INFO: loaded from: classes.dex */
public class InstantiateFactory<T> implements Factory<T> {
    private final Object[] iArgs;
    private final Class<T> iClassToInstantiate;
    private transient Constructor<T> iConstructor;
    private final Class<?>[] iParamTypes;

    public static <T> Factory<T> instantiateFactory(Class<T> classToInstantiate, Class<?>[] paramTypes, Object[] args) {
        if (classToInstantiate == null) {
            throw new NullPointerException("Class to instantiate must not be null");
        }
        if ((paramTypes == null && args != null) || ((paramTypes != null && args == null) || (paramTypes != null && args != null && paramTypes.length != args.length))) {
            throw new IllegalArgumentException("Parameter types must match the arguments");
        }
        if (paramTypes == null || paramTypes.length == 0) {
            return new InstantiateFactory(classToInstantiate);
        }
        return new InstantiateFactory(classToInstantiate, paramTypes, args);
    }

    public InstantiateFactory(Class<T> classToInstantiate) {
        this.iConstructor = null;
        this.iClassToInstantiate = classToInstantiate;
        this.iParamTypes = null;
        this.iArgs = null;
        findConstructor();
    }

    public InstantiateFactory(Class<T> classToInstantiate, Class<?>[] paramTypes, Object[] args) {
        this.iConstructor = null;
        this.iClassToInstantiate = classToInstantiate;
        this.iParamTypes = (Class[]) paramTypes.clone();
        this.iArgs = (Object[]) args.clone();
        findConstructor();
    }

    private void findConstructor() {
        try {
            this.iConstructor = this.iClassToInstantiate.getConstructor(this.iParamTypes);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("InstantiateFactory: The constructor must exist and be public ");
        }
    }

    @Override // org.apache.commons.collections4.Factory
    public T create() {
        if (this.iConstructor == null) {
            findConstructor();
        }
        try {
            return this.iConstructor.newInstance(this.iArgs);
        } catch (IllegalAccessException ex) {
            throw new FunctorException("InstantiateFactory: Constructor must be public", ex);
        } catch (InstantiationException ex2) {
            throw new FunctorException("InstantiateFactory: InstantiationException", ex2);
        } catch (InvocationTargetException ex3) {
            throw new FunctorException("InstantiateFactory: Constructor threw an exception", ex3);
        }
    }
}
