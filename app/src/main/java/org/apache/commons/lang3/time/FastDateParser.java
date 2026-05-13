package org.apache.commons.lang3.time;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.ClassUtils;

/* JADX INFO: loaded from: classes.dex */
public class FastDateParser implements DateParser, Serializable {
    private static final long serialVersionUID = 3;
    private final int century;
    private final Locale locale;
    private final String pattern;
    private transient List<StrategyAndWidth> patterns;
    private final int startYear;
    private final TimeZone timeZone;
    static final Locale JAPANESE_IMPERIAL = new Locale("ja", "JP", "JP");
    private static final Comparator<String> LONGER_FIRST_LOWERCASE = new Comparator() { // from class: org.apache.commons.lang3.time.-$$Lambda$FastDateParser$UYP2Ndj-tct1ADCRcgjx6y76AyA
        @Override // java.util.Comparator
        public final int compare(Object obj, Object obj2) {
            return ((String) obj2).compareTo((String) obj);
        }
    };
    private static final ConcurrentMap<Locale, Strategy>[] caches = new ConcurrentMap[17];
    private static final Strategy ABBREVIATED_YEAR_STRATEGY = new NumberStrategy(1) { // from class: org.apache.commons.lang3.time.FastDateParser.1
        @Override // org.apache.commons.lang3.time.FastDateParser.NumberStrategy
        int modify(FastDateParser parser, int iValue) {
            return iValue < 100 ? parser.adjustYear(iValue) : iValue;
        }
    };
    private static final Strategy NUMBER_MONTH_STRATEGY = new NumberStrategy(2) { // from class: org.apache.commons.lang3.time.FastDateParser.2
        @Override // org.apache.commons.lang3.time.FastDateParser.NumberStrategy
        int modify(FastDateParser parser, int iValue) {
            return iValue - 1;
        }
    };
    private static final Strategy LITERAL_YEAR_STRATEGY = new NumberStrategy(1);
    private static final Strategy WEEK_OF_YEAR_STRATEGY = new NumberStrategy(3);
    private static final Strategy WEEK_OF_MONTH_STRATEGY = new NumberStrategy(4);
    private static final Strategy DAY_OF_YEAR_STRATEGY = new NumberStrategy(6);
    private static final Strategy DAY_OF_MONTH_STRATEGY = new NumberStrategy(5);
    private static final Strategy DAY_OF_WEEK_STRATEGY = new NumberStrategy(7) { // from class: org.apache.commons.lang3.time.FastDateParser.3
        @Override // org.apache.commons.lang3.time.FastDateParser.NumberStrategy
        int modify(FastDateParser parser, int iValue) {
            if (iValue == 7) {
                return 1;
            }
            return iValue + 1;
        }
    };
    private static final Strategy DAY_OF_WEEK_IN_MONTH_STRATEGY = new NumberStrategy(8);
    private static final Strategy HOUR_OF_DAY_STRATEGY = new NumberStrategy(11);
    private static final Strategy HOUR24_OF_DAY_STRATEGY = new NumberStrategy(11) { // from class: org.apache.commons.lang3.time.FastDateParser.4
        @Override // org.apache.commons.lang3.time.FastDateParser.NumberStrategy
        int modify(FastDateParser parser, int iValue) {
            if (iValue == 24) {
                return 0;
            }
            return iValue;
        }
    };
    private static final Strategy HOUR12_STRATEGY = new NumberStrategy(10) { // from class: org.apache.commons.lang3.time.FastDateParser.5
        @Override // org.apache.commons.lang3.time.FastDateParser.NumberStrategy
        int modify(FastDateParser parser, int iValue) {
            if (iValue == 12) {
                return 0;
            }
            return iValue;
        }
    };
    private static final Strategy HOUR_STRATEGY = new NumberStrategy(10);
    private static final Strategy MINUTE_STRATEGY = new NumberStrategy(12);
    private static final Strategy SECOND_STRATEGY = new NumberStrategy(13);
    private static final Strategy MILLISECOND_STRATEGY = new NumberStrategy(14);

    protected FastDateParser(String pattern, TimeZone timeZone, Locale locale) {
        this(pattern, timeZone, locale, null);
    }

