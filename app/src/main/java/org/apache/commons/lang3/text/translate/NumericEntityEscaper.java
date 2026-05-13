package org.apache.commons.lang3.text.translate;

import androidx.appcompat.widget.ActivityChooserView;
import java.io.IOException;
import java.io.Writer;

/* JADX INFO: loaded from: classes.dex */
@Deprecated
public class NumericEntityEscaper extends CodePointTranslator {
    private final int above;
    private final int below;
    private final boolean between;

    private NumericEntityEscaper(int below, int above, boolean between) {
        this.below = below;
        this.above = above;
        this.between = between;
    }

    public NumericEntityEscaper() {
        this(0, ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED, true);
    }

    public static NumericEntityEscaper below(int codepoint) {
        return outsideOf(codepoint, ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED);
    }

    public static NumericEntityEscaper above(int codepoint) {
        return outsideOf(0, codepoint);
    }

    public static NumericEntityEscaper between(int codepointLow, int codepointHigh) {
        return new NumericEntityEscaper(codepointLow, codepointHigh, true);
    }

    public static NumericEntityEscaper outsideOf(int codepointLow, int codepointHigh) {
        return new NumericEntityEscaper(codepointLow, codepointHigh, false);
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
        out.write("&#");
        out.write(Integer.toString(codepoint, 10));
        out.write(59);
        return true;
    }
}
