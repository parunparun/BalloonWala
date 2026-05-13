package org.apache.commons.lang3.time;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/* JADX INFO: loaded from: classes.dex */
public class FastDateFormat extends Format implements DateParser, DatePrinter {
    public static final int FULL = 0;
    public static final int LONG = 1;
    public static final int MEDIUM = 2;
    public static final int SHORT = 3;
    private static final FormatCache<FastDateFormat> cache = new FormatCache<FastDateFormat>() { // from class: org.apache.commons.lang3.time.FastDateFormat.1
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.apache.commons.lang3.time.FormatCache
        public FastDateFormat createInstance(String pattern, TimeZone timeZone, Locale locale) {
            return new FastDateFormat(pattern, timeZone, locale);
        }
    };
    private static final long serialVersionUID = 2;
    private final FastDateParser parser;
    private final FastDatePrinter printer;

    public static FastDateFormat getInstance() {
        return (FastDateFormat) cache.getInstance();
    }

    public static FastDateFormat getInstance(String pattern) {
        return (FastDateFormat) cache.getInstance(pattern, null, null);
    }

    public static FastDateFormat getInstance(String pattern, TimeZone timeZone) {
        return (FastDateFormat) cache.getInstance(pattern, timeZone, null);
    }

    public static FastDateFormat getInstance(String pattern, Locale locale) {
        return (FastDateFormat) cache.getInstance(pattern, null, locale);
    }

    public static FastDateFormat getInstance(String pattern, TimeZone timeZone, Locale locale) {
        return (FastDateFormat) cache.getInstance(pattern, timeZone, locale);
    }

    public static FastDateFormat getDateInstance(int style) {
        return (FastDateFormat) cache.getDateInstance(style, null, null);
    }

    public static FastDateFormat getDateInstance(int style, Locale locale) {
        return (FastDateFormat) cache.getDateInstance(style, null, locale);
    }

    public static FastDateFormat getDateInstance(int style, TimeZone timeZone) {
        return (FastDateFormat) cache.getDateInstance(style, timeZone, null);
    }

    public static FastDateFormat getDateInstance(int style, TimeZone timeZone, Locale locale) {
        return (FastDateFormat) cache.getDateInstance(style, timeZone, locale);
    }

    public static FastDateFormat getTimeInstance(int style) {
        return (FastDateFormat) cache.getTimeInstance(style, null, null);
    }

    public static FastDateFormat getTimeInstance(int style, Locale locale) {
        return (FastDateFormat) cache.getTimeInstance(style, null, locale);
    }

    public static FastDateFormat getTimeInstance(int style, TimeZone timeZone) {
        return (FastDateFormat) cache.getTimeInstance(style, timeZone, null);
    }

    public static FastDateFormat getTimeInstance(int style, TimeZone timeZone, Locale locale) {
        return (FastDateFormat) cache.getTimeInstance(style, timeZone, locale);
    }

    public static FastDateFormat getDateTimeInstance(int dateStyle, int timeStyle) {
        return (FastDateFormat) cache.getDateTimeInstance(dateStyle, timeStyle, (TimeZone) null, (Locale) null);
    }

    public static FastDateFormat getDateTimeInstance(int dateStyle, int timeStyle, Locale locale) {
        return (FastDateFormat) cache.getDateTimeInstance(dateStyle, timeStyle, (TimeZone) null, locale);
    }

    public static FastDateFormat getDateTimeInstance(int dateStyle, int timeStyle, TimeZone timeZone) {
        return getDateTimeInstance(dateStyle, timeStyle, timeZone, null);
    }

    public static FastDateFormat getDateTimeInstance(int dateStyle, int timeStyle, TimeZone timeZone, Locale locale) {
        return (FastDateFormat) cache.getDateTimeInstance(dateStyle, timeStyle, timeZone, locale);
    }

    protected FastDateFormat(String pattern, TimeZone timeZone, Locale locale) {
        this(pattern, timeZone, locale, null);
    }

