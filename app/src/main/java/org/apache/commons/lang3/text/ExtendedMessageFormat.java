package org.apache.commons.lang3.text;

import java.text.Format;
import java.text.MessageFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.Validate;

/* JADX INFO: loaded from: classes.dex */
@Deprecated
public class ExtendedMessageFormat extends MessageFormat {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static final String DUMMY_PATTERN = "";
    private static final char END_FE = '}';
    private static final int HASH_SEED = 31;
    private static final char QUOTE = '\'';
    private static final char START_FE = '{';
    private static final char START_FMT = ',';
    private static final long serialVersionUID = -2362048321261811743L;
    private final Map<String, ? extends FormatFactory> registry;
    private String toPattern;

    public ExtendedMessageFormat(String pattern) {
        this(pattern, Locale.getDefault());
    }

    public ExtendedMessageFormat(String pattern, Locale locale) {
        this(pattern, locale, null);
    }

    public ExtendedMessageFormat(String pattern, Map<String, ? extends FormatFactory> registry) {
        this(pattern, Locale.getDefault(), registry);
    }

    public ExtendedMessageFormat(String pattern, Locale locale, Map<String, ? extends FormatFactory> registry) {
        super("");
        setLocale(locale);
        this.registry = registry;
        applyPattern(pattern);
    }

    @Override // java.text.MessageFormat
    public String toPattern() {
        return this.toPattern;
    }

    @Override // java.text.MessageFormat
    public final void applyPattern(String pattern) {
        if (this.registry == null) {
            super.applyPattern(pattern);
            this.toPattern = super.toPattern();
            return;
        }
        ArrayList<Format> foundFormats = new ArrayList<>();
        ArrayList<String> foundDescriptions = new ArrayList<>();
        StringBuilder stripCustom = new StringBuilder(pattern.length());
        ParsePosition pos = new ParsePosition(0);
        char[] c = pattern.toCharArray();
        int fmtCount = 0;
        while (pos.getIndex() < pattern.length()) {
            char c2 = c[pos.getIndex()];
            if (c2 == '\'') {
                appendQuotedString(pattern, pos, stripCustom);
            } else {
                if (c2 == '{') {
                    fmtCount++;
                    seekNonWs(pattern, pos);
                    int start = pos.getIndex();
                    int index = readArgumentIndex(pattern, next(pos));
                    stripCustom.append(START_FE);
                    stripCustom.append(index);
                    seekNonWs(pattern, pos);
                    Format format = null;
                    String formatDescription = null;
                    if (c[pos.getIndex()] == ',' && (format = getFormat((formatDescription = parseFormatDescription(pattern, next(pos))))) == null) {
                        stripCustom.append(START_FMT);
                        stripCustom.append(formatDescription);
                    }
                    foundFormats.add(format);
                    foundDescriptions.add(format == null ? null : formatDescription);
                    Validate.isTrue(foundFormats.size() == fmtCount);
                    Validate.isTrue(foundDescriptions.size() == fmtCount);
                    if (c[pos.getIndex()] != '}') {
                        throw new IllegalArgumentException("Unreadable format element at position " + start);
                    }
                }
                int start2 = pos.getIndex();
                stripCustom.append(c[start2]);
                next(pos);
            }
        }
        super.applyPattern(stripCustom.toString());
        this.toPattern = insertFormats(super.toPattern(), foundDescriptions);
        if (containsElements(foundFormats)) {
            Format[] origFormats = getFormats();
            int i = 0;
            for (Format f : foundFormats) {
                if (f != null) {
                    origFormats[i] = f;
                }
                i++;
            }
            super.setFormats(origFormats);
        }
    }

    @Override // java.text.MessageFormat
    public void setFormat(int formatElementIndex, Format newFormat) {
        throw new UnsupportedOperationException();
    }

    @Override // java.text.MessageFormat
    public void setFormatByArgumentIndex(int argumentIndex, Format newFormat) {
        throw new UnsupportedOperationException();
    }

    @Override // java.text.MessageFormat
    public void setFormats(Format[] newFormats) {
        throw new UnsupportedOperationException();
    }

    @Override // java.text.MessageFormat
    public void setFormatsByArgumentIndex(Format[] newFormats) {
        throw new UnsupportedOperationException();
    }

