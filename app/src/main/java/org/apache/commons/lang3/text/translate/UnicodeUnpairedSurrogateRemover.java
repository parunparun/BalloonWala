package org.apache.commons.lang3.text.translate;

import java.io.IOException;
import java.io.Writer;

/* JADX INFO: loaded from: classes.dex */
@Deprecated
public class UnicodeUnpairedSurrogateRemover extends CodePointTranslator {
    @Override // org.apache.commons.lang3.text.translate.CodePointTranslator
    public boolean translate(int codepoint, Writer out) throws IOException {
        if (codepoint >= 55296 && codepoint <= 57343) {
            return true;
        }
        return false;
    }
}
