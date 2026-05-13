package org.apache.commons.collections4.sequence;

/* JADX INFO: loaded from: classes.dex */
public class InsertCommand<T> extends EditCommand<T> {
    public InsertCommand(T object) {
        super(object);
    }

    @Override // org.apache.commons.collections4.sequence.EditCommand
    public void accept(CommandVisitor<T> visitor) {
        visitor.visitInsertCommand(getObject());
    }
}
