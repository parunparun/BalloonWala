package org.apache.commons.collections4.sequence;

/* JADX INFO: loaded from: classes.dex */
public class DeleteCommand<T> extends EditCommand<T> {
    public DeleteCommand(T object) {
        super(object);
    }

    @Override // org.apache.commons.collections4.sequence.EditCommand
    public void accept(CommandVisitor<T> visitor) {
        visitor.visitDeleteCommand(getObject());
    }
}