    protected FastDateFormat(String pattern, TimeZone timeZone, Locale locale, Date centuryStart) {
        this.printer = new FastDatePrinter(pattern, timeZone, locale);
        this.parser = new FastDateParser(pattern, timeZone, locale, centuryStart);
    }

    @Override // java.text.Format, org.apache.commons.lang3.time.DatePrinter
    public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
        toAppendTo.append(this.printer.format(obj));
        return toAppendTo;
    }

    @Override // org.apache.commons.lang3.time.DatePrinter
    public String format(long millis) {
        return this.printer.format(millis);
    }

    @Override // org.apache.commons.lang3.time.DatePrinter
    public String format(Date date) {
        return this.printer.format(date);
    }

    @Override // org.apache.commons.lang3.time.DatePrinter
    public String format(Calendar calendar) {
        return this.printer.format(calendar);
    }

    @Override // org.apache.commons.lang3.time.DatePrinter
    @Deprecated
    public StringBuffer format(long millis, StringBuffer buf) {
        return this.printer.format(millis, buf);
    }

    @Override // org.apache.commons.lang3.time.DatePrinter
    @Deprecated
    public StringBuffer format(Date date, StringBuffer buf) {
        return this.printer.format(date, buf);
    }

    @Override // org.apache.commons.lang3.time.DatePrinter
    @Deprecated
    public StringBuffer format(Calendar calendar, StringBuffer buf) {
        return this.printer.format(calendar, buf);
    }

    @Override // org.apache.commons.lang3.time.DatePrinter
    public <B extends Appendable> B format(long j, B b) {
        return (B) this.printer.format(j, b);
    }

    @Override // org.apache.commons.lang3.time.DatePrinter
    public <B extends Appendable> B format(Date date, B b) {
        return (B) this.printer.format(date, b);
    }

    @Override // org.apache.commons.lang3.time.DatePrinter
    public <B extends Appendable> B format(Calendar calendar, B b) {
        return (B) this.printer.format(calendar, b);
    }

    @Override // org.apache.commons.lang3.time.DateParser
    public Date parse(String source) throws ParseException {
        return this.parser.parse(source);
    }

    @Override // org.apache.commons.lang3.time.DateParser
    public Date parse(String source, ParsePosition pos) {
        return this.parser.parse(source, pos);
    }

    @Override // org.apache.commons.lang3.time.DateParser
    public boolean parse(String source, ParsePosition pos, Calendar calendar) {
        return this.parser.parse(source, pos, calendar);
    }

    @Override // java.text.Format, org.apache.commons.lang3.time.DateParser
    public Object parseObject(String source, ParsePosition pos) {
        return this.parser.parseObject(source, pos);
    }

    @Override // org.apache.commons.lang3.time.DateParser, org.apache.commons.lang3.time.DatePrinter
    public String getPattern() {
        return this.printer.getPattern();
    }

    @Override // org.apache.commons.lang3.time.DateParser, org.apache.commons.lang3.time.DatePrinter
    public TimeZone getTimeZone() {
        return this.printer.getTimeZone();
    }

    @Override // org.apache.commons.lang3.time.DateParser, org.apache.commons.lang3.time.DatePrinter
    public Locale getLocale() {
        return this.printer.getLocale();
    }

    public int getMaxLengthEstimate() {
        return this.printer.getMaxLengthEstimate();
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof FastDateFormat)) {
            return false;
        }
        FastDateFormat other = (FastDateFormat) obj;
        return this.printer.equals(other.printer);
    }

    public int hashCode() {
        return this.printer.hashCode();
    }

    public String toString() {
        return "FastDateFormat[" + this.printer.getPattern() + "," + this.printer.getLocale() + "," + this.printer.getTimeZone().getID() + "]";
    }

    @Deprecated
    protected StringBuffer applyRules(Calendar calendar, StringBuffer buf) {
        return this.printer.applyRules(calendar, buf);
    }
}
