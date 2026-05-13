package org.apache.commons.lang3.text.translate;

import androidx.appcompat.widget.ActivityChooserView;
import java.io.IOException;
import java.io.Writer;

/* JADX INFO: loaded from: classes.dex */
@Deprecated
public class UnicodeEscaper extends CodePointTranslator {
    private final int above;
    private final int below;
    private final boolean between;

    public UnicodeEscaper() {
        this(0, ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED, true);
    }

    protected UnicodeEscaper(int below, int above, boolean between) {
        this.below = below;
        this.above = above;
        this.between = between;
    }

    public static UnicodeEscaper below(int codepoint) {
        return outsideOf(codepoint, ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED);
    }

    public static UnicodeEscaper above(int codepoint) {
        return outsideOf(0, codepoint);
    }

    public static UnicodeEscaper outsideOf(int codepointLow, int codepointHigh) {
        return new UnicodeEscaper(codepointLow, codepointHigh, false);
    }

    public static UnicodeEscaper between(int codepointLow, int codepointHigh) {
        return new UnicodeEscaper(codepointLow, codepointHigh, true);
    }

    @Override // org.apache.commons.lang3.text.translate.CodePointTranslator
    public boolean translate(int codepoint, Writer out) throws IOException {
        if (this.between) {
            if (codepoint < this.below || codepoint > this.above) {
                return false;
            }
        } else if (codepoint >= this.below && codepoint <= this.above) {
            return false;
        }
        if (codepoint > 65535) {
            out.write(toUtf16Escape(codepoint));
            return true;
        }
        out.write("\\u");
        out.write(HEX_DIGITS[(codepoint >> 12) & 15]);
        out.write(HEX_DIGITS[(codepoint >> 8) & 15]);
        out.write(HEX_DIGITS[(codepoint >> 4) & 15]);
        out.write(HEX_DIGITS[codepoint & 15]);
        return true;
    }

    protected String toUtf16Escape(int codepoint) {
        return "\\u" + hex(codepoint);
    }
}