    protected FastDateParser(String pattern, TimeZone timeZone, Locale locale, Date centuryStart) {
        int centuryStartYear;
        this.pattern = pattern;
        this.timeZone = timeZone;
        this.locale = locale;
        Calendar definingCalendar = Calendar.getInstance(timeZone, locale);
        if (centuryStart != null) {
            definingCalendar.setTime(centuryStart);
            centuryStartYear = definingCalendar.get(1);
        } else if (locale.equals(JAPANESE_IMPERIAL)) {
            centuryStartYear = 0;
        } else {
            definingCalendar.setTime(new Date());
            centuryStartYear = definingCalendar.get(1) - 80;
        }
        int i = (centuryStartYear / 100) * 100;
        this.century = i;
        this.startYear = centuryStartYear - i;
        init(definingCalendar);
    }

    private void init(Calendar definingCalendar) {
        this.patterns = new ArrayList();
        StrategyParser fm = new StrategyParser(definingCalendar);
        while (true) {
            StrategyAndWidth field = fm.getNextStrategy();
            if (field != null) {
                this.patterns.add(field);
            } else {
                return;
            }
        }
    }

    private static class StrategyAndWidth {
        final Strategy strategy;
        final int width;

        StrategyAndWidth(Strategy strategy, int width) {
            this.strategy = strategy;
            this.width = width;
        }

        int getMaxWidth(ListIterator<StrategyAndWidth> lt) {
            if (!this.strategy.isNumber() || !lt.hasNext()) {
                return 0;
            }
            Strategy nextStrategy = lt.next().strategy;
            lt.previous();
            if (nextStrategy.isNumber()) {
                return this.width;
            }
            return 0;
        }
    }

    private class StrategyParser {
        private int currentIdx;
        private final Calendar definingCalendar;

        StrategyParser(Calendar definingCalendar) {
            this.definingCalendar = definingCalendar;
        }

        StrategyAndWidth getNextStrategy() {
            if (this.currentIdx < FastDateParser.this.pattern.length()) {
                char c = FastDateParser.this.pattern.charAt(this.currentIdx);
                if (FastDateParser.isFormatLetter(c)) {
                    return letterPattern(c);
                }
                return literal();
            }
            return null;
        }

        private StrategyAndWidth letterPattern(char c) {
            int begin = this.currentIdx;
            do {
                int i = this.currentIdx + 1;
                this.currentIdx = i;
                if (i >= FastDateParser.this.pattern.length()) {
                    break;
                }
            } while (FastDateParser.this.pattern.charAt(this.currentIdx) == c);
            int width = this.currentIdx - begin;
            return new StrategyAndWidth(FastDateParser.this.getStrategy(c, width, this.definingCalendar), width);
        }

        private StrategyAndWidth literal() {
            boolean activeQuote = false;
            StringBuilder sb = new StringBuilder();
            while (this.currentIdx < FastDateParser.this.pattern.length()) {
                char c = FastDateParser.this.pattern.charAt(this.currentIdx);
                if (!activeQuote && FastDateParser.isFormatLetter(c)) {
                    break;
                }
                if (c == '\'') {
                    int i = this.currentIdx + 1;
                    this.currentIdx = i;
                    if (i == FastDateParser.this.pattern.length() || FastDateParser.this.pattern.charAt(this.currentIdx) != '\'') {
                        activeQuote = activeQuote ? false : true;
                    }
                }
                this.currentIdx++;
                sb.append(c);
            }
            if (activeQuote) {
                throw new IllegalArgumentException("Unterminated quote");
            }
            String formatField = sb.toString();
            return new StrategyAndWidth(new CopyQuotedStrategy(formatField), formatField.length());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean isFormatLetter(char c) {
        return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z');
    }

    @Override // org.apache.commons.lang3.time.DateParser, org.apache.commons.lang3.time.DatePrinter
    public String getPattern() {
        return this.pattern;
    }

    @Override // org.apache.commons.lang3.time.DateParser, org.apache.commons.lang3.time.DatePrinter
    public TimeZone getTimeZone() {
        return this.timeZone;
    }

    @Override // org.apache.commons.lang3.time.DateParser, org.apache.commons.lang3.time.DatePrinter
    public Locale getLocale() {
        return this.locale;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof FastDateParser)) {
            return false;
        }
        FastDateParser other = (FastDateParser) obj;
        return this.pattern.equals(other.pattern) && this.timeZone.equals(other.timeZone) && this.locale.equals(other.locale);
    }

