package org.apache.commons.lang3.time;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.text.DateFormatSymbols;
import java.text.FieldPosition;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.commons.lang3.exception.ExceptionUtils;

/* JADX INFO: loaded from: classes.dex */
public class FastDatePrinter implements DatePrinter, Serializable {
    public static final int FULL = 0;
    public static final int LONG = 1;
    private static final int MAX_DIGITS = 10;
    public static final int MEDIUM = 2;
    public static final int SHORT = 3;
    private static final ConcurrentMap<TimeZoneDisplayKey, String> cTimeZoneDisplayCache = new ConcurrentHashMap(7);
    private static final long serialVersionUID = 1;
    private final Locale mLocale;
    private transient int mMaxLengthEstimate;
    private final String mPattern;
    private transient Rule[] mRules;
    private final TimeZone mTimeZone;

    private interface NumberRule extends Rule {
        void appendTo(Appendable appendable, int i) throws IOException;
    }

    private interface Rule {
        void appendTo(Appendable appendable, Calendar calendar) throws IOException;

        int estimateLength();
    }

    protected FastDatePrinter(String pattern, TimeZone timeZone, Locale locale) {
        this.mPattern = pattern;
        this.mTimeZone = timeZone;
        this.mLocale = locale;
        init();
    }

    private void init() {
        List<Rule> rulesList = parsePattern();
        Rule[] ruleArr = (Rule[]) rulesList.toArray(new Rule[0]);
        this.mRules = ruleArr;
        int len = 0;
        int i = ruleArr.length;
        while (true) {
            i--;
            if (i >= 0) {
                len += this.mRules[i].estimateLength();
            } else {
                this.mMaxLengthEstimate = len;
                return;
            }
        }
    }

