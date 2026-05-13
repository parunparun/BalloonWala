package org.apache.commons.lang3.time;

import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.commons.lang3.Validate;

/* JADX INFO: loaded from: classes.dex */
abstract class FormatCache<F extends Format> {
    static final int NONE = -1;
    private static final ConcurrentMap<MultipartKey, String> cDateTimeInstanceCache = new ConcurrentHashMap(7);
    private final ConcurrentMap<MultipartKey, F> cInstanceCache = new ConcurrentHashMap(7);

    protected abstract F createInstance(String str, TimeZone timeZone, Locale locale);

    FormatCache() {
    }

    public F getInstance() {
        return (F) getDateTimeInstance(3, 3, TimeZone.getDefault(), Locale.getDefault());
    }

    public F getInstance(String str, TimeZone timeZone, Locale locale) {
        Validate.notNull(str, "pattern must not be null", new Object[0]);
        if (timeZone == null) {
            timeZone = TimeZone.getDefault();
        }
        if (locale == null) {
            locale = Locale.getDefault();
        }
        MultipartKey multipartKey = new MultipartKey(str, timeZone, locale);
        F f = this.cInstanceCache.get(multipartKey);
        if (f == null) {
            F f2 = (F) createInstance(str, timeZone, locale);
            F fPutIfAbsent = this.cInstanceCache.putIfAbsent(multipartKey, f2);
            if (fPutIfAbsent != null) {
                return fPutIfAbsent;
            }
            return f2;
        }
        return f;
    }

    private F getDateTimeInstance(Integer num, Integer num2, TimeZone timeZone, Locale locale) {
        if (locale == null) {
            locale = Locale.getDefault();
        }
        return (F) getInstance(getPatternForStyle(num, num2, locale), timeZone, locale);
    }

    F getDateTimeInstance(int i, int i2, TimeZone timeZone, Locale locale) {
        return (F) getDateTimeInstance(Integer.valueOf(i), Integer.valueOf(i2), timeZone, locale);
    }

    F getDateInstance(int i, TimeZone timeZone, Locale locale) {
        return (F) getDateTimeInstance(Integer.valueOf(i), (Integer) null, timeZone, locale);
    }

    F getTimeInstance(int i, TimeZone timeZone, Locale locale) {
        return (F) getDateTimeInstance((Integer) null, Integer.valueOf(i), timeZone, locale);
    }

    static String getPatternForStyle(Integer dateStyle, Integer timeStyle, Locale locale) {
        DateFormat formatter;
        MultipartKey key = new MultipartKey(dateStyle, timeStyle, locale);
        String pattern = cDateTimeInstanceCache.get(key);
        if (pattern == null) {
            try {
                if (dateStyle == null) {
                    formatter = DateFormat.getTimeInstance(timeStyle.intValue(), locale);
                } else if (timeStyle == null) {
                    formatter = DateFormat.getDateInstance(dateStyle.intValue(), locale);
                } else {
                    formatter = DateFormat.getDateTimeInstance(dateStyle.intValue(), timeStyle.intValue(), locale);
                }
                String pattern2 = ((SimpleDateFormat) formatter).toPattern();
                String previous = cDateTimeInstanceCache.putIfAbsent(key, pattern2);
                return previous != null ? previous : pattern2;
            } catch (ClassCastException e) {
                throw new IllegalArgumentException("No date time pattern for locale: " + locale);
            }
        }
        return pattern;
    }

    private static class MultipartKey {
        private int hashCode;
        private final Object[] keys;

        MultipartKey(Object... keys) {
            this.keys = keys;
        }

        public boolean equals(Object obj) {
            return Arrays.equals(this.keys, ((MultipartKey) obj).keys);
        }

        public int hashCode() {
            if (this.hashCode == 0) {
                int rc = 0;
                for (Object key : this.keys) {
                    if (key != null) {
                        rc = (rc * 7) + key.hashCode();
                    }
                }
                this.hashCode = rc;
            }
            int rc2 = this.hashCode;
            return rc2;
        }
    }
}