    @Override // java.text.MessageFormat
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || !super.equals(obj) || ObjectUtils.notEqual(getClass(), obj.getClass())) {
            return false;
        }
        ExtendedMessageFormat rhs = (ExtendedMessageFormat) obj;
        if (!ObjectUtils.notEqual(this.toPattern, rhs.toPattern)) {
            return true ^ ObjectUtils.notEqual(this.registry, rhs.registry);
        }
        return false;
    }

    @Override // java.text.MessageFormat
    public int hashCode() {
        int result = super.hashCode();
        return (((result * 31) + Objects.hashCode(this.registry)) * 31) + Objects.hashCode(this.toPattern);
    }

    private Format getFormat(String desc) {
        if (this.registry != null) {
            String name = desc;
            String args = null;
            int i = desc.indexOf(44);
            if (i > 0) {
                name = desc.substring(0, i).trim();
                args = desc.substring(i + 1).trim();
            }
            FormatFactory factory = this.registry.get(name);
            if (factory != null) {
                return factory.getFormat(name, args, getLocale());
            }
            return null;
        }
        return null;
    }

    /* JADX WARN: Removed duplicated region for block: B:12:0x003c A[PHI: r3
  0x003c: PHI (r3v5 'c' char) = (r3v4 'c' char), (r3v6 'c' char), (r3v6 'c' char) binds: [B:7:0x0029, B:9:0x0036, B:10:0x0038] A[DONT_GENERATE, DONT_INLINE]] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private int readArgumentIndex(java.lang.String r8, java.text.ParsePosition r9) {
        /*
            r7 = this;
            int r0 = r9.getIndex()
            r7.seekNonWs(r8, r9)
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r2 = 0
        Ld:
            if (r2 != 0) goto L5e
            int r3 = r9.getIndex()
            int r4 = r8.length()
            if (r3 >= r4) goto L5e
            int r3 = r9.getIndex()
            char r3 = r8.charAt(r3)
            boolean r4 = java.lang.Character.isWhitespace(r3)
            r5 = 125(0x7d, float:1.75E-43)
            r6 = 44
            if (r4 == 0) goto L3c
            r7.seekNonWs(r8, r9)
            int r4 = r9.getIndex()
            char r3 = r8.charAt(r4)
            if (r3 == r6) goto L3c
            if (r3 == r5) goto L3c
            r2 = 1
            goto L5a
        L3c:
            if (r3 == r6) goto L40
            if (r3 != r5) goto L50
        L40:
            int r4 = r1.length()
            if (r4 <= 0) goto L50
            java.lang.String r4 = r1.toString()     // Catch: java.lang.NumberFormatException -> L4f
            int r4 = java.lang.Integer.parseInt(r4)     // Catch: java.lang.NumberFormatException -> L4f
            return r4
        L4f:
            r4 = move-exception
        L50:
            boolean r4 = java.lang.Character.isDigit(r3)
            r4 = r4 ^ 1
            r2 = r4
            r1.append(r3)
        L5a:
            r7.next(r9)
            goto Ld
        L5e:
            if (r2 == 0) goto L87
            java.lang.IllegalArgumentException r3 = new java.lang.IllegalArgumentException
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "Invalid format argument index at position "
            r4.append(r5)
            r4.append(r0)
            java.lang.String r5 = ": "
            r4.append(r5)
            int r5 = r9.getIndex()
            java.lang.String r5 = r8.substring(r0, r5)
            r4.append(r5)
            java.lang.String r4 = r4.toString()
            r3.<init>(r4)
            throw r3
        L87:
            java.lang.IllegalArgumentException r3 = new java.lang.IllegalArgumentException
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "Unterminated format element at position "
            r4.append(r5)
            r4.append(r0)
            java.lang.String r4 = r4.toString()
            r3.<init>(r4)
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.text.ExtendedMessageFormat.readArgumentIndex(java.lang.String, java.text.ParsePosition):int");
    }

    private String parseFormatDescription(String pattern, ParsePosition pos) {
        int start = pos.getIndex();
        seekNonWs(pattern, pos);
        int text = pos.getIndex();
        int depth = 1;
        while (pos.getIndex() < pattern.length()) {
            char cCharAt = pattern.charAt(pos.getIndex());
            if (cCharAt == '\'') {
                getQuotedString(pattern, pos);
            } else if (cCharAt == '{') {
                depth++;
            } else if (cCharAt == '}' && depth - 1 == 0) {
                return pattern.substring(text, pos.getIndex());
            }
            next(pos);
        }
        throw new IllegalArgumentException("Unterminated format element at position " + start);
    }

    private String insertFormats(String pattern, ArrayList<String> customPatterns) {
        String customPattern;
        if (!containsElements(customPatterns)) {
            return pattern;
        }
        StringBuilder sb = new StringBuilder(pattern.length() * 2);
        ParsePosition pos = new ParsePosition(0);
        int fe = -1;
        int depth = 0;
        while (pos.getIndex() < pattern.length()) {
            char c = pattern.charAt(pos.getIndex());
            if (c == '\'') {
                appendQuotedString(pattern, pos, sb);
            } else if (c == '{') {
                depth++;
                sb.append(START_FE);
                sb.append(readArgumentIndex(pattern, next(pos)));
                if (depth == 1 && (customPattern = customPatterns.get((fe = fe + 1))) != null) {
                    sb.append(START_FMT);
                    sb.append(customPattern);
                }
            } else {
                if (c == '}') {
                    depth--;
                }
                sb.append(c);
                next(pos);
            }
        }
        return sb.toString();
    }

    private void seekNonWs(String pattern, ParsePosition pos) {
        char[] buffer = pattern.toCharArray();
        do {
            int len = StrMatcher.splitMatcher().isMatch(buffer, pos.getIndex());
            pos.setIndex(pos.getIndex() + len);
            if (len <= 0) {
                return;
            }
        } while (pos.getIndex() < pattern.length());
    }

    private ParsePosition next(ParsePosition pos) {
        pos.setIndex(pos.getIndex() + 1);
        return pos;
    }

    private StringBuilder appendQuotedString(String pattern, ParsePosition pos, StringBuilder appendTo) {
        if (appendTo != null) {
            appendTo.append(QUOTE);
        }
        next(pos);
        int start = pos.getIndex();
        char[] c = pattern.toCharArray();
        for (int i = pos.getIndex(); i < pattern.length(); i++) {
            if (c[pos.getIndex()] == '\'') {
                next(pos);
                if (appendTo == null) {
                    return null;
                }
                appendTo.append(c, start, pos.getIndex() - start);
                return appendTo;
            }
            next(pos);
        }
        throw new IllegalArgumentException("Unterminated quoted string at position " + start);
    }

    private void getQuotedString(String pattern, ParsePosition pos) {
        appendQuotedString(pattern, pos, null);
    }

    private boolean containsElements(Collection<?> coll) {
        if (coll == null || coll.isEmpty()) {
            return false;
        }
        for (Object name : coll) {
            if (name != null) {
                return true;
            }
        }
        return false;
    }
}
