package org.apache.commons.lang3;

import androidx.appcompat.widget.ActivityChooserView;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.regex.Pattern;

/* JADX INFO: loaded from: classes.dex */
public class StringUtils {
    public static final String CR = "\r";
    public static final String EMPTY = "";
    public static final int INDEX_NOT_FOUND = -1;
    public static final String LF = "\n";
    private static final int PAD_LIMIT = 8192;
    public static final String SPACE = " ";
    private static final int STRING_BUILDER_SIZE = 256;

    public static String abbreviate(String str, int maxWidth) {
        return abbreviate(str, "...", 0, maxWidth);
    }

    public static String abbreviate(String str, int offset, int maxWidth) {
        return abbreviate(str, "...", offset, maxWidth);
    }

    public static String abbreviate(String str, String abbrevMarker, int maxWidth) {
        return abbreviate(str, abbrevMarker, 0, maxWidth);
    }

    public static String abbreviate(String str, String abbrevMarker, int offset, int maxWidth) {
        if (isEmpty(str) && isEmpty(abbrevMarker)) {
            return str;
        }
        if (isNotEmpty(str) && "".equals(abbrevMarker) && maxWidth > 0) {
            return str.substring(0, maxWidth);
        }
        if (isEmpty(str) || isEmpty(abbrevMarker)) {
            return str;
        }
        int abbrevMarkerLength = abbrevMarker.length();
        int minAbbrevWidth = abbrevMarkerLength + 1;
        int minAbbrevWidthOffset = abbrevMarkerLength + abbrevMarkerLength + 1;
        if (maxWidth < minAbbrevWidth) {
            throw new IllegalArgumentException(String.format("Minimum abbreviation width is %d", Integer.valueOf(minAbbrevWidth)));
        }
        if (str.length() <= maxWidth) {
            return str;
        }
        if (offset > str.length()) {
            offset = str.length();
        }
        if (str.length() - offset < maxWidth - abbrevMarkerLength) {
            offset = str.length() - (maxWidth - abbrevMarkerLength);
        }
        if (offset <= abbrevMarkerLength + 1) {
            return str.substring(0, maxWidth - abbrevMarkerLength) + abbrevMarker;
        }
        if (maxWidth < minAbbrevWidthOffset) {
            throw new IllegalArgumentException(String.format("Minimum abbreviation width with offset is %d", Integer.valueOf(minAbbrevWidthOffset)));
        }
        if ((offset + maxWidth) - abbrevMarkerLength < str.length()) {
            return abbrevMarker + abbreviate(str.substring(offset), abbrevMarker, maxWidth - abbrevMarkerLength);
        }
        return abbrevMarker + str.substring(str.length() - (maxWidth - abbrevMarkerLength));
    }

    public static String abbreviateMiddle(String str, String middle, int length) {
        if (isEmpty(str) || isEmpty(middle) || length >= str.length() || length < middle.length() + 2) {
            return str;
        }
        int targetSting = length - middle.length();
        int startOffset = (targetSting / 2) + (targetSting % 2);
        int endOffset = str.length() - (targetSting / 2);
        return str.substring(0, startOffset) + middle + str.substring(endOffset);
    }

    private static String appendIfMissing(String str, CharSequence suffix, boolean ignoreCase, CharSequence... suffixes) {
        if (str == null || isEmpty(suffix) || endsWith(str, suffix, ignoreCase)) {
            return str;
        }
        if (ArrayUtils.isNotEmpty(suffixes)) {
            for (CharSequence s : suffixes) {
                if (endsWith(str, s, ignoreCase)) {
                    return str;
                }
            }
        }
        return str + suffix.toString();
    }

    public static String appendIfMissing(String str, CharSequence suffix, CharSequence... suffixes) {
        return appendIfMissing(str, suffix, false, suffixes);
    }

    public static String appendIfMissingIgnoreCase(String str, CharSequence suffix, CharSequence... suffixes) {
        return appendIfMissing(str, suffix, true, suffixes);
    }

    public static String capitalize(String str) {
        int firstCodepoint;
        int newCodePoint;
        int strLen = length(str);
        if (strLen == 0 || firstCodepoint == (newCodePoint = Character.toTitleCase((firstCodepoint = str.codePointAt(0))))) {
            return str;
        }
        int[] newCodePoints = new int[strLen];
        int outOffset = 0 + 1;
        newCodePoints[0] = newCodePoint;
        int inOffset = Character.charCount(firstCodepoint);
        while (inOffset < strLen) {
            int codepoint = str.codePointAt(inOffset);
            newCodePoints[outOffset] = codepoint;
            inOffset += Character.charCount(codepoint);
            outOffset++;
        }
        return new String(newCodePoints, 0, outOffset);
    }

    public static String center(String str, int size) {
        return center(str, size, ' ');
    }

    public static String center(String str, int size, char padChar) {
        int strLen;
        int pads;
        if (str == null || size <= 0 || (pads = size - (strLen = str.length())) <= 0) {
            return str;
        }
        return rightPad(leftPad(str, (pads / 2) + strLen, padChar), size, padChar);
    }

    public static String center(String str, int size, String padStr) {
        if (str == null || size <= 0) {
            return str;
        }
        if (isEmpty(padStr)) {
            padStr = SPACE;
        }
        int strLen = str.length();
        int pads = size - strLen;
        if (pads <= 0) {
            return str;
        }
        return rightPad(leftPad(str, (pads / 2) + strLen, padStr), size, padStr);
    }

    public static String chomp(String str) {
        if (isEmpty(str)) {
            return str;
        }
        if (str.length() == 1) {
            char ch = str.charAt(0);
            if (ch == '\r' || ch == '\n') {
                return "";
            }
            return str;
        }
        int lastIdx = str.length() - 1;
        char last = str.charAt(lastIdx);
        if (last == '\n') {
            if (str.charAt(lastIdx - 1) == '\r') {
                lastIdx--;
            }
        } else if (last != '\r') {
            lastIdx++;
        }
        return str.substring(0, lastIdx);
    }

    @Deprecated
    public static String chomp(String str, String separator) {
        return removeEnd(str, separator);
    }

    public static String chop(String str) {
        if (str == null) {
            return null;
        }
        int strLen = str.length();
        if (strLen < 2) {
            return "";
        }
        int lastIdx = strLen - 1;
        String ret = str.substring(0, lastIdx);
        char last = str.charAt(lastIdx);
        if (last == '\n' && ret.charAt(lastIdx - 1) == '\r') {
            return ret.substring(0, lastIdx - 1);
        }
        return ret;
    }

    public static int compare(String str1, String str2) {
        return compare(str1, str2, true);
    }

    public static int compare(String str1, String str2, boolean nullIsLess) {
        if (str1 == str2) {
            return 0;
        }
        if (str1 == null) {
            return nullIsLess ? -1 : 1;
        }
        if (str2 == null) {
            return nullIsLess ? 1 : -1;
        }
        return str1.compareTo(str2);
    }

    public static int compareIgnoreCase(String str1, String str2) {
        return compareIgnoreCase(str1, str2, true);
    }

    public static int compareIgnoreCase(String str1, String str2, boolean nullIsLess) {
        if (str1 == str2) {
            return 0;
        }
        if (str1 == null) {
            return nullIsLess ? -1 : 1;
        }
        if (str2 == null) {
            return nullIsLess ? 1 : -1;
        }
        return str1.compareToIgnoreCase(str2);
    }

    public static boolean contains(CharSequence seq, CharSequence searchSeq) {
        return (seq == null || searchSeq == null || CharSequenceUtils.indexOf(seq, searchSeq, 0) < 0) ? false : true;
    }

    public static boolean contains(CharSequence seq, int searchChar) {
        return !isEmpty(seq) && CharSequenceUtils.indexOf(seq, searchChar, 0) >= 0;
    }

