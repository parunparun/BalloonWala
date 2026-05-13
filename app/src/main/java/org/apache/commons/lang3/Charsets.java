package org.apache.commons.lang3;

import java.nio.charset.Charset;

/* JADX INFO: loaded from: classes.dex */
class Charsets {
    Charsets() {
    }

    static Charset toCharset(Charset charset) {
        return charset == null ? Charset.defaultCharset() : charset;
    }

    static Charset toCharset(String charsetName) {
        return charsetName == null ? Charset.defaultCharset() : Charset.forName(charsetName);
    }

    static String toCharsetName(String charsetName) {
        return charsetName == null ? Charset.defaultCharset().name() : charsetName;
    }
}
