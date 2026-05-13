package org.apache.commons.lang3.time;

import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.Validate;

/* JADX INFO: loaded from: classes.dex */
public class DateUtils {
    public static final long MILLIS_PER_DAY = 86400000;
    public static final long MILLIS_PER_HOUR = 3600000;
    public static final long MILLIS_PER_MINUTE = 60000;
    public static final long MILLIS_PER_SECOND = 1000;
    public static final int RANGE_MONTH_MONDAY = 6;
    public static final int RANGE_MONTH_SUNDAY = 5;
    public static final int RANGE_WEEK_CENTER = 4;
    public static final int RANGE_WEEK_MONDAY = 2;
    public static final int RANGE_WEEK_RELATIVE = 3;
    public static final int RANGE_WEEK_SUNDAY = 1;
    public static final int SEMI_MONTH = 1001;
    private static final int[][] fields = {new int[]{14}, new int[]{13}, new int[]{12}, new int[]{11, 10}, new int[]{5, 5, 9}, new int[]{2, 1001}, new int[]{1}, new int[]{0}};

    private enum ModifyType {
        TRUNCATE,
        ROUND,
        CEILING
    }

    public static boolean isSameDay(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            throw nullDateIllegalArgumentException();
        }
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return isSameDay(cal1, cal2);
    }

    public static boolean isSameDay(Calendar cal1, Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            throw nullDateIllegalArgumentException();
        }
        return cal1.get(0) == cal2.get(0) && cal1.get(1) == cal2.get(1) && cal1.get(6) == cal2.get(6);
    }

    public static boolean isSameInstant(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            throw nullDateIllegalArgumentException();
        }
        return date1.getTime() == date2.getTime();
    }

    public static boolean isSameInstant(Calendar cal1, Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            throw nullDateIllegalArgumentException();
        }
        return cal1.getTime().getTime() == cal2.getTime().getTime();
    }

    public static boolean isSameLocalTime(Calendar cal1, Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            throw nullDateIllegalArgumentException();
        }
        return cal1.get(14) == cal2.get(14) && cal1.get(13) == cal2.get(13) && cal1.get(12) == cal2.get(12) && cal1.get(11) == cal2.get(11) && cal1.get(6) == cal2.get(6) && cal1.get(1) == cal2.get(1) && cal1.get(0) == cal2.get(0) && cal1.getClass() == cal2.getClass();
    }

    public static Date parseDate(String str, String... parsePatterns) throws ParseException {
        return parseDate(str, null, parsePatterns);
    }

    public static Date parseDate(String str, Locale locale, String... parsePatterns) throws ParseException {
        return parseDateWithLeniency(str, locale, parsePatterns, true);
    }

    public static Date parseDateStrictly(String str, String... parsePatterns) throws ParseException {
        return parseDateStrictly(str, null, parsePatterns);
    }

    public static Date parseDateStrictly(String str, Locale locale, String... parsePatterns) throws ParseException {
        return parseDateWithLeniency(str, locale, parsePatterns, false);
    }

    private static Date parseDateWithLeniency(String str, Locale locale, String[] parsePatterns, boolean lenient) throws ParseException {
        if (str == null || parsePatterns == null) {
            throw new IllegalArgumentException("Date and Patterns must not be null");
        }
        TimeZone tz = TimeZone.getDefault();
        Locale lcl = locale == null ? Locale.getDefault() : locale;
        int i = 0;
        ParsePosition pos = new ParsePosition(0);
        Calendar calendar = Calendar.getInstance(tz, lcl);
        calendar.setLenient(lenient);
        for (String parsePattern : parsePatterns) {
            FastDateParser fdp = new FastDateParser(parsePattern, tz, lcl);
            calendar.clear();
            try {
                if (fdp.parse(str, pos, calendar) && pos.getIndex() == str.length()) {
                    return calendar.getTime();
                }
            } catch (IllegalArgumentException e) {
            }
            pos.setIndex(i);
        }
        throw new ParseException("Unable to parse the date: " + str, -1);
    }

    public static Date addYears(Date date, int amount) {
        return add(date, 1, amount);
    }

    public static Date addMonths(Date date, int amount) {
        return add(date, 2, amount);
    }

    public static Date addWeeks(Date date, int amount) {
        return add(date, 3, amount);
    }

    public static Date addDays(Date date, int amount) {
        return add(date, 5, amount);
    }

    public static Date addHours(Date date, int amount) {
        return add(date, 11, amount);
    }

    public static Date addMinutes(Date date, int amount) {
        return add(date, 12, amount);
    }

    public static Date addSeconds(Date date, int amount) {
        return add(date, 13, amount);
    }

    public static Date addMilliseconds(Date date, int amount) {
        return add(date, 14, amount);
    }

    private static Date add(Date date, int calendarField, int amount) {
        validateDateNotNull(date);
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(calendarField, amount);
        return c.getTime();
    }

    public static Date setYears(Date date, int amount) {
        return set(date, 1, amount);
    }

    public static Date setMonths(Date date, int amount) {
        return set(date, 2, amount);
    }

    public static Date setDays(Date date, int amount) {
        return set(date, 5, amount);
    }

    public static Date setHours(Date date, int amount) {
        return set(date, 11, amount);
    }

    public static Date setMinutes(Date date, int amount) {
        return set(date, 12, amount);
    }

    public static Date setSeconds(Date date, int amount) {
        return set(date, 13, amount);
    }

    public static Date setMilliseconds(Date date, int amount) {
        return set(date, 14, amount);
    }

    private static Date set(Date date, int calendarField, int amount) {
        validateDateNotNull(date);
        Calendar c = Calendar.getInstance();
        c.setLenient(false);
        c.setTime(date);
        c.set(calendarField, amount);
        return c.getTime();
    }

    public static Calendar toCalendar(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c;
    }

    public static Calendar toCalendar(Date date, TimeZone tz) {
        Calendar c = Calendar.getInstance(tz);
        c.setTime(date);
        return c;
    }

    public static Date round(Date date, int field) {
        validateDateNotNull(date);
        Calendar gval = Calendar.getInstance();
        gval.setTime(date);
        modify(gval, field, ModifyType.ROUND);
        return gval.getTime();
    }

    public static Calendar round(Calendar date, int field) {
        if (date == null) {
            throw nullDateIllegalArgumentException();
        }
        Calendar rounded = (Calendar) date.clone();
        modify(rounded, field, ModifyType.ROUND);
        return rounded;
    }

    private static IllegalArgumentException nullDateIllegalArgumentException() {
        return new IllegalArgumentException("The date must not be null");
    }

    public static Date round(Object date, int field) {
        if (date == null) {
            throw nullDateIllegalArgumentException();
        }
        if (date instanceof Date) {
            return round((Date) date, field);
        }
        if (date instanceof Calendar) {
            return round((Calendar) date, field).getTime();
        }
        throw new ClassCastException("Could not round " + date);
    }

    public static Date truncate(Date date, int field) {
        validateDateNotNull(date);
        Calendar gval = Calendar.getInstance();
        gval.setTime(date);
        modify(gval, field, ModifyType.TRUNCATE);
        return gval.getTime();
    }

    public static Calendar truncate(Calendar date, int field) {
        if (date == null) {
            throw nullDateIllegalArgumentException();
        }
        Calendar truncated = (Calendar) date.clone();
        modify(truncated, field, ModifyType.TRUNCATE);
        return truncated;
    }

    public static Date truncate(Object date, int field) {
        if (date == null) {
            throw nullDateIllegalArgumentException();
        }
        if (date instanceof Date) {
            return truncate((Date) date, field);
        }
        if (date instanceof Calendar) {
            return truncate((Calendar) date, field).getTime();
        }
        throw new ClassCastException("Could not truncate " + date);
    }

    public static Date ceiling(Date date, int field) {
        validateDateNotNull(date);
        Calendar gval = Calendar.getInstance();
        gval.setTime(date);
        modify(gval, field, ModifyType.CEILING);
        return gval.getTime();
    }

    public static Calendar ceiling(Calendar date, int field) {
        if (date == null) {
            throw nullDateIllegalArgumentException();
        }
        Calendar ceiled = (Calendar) date.clone();
        modify(ceiled, field, ModifyType.CEILING);
        return ceiled;
    }

    public static Date ceiling(Object date, int field) {
        if (date == null) {
            throw nullDateIllegalArgumentException();
        }
        if (date instanceof Date) {
            return ceiling((Date) date, field);
        }
        if (date instanceof Calendar) {
            return ceiling((Calendar) date, field).getTime();
        }
        throw new ClassCastException("Could not find ceiling of for type: " + date.getClass());
    }

    /* JADX WARN: Code restructure failed: missing block: B:60:0x00ce, code lost:
    
        r3 = 0;
        r4 = false;
     */
    /* JADX WARN: Code restructure failed: missing block: B:61:0x00d2, code lost:
    
        if (r22 == 9) goto L77;
     */
    /* JADX WARN: Code restructure failed: missing block: B:63:0x00d6, code lost:
    
        if (r22 == 1001) goto L65;
     */
    /* JADX WARN: Code restructure failed: missing block: B:66:0x00df, code lost:
    
        if (r11[0] != 5) goto L76;
     */
    /* JADX WARN: Code restructure failed: missing block: B:67:0x00e1, code lost:
    
        r2 = r21.get(5) - 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:68:0x00e9, code lost:
    
        if (r2 < 15) goto L70;
     */
    /* JADX WARN: Code restructure failed: missing block: B:69:0x00eb, code lost:
    
        r3 = r2 - 15;
     */
    /* JADX WARN: Code restructure failed: missing block: B:70:0x00ef, code lost:
    
        r3 = r2;
     */
    /* JADX WARN: Code restructure failed: missing block: B:72:0x00f1, code lost:
    
        if (r3 <= 7) goto L74;
     */
    /* JADX WARN: Code restructure failed: missing block: B:73:0x00f3, code lost:
    
        r2 = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:74:0x00f5, code lost:
    
        r2 = false;
     */
    /* JADX WARN: Code restructure failed: missing block: B:75:0x00f6, code lost:
    
        r10 = r2;
        r4 = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:78:0x0105, code lost:
    
        if (r11[0] != 11) goto L88;
     */
    /* JADX WARN: Code restructure failed: missing block: B:79:0x0107, code lost:
    
        r2 = r21.get(11);
     */
    /* JADX WARN: Code restructure failed: missing block: B:80:0x010d, code lost:
    
        if (r2 < 12) goto L82;
     */
    /* JADX WARN: Code restructure failed: missing block: B:81:0x010f, code lost:
    
        r3 = r2 - 12;
     */
    /* JADX WARN: Code restructure failed: missing block: B:82:0x0113, code lost:
    
        r3 = r2;
     */
    /* JADX WARN: Code restructure failed: missing block: B:84:0x0115, code lost:
    
        if (r3 < 6) goto L86;
     */
    /* JADX WARN: Code restructure failed: missing block: B:85:0x0117, code lost:
    
        r2 = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:86:0x0119, code lost:
    
        r2 = false;
     */
    /* JADX WARN: Code restructure failed: missing block: B:87:0x011a, code lost:
    
        r10 = r2;
        r4 = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:89:0x011f, code lost:
    
        if (r4 != false) goto L95;
     */
    /* JADX WARN: Code restructure failed: missing block: B:90:0x0121, code lost:
    
        r5 = r21.getActualMinimum(r11[0]);
        r3 = r21.getActualMaximum(r11[0]);
        r2 = r21.get(r11[0]) - r5;
     */
    /* JADX WARN: Code restructure failed: missing block: B:91:0x013f, code lost:
    
        if (r2 <= ((r3 - r5) / 2)) goto L93;
     */
    /* JADX WARN: Code restructure failed: missing block: B:92:0x0141, code lost:
    
        r4 = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:93:0x0143, code lost:
    
        r4 = false;
     */
    /* JADX WARN: Code restructure failed: missing block: B:94:0x0144, code lost:
    
        r3 = r2;
        r10 = r4;
     */
    /* JADX WARN: Code restructure failed: missing block: B:96:0x014b, code lost:
    
        if (r3 == 0) goto L98;
     */
    /* JADX WARN: Code restructure failed: missing block: B:97:0x014d, code lost:
    
        r21.set(r11[0], r21.get(r11[0]) - r3);
     */
    /* JADX WARN: Code restructure failed: missing block: B:99:0x015c, code lost:
    
        r15 = r15 + 1;
        r2 = r23;
        r4 = r17;
        r5 = r18;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private static void modify(java.util.Calendar r21, int r22, org.apache.commons.lang3.time.DateUtils.ModifyType r23) {
        /*
            Method dump skipped, instruction units count: 399
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.time.DateUtils.modify(java.util.Calendar, int, org.apache.commons.lang3.time.DateUtils$ModifyType):void");
    }

    public static Iterator<Calendar> iterator(Date focus, int rangeStyle) {
        validateDateNotNull(focus);
        Calendar gval = Calendar.getInstance();
        gval.setTime(focus);
        return iterator(gval, rangeStyle);
    }

    public static Iterator<Calendar> iterator(Calendar focus, int rangeStyle) {
        Calendar start;
        Calendar end;
        if (focus == null) {
            throw nullDateIllegalArgumentException();
        }
        int startCutoff = 1;
        int endCutoff = 7;
        switch (rangeStyle) {
            case 1:
            case 2:
            case 3:
            case 4:
                start = truncate(focus, 5);
                end = truncate(focus, 5);
                if (rangeStyle == 2) {
                    startCutoff = 2;
                    endCutoff = 1;
                } else if (rangeStyle == 3) {
                    startCutoff = focus.get(7);
                    endCutoff = startCutoff - 1;
                } else if (rangeStyle == 4) {
                    startCutoff = focus.get(7) - 3;
                    endCutoff = focus.get(7) + 3;
                }
                break;
            case 5:
            case 6:
                start = truncate(focus, 2);
                end = (Calendar) start.clone();
                end.add(2, 1);
                end.add(5, -1);
                if (rangeStyle == 6) {
                    startCutoff = 2;
                    endCutoff = 1;
                }
                break;
            default:
                throw new IllegalArgumentException("The range style " + rangeStyle + " is not valid.");
        }
        if (startCutoff < 1) {
            startCutoff += 7;
        }
        if (startCutoff > 7) {
            startCutoff -= 7;
        }
        if (endCutoff < 1) {
            endCutoff += 7;
        }
        if (endCutoff > 7) {
            endCutoff -= 7;
        }
        while (start.get(7) != startCutoff) {
            start.add(5, -1);
        }
        while (end.get(7) != endCutoff) {
            end.add(5, 1);
        }
        return new DateIterator(start, end);
    }

    public static Iterator<?> iterator(Object focus, int rangeStyle) {
        if (focus == null) {
            throw nullDateIllegalArgumentException();
        }
        if (focus instanceof Date) {
            return iterator((Date) focus, rangeStyle);
        }
        if (focus instanceof Calendar) {
            return iterator((Calendar) focus, rangeStyle);
        }
        throw new ClassCastException("Could not iterate based on " + focus);
    }

    public static long getFragmentInMilliseconds(Date date, int fragment) {
        return getFragment(date, fragment, TimeUnit.MILLISECONDS);
    }

    public static long getFragmentInSeconds(Date date, int fragment) {
        return getFragment(date, fragment, TimeUnit.SECONDS);
    }

    public static long getFragmentInMinutes(Date date, int fragment) {
        return getFragment(date, fragment, TimeUnit.MINUTES);
    }

    public static long getFragmentInHours(Date date, int fragment) {
        return getFragment(date, fragment, TimeUnit.HOURS);
    }

    public static long getFragmentInDays(Date date, int fragment) {
        return getFragment(date, fragment, TimeUnit.DAYS);
    }

    public static long getFragmentInMilliseconds(Calendar calendar, int fragment) {
        return getFragment(calendar, fragment, TimeUnit.MILLISECONDS);
    }

    public static long getFragmentInSeconds(Calendar calendar, int fragment) {
        return getFragment(calendar, fragment, TimeUnit.SECONDS);
    }

    public static long getFragmentInMinutes(Calendar calendar, int fragment) {
        return getFragment(calendar, fragment, TimeUnit.MINUTES);
    }

    public static long getFragmentInHours(Calendar calendar, int fragment) {
        return getFragment(calendar, fragment, TimeUnit.HOURS);
    }

    public static long getFragmentInDays(Calendar calendar, int fragment) {
        return getFragment(calendar, fragment, TimeUnit.DAYS);
    }

    private static long getFragment(Date date, int fragment, TimeUnit unit) {
        validateDateNotNull(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return getFragment(calendar, fragment, unit);
    }

    private static long getFragment(Calendar calendar, int fragment, TimeUnit unit) {
        if (calendar == null) {
            throw nullDateIllegalArgumentException();
        }
        long result = 0;
        int offset = unit == TimeUnit.DAYS ? 0 : 1;
        if (fragment == 1) {
            result = 0 + unit.convert(calendar.get(6) - offset, TimeUnit.DAYS);
        } else if (fragment == 2) {
            result = 0 + unit.convert(calendar.get(5) - offset, TimeUnit.DAYS);
        }
        if (fragment == 1 || fragment == 2 || fragment == 5 || fragment == 6) {
            result += unit.convert(calendar.get(11), TimeUnit.HOURS);
        } else {
            switch (fragment) {
                case 11:
                    break;
                case 12:
                    result += unit.convert(calendar.get(13), TimeUnit.SECONDS);
                case 13:
                    return result + unit.convert(calendar.get(14), TimeUnit.MILLISECONDS);
                case 14:
                    return result;
                default:
                    throw new IllegalArgumentException("The fragment " + fragment + " is not supported");
            }
        }
        result += unit.convert(calendar.get(12), TimeUnit.MINUTES);
        result += unit.convert(calendar.get(13), TimeUnit.SECONDS);
        return result + unit.convert(calendar.get(14), TimeUnit.MILLISECONDS);
    }

    public static boolean truncatedEquals(Calendar cal1, Calendar cal2, int field) {
        return truncatedCompareTo(cal1, cal2, field) == 0;
    }

    public static boolean truncatedEquals(Date date1, Date date2, int field) {
        return truncatedCompareTo(date1, date2, field) == 0;
    }

    public static int truncatedCompareTo(Calendar cal1, Calendar cal2, int field) {
        Calendar truncatedCal1 = truncate(cal1, field);
        Calendar truncatedCal2 = truncate(cal2, field);
        return truncatedCal1.compareTo(truncatedCal2);
    }

    public static int truncatedCompareTo(Date date1, Date date2, int field) {
        Date truncatedDate1 = truncate(date1, field);
        Date truncatedDate2 = truncate(date2, field);
        return truncatedDate1.compareTo(truncatedDate2);
    }

    private static void validateDateNotNull(Date date) {
        Validate.notNull(date, "The date must not be null", new Object[0]);
    }

    static class DateIterator implements Iterator<Calendar> {
        private final Calendar endFinal;
        private final Calendar spot;

        DateIterator(Calendar startFinal, Calendar endFinal) {
            this.endFinal = endFinal;
            this.spot = startFinal;
            startFinal.add(5, -1);
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return this.spot.before(this.endFinal);
        }

        @Override // java.util.Iterator
        public Calendar next() {
            if (this.spot.equals(this.endFinal)) {
                throw new NoSuchElementException();
            }
            this.spot.add(5, 1);
            return (Calendar) this.spot.clone();
        }

        @Override // java.util.Iterator
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
