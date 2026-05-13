package org.apache.commons.collections4.sequence;

import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class ReplacementsFinder<T> implements CommandVisitor<T> {
    private final ReplacementsHandler<T> handler;
    private final List<T> pendingInsertions = new ArrayList();
    private final List<T> pendingDeletions = new ArrayList();
    private int skipped = 0;

    public ReplacementsFinder(ReplacementsHandler<T> handler) {
        this.handler = handler;
    }

    @Override // org.apache.commons.collections4.sequence.CommandVisitor
    public void visitInsertCommand(T object) {
        this.pendingInsertions.add(object);
    }

    @Override // org.apache.commons.collections4.sequence.CommandVisitor
    public void visitKeepCommand(T object) {
        if (this.pendingDeletions.isEmpty() && this.pendingInsertions.isEmpty()) {
            this.skipped++;
            return;
        }
        this.handler.handleReplacement(this.skipped, this.pendingDeletions, this.pendingInsertions);
        this.pendingDeletions.clear();
        this.pendingInsertions.clear();
        this.skipped = 1;
    }

    @Override // org.apache.commons.collections4.sequence.CommandVisitor
    public void visitDeleteCommand(T object) {
        this.pendingDeletions.add(object);
    }
}
