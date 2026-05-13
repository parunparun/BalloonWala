package org.apache.commons.lang3.mutable;

import java.io.Serializable;

/* JADX INFO: loaded from: classes.dex */
public class MutableObject<T> implements Mutable<T>, Serializable {
    private static final long serialVersionUID = 86241875189L;
    private T value;

    public MutableObject() {
    }

    public MutableObject(T value) {
        this.value = value;
    }

    @Override // org.apache.commons.lang3.mutable.Mutable
    /* JADX INFO: renamed from: getValue */
    public T getValue2() {
        return this.value;
    }

    @Override // org.apache.commons.lang3.mutable.Mutable
    public void setValue(T value) {
        this.value = value;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        MutableObject<?> that = (MutableObject) obj;
        return this.value.equals(that.value);
    }

    public int hashCode() {
        T t = this.value;
        if (t == null) {
            return 0;
        }
        return t.hashCode();
    }

    public String toString() {
        T t = this.value;
        return t == null ? "null" : t.toString();
    }
}
