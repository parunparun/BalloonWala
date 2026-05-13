package org.apache.commons.collections4.sequence;

/* JADX INFO: loaded from: classes.dex */
public interface CommandVisitor<T> {
    void visitDeleteCommand(T t);

    void visitInsertCommand(T t);

    void visitKeepCommand(T t);
}
