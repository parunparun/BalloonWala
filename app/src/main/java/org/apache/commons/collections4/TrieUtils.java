package org.apache.commons.collections4;

import org.apache.commons.collections4.trie.UnmodifiableTrie;

/* JADX INFO: loaded from: classes.dex */
public class TrieUtils {
    private TrieUtils() {
    }

    public static <K, V> Trie<K, V> unmodifiableTrie(Trie<K, ? extends V> trie) {
        return UnmodifiableTrie.unmodifiableTrie(trie);
    }
}
