package org.apache.commons.collections4.functors;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.apache.commons.collections4.Factory;
import org.apache.commons.collections4.FunctorException;

/* JADX INFO: loaded from: classes.dex */
public class PrototypeFactory {
    public static <T> Factory<T> prototypeFactory(T prototype) {
        if (prototype != null) {
            try {
                Method method = prototype.getClass().getMethod("clone", (Class[]) null);
                return new PrototypeCloneFactory(prototype, method);
            } catch (NoSuchMethodException e) {
                try {
                    prototype.getClass().getConstructor(prototype.getClass());
                    return new InstantiateFactory(prototype.getClass(), new Class[]{prototype.getClass()}, new Object[]{prototype});
                } catch (NoSuchMethodException e2) {
                    if (prototype instanceof Serializable) {
                        return new PrototypeSerializationFactory((Serializable) prototype);
                    }
                    throw new IllegalArgumentException("The prototype must be cloneable via a public clone method");
                }
            }
        }
        return ConstantFactory.constantFactory(null);
    }

    private PrototypeFactory() {
    }

    static class PrototypeCloneFactory<T> implements Factory<T> {
        private transient Method iCloneMethod;
        private final T iPrototype;

        private PrototypeCloneFactory(T prototype, Method method) {
            this.iPrototype = prototype;
            this.iCloneMethod = method;
        }

        private void findCloneMethod() {
            try {
                this.iCloneMethod = this.iPrototype.getClass().getMethod("clone", (Class[]) null);
            } catch (NoSuchMethodException e) {
                throw new IllegalArgumentException("PrototypeCloneFactory: The clone method must exist and be public ");
            }
        }

        @Override // org.apache.commons.collections4.Factory
        public T create() {
            if (this.iCloneMethod == null) {
                findCloneMethod();
            }
            try {
                return (T) this.iCloneMethod.invoke(this.iPrototype, (Object[]) null);
            } catch (IllegalAccessException e) {
                throw new FunctorException("PrototypeCloneFactory: Clone method must be public", e);
            } catch (InvocationTargetException e2) {
                throw new FunctorException("PrototypeCloneFactory: Clone method threw an exception", e2);
            }
        }
    }

    static class PrototypeSerializationFactory<T extends Serializable> implements Factory<T> {
        private final T iPrototype;

        private PrototypeSerializationFactory(T prototype) {
            this.iPrototype = prototype;
        }

        @Override // org.apache.commons.collections4.Factory
        public T create() {
            ByteArrayOutputStream baos = new ByteArrayOutputStream(512);
            ByteArrayInputStream bais = null;
            try {
                try {
                    try {
                        ObjectOutputStream out = new ObjectOutputStream(baos);
                        out.writeObject(this.iPrototype);
                        bais = new ByteArrayInputStream(baos.toByteArray());
                        ObjectInputStream in = new ObjectInputStream(bais);
                        T t = (T) in.readObject();
                        try {
                            bais.close();
                        } catch (IOException e) {
                        }
                        try {
                            baos.close();
                        } catch (IOException e2) {
                        }
                        return t;
                    } catch (ClassNotFoundException ex) {
                        throw new FunctorException(ex);
                    }
                } catch (IOException ex2) {
                    throw new FunctorException(ex2);
                }
            } finally {
            }
        }
    }
}
