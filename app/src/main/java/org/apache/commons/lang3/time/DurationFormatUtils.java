package org.apache.commons.lang3.time;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

/* JADX INFO: loaded from: classes.dex */
public class DurationFormatUtils {
    public static final String ISO_EXTENDED_FORMAT_PATTERN = "'P'yyyy'Y'M'M'd'DT'H'H'm'M's.SSS'S'";
    static final Object y = "y";
    static final Object M = "M";
    static final Object d = "d";
    static final Object H = "H";
    static final Object m = "m";
    static final Object s = "s";
    static final Object S = "S";

    public static String formatDurationHMS(long durationMillis) {
        return formatDuration(durationMillis, "HH:mm:ss.SSS");
    }

    public static String formatDurationISO(long durationMillis) {
        return formatDuration(durationMillis, ISO_EXTENDED_FORMAT_PATTERN, false);
    }

    public static String formatDuration(long durationMillis, String format) {
        return formatDuration(durationMillis, format, true);
    }

    public static String formatDuration(long durationMillis, String format, boolean padWithZeros) {
        long seconds;
        long milliseconds;
        Validate.inclusiveBetween(0L, Long.MAX_VALUE, durationMillis, "durationMillis must not be negative");
        Token[] tokens = lexx(format);
        long days = 0;
        long hours = 0;
        long minutes = 0;
        long milliseconds2 = durationMillis;
        if (Token.containsTokenWithValue(tokens, d)) {
            days = milliseconds2 / DateUtils.MILLIS_PER_DAY;
            milliseconds2 -= DateUtils.MILLIS_PER_DAY * days;
        }
        if (Token.containsTokenWithValue(tokens, H)) {
            hours = milliseconds2 / DateUtils.MILLIS_PER_HOUR;
            milliseconds2 -= DateUtils.MILLIS_PER_HOUR * hours;
        }
        if (Token.containsTokenWithValue(tokens, m)) {
            minutes = milliseconds2 / DateUtils.MILLIS_PER_MINUTE;
            milliseconds2 -= DateUtils.MILLIS_PER_MINUTE * minutes;
        }
        if (!Token.containsTokenWithValue(tokens, s)) {
            seconds = 0;
            milliseconds = milliseconds2;
        } else {
            long seconds2 = milliseconds2 / 1000;
            seconds = seconds2;
            milliseconds = milliseconds2 - (1000 * seconds2);
        }
        return format(tokens, 0L, 0L, days, hours, minutes, seconds, milliseconds, padWithZeros);
    }

    public static String formatDurationWords(long durationMillis, boolean suppressLeadingZeroElements, boolean suppressTrailingZeroElements) {
        String duration = formatDuration(durationMillis, "d' days 'H' hours 'm' minutes 's' seconds'");
        if (suppressLeadingZeroElements) {
            duration = StringUtils.SPACE + duration;
            String tmp = StringUtils.replaceOnce(duration, " 0 days", "");
            if (tmp.length() != duration.length()) {
                duration = tmp;
                String tmp2 = StringUtils.replaceOnce(duration, " 0 hours", "");
                if (tmp2.length() != duration.length()) {
                    String tmp3 = StringUtils.replaceOnce(tmp2, " 0 minutes", "");
                    duration = tmp3;
                    if (tmp3.length() != duration.length()) {
                        duration = StringUtils.replaceOnce(tmp3, " 0 seconds", "");
                    }
                }
            }
            if (!duration.isEmpty()) {
                duration = duration.substring(1);
            }
        }
        if (suppressTrailingZeroElements) {
            String tmp4 = StringUtils.replaceOnce(duration, " 0 seconds", "");
            if (tmp4.length() != duration.length()) {
                duration = tmp4;
                String tmp5 = StringUtils.replaceOnce(duration, " 0 minutes", "");
                if (tmp5.length() != duration.length()) {
                    duration = tmp5;
                    String tmp6 = StringUtils.replaceOnce(duration, " 0 hours", "");
                    if (tmp6.length() != duration.length()) {
                        duration = StringUtils.replaceOnce(tmp6, " 0 days", "");
                    }
                }
            }
        }
        return StringUtils.replaceOnce(StringUtils.replaceOnce(StringUtils.replaceOnce(StringUtils.replaceOnce(StringUtils.SPACE + duration, " 1 seconds", " 1 second"), " 1 minutes", " 1 minute"), " 1 hours", " 1 hour"), " 1 days", " 1 day").trim();
    }