    protected List<Rule> parsePattern() {
        String[] ERAs;
        String[] weekdays;
        boolean z;
        Rule rule;
        DateFormatSymbols symbols = new DateFormatSymbols(this.mLocale);
        List<Rule> rules = new ArrayList<>();
        String[] ERAs2 = symbols.getEras();
        String[] months = symbols.getMonths();
        String[] shortMonths = symbols.getShortMonths();
        String[] weekdays2 = symbols.getWeekdays();
        String[] shortWeekdays = symbols.getShortWeekdays();
        String[] AmPmStrings = symbols.getAmPmStrings();
        int length = this.mPattern.length();
        int[] indexRef = new int[1];
        int i = 0;
        while (i < length) {
            indexRef[0] = i;
            String token = parseToken(this.mPattern, indexRef);
            int i2 = indexRef[0];
            int tokenLen = token.length();
            if (tokenLen != 0) {
                char c = token.charAt(0);
                DateFormatSymbols symbols2 = symbols;
                if (c == 'y') {
                    ERAs = ERAs2;
                    weekdays = weekdays2;
                } else {
                    if (c == 'z') {
                        weekdays = weekdays2;
                        if (tokenLen >= 4) {
                            ERAs = ERAs2;
                            rule = new TimeZoneNameRule(this.mTimeZone, this.mLocale, 1);
                            z = true;
                        } else {
                            ERAs = ERAs2;
                            rule = new TimeZoneNameRule(this.mTimeZone, this.mLocale, 0);
                            z = true;
                        }
                    } else {
                        switch (c) {
                            case '\'':
                                weekdays = weekdays2;
                                String sub = token.substring(1);
                                if (sub.length() == 1) {
                                    rule = new CharacterLiteral(sub.charAt(0));
                                    ERAs = ERAs2;
                                    z = true;
                                    continue;
                                } else {
                                    rule = new StringLiteral(sub);
                                    ERAs = ERAs2;
                                    z = true;
                                }
                                break;
                            case 'K':
                                weekdays = weekdays2;
                                rule = selectNumberRule(10, tokenLen);
                                ERAs = ERAs2;
                                z = true;
                                continue;
                            case 'M':
                                weekdays = weekdays2;
                                if (tokenLen >= 4) {
                                    rule = new TextField(2, months);
                                    ERAs = ERAs2;
                                    z = true;
                                    continue;
                                } else if (tokenLen == 3) {
                                    rule = new TextField(2, shortMonths);
                                    ERAs = ERAs2;
                                    z = true;
                                } else if (tokenLen == 2) {
                                    rule = TwoDigitMonthField.INSTANCE;
                                    ERAs = ERAs2;
                                    z = true;
                                } else {
                                    rule = UnpaddedMonthField.INSTANCE;
                                    ERAs = ERAs2;
                                    z = true;
                                }
                                break;
                            case 'S':
                                weekdays = weekdays2;
                                rule = selectNumberRule(14, tokenLen);
                                ERAs = ERAs2;
                                z = true;
                                continue;
                            case 'a':
                                weekdays = weekdays2;
                                rule = new TextField(9, AmPmStrings);
                                ERAs = ERAs2;
                                z = true;
                                continue;
                            case 'd':
                                weekdays = weekdays2;
                                rule = selectNumberRule(5, tokenLen);
                                ERAs = ERAs2;
                                z = true;
                                continue;
                            case 'h':
                                weekdays = weekdays2;
                                rule = new TwelveHourField(selectNumberRule(10, tokenLen));
                                ERAs = ERAs2;
                                z = true;
                                continue;
                            case 'k':
                                weekdays = weekdays2;
                                rule = new TwentyFourHourField(selectNumberRule(11, tokenLen));
                                ERAs = ERAs2;
                                z = true;
                                continue;
                            case 'm':
                                weekdays = weekdays2;
                                rule = selectNumberRule(12, tokenLen);
                                ERAs = ERAs2;
                                z = true;
                                continue;
                            case 's':
                                weekdays = weekdays2;
                                rule = selectNumberRule(13, tokenLen);
                                ERAs = ERAs2;
                                z = true;
                                continue;
                            case 'u':
                                weekdays = weekdays2;
                                rule = new DayInWeekField(selectNumberRule(7, tokenLen));
                                ERAs = ERAs2;
                                z = true;
                                continue;
                            case 'w':
                                weekdays = weekdays2;
                                rule = selectNumberRule(3, tokenLen);
                                ERAs = ERAs2;
                                z = true;
                                continue;
                            default:
                                switch (c) {
                                    case 'D':
                                        weekdays = weekdays2;
                                        rule = selectNumberRule(6, tokenLen);
                                        ERAs = ERAs2;
                                        z = true;
                                        continue;
                                    case 'E':
                                        weekdays = weekdays2;
                                        rule = new TextField(7, tokenLen < 4 ? shortWeekdays : weekdays);
                                        ERAs = ERAs2;
                                        z = true;
                                        continue;
                                    case 'F':
                                        weekdays = weekdays2;
                                        rule = selectNumberRule(8, tokenLen);
                                        ERAs = ERAs2;
                                        z = true;
                                        continue;
                                    case 'G':
                                        weekdays = weekdays2;
                                        rule = new TextField(0, ERAs2);
                                        ERAs = ERAs2;
                                        z = true;
                                        continue;
                                    case 'H':
                                        weekdays = weekdays2;
                                        rule = selectNumberRule(11, tokenLen);
                                        ERAs = ERAs2;
                                        z = true;
                                        continue;
                                    default:
                                        switch (c) {
                                            case 'W':
                                                weekdays = weekdays2;
                                                rule = selectNumberRule(4, tokenLen);
                                                ERAs = ERAs2;
                                                z = true;
                                                continue;
                                            case 'X':
                                                weekdays = weekdays2;
                                                rule = Iso8601_Rule.getRule(tokenLen);
                                                ERAs = ERAs2;
                                                z = true;
                                                continue;
                                            case 'Y':
                                                weekdays = weekdays2;
                                                ERAs = ERAs2;
                                                break;
                                            case 'Z':
                                                weekdays = weekdays2;
                                                if (tokenLen == 1) {
                                                    rule = TimeZoneNumberRule.INSTANCE_NO_COLON;
                                                    ERAs = ERAs2;
                                                    z = true;
                                                    continue;
                                                } else if (tokenLen == 2) {
                                                    rule = Iso8601_Rule.ISO8601_HOURS_COLON_MINUTES;
                                                    ERAs = ERAs2;
                                                    z = true;
                                                } else {
                                                    rule = TimeZoneNumberRule.INSTANCE_COLON;
                                                    ERAs = ERAs2;
                                                    z = true;
                                                }
                                                break;
                                            default:
                                                throw new IllegalArgumentException("Illegal pattern component: " + token);
                                        }
                                        break;
                                }
                                break;
                        }
                    }
                    rules.add(rule);
                    i = i2 + 1;
                    symbols = symbols2;
                    ERAs2 = ERAs;
                    weekdays2 = weekdays;
                }
                if (tokenLen != 2) {
                    z = true;
                    rule = selectNumberRule(1, tokenLen >= 4 ? tokenLen : 4);
                } else {
                    rule = TwoDigitYearField.INSTANCE;
                    z = true;
                }
                if (c == 'Y') {
                    rule = new WeekYear((NumberRule) rule);
                }
                rules.add(rule);
                i = i2 + 1;
                symbols = symbols2;
                ERAs2 = ERAs;
                weekdays2 = weekdays;
            } else {
                return rules;
            }
        }
        return rules;
    }

