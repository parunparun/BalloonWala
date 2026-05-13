package org.apache.commons.collections4.sequence;

import java.util.List;

/* JADX INFO: loaded from: classes.dex */
@FunctionalInterface
public interface ReplacementsHandler<T> {
    void handleReplacement(int i, List<T> list, List<T> list2);
}