    public static String formatPeriodISO(long startMillis, long endMillis) {
        return formatPeriod(startMillis, endMillis, ISO_EXTENDED_FORMAT_PATTERN, false, TimeZone.getDefault());
    }

    public static String formatPeriod(long startMillis, long endMillis, String format) {
        return formatPeriod(startMillis, endMillis, format, true, TimeZone.getDefault());
    }

    public static String formatPeriod(long startMillis, long endMillis, String format, boolean padWithZeros, TimeZone timezone) {
        int months;
        int months2;
        int hours;
        int milliseconds;
        int milliseconds2;
        Validate.isTrue(startMillis <= endMillis, "startMillis must not be greater than endMillis", new Object[0]);
        Token[] tokens = lexx(format);
        Calendar start = Calendar.getInstance(timezone);
        start.setTime(new Date(startMillis));
        Calendar end = Calendar.getInstance(timezone);
        end.setTime(new Date(endMillis));
        int milliseconds3 = end.get(14) - start.get(14);
        int seconds = end.get(13) - start.get(13);
        int minutes = end.get(12) - start.get(12);
        int hours2 = end.get(11) - start.get(11);
        int days = end.get(5) - start.get(5);
        int months3 = end.get(2) - start.get(2);
        int years = end.get(1) - start.get(1);
        while (milliseconds3 < 0) {
            milliseconds3 += 1000;
            seconds--;
        }
        while (seconds < 0) {
            seconds += 60;
            minutes--;
        }
        while (minutes < 0) {
            minutes += 60;
            hours2--;
        }
        while (hours2 < 0) {
            hours2 += 24;
            days--;
        }
        if (Token.containsTokenWithValue(tokens, M)) {
            while (days < 0) {
                days += start.getActualMaximum(5);
                months3--;
                start.add(2, 1);
            }
            while (months3 < 0) {
                months3 += 12;
                years--;
            }
            if (!Token.containsTokenWithValue(tokens, y) && years != 0) {
                while (years != 0) {
                    months3 += years * 12;
                    years = 0;
                }
                months = months3;
                months2 = years;
            } else {
                months = months3;
                months2 = years;
            }
        } else {
            if (!Token.containsTokenWithValue(tokens, y)) {
                int target = end.get(1);
                int target2 = months3 >= 0 ? target : target - 1;
                for (int i = 1; start.get(i) != target2; i = 1) {
                    int days2 = days + (start.getActualMaximum(6) - start.get(6));
                    if ((start instanceof GregorianCalendar) && start.get(2) == 1 && start.get(5) == 29) {
                        days2++;
                    }
                    start.add(1, 1);
                    days = days2 + start.get(6);
                }
                years = 0;
            }
            while (start.get(2) != end.get(2)) {
                days += start.getActualMaximum(5);
                start.add(2, 1);
            }
            int months4 = 0;
            while (days < 0) {
                days += start.getActualMaximum(5);
                months4--;
                start.add(2, 1);
            }
            months = months4;
            months2 = years;
        }
        if (!Token.containsTokenWithValue(tokens, d)) {
            hours2 += days * 24;
            days = 0;
        }
        if (Token.containsTokenWithValue(tokens, H)) {
            hours = hours2;
        } else {
            minutes += hours2 * 60;
            hours = 0;
        }
        if (!Token.containsTokenWithValue(tokens, m)) {
            seconds += minutes * 60;
            minutes = 0;
        }
        if (Token.containsTokenWithValue(tokens, s)) {
            milliseconds = milliseconds3;
            milliseconds2 = seconds;
        } else {
            milliseconds = milliseconds3 + (seconds * 1000);
            milliseconds2 = 0;
        }
        return format(tokens, months2, months, days, hours, minutes, milliseconds2, milliseconds, padWithZeros);
    }