    public static boolean containsAny(CharSequence cs, char... searchChars) {
        if (isEmpty(cs) || ArrayUtils.isEmpty(searchChars)) {
            return false;
        }
        int csLength = cs.length();
        int searchLength = searchChars.length;
        int csLast = csLength - 1;
        int searchLast = searchLength - 1;
        for (int i = 0; i < csLength; i++) {
            char ch = cs.charAt(i);
            for (int j = 0; j < searchLength; j++) {
                if (searchChars[j] == ch) {
                    if (!Character.isHighSurrogate(ch) || j == searchLast) {
                        return true;
                    }
                    if (i < csLast && searchChars[j + 1] == cs.charAt(i + 1)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean containsAny(CharSequence cs, CharSequence searchChars) {
        if (searchChars == null) {
            return false;
        }
        return containsAny(cs, CharSequenceUtils.toCharArray(searchChars));
    }

    public static boolean containsAny(CharSequence cs, CharSequence... searchCharSequences) {
        if (isEmpty(cs) || ArrayUtils.isEmpty(searchCharSequences)) {
            return false;
        }
        for (CharSequence searchCharSequence : searchCharSequences) {
            if (contains(cs, searchCharSequence)) {
                return true;
            }
        }
        return false;
    }

    public static boolean containsIgnoreCase(CharSequence str, CharSequence searchStr) {
        if (str == null || searchStr == null) {
            return false;
        }
        int len = searchStr.length();
        int max = str.length() - len;
        for (int i = 0; i <= max; i++) {
            if (CharSequenceUtils.regionMatches(str, true, i, searchStr, 0, len)) {
                return true;
            }
        }
        return false;
    }

    public static boolean containsNone(CharSequence cs, char... searchChars) {
        if (cs == null || searchChars == null) {
            return true;
        }
        int csLen = cs.length();
        int csLast = csLen - 1;
        int searchLen = searchChars.length;
        int searchLast = searchLen - 1;
        for (int i = 0; i < csLen; i++) {
            char ch = cs.charAt(i);
            for (int j = 0; j < searchLen; j++) {
                if (searchChars[j] == ch) {
                    if (!Character.isHighSurrogate(ch) || j == searchLast) {
                        return false;
                    }
                    if (i < csLast && searchChars[j + 1] == cs.charAt(i + 1)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public static boolean containsNone(CharSequence cs, String invalidChars) {
        if (cs == null || invalidChars == null) {
            return true;
        }
        return containsNone(cs, invalidChars.toCharArray());
    }

    public static boolean containsOnly(CharSequence cs, char... valid) {
        if (valid == null || cs == null) {
            return false;
        }
        if (cs.length() == 0) {
            return true;
        }
        return valid.length != 0 && indexOfAnyBut(cs, valid) == -1;
    }

    public static boolean containsOnly(CharSequence cs, String validChars) {
        if (cs == null || validChars == null) {
            return false;
        }
        return containsOnly(cs, validChars.toCharArray());
    }

    public static boolean containsWhitespace(CharSequence seq) {
        if (isEmpty(seq)) {
            return false;
        }
        int strLen = seq.length();
        for (int i = 0; i < strLen; i++) {
            if (Character.isWhitespace(seq.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    private static void convertRemainingAccentCharacters(StringBuilder decomposed) {
        for (int i = 0; i < decomposed.length(); i++) {
            if (decomposed.charAt(i) == 321) {
                decomposed.deleteCharAt(i);
                decomposed.insert(i, 'L');
            } else if (decomposed.charAt(i) == 322) {
                decomposed.deleteCharAt(i);
                decomposed.insert(i, 'l');
            }
        }
    }

    public static int countMatches(CharSequence str, char ch) {
        if (isEmpty(str)) {
            return 0;
        }
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            if (ch == str.charAt(i)) {
                count++;
            }
        }
        return count;
    }

    public static int countMatches(CharSequence str, CharSequence sub) {
        if (isEmpty(str) || isEmpty(sub)) {
            return 0;
        }
        int count = 0;
        int idx = 0;
        while (true) {
            int idx2 = CharSequenceUtils.indexOf(str, sub, idx);
            if (idx2 != -1) {
                count++;
                idx = idx2 + sub.length();
            } else {
                return count;
            }
        }
    }

    public static <T extends CharSequence> T defaultIfBlank(T str, T defaultStr) {
        return isBlank(str) ? defaultStr : str;
    }

    public static <T extends CharSequence> T defaultIfEmpty(T str, T defaultStr) {
        return isEmpty(str) ? defaultStr : str;
    }

    public static String defaultString(String str) {
        return defaultString(str, "");
    }

    public static String defaultString(String str, String defaultStr) {
        return str == null ? defaultStr : str;
    }

    public static String deleteWhitespace(String str) {
        if (isEmpty(str)) {
            return str;
        }
        int sz = str.length();
        char[] chs = new char[sz];
        int count = 0;
        for (int i = 0; i < sz; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                chs[count] = str.charAt(i);
                count++;
            }
        }
        if (count == sz) {
            return str;
        }
        return new String(chs, 0, count);
    }

    public static String difference(String str1, String str2) {
        if (str1 == null) {
            return str2;
        }
        if (str2 == null) {
            return str1;
        }
        int at = indexOfDifference(str1, str2);
        if (at == -1) {
            return "";
        }
        return str2.substring(at);
    }

    public static boolean endsWith(CharSequence str, CharSequence suffix) {
        return endsWith(str, suffix, false);
    }

    private static boolean endsWith(CharSequence str, CharSequence suffix, boolean ignoreCase) {
        if (str == null || suffix == null) {
            return str == suffix;
        }
        if (suffix.length() > str.length()) {
            return false;
        }
        int strOffset = str.length() - suffix.length();
        return CharSequenceUtils.regionMatches(str, ignoreCase, strOffset, suffix, 0, suffix.length());
    }

    public static boolean endsWithAny(CharSequence sequence, CharSequence... searchStrings) {
        if (isEmpty(sequence) || ArrayUtils.isEmpty(searchStrings)) {
            return false;
        }
        for (CharSequence searchString : searchStrings) {
            if (endsWith(sequence, searchString)) {
                return true;
            }
        }
        return false;
    }

    public static boolean endsWithIgnoreCase(CharSequence str, CharSequence suffix) {
        return endsWith(str, suffix, true);
    }

    public static boolean equals(CharSequence cs1, CharSequence cs2) {
        if (cs1 == cs2) {
            return true;
        }
        if (cs1 == null || cs2 == null || cs1.length() != cs2.length()) {
            return false;
        }
        if ((cs1 instanceof String) && (cs2 instanceof String)) {
            return cs1.equals(cs2);
        }
        int length = cs1.length();
        for (int i = 0; i < length; i++) {
            if (cs1.charAt(i) != cs2.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    public static boolean equalsAny(CharSequence string, CharSequence... searchStrings) {
        if (ArrayUtils.isNotEmpty(searchStrings)) {
            for (CharSequence next : searchStrings) {
                if (equals(string, next)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean equalsAnyIgnoreCase(CharSequence string, CharSequence... searchStrings) {
        if (ArrayUtils.isNotEmpty(searchStrings)) {
            for (CharSequence next : searchStrings) {
                if (equalsIgnoreCase(string, next)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean equalsIgnoreCase(CharSequence cs1, CharSequence cs2) {
        if (cs1 == cs2) {
            return true;
        }
        if (cs1 == null || cs2 == null || cs1.length() != cs2.length()) {
            return false;
        }
        return CharSequenceUtils.regionMatches(cs1, true, 0, cs2, 0, cs1.length());
    }

    @SafeVarargs
    public static <T extends CharSequence> T firstNonBlank(T... values) {
        if (values != null) {
            for (T val : values) {
                if (isNotBlank(val)) {
                    return val;
                }
            }
            return null;
        }
        return null;
    }

    @SafeVarargs
    public static <T extends CharSequence> T firstNonEmpty(T... values) {
        if (values != null) {
            for (T val : values) {
                if (isNotEmpty(val)) {
                    return val;
                }
            }
            return null;
        }
        return null;
    }

    public static byte[] getBytes(String string, Charset charset) {
        return string == null ? ArrayUtils.EMPTY_BYTE_ARRAY : string.getBytes(Charsets.toCharset(charset));
    }

    public static byte[] getBytes(String string, String charset) throws UnsupportedEncodingException {
        return string == null ? ArrayUtils.EMPTY_BYTE_ARRAY : string.getBytes(Charsets.toCharsetName(charset));
    }

    public static String getCommonPrefix(String... strs) {
        if (ArrayUtils.isEmpty(strs)) {
            return "";
        }
        int smallestIndexOfDiff = indexOfDifference(strs);
        return smallestIndexOfDiff == -1 ? strs[0] == null ? "" : strs[0] : smallestIndexOfDiff == 0 ? "" : strs[0].substring(0, smallestIndexOfDiff);
    }

    public static String getDigits(String str) {
        if (isEmpty(str)) {
            return str;
        }
        int sz = str.length();
        StringBuilder strDigits = new StringBuilder(sz);
        for (int i = 0; i < sz; i++) {
            char tempChar = str.charAt(i);
            if (Character.isDigit(tempChar)) {
                strDigits.append(tempChar);
            }
        }
        return strDigits.toString();
    }

    @Deprecated
    public static int getFuzzyDistance(CharSequence term, CharSequence query, Locale locale) {
        if (term == null || query == null) {
            throw new IllegalArgumentException("Strings must not be null");
        }
        if (locale == null) {
            throw new IllegalArgumentException("Locale must not be null");
        }
        String termLowerCase = term.toString().toLowerCase(locale);
        String queryLowerCase = query.toString().toLowerCase(locale);
        int score = 0;
        int termIndex = 0;
        int previousMatchingCharacterIndex = Integer.MIN_VALUE;
        for (int queryIndex = 0; queryIndex < queryLowerCase.length(); queryIndex++) {
            char queryChar = queryLowerCase.charAt(queryIndex);
            boolean termCharacterMatchFound = false;
            while (termIndex < termLowerCase.length() && !termCharacterMatchFound) {
                char termChar = termLowerCase.charAt(termIndex);
                if (queryChar == termChar) {
                    score++;
                    if (previousMatchingCharacterIndex + 1 == termIndex) {
                        score += 2;
                    }
                    previousMatchingCharacterIndex = termIndex;
                    termCharacterMatchFound = true;
                }
                termIndex++;
            }
        }
        return score;
    }

    public static <T extends CharSequence> T getIfBlank(T str, Supplier<T> defaultSupplier) {
        if (!isBlank(str)) {
            return str;
        }
        if (defaultSupplier == null) {
            return null;
        }
        return defaultSupplier.get();
    }

    public static <T extends CharSequence> T getIfEmpty(T str, Supplier<T> defaultSupplier) {
        if (!isEmpty(str)) {
            return str;
        }
        if (defaultSupplier == null) {
            return null;
        }
        return defaultSupplier.get();
    }

    @Deprecated
    public static double getJaroWinklerDistance(CharSequence first, CharSequence second) {
        if (first == null || second == null) {
            throw new IllegalArgumentException("Strings must not be null");
        }
        int[] mtp = matches(first, second);
        double m = mtp[0];
        if (m == 0.0d) {
            return 0.0d;
        }
        double j = (((m / ((double) first.length())) + (m / ((double) second.length()))) + ((m - ((double) mtp[1])) / m)) / 3.0d;
        double jw = j < 0.7d ? j : (Math.min(0.1d, 1.0d / ((double) mtp[3])) * ((double) mtp[2]) * (1.0d - j)) + j;
        return Math.round(jw * 100.0d) / 100.0d;
    }

    @Deprecated
    public static int getLevenshteinDistance(CharSequence s, CharSequence t) {
        if (s == null || t == null) {
            throw new IllegalArgumentException("Strings must not be null");
        }
        int n = s.length();
        int m = t.length();
        if (n == 0) {
            return m;
        }
        if (m == 0) {
            return n;
        }
        if (n > m) {
            s = t;
            t = s;
            n = m;
            m = t.length();
        }
        int[] p = new int[n + 1];
        for (int i = 0; i <= n; i++) {
            p[i] = i;
        }
        for (int j = 1; j <= m; j++) {
            int upper_left = p[0];
            char t_j = t.charAt(j - 1);
            p[0] = j;
            for (int i2 = 1; i2 <= n; i2++) {
                int upper = p[i2];
                int cost = s.charAt(i2 + (-1)) == t_j ? 0 : 1;
                p[i2] = Math.min(Math.min(p[i2 - 1] + 1, p[i2] + 1), upper_left + cost);
                upper_left = upper;
            }
        }
        return p[n];
    }

    @Deprecated
    public static int getLevenshteinDistance(CharSequence s, CharSequence t, int threshold) {
        CharSequence s2;
        CharSequence t2;
        if (s == null || t == null) {
            throw new IllegalArgumentException("Strings must not be null");
        }
        if (threshold < 0) {
            throw new IllegalArgumentException("Threshold must not be negative");
        }
        int n = s.length();
        int m = t.length();
        int i = -1;
        if (n == 0) {
            if (m <= threshold) {
                return m;
            }
            return -1;
        }
        if (m != 0) {
            if (Math.abs(n - m) > threshold) {
                return -1;
            }
            if (n <= m) {
                s2 = s;
                t2 = t;
            } else {
                s2 = t;
                t2 = s;
                n = m;
                m = t2.length();
            }
            int[] p = new int[n + 1];
            int[] d = new int[n + 1];
            int boundary = Math.min(n, threshold) + 1;
            for (int i2 = 0; i2 < boundary; i2++) {
                p[i2] = i2;
            }
            int i3 = p.length;
            int i4 = ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
            Arrays.fill(p, boundary, i3, ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED);
            Arrays.fill(d, ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED);
            int j = 1;
            while (j <= m) {
                char t_j = t2.charAt(j - 1);
                d[0] = j;
                int min = Math.max(1, j - threshold);
                int max = j > i4 - threshold ? n : Math.min(n, j + threshold);
                if (min > max) {
                    return i;
                }
                if (min > 1) {
                    d[min - 1] = i4;
                }
                for (int i5 = min; i5 <= max; i5++) {
                    if (s2.charAt(i5 - 1) == t_j) {
                        d[i5] = p[i5 - 1];
                    } else {
                        d[i5] = Math.min(Math.min(d[i5 - 1], p[i5]), p[i5 - 1]) + 1;
                    }
                }
                int[] _d = p;
                p = d;
                d = _d;
                j++;
                i = -1;
                i4 = ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
            }
            if (p[n] <= threshold) {
                return p[n];
            }
            return -1;
        }
        if (n <= threshold) {
            return n;
        }
        return -1;
    }

    public static int indexOf(CharSequence seq, CharSequence searchSeq) {
        if (seq == null || searchSeq == null) {
            return -1;
        }
        return CharSequenceUtils.indexOf(seq, searchSeq, 0);
    }

    public static int indexOf(CharSequence seq, CharSequence searchSeq, int startPos) {
        if (seq == null || searchSeq == null) {
            return -1;
        }
        return CharSequenceUtils.indexOf(seq, searchSeq, startPos);
    }

    public static int indexOf(CharSequence seq, int searchChar) {
        if (isEmpty(seq)) {
            return -1;
        }
        return CharSequenceUtils.indexOf(seq, searchChar, 0);
    }

    public static int indexOf(CharSequence seq, int searchChar, int startPos) {
        if (isEmpty(seq)) {
            return -1;
        }
        return CharSequenceUtils.indexOf(seq, searchChar, startPos);
    }

    public static int indexOfAny(CharSequence cs, char... searchChars) {
        if (isEmpty(cs) || ArrayUtils.isEmpty(searchChars)) {
            return -1;
        }
        int csLen = cs.length();
        int csLast = csLen - 1;
        int searchLen = searchChars.length;
        int searchLast = searchLen - 1;
        for (int i = 0; i < csLen; i++) {
            char ch = cs.charAt(i);
            for (int j = 0; j < searchLen; j++) {
                if (searchChars[j] == ch) {
                    if (i < csLast && j < searchLast && Character.isHighSurrogate(ch)) {
                        if (searchChars[j + 1] == cs.charAt(i + 1)) {
                            return i;
                        }
                    } else {
                        return i;
                    }
                }
            }
        }
        return -1;
    }

    public static int indexOfAny(CharSequence str, CharSequence... searchStrs) {
        int tmp;
        if (str == null || searchStrs == null) {
            return -1;
        }
        int ret = ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
        for (CharSequence search : searchStrs) {
            if (search != null && (tmp = CharSequenceUtils.indexOf(str, search, 0)) != -1 && tmp < ret) {
                ret = tmp;
            }
        }
        if (ret == Integer.MAX_VALUE) {
            return -1;
        }
        return ret;
    }

    public static int indexOfAny(CharSequence cs, String searchChars) {
        if (isEmpty(cs) || isEmpty(searchChars)) {
            return -1;
        }
        return indexOfAny(cs, searchChars.toCharArray());
    }

    /* JADX WARN: Code restructure failed: missing block: B:19:0x003b, code lost:
    
        r5 = r5 + 1;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static int indexOfAnyBut(java.lang.CharSequence r10, char... r11) {
        /*
            boolean r0 = isEmpty(r10)
            r1 = -1
            if (r0 != 0) goto L43
            boolean r0 = org.apache.commons.lang3.ArrayUtils.isEmpty(r11)
            if (r0 == 0) goto Le
            goto L43
        Le:
            int r0 = r10.length()
            int r2 = r0 + (-1)
            int r3 = r11.length
            int r4 = r3 + (-1)
            r5 = 0
        L18:
            if (r5 >= r0) goto L42
            char r6 = r10.charAt(r5)
            r7 = 0
        L1f:
            if (r7 >= r3) goto L41
            char r8 = r11[r7]
            if (r8 != r6) goto L3e
            if (r5 >= r2) goto L3b
            if (r7 >= r4) goto L3b
            boolean r8 = java.lang.Character.isHighSurrogate(r6)
            if (r8 == 0) goto L3b
            int r8 = r7 + 1
            char r8 = r11[r8]
            int r9 = r5 + 1
            char r9 = r10.charAt(r9)
            if (r8 != r9) goto L3e
        L3b:
            int r5 = r5 + 1
            goto L18
        L3e:
            int r7 = r7 + 1
            goto L1f
        L41:
            return r5
        L42:
            return r1
        L43:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.StringUtils.indexOfAnyBut(java.lang.CharSequence, char[]):int");
    }

    public static int indexOfAnyBut(CharSequence seq, CharSequence searchChars) {
        if (isEmpty(seq) || isEmpty(searchChars)) {
            return -1;
        }
        int strLen = seq.length();
        for (int i = 0; i < strLen; i++) {
            char ch = seq.charAt(i);
            boolean chFound = CharSequenceUtils.indexOf(searchChars, ch, 0) >= 0;
            if (i + 1 < strLen && Character.isHighSurrogate(ch)) {
                char ch2 = seq.charAt(i + 1);
                if (chFound && CharSequenceUtils.indexOf(searchChars, ch2, 0) < 0) {
                    return i;
                }
            } else if (!chFound) {
                return i;
            }
        }
        return -1;
    }

    public static int indexOfDifference(CharSequence... css) {
        if (ArrayUtils.getLength(css) <= 1) {
            return -1;
        }
        boolean anyStringNull = false;
        boolean allStringsNull = true;
        int arrayLen = css.length;
        int shortestStrLen = ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
        int longestStrLen = 0;
        for (CharSequence cs : css) {
            if (cs == null) {
                anyStringNull = true;
                shortestStrLen = 0;
            } else {
                allStringsNull = false;
                shortestStrLen = Math.min(cs.length(), shortestStrLen);
                longestStrLen = Math.max(cs.length(), longestStrLen);
            }
        }
        if (allStringsNull || (longestStrLen == 0 && !anyStringNull)) {
            return -1;
        }
        if (shortestStrLen == 0) {
            return 0;
        }
        int firstDiff = -1;
        for (int stringPos = 0; stringPos < shortestStrLen; stringPos++) {
            char comparisonChar = css[0].charAt(stringPos);
            int arrayPos = 1;
            while (true) {
                if (arrayPos >= arrayLen) {
                    break;
                }
                if (css[arrayPos].charAt(stringPos) == comparisonChar) {
                    arrayPos++;
                } else {
                    firstDiff = stringPos;
                    break;
                }
            }
            if (firstDiff != -1) {
                break;
            }
        }
        if (firstDiff == -1 && shortestStrLen != longestStrLen) {
            return shortestStrLen;
        }
        return firstDiff;
    }

    public static int indexOfDifference(CharSequence cs1, CharSequence cs2) {
        if (cs1 == cs2) {
            return -1;
        }
        if (cs1 == null || cs2 == null) {
            return 0;
        }
        int i = 0;
        while (i < cs1.length() && i < cs2.length() && cs1.charAt(i) == cs2.charAt(i)) {
            i++;
        }
        if (i >= cs2.length() && i >= cs1.length()) {
            return -1;
        }
        return i;
    }

    public static int indexOfIgnoreCase(CharSequence str, CharSequence searchStr) {
        return indexOfIgnoreCase(str, searchStr, 0);
    }

    public static int indexOfIgnoreCase(CharSequence str, CharSequence searchStr, int startPos) {
        if (str == null || searchStr == null) {
            return -1;
        }
        if (startPos < 0) {
            startPos = 0;
        }
        int endLimit = (str.length() - searchStr.length()) + 1;
        if (startPos > endLimit) {
            return -1;
        }
        if (searchStr.length() == 0) {
            return startPos;
        }
        for (int i = startPos; i < endLimit; i++) {
            if (CharSequenceUtils.regionMatches(str, true, i, searchStr, 0, searchStr.length())) {
                return i;
            }
        }
        return -1;
    }

    public static boolean isAllBlank(CharSequence... css) {
        if (ArrayUtils.isEmpty(css)) {
            return true;
        }
        for (CharSequence cs : css) {
            if (isNotBlank(cs)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isAllEmpty(CharSequence... css) {
        if (ArrayUtils.isEmpty(css)) {
            return true;
        }
        for (CharSequence cs : css) {
            if (isNotEmpty(cs)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isAllLowerCase(CharSequence cs) {
        if (isEmpty(cs)) {
            return false;
        }
        int sz = cs.length();
        for (int i = 0; i < sz; i++) {
            if (!Character.isLowerCase(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isAllUpperCase(CharSequence cs) {
        if (isEmpty(cs)) {
            return false;
        }
        int sz = cs.length();
        for (int i = 0; i < sz; i++) {
            if (!Character.isUpperCase(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isAlpha(CharSequence cs) {
        if (isEmpty(cs)) {
            return false;
        }
        int sz = cs.length();
        for (int i = 0; i < sz; i++) {
            if (!Character.isLetter(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isAlphanumeric(CharSequence cs) {
        if (isEmpty(cs)) {
            return false;
        }
        int sz = cs.length();
        for (int i = 0; i < sz; i++) {
            if (!Character.isLetterOrDigit(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isAlphanumericSpace(CharSequence cs) {
        if (cs == null) {
            return false;
        }
        int sz = cs.length();
        for (int i = 0; i < sz; i++) {
            if (!Character.isLetterOrDigit(cs.charAt(i)) && cs.charAt(i) != ' ') {
                return false;
            }
        }
        return true;
    }

    public static boolean isAlphaSpace(CharSequence cs) {
        if (cs == null) {
            return false;
        }
        int sz = cs.length();
        for (int i = 0; i < sz; i++) {
            if (!Character.isLetter(cs.charAt(i)) && cs.charAt(i) != ' ') {
                return false;
            }
        }
        return true;
    }

    public static boolean isAnyBlank(CharSequence... css) {
        if (ArrayUtils.isEmpty(css)) {
            return false;
        }
        for (CharSequence cs : css) {
            if (isBlank(cs)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAnyEmpty(CharSequence... css) {
        if (ArrayUtils.isEmpty(css)) {
            return false;
        }
        for (CharSequence cs : css) {
            if (isEmpty(cs)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAsciiPrintable(CharSequence cs) {
        if (cs == null) {
            return false;
        }
        int sz = cs.length();
        for (int i = 0; i < sz; i++) {
            if (!CharUtils.isAsciiPrintable(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isBlank(CharSequence cs) {
        int strLen = length(cs);
        if (strLen == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isEmpty(CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    public static boolean isMixedCase(CharSequence cs) {
        if (isEmpty(cs) || cs.length() == 1) {
            return false;
        }
        boolean containsUppercase = false;
        boolean containsLowercase = false;
        int sz = cs.length();
        for (int i = 0; i < sz; i++) {
            if (containsUppercase && containsLowercase) {
                return true;
            }
            if (Character.isUpperCase(cs.charAt(i))) {
                containsUppercase = true;
            } else if (Character.isLowerCase(cs.charAt(i))) {
                containsLowercase = true;
            }
        }
        return containsUppercase && containsLowercase;
    }

    public static boolean isNoneBlank(CharSequence... css) {
        return !isAnyBlank(css);
    }

    public static boolean isNoneEmpty(CharSequence... css) {
        return !isAnyEmpty(css);
    }

    public static boolean isNotBlank(CharSequence cs) {
        return !isBlank(cs);
    }

    public static boolean isNotEmpty(CharSequence cs) {
        return !isEmpty(cs);
    }

    public static boolean isNumeric(CharSequence cs) {
        if (isEmpty(cs)) {
            return false;
        }
        int sz = cs.length();
        for (int i = 0; i < sz; i++) {
            if (!Character.isDigit(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNumericSpace(CharSequence cs) {
        if (cs == null) {
            return false;
        }
        int sz = cs.length();
        for (int i = 0; i < sz; i++) {
            if (!Character.isDigit(cs.charAt(i)) && cs.charAt(i) != ' ') {
                return false;
            }
        }
        return true;
    }

    public static boolean isWhitespace(CharSequence cs) {
        if (cs == null) {
            return false;
        }
        int sz = cs.length();
        for (int i = 0; i < sz; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static String join(byte[] array, char separator) {
        if (array == null) {
            return null;
        }
        return join(array, separator, 0, array.length);
    }

    public static String join(byte[] array, char separator, int startIndex, int endIndex) {
        if (array == null) {
            return null;
        }
        int noOfItems = endIndex - startIndex;
        if (noOfItems <= 0) {
            return "";
        }
        StringBuilder buf = newStringBuilder(noOfItems);
        buf.append((int) array[startIndex]);
        for (int i = startIndex + 1; i < endIndex; i++) {
            buf.append(separator);
            buf.append((int) array[i]);
        }
        return buf.toString();
    }

    public static String join(char[] array, char separator) {
        if (array == null) {
            return null;
        }
        return join(array, separator, 0, array.length);
    }

    public static String join(char[] array, char separator, int startIndex, int endIndex) {
        if (array == null) {
            return null;
        }
        int noOfItems = endIndex - startIndex;
        if (noOfItems <= 0) {
            return "";
        }
        StringBuilder buf = newStringBuilder(noOfItems);
        buf.append(array[startIndex]);
        for (int i = startIndex + 1; i < endIndex; i++) {
            buf.append(separator);
            buf.append(array[i]);
        }
        return buf.toString();
    }

    public static String join(double[] array, char separator) {
        if (array == null) {
            return null;
        }
        return join(array, separator, 0, array.length);
    }

    public static String join(double[] array, char separator, int startIndex, int endIndex) {
        if (array == null) {
            return null;
        }
        int noOfItems = endIndex - startIndex;
        if (noOfItems <= 0) {
            return "";
        }
        StringBuilder buf = newStringBuilder(noOfItems);
        buf.append(array[startIndex]);
        for (int i = startIndex + 1; i < endIndex; i++) {
            buf.append(separator);
            buf.append(array[i]);
        }
        return buf.toString();
    }

    public static String join(float[] array, char separator) {
        if (array == null) {
            return null;
        }
        return join(array, separator, 0, array.length);
    }

    public static String join(float[] array, char separator, int startIndex, int endIndex) {
        if (array == null) {
            return null;
        }
        int noOfItems = endIndex - startIndex;
        if (noOfItems <= 0) {
            return "";
        }
        StringBuilder buf = newStringBuilder(noOfItems);
        buf.append(array[startIndex]);
        for (int i = startIndex + 1; i < endIndex; i++) {
            buf.append(separator);
            buf.append(array[i]);
        }
        return buf.toString();
    }

    public static String join(int[] array, char separator) {
        if (array == null) {
            return null;
        }
        return join(array, separator, 0, array.length);
    }

    public static String join(int[] array, char separator, int startIndex, int endIndex) {
        if (array == null) {
            return null;
        }
        int noOfItems = endIndex - startIndex;
        if (noOfItems <= 0) {
            return "";
        }
        StringBuilder buf = newStringBuilder(noOfItems);
        buf.append(array[startIndex]);
        for (int i = startIndex + 1; i < endIndex; i++) {
            buf.append(separator);
            buf.append(array[i]);
        }
        return buf.toString();
    }

    public static String join(Iterable<?> iterable, char separator) {
        if (iterable == null) {
            return null;
        }
        return join(iterable.iterator(), separator);
    }

    public static String join(Iterable<?> iterable, String separator) {
        if (iterable == null) {
            return null;
        }
        return join(iterable.iterator(), separator);
    }

    public static String join(Iterator<?> iterator, char separator) {
        if (iterator == null) {
            return null;
        }
        if (!iterator.hasNext()) {
            return "";
        }
        Object first = iterator.next();
        if (!iterator.hasNext()) {
            return Objects.toString(first, "");
        }
        StringBuilder buf = new StringBuilder(256);
        if (first != null) {
            buf.append(first);
        }
        while (iterator.hasNext()) {
            buf.append(separator);
            Object obj = iterator.next();
            if (obj != null) {
                buf.append(obj);
            }
        }
        return buf.toString();
    }

    public static String join(Iterator<?> iterator, String separator) {
        if (iterator == null) {
            return null;
        }
        if (!iterator.hasNext()) {
            return "";
        }
        Object first = iterator.next();
        if (!iterator.hasNext()) {
            return Objects.toString(first, "");
        }
        StringBuilder buf = new StringBuilder(256);
        if (first != null) {
            buf.append(first);
        }
        while (iterator.hasNext()) {
            if (separator != null) {
                buf.append(separator);
            }
            Object obj = iterator.next();
            if (obj != null) {
                buf.append(obj);
            }
        }
        return buf.toString();
    }

    public static String join(List<?> list, char separator, int startIndex, int endIndex) {
        if (list == null) {
            return null;
        }
        int noOfItems = endIndex - startIndex;
        if (noOfItems <= 0) {
            return "";
        }
        List<?> subList = list.subList(startIndex, endIndex);
        return join(subList.iterator(), separator);
    }

    public static String join(List<?> list, String separator, int startIndex, int endIndex) {
        if (list == null) {
            return null;
        }
        int noOfItems = endIndex - startIndex;
        if (noOfItems <= 0) {
            return "";
        }
        List<?> subList = list.subList(startIndex, endIndex);
        return join(subList.iterator(), separator);
    }

    public static String join(long[] array, char separator) {
        if (array == null) {
            return null;
        }
        return join(array, separator, 0, array.length);
    }

    public static String join(long[] array, char separator, int startIndex, int endIndex) {
        if (array == null) {
            return null;
        }
        int noOfItems = endIndex - startIndex;
        if (noOfItems <= 0) {
            return "";
        }
        StringBuilder buf = newStringBuilder(noOfItems);
        buf.append(array[startIndex]);
        for (int i = startIndex + 1; i < endIndex; i++) {
            buf.append(separator);
            buf.append(array[i]);
        }
        return buf.toString();
    }

    public static String join(Object[] array, char separator) {
        if (array == null) {
            return null;
        }
        return join(array, separator, 0, array.length);
    }

    public static String join(Object[] array, char separator, int startIndex, int endIndex) {
        if (array == null) {
            return null;
        }
        int noOfItems = endIndex - startIndex;
        if (noOfItems <= 0) {
            return "";
        }
        StringBuilder buf = newStringBuilder(noOfItems);
        if (array[startIndex] != null) {
            buf.append(array[startIndex]);
        }
        for (int i = startIndex + 1; i < endIndex; i++) {
            buf.append(separator);
            if (array[i] != null) {
                buf.append(array[i]);
            }
        }
        return buf.toString();
    }

    public static String join(Object[] array, String separator) {
        if (array == null) {
            return null;
        }
        return join(array, separator, 0, array.length);
    }

    public static String join(Object[] array, String separator, int startIndex, int endIndex) {
        if (array == null) {
            return null;
        }
        if (separator == null) {
            separator = "";
        }
        int noOfItems = endIndex - startIndex;
        if (noOfItems <= 0) {
            return "";
        }
        StringBuilder buf = newStringBuilder(noOfItems);
        if (array[startIndex] != null) {
            buf.append(array[startIndex]);
        }
        for (int i = startIndex + 1; i < endIndex; i++) {
            buf.append(separator);
            if (array[i] != null) {
                buf.append(array[i]);
            }
        }
        return buf.toString();
    }

    public static String join(short[] array, char separator) {
        if (array == null) {
            return null;
        }
        return join(array, separator, 0, array.length);
    }

    public static String join(short[] array, char separator, int startIndex, int endIndex) {
        if (array == null) {
            return null;
        }
        int noOfItems = endIndex - startIndex;
        if (noOfItems <= 0) {
            return "";
        }
        StringBuilder buf = newStringBuilder(noOfItems);
        buf.append((int) array[startIndex]);
        for (int i = startIndex + 1; i < endIndex; i++) {
            buf.append(separator);
            buf.append((int) array[i]);
        }
        return buf.toString();
    }

    @SafeVarargs
    public static <T> String join(T... elements) {
        return join(elements, (String) null);
    }

    public static String joinWith(String separator, Object... objects) {
        if (objects == null) {
            throw new IllegalArgumentException("Object varargs must not be null");
        }
        String sanitizedSeparator = defaultString(separator);
        StringBuilder result = new StringBuilder();
        Iterator<Object> iterator = Arrays.asList(objects).iterator();
        while (iterator.hasNext()) {
            String value = Objects.toString(iterator.next(), "");
            result.append(value);
            if (iterator.hasNext()) {
                result.append(sanitizedSeparator);
            }
        }
        return result.toString();
    }

    public static int lastIndexOf(CharSequence seq, CharSequence searchSeq) {
        if (seq == null || searchSeq == null) {
            return -1;
        }
        return CharSequenceUtils.lastIndexOf(seq, searchSeq, seq.length());
    }

    public static int lastIndexOf(CharSequence seq, CharSequence searchSeq, int startPos) {
        if (seq == null || searchSeq == null) {
            return -1;
        }
        return CharSequenceUtils.lastIndexOf(seq, searchSeq, startPos);
    }

    public static int lastIndexOf(CharSequence seq, int searchChar) {
        if (isEmpty(seq)) {
            return -1;
        }
        return CharSequenceUtils.lastIndexOf(seq, searchChar, seq.length());
    }

    public static int lastIndexOf(CharSequence seq, int searchChar, int startPos) {
        if (isEmpty(seq)) {
            return -1;
        }
        return CharSequenceUtils.lastIndexOf(seq, searchChar, startPos);
    }

    public static int lastIndexOfAny(CharSequence str, CharSequence... searchStrs) {
        int tmp;
        if (str == null || searchStrs == null) {
            return -1;
        }
        int ret = -1;
        for (CharSequence search : searchStrs) {
            if (search != null && (tmp = CharSequenceUtils.lastIndexOf(str, search, str.length())) > ret) {
                ret = tmp;
            }
        }
        return ret;
    }

    public static int lastIndexOfIgnoreCase(CharSequence str, CharSequence searchStr) {
        if (str == null || searchStr == null) {
            return -1;
        }
        return lastIndexOfIgnoreCase(str, searchStr, str.length());
    }

    public static int lastIndexOfIgnoreCase(CharSequence str, CharSequence searchStr, int startPos) {
        if (str == null || searchStr == null) {
            return -1;
        }
        if (startPos > str.length() - searchStr.length()) {
            startPos = str.length() - searchStr.length();
        }
        if (startPos < 0) {
            return -1;
        }
        if (searchStr.length() == 0) {
            return startPos;
        }
        for (int i = startPos; i >= 0; i--) {
            if (CharSequenceUtils.regionMatches(str, true, i, searchStr, 0, searchStr.length())) {
                return i;
            }
        }
        return -1;
    }

    public static int lastOrdinalIndexOf(CharSequence str, CharSequence searchStr, int ordinal) {
        return ordinalIndexOf(str, searchStr, ordinal, true);
    }

    public static String left(String str, int len) {
        if (str == null) {
            return null;
        }
        if (len < 0) {
            return "";
        }
        if (str.length() <= len) {
            return str;
        }
        return str.substring(0, len);
    }

    public static String leftPad(String str, int size) {
        return leftPad(str, size, ' ');
    }

    public static String leftPad(String str, int size, char padChar) {
        if (str == null) {
            return null;
        }
        int pads = size - str.length();
        if (pads <= 0) {
            return str;
        }
        if (pads > 8192) {
            return leftPad(str, size, String.valueOf(padChar));
        }
        return repeat(padChar, pads).concat(str);
    }

    public static String leftPad(String str, int size, String padStr) {
        if (str == null) {
            return null;
        }
        if (isEmpty(padStr)) {
            padStr = SPACE;
        }
        int padLen = padStr.length();
        int strLen = str.length();
        int pads = size - strLen;
        if (pads <= 0) {
            return str;
        }
        if (padLen == 1 && pads <= 8192) {
            return leftPad(str, size, padStr.charAt(0));
        }
        if (pads == padLen) {
            return padStr.concat(str);
        }
        if (pads < padLen) {
            return padStr.substring(0, pads).concat(str);
        }
        char[] padding = new char[pads];
        char[] padChars = padStr.toCharArray();
        for (int i = 0; i < pads; i++) {
            padding[i] = padChars[i % padLen];
        }
        return new String(padding).concat(str);
    }

    public static int length(CharSequence cs) {
        if (cs == null) {
            return 0;
        }
        return cs.length();
    }

    public static String lowerCase(String str) {
        if (str == null) {
            return null;
        }
        return str.toLowerCase();
    }

    public static String lowerCase(String str, Locale locale) {
        if (str == null) {
            return null;
        }
        return str.toLowerCase(locale);
    }

    private static int[] matches(CharSequence first, CharSequence second) {
        CharSequence max;
        CharSequence min;
        if (first.length() > second.length()) {
            max = first;
            min = second;
        } else {
            max = second;
            min = first;
        }
        int range = Math.max((max.length() / 2) - 1, 0);
        int[] matchIndexes = new int[min.length()];
        Arrays.fill(matchIndexes, -1);
        boolean[] matchFlags = new boolean[max.length()];
        int matches = 0;
        for (int mi = 0; mi < min.length(); mi++) {
            char c1 = min.charAt(mi);
            int xi = Math.max(mi - range, 0);
            int xn = Math.min(mi + range + 1, max.length());
            while (true) {
                if (xi >= xn) {
                    break;
                }
                if (matchFlags[xi] || c1 != max.charAt(xi)) {
                    xi++;
                } else {
                    matchIndexes[mi] = xi;
                    matchFlags[xi] = true;
                    matches++;
                    break;
                }
            }
        }
        char[] ms1 = new char[matches];
        char[] ms2 = new char[matches];
        int si = 0;
        for (int i = 0; i < min.length(); i++) {
            if (matchIndexes[i] != -1) {
                ms1[si] = min.charAt(i);
                si++;
            }
        }
        int si2 = 0;
        for (int i2 = 0; i2 < max.length(); i2++) {
            if (matchFlags[i2]) {
                ms2[si2] = max.charAt(i2);
                si2++;
            }
        }
        int transpositions = 0;
        for (int mi2 = 0; mi2 < ms1.length; mi2++) {
            if (ms1[mi2] != ms2[mi2]) {
                transpositions++;
            }
        }
        int prefix = 0;
        for (int mi3 = 0; mi3 < min.length() && first.charAt(mi3) == second.charAt(mi3); mi3++) {
            prefix++;
        }
        return new int[]{matches, transpositions / 2, prefix, max.length()};
    }

    public static String mid(String str, int pos, int len) {
        if (str == null) {
            return null;
        }
        if (len < 0 || pos > str.length()) {
            return "";
        }
        if (pos < 0) {
            pos = 0;
        }
        if (str.length() <= pos + len) {
            return str.substring(pos);
        }
        return str.substring(pos, pos + len);
    }

    private static StringBuilder newStringBuilder(int noOfItems) {
        return new StringBuilder(noOfItems * 16);
    }

    public static String normalizeSpace(String str) {
        if (isEmpty(str)) {
            return str;
        }
        int size = str.length();
        char[] newChars = new char[size];
        int count = 0;
        int whitespacesCount = 0;
        boolean startWhitespaces = true;
        for (int i = 0; i < size; i++) {
            char actualChar = str.charAt(i);
            boolean isWhitespace = Character.isWhitespace(actualChar);
            if (isWhitespace) {
                if (whitespacesCount == 0 && !startWhitespaces) {
                    newChars[count] = SPACE.charAt(0);
                    count++;
                }
                whitespacesCount++;
            } else {
                startWhitespaces = false;
                int count2 = count + 1;
                newChars[count] = actualChar == 160 ? ' ' : actualChar;
                whitespacesCount = 0;
                count = count2;
            }
        }
        if (startWhitespaces) {
            return "";
        }
        return new String(newChars, 0, count - (whitespacesCount > 0 ? 1 : 0)).trim();
    }

    public static int ordinalIndexOf(CharSequence str, CharSequence searchStr, int ordinal) {
        return ordinalIndexOf(str, searchStr, ordinal, false);
    }

    private static int ordinalIndexOf(CharSequence str, CharSequence searchStr, int ordinal, boolean lastIndex) {
        if (str == null || searchStr == null || ordinal <= 0) {
            return -1;
        }
        if (searchStr.length() == 0) {
            if (lastIndex) {
                return str.length();
            }
            return 0;
        }
        int found = 0;
        int index = lastIndex ? str.length() : -1;
        do {
            if (lastIndex) {
                index = CharSequenceUtils.lastIndexOf(str, searchStr, index - 1);
            } else {
                index = CharSequenceUtils.indexOf(str, searchStr, index + 1);
            }
            if (index < 0) {
                return index;
            }
            found++;
        } while (found < ordinal);
        return index;
    }

    public static String overlay(String str, String overlay, int start, int end) {
        if (str == null) {
            return null;
        }
        if (overlay == null) {
            overlay = "";
        }
        int len = str.length();
        if (start < 0) {
            start = 0;
        }
        if (start > len) {
            start = len;
        }
        if (end < 0) {
            end = 0;
        }
        if (end > len) {
            end = len;
        }
        if (start > end) {
            int temp = start;
            start = end;
            end = temp;
        }
        return str.substring(0, start) + overlay + str.substring(end);
    }

    private static String prependIfMissing(String str, CharSequence prefix, boolean ignoreCase, CharSequence... prefixes) {
        if (str == null || isEmpty(prefix) || startsWith(str, prefix, ignoreCase)) {
            return str;
        }
        if (ArrayUtils.isNotEmpty(prefixes)) {
            for (CharSequence p : prefixes) {
                if (startsWith(str, p, ignoreCase)) {
                    return str;
                }
            }
        }
        return prefix.toString() + str;
    }

    public static String prependIfMissing(String str, CharSequence prefix, CharSequence... prefixes) {
        return prependIfMissing(str, prefix, false, prefixes);
    }

    public static String prependIfMissingIgnoreCase(String str, CharSequence prefix, CharSequence... prefixes) {
        return prependIfMissing(str, prefix, true, prefixes);
    }

    public static String remove(String str, char remove) {
        if (isEmpty(str) || str.indexOf(remove) == -1) {
            return str;
        }
        char[] chars = str.toCharArray();
        int pos = 0;
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] != remove) {
                chars[pos] = chars[i];
                pos++;
            }
        }
        return new String(chars, 0, pos);
    }

    public static String remove(String str, String remove) {
        if (isEmpty(str) || isEmpty(remove)) {
            return str;
        }
        return replace(str, remove, "", -1);
    }

    @Deprecated
    public static String removeAll(String text, String regex) {
        return RegExUtils.removeAll(text, regex);
    }

    public static String removeEnd(String str, String remove) {
        if (!isEmpty(str) && !isEmpty(remove) && str.endsWith(remove)) {
            return str.substring(0, str.length() - remove.length());
        }
        return str;
    }

    public static String removeEndIgnoreCase(String str, String remove) {
        if (!isEmpty(str) && !isEmpty(remove) && endsWithIgnoreCase(str, remove)) {
            return str.substring(0, str.length() - remove.length());
        }
        return str;
    }

    @Deprecated
    public static String removeFirst(String text, String regex) {
        return replaceFirst(text, regex, "");
    }

    public static String removeIgnoreCase(String str, String remove) {
        if (isEmpty(str) || isEmpty(remove)) {
            return str;
        }
        return replaceIgnoreCase(str, remove, "", -1);
    }

    @Deprecated
    public static String removePattern(String source, String regex) {
        return RegExUtils.removePattern(source, regex);
    }

    public static String removeStart(String str, String remove) {
        if (!isEmpty(str) && !isEmpty(remove) && str.startsWith(remove)) {
            return str.substring(remove.length());
        }
        return str;
    }

    public static String removeStartIgnoreCase(String str, String remove) {
        if (!isEmpty(str) && !isEmpty(remove) && startsWithIgnoreCase(str, remove)) {
            return str.substring(remove.length());
        }
        return str;
    }

    public static String repeat(char ch, int repeat) {
        if (repeat <= 0) {
            return "";
        }
        char[] buf = new char[repeat];
        for (int i = repeat - 1; i >= 0; i--) {
            buf[i] = ch;
        }
        return new String(buf);
    }

    public static String repeat(String str, int repeat) {
        if (str == null) {
            return null;
        }
        if (repeat <= 0) {
            return "";
        }
        int inputLength = str.length();
        if (repeat != 1 && inputLength != 0) {
            if (inputLength == 1 && repeat <= 8192) {
                return repeat(str.charAt(0), repeat);
            }
            int outputLength = inputLength * repeat;
            if (inputLength == 1) {
                char ch1 = str.charAt(0);
                return repeat(ch1, repeat);
            }
            if (inputLength == 2) {
                char ch0 = str.charAt(0);
                char ch12 = str.charAt(1);
                char[] output2 = new char[outputLength];
                for (int i = (repeat * 2) - 2; i >= 0; i = (i - 1) - 1) {
                    output2[i] = ch0;
                    output2[i + 1] = ch12;
                }
                return new String(output2);
            }
            StringBuilder buf = new StringBuilder(outputLength);
            for (int i2 = 0; i2 < repeat; i2++) {
                buf.append(str);
            }
            return buf.toString();
        }
        return str;
    }

    public static String repeat(String str, String separator, int repeat) {
        if (str == null || separator == null) {
            String result = repeat(str, repeat);
            return result;
        }
        String result2 = repeat(str + separator, repeat);
        return removeEnd(result2, separator);
    }

    public static String replace(String text, String searchString, String replacement) {
        return replace(text, searchString, replacement, -1);
    }

    public static String replace(String text, String searchString, String replacement, int max) {
        return replace(text, searchString, replacement, max, false);
    }

    private static String replace(String text, String searchString, String replacement, int max, boolean ignoreCase) {
        if (isEmpty(text) || isEmpty(searchString) || replacement == null || max == 0) {
            return text;
        }
        if (ignoreCase) {
            searchString = searchString.toLowerCase();
        }
        int start = 0;
        int end = ignoreCase ? indexOfIgnoreCase(text, searchString, 0) : indexOf(text, searchString, 0);
        if (end == -1) {
            return text;
        }
        int replLength = searchString.length();
        int increase = replacement.length() - replLength;
        int increase2 = increase < 0 ? 0 : increase;
        int i = 64;
        if (max < 0) {
            i = 16;
        } else if (max <= 64) {
            i = max;
        }
        StringBuilder buf = new StringBuilder(text.length() + (increase2 * i));
        while (end != -1) {
            buf.append((CharSequence) text, start, end);
            buf.append(replacement);
            start = end + replLength;
            max--;
            if (max == 0) {
                break;
            }
            end = ignoreCase ? indexOfIgnoreCase(text, searchString, start) : indexOf(text, searchString, start);
        }
        buf.append((CharSequence) text, start, text.length());
        return buf.toString();
    }

    @Deprecated
    public static String replaceAll(String text, String regex, String replacement) {
        return RegExUtils.replaceAll(text, regex, replacement);
    }

    public static String replaceChars(String str, char searchChar, char replaceChar) {
        if (str == null) {
            return null;
        }
        return str.replace(searchChar, replaceChar);
    }

    public static String replaceChars(String str, String searchChars, String replaceChars) {
        if (isEmpty(str) || isEmpty(searchChars)) {
            return str;
        }
        if (replaceChars == null) {
            replaceChars = "";
        }
        boolean modified = false;
        int replaceCharsLength = replaceChars.length();
        int strLength = str.length();
        StringBuilder buf = new StringBuilder(strLength);
        for (int i = 0; i < strLength; i++) {
            char ch = str.charAt(i);
            int index = searchChars.indexOf(ch);
            if (index >= 0) {
                modified = true;
                if (index < replaceCharsLength) {
                    buf.append(replaceChars.charAt(index));
                }
            } else {
                buf.append(ch);
            }
        }
        if (modified) {
            return buf.toString();
        }
        return str;
    }

    public static String replaceEach(String text, String[] searchList, String[] replacementList) {
        return replaceEach(text, searchList, replacementList, false, 0);
    }

    private static String replaceEach(String text, String[] searchList, String[] replacementList, boolean repeat, int timeToLive) {
        int greater;
        if (isEmpty(text) || ArrayUtils.isEmpty(searchList) || ArrayUtils.isEmpty(replacementList)) {
            return text;
        }
        if (timeToLive < 0) {
            throw new IllegalStateException("Aborting to protect against StackOverflowError - output of one loop is the input of another");
        }
        int searchLength = searchList.length;
        int replacementLength = replacementList.length;
        if (searchLength != replacementLength) {
            throw new IllegalArgumentException("Search and Replace array lengths don't match: " + searchLength + " vs " + replacementLength);
        }
        boolean[] noMoreMatchesForReplIndex = new boolean[searchLength];
        int textIndex = -1;
        int replaceIndex = -1;
        for (int i = 0; i < searchLength; i++) {
            if (!noMoreMatchesForReplIndex[i] && searchList[i] != null && !searchList[i].isEmpty() && replacementList[i] != null) {
                int tempIndex = text.indexOf(searchList[i]);
                if (tempIndex == -1) {
                    noMoreMatchesForReplIndex[i] = true;
                } else if (textIndex == -1 || tempIndex < textIndex) {
                    textIndex = tempIndex;
                    replaceIndex = i;
                }
            }
        }
        if (textIndex == -1) {
            return text;
        }
        int start = 0;
        int increase = 0;
        for (int i2 = 0; i2 < searchList.length; i2++) {
            if (searchList[i2] != null && replacementList[i2] != null && (greater = replacementList[i2].length() - searchList[i2].length()) > 0) {
                increase += greater * 3;
            }
        }
        int i3 = text.length();
        StringBuilder buf = new StringBuilder(text.length() + Math.min(increase, i3 / 5));
        while (textIndex != -1) {
            for (int i4 = start; i4 < textIndex; i4++) {
                buf.append(text.charAt(i4));
            }
            buf.append(replacementList[replaceIndex]);
            start = textIndex + searchList[replaceIndex].length();
            textIndex = -1;
            replaceIndex = -1;
            for (int i5 = 0; i5 < searchLength; i5++) {
                if (!noMoreMatchesForReplIndex[i5] && searchList[i5] != null) {
                    if (!searchList[i5].isEmpty() && replacementList[i5] != null) {
                        int tempIndex2 = text.indexOf(searchList[i5], start);
                        if (tempIndex2 == -1) {
                            noMoreMatchesForReplIndex[i5] = true;
                        } else if (textIndex == -1 || tempIndex2 < textIndex) {
                            textIndex = tempIndex2;
                            replaceIndex = i5;
                        }
                    }
                }
            }
        }
        int textLength = text.length();
        for (int i6 = start; i6 < textLength; i6++) {
            buf.append(text.charAt(i6));
        }
        String result = buf.toString();
        if (!repeat) {
            return result;
        }
        return replaceEach(result, searchList, replacementList, repeat, timeToLive - 1);
    }

    public static String replaceEachRepeatedly(String text, String[] searchList, String[] replacementList) {
        int timeToLive = searchList == null ? 0 : searchList.length;
        return replaceEach(text, searchList, replacementList, true, timeToLive);
    }

    @Deprecated
    public static String replaceFirst(String text, String regex, String replacement) {
        return RegExUtils.replaceFirst(text, regex, replacement);
    }

    public static String replaceIgnoreCase(String text, String searchString, String replacement) {
        return replaceIgnoreCase(text, searchString, replacement, -1);
    }

    public static String replaceIgnoreCase(String text, String searchString, String replacement, int max) {
        return replace(text, searchString, replacement, max, true);
    }

    public static String replaceOnce(String text, String searchString, String replacement) {
        return replace(text, searchString, replacement, 1);
    }

    public static String replaceOnceIgnoreCase(String text, String searchString, String replacement) {
        return replaceIgnoreCase(text, searchString, replacement, 1);
    }

    @Deprecated
    public static String replacePattern(String source, String regex, String replacement) {
        return RegExUtils.replacePattern(source, regex, replacement);
    }

    public static String reverse(String str) {
        if (str == null) {
            return null;
        }
        return new StringBuilder(str).reverse().toString();
    }

    public static String reverseDelimited(String str, char separatorChar) {
        if (str == null) {
            return null;
        }
        String[] strs = split(str, separatorChar);
        ArrayUtils.reverse(strs);
        return join(strs, separatorChar);
    }

    public static String right(String str, int len) {
        if (str == null) {
            return null;
        }
        if (len < 0) {
            return "";
        }
        if (str.length() <= len) {
            return str;
        }
        return str.substring(str.length() - len);
    }

    public static String rightPad(String str, int size) {
        return rightPad(str, size, ' ');
    }

    public static String rightPad(String str, int size, char padChar) {
        if (str == null) {
            return null;
        }
        int pads = size - str.length();
        if (pads <= 0) {
            return str;
        }
        if (pads > 8192) {
            return rightPad(str, size, String.valueOf(padChar));
        }
        return str.concat(repeat(padChar, pads));
    }

    public static String rightPad(String str, int size, String padStr) {
        if (str == null) {
            return null;
        }
        if (isEmpty(padStr)) {
            padStr = SPACE;
        }
        int padLen = padStr.length();
        int strLen = str.length();
        int pads = size - strLen;
        if (pads <= 0) {
            return str;
        }
        if (padLen == 1 && pads <= 8192) {
            return rightPad(str, size, padStr.charAt(0));
        }
        if (pads == padLen) {
            return str.concat(padStr);
        }
        if (pads < padLen) {
            return str.concat(padStr.substring(0, pads));
        }
        char[] padding = new char[pads];
        char[] padChars = padStr.toCharArray();
        for (int i = 0; i < pads; i++) {
            padding[i] = padChars[i % padLen];
        }
        return str.concat(new String(padding));
    }

    public static String rotate(String str, int shift) {
        if (str == null) {
            return null;
        }
        int strLen = str.length();
        if (shift == 0 || strLen == 0 || shift % strLen == 0) {
            return str;
        }
        StringBuilder builder = new StringBuilder(strLen);
        int offset = -(shift % strLen);
        builder.append(substring(str, offset));
        builder.append(substring(str, 0, offset));
        return builder.toString();
    }

    public static String[] split(String str) {
        return split(str, null, -1);
    }

    public static String[] split(String str, char separatorChar) {
        return splitWorker(str, separatorChar, false);
    }

    public static String[] split(String str, String separatorChars) {
        return splitWorker(str, separatorChars, -1, false);
    }

    public static String[] split(String str, String separatorChars, int max) {
        return splitWorker(str, separatorChars, max, false);
    }

    public static String[] splitByCharacterType(String str) {
        return splitByCharacterType(str, false);
    }

    private static String[] splitByCharacterType(String str, boolean camelCase) {
        if (str == null) {
            return null;
        }
        if (str.isEmpty()) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }
        char[] c = str.toCharArray();
        List<String> list = new ArrayList<>();
        int tokenStart = 0;
        int currentType = Character.getType(c[0]);
        for (int pos = 0 + 1; pos < c.length; pos++) {
            int type = Character.getType(c[pos]);
            if (type != currentType) {
                if (camelCase && type == 2 && currentType == 1) {
                    int newTokenStart = pos - 1;
                    if (newTokenStart != tokenStart) {
                        list.add(new String(c, tokenStart, newTokenStart - tokenStart));
                        tokenStart = newTokenStart;
                    }
                } else {
                    list.add(new String(c, tokenStart, pos - tokenStart));
                    tokenStart = pos;
                }
                currentType = type;
            }
        }
        list.add(new String(c, tokenStart, c.length - tokenStart));
        return (String[]) list.toArray(ArrayUtils.EMPTY_STRING_ARRAY);
    }

    public static String[] splitByCharacterTypeCamelCase(String str) {
        return splitByCharacterType(str, true);
    }

    public static String[] splitByWholeSeparator(String str, String separator) {
        return splitByWholeSeparatorWorker(str, separator, -1, false);
    }

    public static String[] splitByWholeSeparator(String str, String separator, int max) {
        return splitByWholeSeparatorWorker(str, separator, max, false);
    }

    public static String[] splitByWholeSeparatorPreserveAllTokens(String str, String separator) {
        return splitByWholeSeparatorWorker(str, separator, -1, true);
    }

    public static String[] splitByWholeSeparatorPreserveAllTokens(String str, String separator, int max) {
        return splitByWholeSeparatorWorker(str, separator, max, true);
    }

    private static String[] splitByWholeSeparatorWorker(String str, String separator, int max, boolean preserveAllTokens) {
        if (str == null) {
            return null;
        }
        int len = str.length();
        if (len == 0) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }
        if (separator == null || "".equals(separator)) {
            return splitWorker(str, null, max, preserveAllTokens);
        }
        int separatorLength = separator.length();
        ArrayList<String> substrings = new ArrayList<>();
        int numberOfSubstrings = 0;
        int beg = 0;
        int end = 0;
        while (end < len) {
            end = str.indexOf(separator, beg);
            if (end > -1) {
                if (end > beg) {
                    numberOfSubstrings++;
                    if (numberOfSubstrings == max) {
                        end = len;
                        substrings.add(str.substring(beg));
                    } else {
                        substrings.add(str.substring(beg, end));
                        beg = end + separatorLength;
                    }
                } else {
                    if (preserveAllTokens) {
                        numberOfSubstrings++;
                        if (numberOfSubstrings == max) {
                            end = len;
                            substrings.add(str.substring(beg));
                        } else {
                            substrings.add("");
                        }
                    }
                    beg = end + separatorLength;
                }
            } else {
                substrings.add(str.substring(beg));
                end = len;
            }
        }
        return (String[]) substrings.toArray(ArrayUtils.EMPTY_STRING_ARRAY);
    }

    public static String[] splitPreserveAllTokens(String str) {
        return splitWorker(str, null, -1, true);
    }

    public static String[] splitPreserveAllTokens(String str, char separatorChar) {
        return splitWorker(str, separatorChar, true);
    }

    public static String[] splitPreserveAllTokens(String str, String separatorChars) {
        return splitWorker(str, separatorChars, -1, true);
    }

    public static String[] splitPreserveAllTokens(String str, String separatorChars, int max) {
        return splitWorker(str, separatorChars, max, true);
    }

    private static String[] splitWorker(String str, char separatorChar, boolean preserveAllTokens) {
        if (str == null) {
            return null;
        }
        int len = str.length();
        if (len == 0) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }
        List<String> list = new ArrayList<>();
        int i = 0;
        int start = 0;
        boolean match = false;
        boolean lastMatch = false;
        while (i < len) {
            if (str.charAt(i) == separatorChar) {
                if (match || preserveAllTokens) {
                    list.add(str.substring(start, i));
                    match = false;
                    lastMatch = true;
                }
                i++;
                start = i;
            } else {
                lastMatch = false;
                match = true;
                i++;
            }
        }
        if (match || (preserveAllTokens && lastMatch)) {
            list.add(str.substring(start, i));
        }
        return (String[]) list.toArray(ArrayUtils.EMPTY_STRING_ARRAY);
    }

    private static String[] splitWorker(String str, String separatorChars, int max, boolean preserveAllTokens) {
        if (str == null) {
            return null;
        }
        int len = str.length();
        if (len == 0) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }
        List<String> list = new ArrayList<>();
        int sizePlus1 = 1;
        int i = 0;
        int start = 0;
        boolean match = false;
        boolean lastMatch = false;
        if (separatorChars != null) {
            if (separatorChars.length() == 1) {
                char sep = separatorChars.charAt(0);
                while (i < len) {
                    if (str.charAt(i) == sep) {
                        if (match || preserveAllTokens) {
                            lastMatch = true;
                            int sizePlus12 = sizePlus1 + 1;
                            if (sizePlus1 == max) {
                                lastMatch = false;
                                i = len;
                            }
                            list.add(str.substring(start, i));
                            match = false;
                            sizePlus1 = sizePlus12;
                        }
                        i++;
                        start = i;
                    } else {
                        lastMatch = false;
                        match = true;
                        i++;
                    }
                }
            } else {
                while (i < len) {
                    if (separatorChars.indexOf(str.charAt(i)) >= 0) {
                        if (match || preserveAllTokens) {
                            lastMatch = true;
                            int sizePlus13 = sizePlus1 + 1;
                            if (sizePlus1 == max) {
                                lastMatch = false;
                                i = len;
                            }
                            list.add(str.substring(start, i));
                            match = false;
                            sizePlus1 = sizePlus13;
                        }
                        i++;
                        start = i;
                    } else {
                        lastMatch = false;
                        match = true;
                        i++;
                    }
                }
            }
        } else {
            while (i < len) {
                if (Character.isWhitespace(str.charAt(i))) {
                    if (match || preserveAllTokens) {
                        lastMatch = true;
                        int sizePlus14 = sizePlus1 + 1;
                        if (sizePlus1 == max) {
                            lastMatch = false;
                            i = len;
                        }
                        list.add(str.substring(start, i));
                        match = false;
                        sizePlus1 = sizePlus14;
                    }
                    i++;
                    start = i;
                } else {
                    lastMatch = false;
                    match = true;
                    i++;
                }
            }
        }
        if (match || (preserveAllTokens && lastMatch)) {
            list.add(str.substring(start, i));
        }
        return (String[]) list.toArray(ArrayUtils.EMPTY_STRING_ARRAY);
    }

    public static boolean startsWith(CharSequence str, CharSequence prefix) {
        return startsWith(str, prefix, false);
    }

    private static boolean startsWith(CharSequence str, CharSequence prefix, boolean ignoreCase) {
        if (str == null || prefix == null) {
            return str == prefix;
        }
        if (prefix.length() > str.length()) {
            return false;
        }
        return CharSequenceUtils.regionMatches(str, ignoreCase, 0, prefix, 0, prefix.length());
    }

    public static boolean startsWithAny(CharSequence sequence, CharSequence... searchStrings) {
        if (isEmpty(sequence) || ArrayUtils.isEmpty(searchStrings)) {
            return false;
        }
        for (CharSequence searchString : searchStrings) {
            if (startsWith(sequence, searchString)) {
                return true;
            }
        }
        return false;
    }

    public static boolean startsWithIgnoreCase(CharSequence str, CharSequence prefix) {
        return startsWith(str, prefix, true);
    }

    public static String strip(String str) {
        return strip(str, null);
    }

    public static String strip(String str, String stripChars) {
        if (isEmpty(str)) {
            return str;
        }
        return stripEnd(stripStart(str, stripChars), stripChars);
    }

    public static String stripAccents(String input) {
        if (input == null) {
            return null;
        }
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        StringBuilder decomposed = new StringBuilder(Normalizer.normalize(input, Normalizer.Form.NFD));
        convertRemainingAccentCharacters(decomposed);
        return pattern.matcher(decomposed).replaceAll("");
    }

    public static String[] stripAll(String... strs) {
        return stripAll(strs, null);
    }

    public static String[] stripAll(String[] strs, String stripChars) {
        int strsLen;
        if (strs == null || (strsLen = strs.length) == 0) {
            return strs;
        }
        String[] newArr = new String[strsLen];
        for (int i = 0; i < strsLen; i++) {
            newArr[i] = strip(strs[i], stripChars);
        }
        return newArr;
    }

    public static String stripEnd(String str, String stripChars) {
        int end = length(str);
        if (end == 0) {
            return str;
        }
        if (stripChars == null) {
            while (end != 0 && Character.isWhitespace(str.charAt(end - 1))) {
                end--;
            }
        } else {
            if (stripChars.isEmpty()) {
                return str;
            }
            while (end != 0 && stripChars.indexOf(str.charAt(end - 1)) != -1) {
                end--;
            }
        }
        return str.substring(0, end);
    }

    public static String stripStart(String str, String stripChars) {
        int strLen = length(str);
        if (strLen == 0) {
            return str;
        }
        int start = 0;
        if (stripChars == null) {
            while (start != strLen && Character.isWhitespace(str.charAt(start))) {
                start++;
            }
        } else {
            if (stripChars.isEmpty()) {
                return str;
            }
            while (start != strLen && stripChars.indexOf(str.charAt(start)) != -1) {
                start++;
            }
        }
        return str.substring(start);
    }

    public static String stripToEmpty(String str) {
        return str == null ? "" : strip(str, null);
    }

    public static String stripToNull(String str) {
        if (str == null) {
            return null;
        }
        String str2 = strip(str, null);
        if (str2.isEmpty()) {
            return null;
        }
        return str2;
    }

    public static String substring(String str, int start) {
        if (str == null) {
            return null;
        }
        if (start < 0) {
            start += str.length();
        }
        if (start < 0) {
            start = 0;
        }
        if (start > str.length()) {
            return "";
        }
        return str.substring(start);
    }

    public static String substring(String str, int start, int end) {
        if (str == null) {
            return null;
        }
        if (end < 0) {
            end += str.length();
        }
        if (start < 0) {
            start += str.length();
        }
        if (end > str.length()) {
            end = str.length();
        }
        if (start > end) {
            return "";
        }
        if (start < 0) {
            start = 0;
        }
        if (end < 0) {
            end = 0;
        }
        return str.substring(start, end);
    }

    public static String substringAfter(String str, String separator) {
        int pos;
        if (isEmpty(str)) {
            return str;
        }
        if (separator == null || (pos = str.indexOf(separator)) == -1) {
            return "";
        }
        return str.substring(separator.length() + pos);
    }

    public static String substringAfterLast(String str, String separator) {
        int pos;
        if (isEmpty(str)) {
            return str;
        }
        return (isEmpty(separator) || (pos = str.lastIndexOf(separator)) == -1 || pos == str.length() - separator.length()) ? "" : str.substring(separator.length() + pos);
    }

    public static String substringBefore(String str, String separator) {
        if (isEmpty(str) || separator == null) {
            return str;
        }
        if (separator.isEmpty()) {
            return "";
        }
        int pos = str.indexOf(separator);
        if (pos == -1) {
            return str;
        }
        return str.substring(0, pos);
    }

    public static String substringBeforeLast(String str, String separator) {
        int pos;
        if (isEmpty(str) || isEmpty(separator) || (pos = str.lastIndexOf(separator)) == -1) {
            return str;
        }
        return str.substring(0, pos);
    }

    public static String substringBetween(String str, String tag) {
        return substringBetween(str, tag, tag);
    }

    public static String substringBetween(String str, String open, String close) {
        int start;
        int end;
        if (str == null || open == null || close == null || (start = str.indexOf(open)) == -1 || (end = str.indexOf(close, open.length() + start)) == -1) {
            return null;
        }
        return str.substring(open.length() + start, end);
    }

    public static String[] substringsBetween(String str, String open, String close) {
        int start;
        int start2;
        int end;
        if (str == null || isEmpty(open) || isEmpty(close)) {
            return null;
        }
        int strLen = str.length();
        if (strLen == 0) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }
        int closeLen = close.length();
        int openLen = open.length();
        List<String> list = new ArrayList<>();
        int pos = 0;
        while (pos < strLen - closeLen && (start = str.indexOf(open, pos)) >= 0 && (end = str.indexOf(close, (start2 = start + openLen))) >= 0) {
            list.add(str.substring(start2, end));
            pos = end + closeLen;
        }
        if (list.isEmpty()) {
            return null;
        }
        return (String[]) list.toArray(ArrayUtils.EMPTY_STRING_ARRAY);
    }

    public static String swapCase(String str) {
        int newCodePoint;
        if (isEmpty(str)) {
            return str;
        }
        int strLen = str.length();
        int[] newCodePoints = new int[strLen];
        int outOffset = 0;
        int i = 0;
        while (i < strLen) {
            int oldCodepoint = str.codePointAt(i);
            if (Character.isUpperCase(oldCodepoint)) {
                newCodePoint = Character.toLowerCase(oldCodepoint);
            } else if (Character.isTitleCase(oldCodepoint)) {
                newCodePoint = Character.toLowerCase(oldCodepoint);
            } else if (Character.isLowerCase(oldCodepoint)) {
                newCodePoint = Character.toUpperCase(oldCodepoint);
            } else {
                newCodePoint = oldCodepoint;
            }
            newCodePoints[outOffset] = newCodePoint;
            i += Character.charCount(newCodePoint);
            outOffset++;
        }
        return new String(newCodePoints, 0, outOffset);
    }

    public static int[] toCodePoints(CharSequence str) {
        if (str == null) {
            return null;
        }
        if (str.length() == 0) {
            return ArrayUtils.EMPTY_INT_ARRAY;
        }
        String s = str.toString();
        int[] result = new int[s.codePointCount(0, s.length())];
        int index = 0;
        for (int i = 0; i < result.length; i++) {
            result[i] = s.codePointAt(index);
            index += Character.charCount(result[i]);
        }
        return result;
    }

    public static String toEncodedString(byte[] bytes, Charset charset) {
        return new String(bytes, Charsets.toCharset(charset));
    }

    public static String toRootLowerCase(String source) {
        if (source == null) {
            return null;
        }
        return source.toLowerCase(Locale.ROOT);
    }

    public static String toRootUpperCase(String source) {
        if (source == null) {
            return null;
        }
        return source.toUpperCase(Locale.ROOT);
    }

    @Deprecated
    public static String toString(byte[] bytes, String charsetName) throws UnsupportedEncodingException {
        return charsetName != null ? new String(bytes, charsetName) : new String(bytes, Charset.defaultCharset());
    }

    public static String trim(String str) {
        if (str == null) {
            return null;
        }
        return str.trim();
    }

    public static String trimToEmpty(String str) {
        return str == null ? "" : str.trim();
    }

    public static String trimToNull(String str) {
        String ts = trim(str);
        if (isEmpty(ts)) {
            return null;
        }
        return ts;
    }

    public static String truncate(String str, int maxWidth) {
        return truncate(str, 0, maxWidth);
    }

    public static String truncate(String str, int offset, int maxWidth) {
        if (offset < 0) {
            throw new IllegalArgumentException("offset cannot be negative");
        }
        if (maxWidth < 0) {
            throw new IllegalArgumentException("maxWith cannot be negative");
        }
        if (str == null) {
            return null;
        }
        if (offset > str.length()) {
            return "";
        }
        if (str.length() > maxWidth) {
            int ix = offset + maxWidth > str.length() ? str.length() : offset + maxWidth;
            return str.substring(offset, ix);
        }
        return str.substring(offset);
    }

    public static String uncapitalize(String str) {
        int firstCodepoint;
        int newCodePoint;
        int strLen = length(str);
        if (strLen == 0 || firstCodepoint == (newCodePoint = Character.toLowerCase((firstCodepoint = str.codePointAt(0))))) {
            return str;
        }
        int[] newCodePoints = new int[strLen];
        int outOffset = 0 + 1;
        newCodePoints[0] = newCodePoint;
        int inOffset = Character.charCount(firstCodepoint);
        while (inOffset < strLen) {
            int codepoint = str.codePointAt(inOffset);
            newCodePoints[outOffset] = codepoint;
            inOffset += Character.charCount(codepoint);
            outOffset++;
        }
        return new String(newCodePoints, 0, outOffset);
    }

    public static String unwrap(String str, char wrapChar) {
        if (!isEmpty(str) && wrapChar != 0 && str.length() != 1 && str.charAt(0) == wrapChar && str.charAt(str.length() - 1) == wrapChar) {
            int endIndex = str.length() - 1;
            return str.substring(1, endIndex);
        }
        return str;
    }

    public static String unwrap(String str, String wrapToken) {
        if (!isEmpty(str) && !isEmpty(wrapToken) && str.length() != 1 && startsWith(str, wrapToken) && endsWith(str, wrapToken)) {
            int startIndex = str.indexOf(wrapToken);
            int endIndex = str.lastIndexOf(wrapToken);
            int wrapLength = wrapToken.length();
            if (startIndex != -1 && endIndex != -1) {
                return str.substring(startIndex + wrapLength, endIndex);
            }
        }
        return str;
    }

    public static String upperCase(String str) {
        if (str == null) {
            return null;
        }
        return str.toUpperCase();
    }

    public static String upperCase(String str, Locale locale) {
        if (str == null) {
            return null;
        }
        return str.toUpperCase(locale);
    }

    public static String valueOf(char[] value) {
        if (value == null) {
            return null;
        }
        return String.valueOf(value);
    }

    public static String wrap(String str, char wrapWith) {
        if (isEmpty(str) || wrapWith == 0) {
            return str;
        }
        return wrapWith + str + wrapWith;
    }

    public static String wrap(String str, String wrapWith) {
        if (isEmpty(str) || isEmpty(wrapWith)) {
            return str;
        }
        return wrapWith.concat(str).concat(wrapWith);
    }

    public static String wrapIfMissing(String str, char wrapWith) {
        if (isEmpty(str) || wrapWith == 0) {
            return str;
        }
        boolean wrapStart = str.charAt(0) != wrapWith;
        boolean wrapEnd = str.charAt(str.length() - 1) != wrapWith;
        if (!wrapStart && !wrapEnd) {
            return str;
        }
        StringBuilder builder = new StringBuilder(str.length() + 2);
        if (wrapStart) {
            builder.append(wrapWith);
        }
        builder.append(str);
        if (wrapEnd) {
            builder.append(wrapWith);
        }
        return builder.toString();
    }

    public static String wrapIfMissing(String str, String wrapWith) {
        if (isEmpty(str) || isEmpty(wrapWith)) {
            return str;
        }
        boolean wrapStart = !str.startsWith(wrapWith);
        boolean wrapEnd = !str.endsWith(wrapWith);
        if (!wrapStart && !wrapEnd) {
            return str;
        }
        StringBuilder builder = new StringBuilder(str.length() + wrapWith.length() + wrapWith.length());
        if (wrapStart) {
            builder.append(wrapWith);
        }
        builder.append(str);
        if (wrapEnd) {
            builder.append(wrapWith);
        }
        return builder.toString();
    }
}
