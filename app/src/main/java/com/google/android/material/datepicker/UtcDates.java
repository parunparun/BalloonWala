package com.google.android.material.datepicker;

import android.content.res.Resources;
import android.icu.text.DateFormat;
import com.google.android.material.R;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import org.apache.commons.lang3.StringUtils;

/* JADX INFO: loaded from: classes.dex */
class UtcDates {
    static final String UTC = "UTC";

    private UtcDates() {
    }

    static TimeZone getTimeZone() {
        return TimeZone.getTimeZone(UTC);
    }

    private static android.icu.util.TimeZone getAndroidTimeZone() {
        return android.icu.util.TimeZone.getTimeZone(UTC);
    }

    static Calendar getTodayCalendar() {
        return getDayCopy(Calendar.getInstance());
    }

    static Calendar getCalendar() {
        Calendar utc = Calendar.getInstance(getTimeZone());
        utc.clear();
        return utc;
    }

    static Calendar getDayCopy(Calendar rawCalendar) {
        Calendar safe = getCalendar();
        safe.set(rawCalendar.get(1), rawCalendar.get(2), rawCalendar.get(5));
        return safe;
    }

    static long canonicalYearMonthDay(long rawDate) {
        Calendar rawCalendar = getCalendar();
        rawCalendar.setTimeInMillis(rawDate);
        Calendar sanitizedStartItem = getDayCopy(rawCalendar);
        return sanitizedStartItem.getTimeInMillis();
    }

    private static DateFormat getAndroidFormat(String pattern, Locale locale) {
        DateFormat format = DateFormat.getInstanceForSkeleton(pattern, locale);
        format.setTimeZone(getAndroidTimeZone());
        return format;
    }

    private static java.text.DateFormat getFormat(int style, Locale locale) {
        java.text.DateFormat format = java.text.DateFormat.getDateInstance(style, locale);
        format.setTimeZone(getTimeZone());
        return format;
    }

    static SimpleDateFormat getTextInputFormat() {
        String pattern = ((SimpleDateFormat) java.text.DateFormat.getDateInstance(3, Locale.getDefault())).toLocalizedPattern().replaceAll("\\s+", "");
        SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.getDefault());
        format.setTimeZone(getTimeZone());
        format.setLenient(false);
        return format;
    }

    static String getTextInputHint(Resources res, SimpleDateFormat format) {
        String formatHint = format.toLocalizedPattern();
        String yearChar = res.getString(R.string.mtrl_picker_text_input_year_abbr);
        String monthChar = res.getString(R.string.mtrl_picker_text_input_month_abbr);
        String dayChar = res.getString(R.string.mtrl_picker_text_input_day_abbr);
        return formatHint.replaceAll("d", dayChar).replaceAll("M", monthChar).replaceAll("y", yearChar);
    }

    static SimpleDateFormat getSimpleFormat(String pattern) {
        return getSimpleFormat(pattern, Locale.getDefault());
    }

    private static SimpleDateFormat getSimpleFormat(String pattern, Locale locale) {
        SimpleDateFormat format = new SimpleDateFormat(pattern, locale);
        format.setTimeZone(getTimeZone());
        return format;
    }

    static DateFormat getYearAbbrMonthDayFormat(Locale locale) {
        return getAndroidFormat("yMMMd", locale);
    }

    static DateFormat getAbbrMonthDayFormat(Locale locale) {
        return getAndroidFormat("MMMd", locale);
    }

    static DateFormat getAbbrMonthWeekdayDayFormat(Locale locale) {
        return getAndroidFormat("MMMEd", locale);
    }

    static DateFormat getYearAbbrMonthWeekdayDayFormat(Locale locale) {
        return getAndroidFormat("yMMMEd", locale);
    }

    static java.text.DateFormat getMediumFormat() {
        return getMediumFormat(Locale.getDefault());
    }

    static java.text.DateFormat getMediumFormat(Locale locale) {
        return getFormat(2, locale);
    }

    static java.text.DateFormat getMediumNoYear() {
        return getMediumNoYear(Locale.getDefault());
    }

    static java.text.DateFormat getMediumNoYear(Locale locale) {
        SimpleDateFormat format = (SimpleDateFormat) getMediumFormat(locale);
        format.applyPattern(removeYearFromDateFormatPattern(format.toPattern()));
        return format;
    }

    static java.text.DateFormat getFullFormat() {
        return getFullFormat(Locale.getDefault());
    }

    static java.text.DateFormat getFullFormat(Locale locale) {
        return getFormat(0, locale);
    }

    static SimpleDateFormat getYearMonthFormat() {
        return getYearMonthFormat(Locale.getDefault());
    }

    private static SimpleDateFormat getYearMonthFormat(Locale locale) {
        return getSimpleFormat("MMMM, yyyy", locale);
    }

    private static String removeYearFromDateFormatPattern(String pattern) {
        int yearPosition = findCharactersInDateFormatPattern(pattern, "yY", 1, 0);
        if (yearPosition >= pattern.length()) {
            return pattern;
        }
        String monthDayCharacters = "EMd";
        int yearEndPosition = findCharactersInDateFormatPattern(pattern, "EMd", 1, yearPosition);
        if (yearEndPosition < pattern.length()) {
            monthDayCharacters = "EMd,";
        }
        int yearStartPosition = findCharactersInDateFormatPattern(pattern, monthDayCharacters, -1, yearPosition);
        String yearPattern = pattern.substring(yearStartPosition + 1, yearEndPosition);
        return pattern.replace(yearPattern, StringUtils.SPACE).trim();
    }

    private static int findCharactersInDateFormatPattern(String pattern, String characterSequence, int increment, int initialPosition) {
        int position = initialPosition;
        while (position >= 0 && position < pattern.length() && characterSequence.indexOf(pattern.charAt(position)) == -1) {
            if (pattern.charAt(position) == '\'') {
                position += increment;
                while (position >= 0 && position < pattern.length() && pattern.charAt(position) != '\'') {
                    position += increment;
                }
            }
            position += increment;
        }
        return position;
    }
}