    protected String parseToken(String pattern, int[] indexRef) {
        boolean z;
        StringBuilder buf = new StringBuilder();
        int i = indexRef[0];
        int length = pattern.length();
        char c = pattern.charAt(i);
        if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')) {
            buf.append(c);
            while (i + 1 < length) {
                char peek = pattern.charAt(i + 1);
                if (peek != c) {
                    break;
                }
                buf.append(c);
                i++;
            }
        } else {
            buf.append('\'');
            boolean inLiteral = false;
            while (i < length) {
                char c2 = pattern.charAt(i);
                if (c2 == '\'') {
                    if (i + 1 < length && pattern.charAt(i + 1) == '\'') {
                        i++;
                        buf.append(c2);
                    } else {
                        if (inLiteral) {
                            z = false;
                        } else {
                            z = true;
                        }
                        inLiteral = z;
                    }
                } else {
                    if (!inLiteral && ((c2 >= 'A' && c2 <= 'Z') || (c2 >= 'a' && c2 <= 'z'))) {
                        i--;
                        break;
                    }
                    buf.append(c2);
                }
                i++;
            }
        }
        indexRef[0] = i;
        return buf.toString();
    }

    protected NumberRule selectNumberRule(int field, int padding) {
        if (padding == 1) {
            return new UnpaddedNumberField(field);
        }
        if (padding == 2) {
            return new TwoDigitNumberField(field);
        }
        return new PaddedNumberField(field, padding);
    }

    @Override // org.apache.commons.lang3.time.DatePrinter
    @Deprecated
    public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
        if (obj instanceof Date) {
            return format((Date) obj, toAppendTo);
        }
        if (obj instanceof Calendar) {
            return format((Calendar) obj, toAppendTo);
        }
        if (obj instanceof Long) {
            return format(((Long) obj).longValue(), toAppendTo);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Unknown class: ");
        sb.append(obj == null ? "<null>" : obj.getClass().getName());
        throw new IllegalArgumentException(sb.toString());
    }

    String format(Object obj) {
        if (obj instanceof Date) {
            return format((Date) obj);
        }
        if (obj instanceof Calendar) {
            return format((Calendar) obj);
        }
        if (obj instanceof Long) {
            return format(((Long) obj).longValue());
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Unknown class: ");
        sb.append(obj == null ? "<null>" : obj.getClass().getName());
        throw new IllegalArgumentException(sb.toString());
    }

    @Override // org.apache.commons.lang3.time.DatePrinter
    public String format(long millis) {
        Calendar c = newCalendar();
        c.setTimeInMillis(millis);
        return applyRulesToString(c);
    }

    private String applyRulesToString(Calendar c) {
        return ((StringBuilder) applyRules(c, new StringBuilder(this.mMaxLengthEstimate))).toString();
    }

    private Calendar newCalendar() {
        return Calendar.getInstance(this.mTimeZone, this.mLocale);
    }

    @Override // org.apache.commons.lang3.time.DatePrinter
    public String format(Date date) {
        Calendar c = newCalendar();
        c.setTime(date);
        return applyRulesToString(c);
    }

    @Override // org.apache.commons.lang3.time.DatePrinter
    public String format(Calendar calendar) {
        return ((StringBuilder) format(calendar, new StringBuilder(this.mMaxLengthEstimate))).toString();
    }

    @Override // org.apache.commons.lang3.time.DatePrinter
    public StringBuffer format(long millis, StringBuffer buf) {
        Calendar c = newCalendar();
        c.setTimeInMillis(millis);
        return (StringBuffer) applyRules(c, buf);
    }

    @Override // org.apache.commons.lang3.time.DatePrinter
    public StringBuffer format(Date date, StringBuffer buf) {
        Calendar c = newCalendar();
        c.setTime(date);
        return (StringBuffer) applyRules(c, buf);
    }

    @Override // org.apache.commons.lang3.time.DatePrinter
    public StringBuffer format(Calendar calendar, StringBuffer buf) {
        return format(calendar.getTime(), buf);
    }

    @Override // org.apache.commons.lang3.time.DatePrinter
    public <B extends Appendable> B format(long j, B b) {
        Calendar calendarNewCalendar = newCalendar();
        calendarNewCalendar.setTimeInMillis(j);
        return (B) applyRules(calendarNewCalendar, b);
    }

    @Override // org.apache.commons.lang3.time.DatePrinter
    public <B extends Appendable> B format(Date date, B b) {
        Calendar calendarNewCalendar = newCalendar();
        calendarNewCalendar.setTime(date);
        return (B) applyRules(calendarNewCalendar, b);
    }

    @Override // org.apache.commons.lang3.time.DatePrinter
    public <B extends Appendable> B format(Calendar calendar, B b) {
        if (!calendar.getTimeZone().equals(this.mTimeZone)) {
            calendar = (Calendar) calendar.clone();
            calendar.setTimeZone(this.mTimeZone);
        }
        return (B) applyRules(calendar, b);
    }

    @Deprecated
    protected StringBuffer applyRules(Calendar calendar, StringBuffer buf) {
        return (StringBuffer) applyRules(calendar, buf);
    }

    private <B extends Appendable> B applyRules(Calendar calendar, B buf) {
        try {
            for (Rule rule : this.mRules) {
                rule.appendTo(buf, calendar);
            }
        } catch (IOException ioe) {
            ExceptionUtils.rethrow(ioe);
        }
        return buf;
    }

    @Override // org.apache.commons.lang3.time.DatePrinter
    public String getPattern() {
        return this.mPattern;
    }

    @Override // org.apache.commons.lang3.time.DatePrinter
    public TimeZone getTimeZone() {
        return this.mTimeZone;
    }

    @Override // org.apache.commons.lang3.time.DatePrinter
    public Locale getLocale() {
        return this.mLocale;
    }

    public int getMaxLengthEstimate() {
        return this.mMaxLengthEstimate;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof FastDatePrinter)) {
            return false;
        }
        FastDatePrinter other = (FastDatePrinter) obj;
        return this.mPattern.equals(other.mPattern) && this.mTimeZone.equals(other.mTimeZone) && this.mLocale.equals(other.mLocale);
    }

    public int hashCode() {
        return this.mPattern.hashCode() + ((this.mTimeZone.hashCode() + (this.mLocale.hashCode() * 13)) * 13);
    }

    public String toString() {
        return "FastDatePrinter[" + this.mPattern + "," + this.mLocale + "," + this.mTimeZone.getID() + "]";
    }

    private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
        in.defaultReadObject();
        init();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void appendDigits(Appendable buffer, int value) throws IOException {
        buffer.append((char) ((value / 10) + 48));
        buffer.append((char) ((value % 10) + 48));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void appendFullDigits(Appendable buffer, int value, int minFieldWidth) throws IOException {
        if (value < 10000) {
            int nDigits = 4;
            if (value < 1000) {
                nDigits = 4 - 1;
                if (value < 100) {
                    nDigits--;
                    if (value < 10) {
                        nDigits--;
                    }
                }
            }
            for (int i = minFieldWidth - nDigits; i > 0; i--) {
                buffer.append('0');
            }
            if (nDigits != 1) {
                if (nDigits != 2) {
                    if (nDigits != 3) {
                        if (nDigits == 4) {
                            buffer.append((char) ((value / 1000) + 48));
                            value %= 1000;
                        } else {
                            return;
                        }
                    }
                    if (value >= 100) {
                        buffer.append((char) ((value / 100) + 48));
                        value %= 100;
                    } else {
                        buffer.append('0');
                    }
                }
                if (value >= 10) {
                    buffer.append((char) ((value / 10) + 48));
                    value %= 10;
                } else {
                    buffer.append('0');
                }
            }
            buffer.append((char) (value + 48));
            return;
        }
        char[] work = new char[10];
        int digit = 0;
        while (value != 0) {
            work[digit] = (char) ((value % 10) + 48);
            value /= 10;
            digit++;
        }
        while (digit < minFieldWidth) {
            buffer.append('0');
            minFieldWidth--;
        }
        while (true) {
            digit--;
            if (digit >= 0) {
                buffer.append(work[digit]);
            } else {
                return;
            }
        }
    }

    private static class CharacterLiteral implements Rule {
        private final char mValue;

        CharacterLiteral(char value) {
            this.mValue = value;
        }

        @Override // org.apache.commons.lang3.time.FastDatePrinter.Rule
        public int estimateLength() {
            return 1;
        }

        @Override // org.apache.commons.lang3.time.FastDatePrinter.Rule
        public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
            buffer.append(this.mValue);
        }
    }

    private static class StringLiteral implements Rule {
        private final String mValue;

        StringLiteral(String value) {
            this.mValue = value;
        }

        @Override // org.apache.commons.lang3.time.FastDatePrinter.Rule
        public int estimateLength() {
            return this.mValue.length();
        }

        @Override // org.apache.commons.lang3.time.FastDatePrinter.Rule
        public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
            buffer.append(this.mValue);
        }
    }

    private static class TextField implements Rule {
        private final int mField;
        private final String[] mValues;

        TextField(int field, String[] values) {
            this.mField = field;
            this.mValues = values;
        }

        @Override // org.apache.commons.lang3.time.FastDatePrinter.Rule
        public int estimateLength() {
            int max = 0;
            int i = this.mValues.length;
            while (true) {
                i--;
                if (i >= 0) {
                    int len = this.mValues[i].length();
                    if (len > max) {
                        max = len;
                    }
                } else {
                    return max;
                }
            }
        }

        @Override // org.apache.commons.lang3.time.FastDatePrinter.Rule
        public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
            buffer.append(this.mValues[calendar.get(this.mField)]);
        }
    }

    private static class UnpaddedNumberField implements NumberRule {
        private final int mField;

        UnpaddedNumberField(int field) {
            this.mField = field;
        }

        @Override // org.apache.commons.lang3.time.FastDatePrinter.Rule
        public int estimateLength() {
            return 4;
        }

        @Override // org.apache.commons.lang3.time.FastDatePrinter.Rule
        public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
            appendTo(buffer, calendar.get(this.mField));
        }

        @Override // org.apache.commons.lang3.time.FastDatePrinter.NumberRule
        public final void appendTo(Appendable buffer, int value) throws IOException {
            if (value < 10) {
                buffer.append((char) (value + 48));
            } else if (value < 100) {
                FastDatePrinter.appendDigits(buffer, value);
            } else {
                FastDatePrinter.appendFullDigits(buffer, value, 1);
            }
        }
    }

    private static class UnpaddedMonthField implements NumberRule {
        static final UnpaddedMonthField INSTANCE = new UnpaddedMonthField();

        UnpaddedMonthField() {
        }

        @Override // org.apache.commons.lang3.time.FastDatePrinter.Rule
        public int estimateLength() {
            return 2;
        }

        @Override // org.apache.commons.lang3.time.FastDatePrinter.Rule
        public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
            appendTo(buffer, calendar.get(2) + 1);
        }

        @Override // org.apache.commons.lang3.time.FastDatePrinter.NumberRule
        public final void appendTo(Appendable buffer, int value) throws IOException {
            if (value >= 10) {
                FastDatePrinter.appendDigits(buffer, value);
            } else {
                buffer.append((char) (value + 48));
            }
        }
    }

    private static class PaddedNumberField implements NumberRule {
        private final int mField;
        private final int mSize;

        PaddedNumberField(int field, int size) {
            if (size < 3) {
                throw new IllegalArgumentException();
            }
            this.mField = field;
            this.mSize = size;
        }

        @Override // org.apache.commons.lang3.time.FastDatePrinter.Rule
        public int estimateLength() {
            return this.mSize;
        }

        @Override // org.apache.commons.lang3.time.FastDatePrinter.Rule
        public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
            appendTo(buffer, calendar.get(this.mField));
        }

        @Override // org.apache.commons.lang3.time.FastDatePrinter.NumberRule
        public final void appendTo(Appendable buffer, int value) throws IOException {
            FastDatePrinter.appendFullDigits(buffer, value, this.mSize);
        }
    }

    private static class TwoDigitNumberField implements NumberRule {
        private final int mField;

        TwoDigitNumberField(int field) {
            this.mField = field;
        }

        @Override // org.apache.commons.lang3.time.FastDatePrinter.Rule
        public int estimateLength() {
            return 2;
        }

        @Override // org.apache.commons.lang3.time.FastDatePrinter.Rule
        public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
            appendTo(buffer, calendar.get(this.mField));
        }

        @Override // org.apache.commons.lang3.time.FastDatePrinter.NumberRule
        public final void appendTo(Appendable buffer, int value) throws IOException {
            if (value < 100) {
                FastDatePrinter.appendDigits(buffer, value);
            } else {
                FastDatePrinter.appendFullDigits(buffer, value, 2);
            }
        }
    }

    private static class TwoDigitYearField implements NumberRule {
        static final TwoDigitYearField INSTANCE = new TwoDigitYearField();

        TwoDigitYearField() {
        }

        @Override // org.apache.commons.lang3.time.FastDatePrinter.Rule
        public int estimateLength() {
            return 2;
        }

        @Override // org.apache.commons.lang3.time.FastDatePrinter.Rule
        public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
            appendTo(buffer, calendar.get(1) % 100);
        }

        @Override // org.apache.commons.lang3.time.FastDatePrinter.NumberRule
        public final void appendTo(Appendable buffer, int value) throws IOException {
            FastDatePrinter.appendDigits(buffer, value);
        }
    }

    private static class TwoDigitMonthField implements NumberRule {
        static final TwoDigitMonthField INSTANCE = new TwoDigitMonthField();

        TwoDigitMonthField() {
        }

        @Override // org.apache.commons.lang3.time.FastDatePrinter.Rule
        public int estimateLength() {
            return 2;
        }

        @Override // org.apache.commons.lang3.time.FastDatePrinter.Rule
        public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
            appendTo(buffer, calendar.get(2) + 1);
        }

        @Override // org.apache.commons.lang3.time.FastDatePrinter.NumberRule
        public final void appendTo(Appendable buffer, int value) throws IOException {
            FastDatePrinter.appendDigits(buffer, value);
        }
    }

    private static class TwelveHourField implements NumberRule {
        private final NumberRule mRule;

        TwelveHourField(NumberRule rule) {
            this.mRule = rule;
        }

        @Override // org.apache.commons.lang3.time.FastDatePrinter.Rule
        public int estimateLength() {
            return this.mRule.estimateLength();
        }

        @Override // org.apache.commons.lang3.time.FastDatePrinter.Rule
        public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
            int value = calendar.get(10);
            if (value == 0) {
                value = calendar.getLeastMaximum(10) + 1;
            }
            this.mRule.appendTo(buffer, value);
        }

        @Override // org.apache.commons.lang3.time.FastDatePrinter.NumberRule
        public void appendTo(Appendable buffer, int value) throws IOException {
            this.mRule.appendTo(buffer, value);
        }
    }

    private static class TwentyFourHourField implements NumberRule {
        private final NumberRule mRule;

        TwentyFourHourField(NumberRule rule) {
            this.mRule = rule;
        }

        @Override // org.apache.commons.lang3.time.FastDatePrinter.Rule
        public int estimateLength() {
            return this.mRule.estimateLength();
        }

        @Override // org.apache.commons.lang3.time.FastDatePrinter.Rule
        public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
            int value = calendar.get(11);
            if (value == 0) {
                value = calendar.getMaximum(11) + 1;
            }
            this.mRule.appendTo(buffer, value);
        }

        @Override // org.apache.commons.lang3.time.FastDatePrinter.NumberRule
        public void appendTo(Appendable buffer, int value) throws IOException {
            this.mRule.appendTo(buffer, value);
        }
    }

    private static class DayInWeekField implements NumberRule {
        private final NumberRule mRule;

        DayInWeekField(NumberRule rule) {
            this.mRule = rule;
        }

        @Override // org.apache.commons.lang3.time.FastDatePrinter.Rule
        public int estimateLength() {
            return this.mRule.estimateLength();
        }

        @Override // org.apache.commons.lang3.time.FastDatePrinter.Rule
        public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
            int value = calendar.get(7);
            this.mRule.appendTo(buffer, value != 1 ? value - 1 : 7);
        }

        @Override // org.apache.commons.lang3.time.FastDatePrinter.NumberRule
        public void appendTo(Appendable buffer, int value) throws IOException {
            this.mRule.appendTo(buffer, value);
        }
    }

    private static class WeekYear implements NumberRule {
        private final NumberRule mRule;

        WeekYear(NumberRule rule) {
            this.mRule = rule;
        }

        @Override // org.apache.commons.lang3.time.FastDatePrinter.Rule
        public int estimateLength() {
            return this.mRule.estimateLength();
        }

        @Override // org.apache.commons.lang3.time.FastDatePrinter.Rule
        public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
            this.mRule.appendTo(buffer, calendar.getWeekYear());
        }

        @Override // org.apache.commons.lang3.time.FastDatePrinter.NumberRule
        public void appendTo(Appendable buffer, int value) throws IOException {
            this.mRule.appendTo(buffer, value);
        }
    }

    static String getTimeZoneDisplay(TimeZone tz, boolean daylight, int style, Locale locale) {
        TimeZoneDisplayKey key = new TimeZoneDisplayKey(tz, daylight, style, locale);
        String value = cTimeZoneDisplayCache.get(key);
        if (value == null) {
            String value2 = tz.getDisplayName(daylight, style, locale);
            String prior = cTimeZoneDisplayCache.putIfAbsent(key, value2);
            if (prior != null) {
                return prior;
            }
            return value2;
        }
        return value;
    }

    private static class TimeZoneNameRule implements Rule {
        private final String mDaylight;
        private final Locale mLocale;
        private final String mStandard;
        private final int mStyle;

        TimeZoneNameRule(TimeZone timeZone, Locale locale, int style) {
            this.mLocale = locale;
            this.mStyle = style;
            this.mStandard = FastDatePrinter.getTimeZoneDisplay(timeZone, false, style, locale);
            this.mDaylight = FastDatePrinter.getTimeZoneDisplay(timeZone, true, style, locale);
        }

        @Override // org.apache.commons.lang3.time.FastDatePrinter.Rule
        public int estimateLength() {
            return Math.max(this.mStandard.length(), this.mDaylight.length());
        }

        @Override // org.apache.commons.lang3.time.FastDatePrinter.Rule
        public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
            TimeZone zone = calendar.getTimeZone();
            if (calendar.get(16) == 0) {
                buffer.append(FastDatePrinter.getTimeZoneDisplay(zone, false, this.mStyle, this.mLocale));
            } else {
                buffer.append(FastDatePrinter.getTimeZoneDisplay(zone, true, this.mStyle, this.mLocale));
            }
        }
    }

    private static class TimeZoneNumberRule implements Rule {
        static final TimeZoneNumberRule INSTANCE_COLON = new TimeZoneNumberRule(true);
        static final TimeZoneNumberRule INSTANCE_NO_COLON = new TimeZoneNumberRule(false);
        final boolean mColon;

        TimeZoneNumberRule(boolean colon) {
            this.mColon = colon;
        }

        @Override // org.apache.commons.lang3.time.FastDatePrinter.Rule
        public int estimateLength() {
            return 5;
        }

        @Override // org.apache.commons.lang3.time.FastDatePrinter.Rule
        public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
            int offset = calendar.get(15) + calendar.get(16);
            if (offset < 0) {
                buffer.append('-');
                offset = -offset;
            } else {
                buffer.append('+');
            }
            int hours = offset / 3600000;
            FastDatePrinter.appendDigits(buffer, hours);
            if (this.mColon) {
                buffer.append(':');
            }
            int minutes = (offset / 60000) - (hours * 60);
            FastDatePrinter.appendDigits(buffer, minutes);
        }
    }

    private static class Iso8601_Rule implements Rule {
        final int length;
        static final Iso8601_Rule ISO8601_HOURS = new Iso8601_Rule(3);
        static final Iso8601_Rule ISO8601_HOURS_MINUTES = new Iso8601_Rule(5);
        static final Iso8601_Rule ISO8601_HOURS_COLON_MINUTES = new Iso8601_Rule(6);

        static Iso8601_Rule getRule(int tokenLen) {
            if (tokenLen == 1) {
                return ISO8601_HOURS;
            }
            if (tokenLen == 2) {
                return ISO8601_HOURS_MINUTES;
            }
            if (tokenLen == 3) {
                return ISO8601_HOURS_COLON_MINUTES;
            }
            throw new IllegalArgumentException("invalid number of X");
        }

        Iso8601_Rule(int length) {
            this.length = length;
        }

        @Override // org.apache.commons.lang3.time.FastDatePrinter.Rule
        public int estimateLength() {
            return this.length;
        }

        @Override // org.apache.commons.lang3.time.FastDatePrinter.Rule
        public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
            int offset = calendar.get(15) + calendar.get(16);
            if (offset == 0) {
                buffer.append("Z");
                return;
            }
            if (offset < 0) {
                buffer.append('-');
                offset = -offset;
            } else {
                buffer.append('+');
            }
            int hours = offset / 3600000;
            FastDatePrinter.appendDigits(buffer, hours);
            int i = this.length;
            if (i < 5) {
                return;
            }
            if (i == 6) {
                buffer.append(':');
            }
            int minutes = (offset / 60000) - (hours * 60);
            FastDatePrinter.appendDigits(buffer, minutes);
        }
    }

    private static class TimeZoneDisplayKey {
        private final Locale mLocale;
        private final int mStyle;
        private final TimeZone mTimeZone;

        TimeZoneDisplayKey(TimeZone timeZone, boolean daylight, int style, Locale locale) {
            this.mTimeZone = timeZone;
            if (daylight) {
                this.mStyle = Integer.MIN_VALUE | style;
            } else {
                this.mStyle = style;
            }
            this.mLocale = locale;
        }

        public int hashCode() {
            return (((this.mStyle * 31) + this.mLocale.hashCode()) * 31) + this.mTimeZone.hashCode();
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof TimeZoneDisplayKey)) {
                return false;
            }
            TimeZoneDisplayKey other = (TimeZoneDisplayKey) obj;
            return this.mTimeZone.equals(other.mTimeZone) && this.mStyle == other.mStyle && this.mLocale.equals(other.mLocale);
        }
    }
}