    public int hashCode() {
        return this.pattern.hashCode() + ((this.timeZone.hashCode() + (this.locale.hashCode() * 13)) * 13);
    }

    public String toString() {
        return "FastDateParser[" + this.pattern + "," + this.locale + "," + this.timeZone.getID() + "]";
    }

    private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
        in.defaultReadObject();
        Calendar definingCalendar = Calendar.getInstance(this.timeZone, this.locale);
        init(definingCalendar);
    }

    @Override // org.apache.commons.lang3.time.DateParser
    public Object parseObject(String source) throws ParseException {
        return parse(source);
    }

    @Override // org.apache.commons.lang3.time.DateParser
    public Date parse(String source) throws ParseException {
        ParsePosition pp = new ParsePosition(0);
        Date date = parse(source, pp);
        if (date == null) {
            if (this.locale.equals(JAPANESE_IMPERIAL)) {
                throw new ParseException("(The " + this.locale + " locale does not support dates before 1868 AD)\nUnparseable date: \"" + source, pp.getErrorIndex());
            }
            throw new ParseException("Unparseable date: " + source, pp.getErrorIndex());
        }
        return date;
    }

    @Override // org.apache.commons.lang3.time.DateParser
    public Object parseObject(String source, ParsePosition pos) {
        return parse(source, pos);
    }

    @Override // org.apache.commons.lang3.time.DateParser
    public Date parse(String source, ParsePosition pos) {
        Calendar cal = Calendar.getInstance(this.timeZone, this.locale);
        cal.clear();
        if (parse(source, pos, cal)) {
            return cal.getTime();
        }
        return null;
    }

    @Override // org.apache.commons.lang3.time.DateParser
    public boolean parse(String source, ParsePosition pos, Calendar calendar) {
        ListIterator<StrategyAndWidth> lt = this.patterns.listIterator();
        while (lt.hasNext()) {
            StrategyAndWidth strategyAndWidth = lt.next();
            int maxWidth = strategyAndWidth.getMaxWidth(lt);
            if (!strategyAndWidth.strategy.parse(this, calendar, source, pos, maxWidth)) {
                return false;
            }
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:20:0x002f  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static java.lang.StringBuilder simpleQuote(java.lang.StringBuilder r6, java.lang.String r7) {
        /*
            r0 = 0
        L1:
            int r1 = r7.length()
            r2 = 63
            r3 = 46
            if (r0 >= r1) goto L38
            char r1 = r7.charAt(r0)
            r4 = 36
            r5 = 92
            if (r1 == r4) goto L2f
            if (r1 == r3) goto L2f
            if (r1 == r2) goto L2f
            r2 = 94
            if (r1 == r2) goto L2f
            r2 = 91
            if (r1 == r2) goto L2f
            if (r1 == r5) goto L2f
            r2 = 123(0x7b, float:1.72E-43)
            if (r1 == r2) goto L2f
            r2 = 124(0x7c, float:1.74E-43)
            if (r1 == r2) goto L2f
            switch(r1) {
                case 40: goto L2f;
                case 41: goto L2f;
                case 42: goto L2f;
                case 43: goto L2f;
                default: goto L2e;
            }
        L2e:
            goto L32
        L2f:
            r6.append(r5)
        L32:
            r6.append(r1)
            int r0 = r0 + 1
            goto L1
        L38:
            int r0 = r6.length()
            int r0 = r0 + (-1)
            char r0 = r6.charAt(r0)
            if (r0 != r3) goto L47
            r6.append(r2)
        L47:
            return r6
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.time.FastDateParser.simpleQuote(java.lang.StringBuilder, java.lang.String):java.lang.StringBuilder");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static Map<String, Integer> appendDisplayNames(Calendar cal, Locale locale, int field, StringBuilder regex) {
        Map<String, Integer> values = new HashMap<>();
        Map<String, Integer> displayNames = cal.getDisplayNames(field, 0, locale);
        TreeSet<String> sorted = new TreeSet<>(LONGER_FIRST_LOWERCASE);
        for (Map.Entry<String, Integer> displayName : displayNames.entrySet()) {
            String key = displayName.getKey().toLowerCase(locale);
            if (sorted.add(key)) {
                values.put(key, displayName.getValue());
            }
        }
        for (String symbol : sorted) {
            simpleQuote(regex, symbol).append('|');
        }
        return values;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int adjustYear(int twoDigitYear) {
        int trial = this.century + twoDigitYear;
        return twoDigitYear >= this.startYear ? trial : trial + 100;
    }

    private static abstract class Strategy {
        abstract boolean parse(FastDateParser fastDateParser, Calendar calendar, String str, ParsePosition parsePosition, int i);

        private Strategy() {
        }

        boolean isNumber() {
            return false;
        }
    }

    private static abstract class PatternStrategy extends Strategy {
        private Pattern pattern;

        abstract void setCalendar(FastDateParser fastDateParser, Calendar calendar, String str);

        private PatternStrategy() {
            super();
        }

        void createPattern(StringBuilder regex) {
            createPattern(regex.toString());
        }

        void createPattern(String regex) {
            this.pattern = Pattern.compile(regex);
        }

        @Override // org.apache.commons.lang3.time.FastDateParser.Strategy
        boolean isNumber() {
            return false;
        }

        @Override // org.apache.commons.lang3.time.FastDateParser.Strategy
        boolean parse(FastDateParser parser, Calendar calendar, String source, ParsePosition pos, int maxWidth) {
            Matcher matcher = this.pattern.matcher(source.substring(pos.getIndex()));
            if (!matcher.lookingAt()) {
                pos.setErrorIndex(pos.getIndex());
                return false;
            }
            pos.setIndex(pos.getIndex() + matcher.end(1));
            setCalendar(parser, calendar, matcher.group(1));
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Strategy getStrategy(char f, int width, Calendar definingCalendar) {
        if (f != 'y') {
            if (f != 'z') {
                switch (f) {
                    case 'D':
                        return DAY_OF_YEAR_STRATEGY;
                    case 'E':
                        return getLocaleSpecificStrategy(7, definingCalendar);
                    case 'F':
                        return DAY_OF_WEEK_IN_MONTH_STRATEGY;
                    case 'G':
                        return getLocaleSpecificStrategy(0, definingCalendar);
                    case 'H':
                        return HOUR_OF_DAY_STRATEGY;
                    default:
                        switch (f) {
                            case 'K':
                                return HOUR_STRATEGY;
                            case 'M':
                                return width >= 3 ? getLocaleSpecificStrategy(2, definingCalendar) : NUMBER_MONTH_STRATEGY;
                            case 'S':
                                return MILLISECOND_STRATEGY;
                            case 'a':
                                return getLocaleSpecificStrategy(9, definingCalendar);
                            case 'd':
                                return DAY_OF_MONTH_STRATEGY;
                            case 'h':
                                return HOUR12_STRATEGY;
                            case 'k':
                                return HOUR24_OF_DAY_STRATEGY;
                            case 'm':
                                return MINUTE_STRATEGY;
                            case 's':
                                return SECOND_STRATEGY;
                            case 'u':
                                return DAY_OF_WEEK_STRATEGY;
                            case 'w':
                                return WEEK_OF_YEAR_STRATEGY;
                            default:
                                switch (f) {
                                    case 'W':
                                        return WEEK_OF_MONTH_STRATEGY;
                                    case 'X':
                                        return ISO8601TimeZoneStrategy.getStrategy(width);
                                    case 'Y':
                                        break;
                                    case 'Z':
                                        if (width == 2) {
                                            return ISO8601TimeZoneStrategy.ISO_8601_3_STRATEGY;
                                        }
                                        break;
                                    default:
                                        throw new IllegalArgumentException("Format '" + f + "' not supported");
                                }
                                break;
                        }
                        break;
                }
            }
            return getLocaleSpecificStrategy(15, definingCalendar);
        }
        return width > 2 ? LITERAL_YEAR_STRATEGY : ABBREVIATED_YEAR_STRATEGY;
    }

    private static ConcurrentMap<Locale, Strategy> getCache(int field) {
        ConcurrentMap<Locale, Strategy> concurrentMap;
        synchronized (caches) {
            if (caches[field] == null) {
                caches[field] = new ConcurrentHashMap(3);
            }
            concurrentMap = caches[field];
        }
        return concurrentMap;
    }

    private Strategy getLocaleSpecificStrategy(int field, Calendar definingCalendar) {
        ConcurrentMap<Locale, Strategy> cache = getCache(field);
        Strategy strategy = cache.get(this.locale);
        if (strategy == null) {
            strategy = field == 15 ? new TimeZoneStrategy(this.locale) : new CaseInsensitiveTextStrategy(field, definingCalendar, this.locale);
            Strategy inCache = cache.putIfAbsent(this.locale, strategy);
            if (inCache != null) {
                return inCache;
            }
        }
        return strategy;
    }

    private static class CopyQuotedStrategy extends Strategy {
        private final String formatField;

        CopyQuotedStrategy(String formatField) {
            super();
            this.formatField = formatField;
        }

        @Override // org.apache.commons.lang3.time.FastDateParser.Strategy
        boolean isNumber() {
            return false;
        }

        @Override // org.apache.commons.lang3.time.FastDateParser.Strategy
        boolean parse(FastDateParser parser, Calendar calendar, String source, ParsePosition pos, int maxWidth) {
            for (int idx = 0; idx < this.formatField.length(); idx++) {
                int sIdx = pos.getIndex() + idx;
                if (sIdx == source.length()) {
                    pos.setErrorIndex(sIdx);
                    return false;
                }
                if (this.formatField.charAt(idx) != source.charAt(sIdx)) {
                    pos.setErrorIndex(sIdx);
                    return false;
                }
            }
            pos.setIndex(this.formatField.length() + pos.getIndex());
            return true;
        }
    }

    private static class CaseInsensitiveTextStrategy extends PatternStrategy {
        private final int field;
        private final Map<String, Integer> lKeyValues;
        final Locale locale;

        CaseInsensitiveTextStrategy(int field, Calendar definingCalendar, Locale locale) {
            super();
            this.field = field;
            this.locale = locale;
            StringBuilder regex = new StringBuilder();
            regex.append("((?iu)");
            this.lKeyValues = FastDateParser.appendDisplayNames(definingCalendar, locale, field, regex);
            regex.setLength(regex.length() - 1);
            regex.append(")");
            createPattern(regex);
        }

        @Override // org.apache.commons.lang3.time.FastDateParser.PatternStrategy
        void setCalendar(FastDateParser parser, Calendar cal, String value) {
            String lowerCase = value.toLowerCase(this.locale);
            Integer iVal = this.lKeyValues.get(lowerCase);
            if (iVal == null) {
                iVal = this.lKeyValues.get(lowerCase + ClassUtils.PACKAGE_SEPARATOR_CHAR);
            }
            cal.set(this.field, iVal.intValue());
        }
    }

    private static class NumberStrategy extends Strategy {
        private final int field;

        NumberStrategy(int field) {
            super();
            this.field = field;
        }

        @Override // org.apache.commons.lang3.time.FastDateParser.Strategy
        boolean isNumber() {
            return true;
        }

        @Override // org.apache.commons.lang3.time.FastDateParser.Strategy
        boolean parse(FastDateParser parser, Calendar calendar, String source, ParsePosition pos, int maxWidth) {
            int idx = pos.getIndex();
            int last = source.length();
            if (maxWidth == 0) {
                while (idx < last) {
                    char c = source.charAt(idx);
                    if (!Character.isWhitespace(c)) {
                        break;
                    }
                    idx++;
                }
                pos.setIndex(idx);
            } else {
                int end = idx + maxWidth;
                if (last > end) {
                    last = end;
                }
            }
            while (idx < last) {
                char c2 = source.charAt(idx);
                if (!Character.isDigit(c2)) {
                    break;
                }
                idx++;
            }
            if (pos.getIndex() == idx) {
                pos.setErrorIndex(idx);
                return false;
            }
            int value = Integer.parseInt(source.substring(pos.getIndex(), idx));
            pos.setIndex(idx);
            calendar.set(this.field, modify(parser, value));
            return true;
        }

        int modify(FastDateParser parser, int iValue) {
            return iValue;
        }
    }

    static class TimeZoneStrategy extends PatternStrategy {
        private static final String GMT_OPTION = "GMT[+-]\\d{1,2}:\\d{2}";
        private static final int ID = 0;
        private static final String RFC_822_TIME_ZONE = "[+-]\\d{4}";
        private final Locale locale;
        private final Map<String, TzInfo> tzNames;

        private static class TzInfo {
            int dstOffset;
            TimeZone zone;

            TzInfo(TimeZone tz, boolean useDst) {
                this.zone = tz;
                this.dstOffset = useDst ? tz.getDSTSavings() : 0;
            }
        }

        TimeZoneStrategy(Locale locale) {
            super();
            this.tzNames = new HashMap();
            this.locale = locale;
            StringBuilder sb = new StringBuilder();
            sb.append("((?iu)[+-]\\d{4}|GMT[+-]\\d{1,2}:\\d{2}");
            Set<String> sorted = new TreeSet<>((Comparator<? super String>) FastDateParser.LONGER_FIRST_LOWERCASE);
            String[][] zones = DateFormatSymbols.getInstance(locale).getZoneStrings();
            for (String[] zoneNames : zones) {
                String tzId = zoneNames[0];
                if (!tzId.equalsIgnoreCase(TimeZones.GMT_ID)) {
                    TimeZone tz = TimeZone.getTimeZone(tzId);
                    TzInfo standard = new TzInfo(tz, false);
                    TzInfo tzInfo = standard;
                    for (int i = 1; i < zoneNames.length; i++) {
                        if (i == 3) {
                            tzInfo = new TzInfo(tz, true);
                        } else if (i == 5) {
                            tzInfo = standard;
                        }
                        if (zoneNames[i] != null) {
                            String key = zoneNames[i].toLowerCase(locale);
                            if (sorted.add(key)) {
                                this.tzNames.put(key, tzInfo);
                            }
                        }
                    }
                }
            }
            for (String zoneName : sorted) {
                sb.append('|');
                FastDateParser.simpleQuote(sb, zoneName);
            }
            sb.append(")");
            createPattern(sb);
        }

        @Override // org.apache.commons.lang3.time.FastDateParser.PatternStrategy
        void setCalendar(FastDateParser parser, Calendar cal, String timeZone) {
            TimeZone tz = FastTimeZone.getGmtTimeZone(timeZone);
            if (tz != null) {
                cal.setTimeZone(tz);
                return;
            }
            String lowerCase = timeZone.toLowerCase(this.locale);
            TzInfo tzInfo = this.tzNames.get(lowerCase);
            if (tzInfo == null) {
                tzInfo = this.tzNames.get(lowerCase + ClassUtils.PACKAGE_SEPARATOR_CHAR);
            }
            cal.set(16, tzInfo.dstOffset);
            cal.set(15, tzInfo.zone.getRawOffset());
        }
    }

    private static class ISO8601TimeZoneStrategy extends PatternStrategy {
        private static final Strategy ISO_8601_1_STRATEGY = new ISO8601TimeZoneStrategy("(Z|(?:[+-]\\d{2}))");
        private static final Strategy ISO_8601_2_STRATEGY = new ISO8601TimeZoneStrategy("(Z|(?:[+-]\\d{2}\\d{2}))");
        private static final Strategy ISO_8601_3_STRATEGY = new ISO8601TimeZoneStrategy("(Z|(?:[+-]\\d{2}(?::)\\d{2}))");

        ISO8601TimeZoneStrategy(String pattern) {
            super();
            createPattern(pattern);
        }

        @Override // org.apache.commons.lang3.time.FastDateParser.PatternStrategy
        void setCalendar(FastDateParser parser, Calendar cal, String value) {
            cal.setTimeZone(FastTimeZone.getGmtTimeZone(value));
        }

        static Strategy getStrategy(int tokenLen) {
            if (tokenLen == 1) {
                return ISO_8601_1_STRATEGY;
            }
            if (tokenLen == 2) {
                return ISO_8601_2_STRATEGY;
            }
            if (tokenLen == 3) {
                return ISO_8601_3_STRATEGY;
            }
            throw new IllegalArgumentException("invalid number of X");
        }
    }
}