    static String format(Token[] tokens, long years, long months, long days, long hours, long minutes, long seconds, long milliseconds, boolean padWithZeros) {
        Token[] tokenArr = tokens;
        StringBuilder buffer = new StringBuilder();
        boolean lastOutputSeconds = false;
        int length = tokenArr.length;
        int i = 0;
        while (i < length) {
            Token token = tokenArr[i];
            Object value = token.getValue();
            int count = token.getCount();
            if (value instanceof StringBuilder) {
                buffer.append(value.toString());
            } else if (value.equals(y)) {
                buffer.append(paddedValue(years, padWithZeros, count));
                lastOutputSeconds = false;
            } else if (value.equals(M)) {
                buffer.append(paddedValue(months, padWithZeros, count));
                lastOutputSeconds = false;
            } else if (value.equals(d)) {
                buffer.append(paddedValue(days, padWithZeros, count));
                lastOutputSeconds = false;
            } else if (value.equals(H)) {
                buffer.append(paddedValue(hours, padWithZeros, count));
                lastOutputSeconds = false;
            } else if (value.equals(m)) {
                buffer.append(paddedValue(minutes, padWithZeros, count));
                lastOutputSeconds = false;
            } else if (value.equals(s)) {
                buffer.append(paddedValue(seconds, padWithZeros, count));
                lastOutputSeconds = true;
            } else if (value.equals(S)) {
                if (lastOutputSeconds) {
                    int width = padWithZeros ? Math.max(3, count) : 3;
                    buffer.append(paddedValue(milliseconds, true, width));
                } else {
                    buffer.append(paddedValue(milliseconds, padWithZeros, count));
                }
                lastOutputSeconds = false;
            }
            i++;
            tokenArr = tokens;
        }
        return buffer.toString();
    }

    private static String paddedValue(long value, boolean padWithZeros, int count) {
        String longString = Long.toString(value);
        return padWithZeros ? StringUtils.leftPad(longString, count, '0') : longString;
    }

    static Token[] lexx(String format) {
        ArrayList<Token> list = new ArrayList<>(format.length());
        boolean inLiteral = false;
        StringBuilder buffer = null;
        Token previous = null;
        for (int i = 0; i < format.length(); i++) {
            char ch = format.charAt(i);
            if (inLiteral && ch != '\'') {
                buffer.append(ch);
            } else {
                Object value = null;
                if (ch != '\'') {
                    if (ch == 'H') {
                        value = H;
                    } else if (ch == 'M') {
                        value = M;
                    } else if (ch == 'S') {
                        value = S;
                    } else if (ch == 'd') {
                        value = d;
                    } else if (ch == 'm') {
                        value = m;
                    } else if (ch == 's') {
                        value = s;
                    } else if (ch == 'y') {
                        value = y;
                    } else {
                        if (buffer == null) {
                            buffer = new StringBuilder();
                            list.add(new Token(buffer));
                        }
                        buffer.append(ch);
                    }
                } else if (inLiteral) {
                    buffer = null;
                    inLiteral = false;
                } else {
                    buffer = new StringBuilder();
                    list.add(new Token(buffer));
                    inLiteral = true;
                }
                if (value != null) {
                    if (previous != null && previous.getValue().equals(value)) {
                        previous.increment();
                    } else {
                        Token token = new Token(value);
                        list.add(token);
                        previous = token;
                    }
                    buffer = null;
                }
            }
        }
        if (inLiteral) {
            throw new IllegalArgumentException("Unmatched quote in format: " + format);
        }
        return (Token[]) list.toArray(new Token[0]);
    }

    static class Token {
        private int count;
        private final Object value;

        static boolean containsTokenWithValue(Token[] tokens, Object value) {
            for (Token token : tokens) {
                if (token.getValue() == value) {
                    return true;
                }
            }
            return false;
        }

        Token(Object value) {
            this.value = value;
            this.count = 1;
        }

        Token(Object value, int count) {
            this.value = value;
            this.count = count;
        }

        void increment() {
            this.count++;
        }

        int getCount() {
            return this.count;
        }

        Object getValue() {
            return this.value;
        }

        public boolean equals(Object obj2) {
            if (!(obj2 instanceof Token)) {
                return false;
            }
            Token tok2 = (Token) obj2;
            if (this.value.getClass() != tok2.value.getClass() || this.count != tok2.count) {
                return false;
            }
            Object obj = this.value;
            if (obj instanceof StringBuilder) {
                return obj.toString().equals(tok2.value.toString());
            }
            if (obj instanceof Number) {
                return obj.equals(tok2.value);
            }
            return obj == tok2.value;
        }

        public int hashCode() {
            return this.value.hashCode();
        }

        public String toString() {
            return StringUtils.repeat(this.value.toString(), this.count);
        }
    }
}
