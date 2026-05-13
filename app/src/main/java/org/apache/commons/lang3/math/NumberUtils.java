package org.apache.commons.lang3.math;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

/* JADX INFO: loaded from: classes.dex */
public class NumberUtils {
    public static final Long LONG_ZERO = 0L;
    public static final Long LONG_ONE = 1L;
    public static final Long LONG_MINUS_ONE = -1L;
    public static final Integer INTEGER_ZERO = 0;
    public static final Integer INTEGER_ONE = 1;
    public static final Integer INTEGER_TWO = 2;
    public static final Integer INTEGER_MINUS_ONE = -1;
    public static final Short SHORT_ZERO = 0;
    public static final Short SHORT_ONE = 1;
    public static final Short SHORT_MINUS_ONE = -1;
    public static final Byte BYTE_ZERO = (byte) 0;
    public static final Byte BYTE_ONE = (byte) 1;
    public static final Byte BYTE_MINUS_ONE = (byte) -1;
    public static final Double DOUBLE_ZERO = Double.valueOf(0.0d);
    public static final Double DOUBLE_ONE = Double.valueOf(1.0d);
    public static final Double DOUBLE_MINUS_ONE = Double.valueOf(-1.0d);
    public static final Float FLOAT_ZERO = Float.valueOf(0.0f);
    public static final Float FLOAT_ONE = Float.valueOf(1.0f);
    public static final Float FLOAT_MINUS_ONE = Float.valueOf(-1.0f);

    public static int toInt(String str) {
        return toInt(str, 0);
    }

    public static int toInt(String str, int defaultValue) {
        if (str == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static long toLong(String str) {
        return toLong(str, 0L);
    }

    public static long toLong(String str, long defaultValue) {
        if (str == null) {
            return defaultValue;
        }
        try {
            return Long.parseLong(str);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static float toFloat(String str) {
        return toFloat(str, 0.0f);
    }

    public static float toFloat(String str, float defaultValue) {
        if (str == null) {
            return defaultValue;
        }
        try {
            return Float.parseFloat(str);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static double toDouble(String str) {
        return toDouble(str, 0.0d);
    }

    public static double toDouble(String str, double defaultValue) {
        if (str == null) {
            return defaultValue;
        }
        try {
            return Double.parseDouble(str);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static double toDouble(BigDecimal value) {
        return toDouble(value, 0.0d);
    }

    public static double toDouble(BigDecimal value, double defaultValue) {
        return value == null ? defaultValue : value.doubleValue();
    }

    public static byte toByte(String str) {
        return toByte(str, (byte) 0);
    }

    public static byte toByte(String str, byte defaultValue) {
        if (str == null) {
            return defaultValue;
        }
        try {
            return Byte.parseByte(str);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static short toShort(String str) {
        return toShort(str, (short) 0);
    }

    public static short toShort(String str, short defaultValue) {
        if (str == null) {
            return defaultValue;
        }
        try {
            return Short.parseShort(str);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static BigDecimal toScaledBigDecimal(BigDecimal value) {
        return toScaledBigDecimal(value, INTEGER_TWO.intValue(), RoundingMode.HALF_EVEN);
    }

    public static BigDecimal toScaledBigDecimal(BigDecimal value, int scale, RoundingMode roundingMode) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        return value.setScale(scale, roundingMode == null ? RoundingMode.HALF_EVEN : roundingMode);
    }

    public static BigDecimal toScaledBigDecimal(Float value) {
        return toScaledBigDecimal(value, INTEGER_TWO.intValue(), RoundingMode.HALF_EVEN);
    }

    public static BigDecimal toScaledBigDecimal(Float value, int scale, RoundingMode roundingMode) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        return toScaledBigDecimal(BigDecimal.valueOf(value.floatValue()), scale, roundingMode);
    }

    public static BigDecimal toScaledBigDecimal(Double value) {
        return toScaledBigDecimal(value, INTEGER_TWO.intValue(), RoundingMode.HALF_EVEN);
    }

    public static BigDecimal toScaledBigDecimal(Double value, int scale, RoundingMode roundingMode) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        return toScaledBigDecimal(BigDecimal.valueOf(value.doubleValue()), scale, roundingMode);
    }

    public static BigDecimal toScaledBigDecimal(String value) {
        return toScaledBigDecimal(value, INTEGER_TWO.intValue(), RoundingMode.HALF_EVEN);
    }

    public static BigDecimal toScaledBigDecimal(String value, int scale, RoundingMode roundingMode) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        return toScaledBigDecimal(createBigDecimal(value), scale, roundingMode);
    }

    /* JADX WARN: Code restructure failed: missing block: B:83:0x0145, code lost:
    
        if (r6 == 'l') goto L84;
     */
    /* JADX WARN: Removed duplicated region for block: B:103:0x0193 A[Catch: NumberFormatException -> 0x019f, TRY_LEAVE, TryCatch #5 {NumberFormatException -> 0x019f, blocks: (B:101:0x0189, B:103:0x0193), top: B:177:0x0189 }] */
    /* JADX WARN: Removed duplicated region for block: B:112:0x01aa A[Catch: NumberFormatException -> 0x01b7, TRY_LEAVE, TryCatch #0 {NumberFormatException -> 0x01b7, blocks: (B:110:0x01a0, B:112:0x01aa), top: B:167:0x01a0 }] */
    /* JADX WARN: Removed duplicated region for block: B:167:0x01a0 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static java.lang.Number createNumber(java.lang.String r18) {
        /*
            Method dump skipped, instruction units count: 614
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.math.NumberUtils.createNumber(java.lang.String):java.lang.Number");
    }

    private static String getMantissa(String str) {
        return getMantissa(str, str.length());
    }

    private static String getMantissa(String str, int stopPos) {
        boolean hasSign;
        char firstChar = str.charAt(0);
        if (firstChar != '-' && firstChar != '+') {
            hasSign = false;
        } else {
            hasSign = true;
        }
        if (!hasSign) {
            return str.substring(0, stopPos);
        }
        return str.substring(1, stopPos);
    }

    private static boolean isAllZeros(String str) {
        if (str == null) {
            return true;
        }
        for (int i = str.length() - 1; i >= 0; i--) {
            if (str.charAt(i) != '0') {
                return false;
            }
        }
        return true ^ str.isEmpty();
    }

    public static Float createFloat(String str) {
        if (str == null) {
            return null;
        }
        return Float.valueOf(str);
    }

    public static Double createDouble(String str) {
        if (str == null) {
            return null;
        }
        return Double.valueOf(str);
    }

    public static Integer createInteger(String str) {
        if (str == null) {
            return null;
        }
        return Integer.decode(str);
    }

    public static Long createLong(String str) {
        if (str == null) {
            return null;
        }
        return Long.decode(str);
    }

    public static BigInteger createBigInteger(String str) {
        if (str == null) {
            return null;
        }
        int pos = 0;
        int radix = 10;
        boolean negate = false;
        if (str.startsWith("-")) {
            negate = true;
            pos = 1;
        }
        if (str.startsWith("0x", pos) || str.startsWith("0X", pos)) {
            radix = 16;
            pos += 2;
        } else if (str.startsWith("#", pos)) {
            radix = 16;
            pos++;
        } else if (str.startsWith("0", pos) && str.length() > pos + 1) {
            radix = 8;
            pos++;
        }
        BigInteger value = new BigInteger(str.substring(pos), radix);
        return negate ? value.negate() : value;
    }

    public static BigDecimal createBigDecimal(String str) {
        if (str == null) {
            return null;
        }
        if (StringUtils.isBlank(str)) {
            throw new NumberFormatException("A blank string is not a valid number");
        }
        return new BigDecimal(str);
    }

    public static long min(long... array) {
        validateArray(array);
        long min = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] < min) {
                min = array[i];
            }
        }
        return min;
    }

    public static int min(int... array) {
        validateArray(array);
        int min = array[0];
        for (int j = 1; j < array.length; j++) {
            if (array[j] < min) {
                min = array[j];
            }
        }
        return min;
    }

    public static short min(short... array) {
        validateArray(array);
        short min = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] < min) {
                min = array[i];
            }
        }
        return min;
    }

    public static byte min(byte... array) {
        validateArray(array);
        byte min = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] < min) {
                min = array[i];
            }
        }
        return min;
    }

    public static double min(double... array) {
        validateArray(array);
        double min = array[0];
        for (int i = 1; i < array.length; i++) {
            if (!Double.isNaN(array[i])) {
                if (array[i] < min) {
                    min = array[i];
                }
            } else {
                return Double.NaN;
            }
        }
        return min;
    }

    public static float min(float... array) {
        validateArray(array);
        float min = array[0];
        for (int i = 1; i < array.length; i++) {
            if (!Float.isNaN(array[i])) {
                if (array[i] < min) {
                    min = array[i];
                }
            } else {
                return Float.NaN;
            }
        }
        return min;
    }

    public static long max(long... array) {
        validateArray(array);
        long max = array[0];
        for (int j = 1; j < array.length; j++) {
            if (array[j] > max) {
                max = array[j];
            }
        }
        return max;
    }

    public static int max(int... array) {
        validateArray(array);
        int max = array[0];
        for (int j = 1; j < array.length; j++) {
            if (array[j] > max) {
                max = array[j];
            }
        }
        return max;
    }

    public static short max(short... array) {
        validateArray(array);
        short max = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] > max) {
                max = array[i];
            }
        }
        return max;
    }

    public static byte max(byte... array) {
        validateArray(array);
        byte max = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] > max) {
                max = array[i];
            }
        }
        return max;
    }

    public static double max(double... array) {
        validateArray(array);
        double max = array[0];
        for (int j = 1; j < array.length; j++) {
            if (!Double.isNaN(array[j])) {
                if (array[j] > max) {
                    max = array[j];
                }
            } else {
                return Double.NaN;
            }
        }
        return max;
    }

    public static float max(float... array) {
        validateArray(array);
        float max = array[0];
        for (int j = 1; j < array.length; j++) {
            if (!Float.isNaN(array[j])) {
                if (array[j] > max) {
                    max = array[j];
                }
            } else {
                return Float.NaN;
            }
        }
        return max;
    }

    private static void validateArray(Object array) {
        Validate.notNull(array, "The Array must not be null", new Object[0]);
        Validate.isTrue(Array.getLength(array) != 0, "Array cannot be empty.", new Object[0]);
    }

    public static long min(long a, long b, long c) {
        if (b < a) {
            a = b;
        }
        if (c < a) {
            return c;
        }
        return a;
    }

    public static int min(int a, int b, int c) {
        if (b < a) {
            a = b;
        }
        if (c < a) {
            return c;
        }
        return a;
    }

    public static short min(short a, short b, short c) {
        if (b < a) {
            a = b;
        }
        if (c < a) {
            return c;
        }
        return a;
    }

    public static byte min(byte a, byte b, byte c) {
        if (b < a) {
            a = b;
        }
        if (c < a) {
            return c;
        }
        return a;
    }

    public static double min(double a, double b, double c) {
        return Math.min(Math.min(a, b), c);
    }

    public static float min(float a, float b, float c) {
        return Math.min(Math.min(a, b), c);
    }

    public static long max(long a, long b, long c) {
        if (b > a) {
            a = b;
        }
        if (c > a) {
            return c;
        }
        return a;
    }

    public static int max(int a, int b, int c) {
        if (b > a) {
            a = b;
        }
        if (c > a) {
            return c;
        }
        return a;
    }

    public static short max(short a, short b, short c) {
        if (b > a) {
            a = b;
        }
        if (c > a) {
            return c;
        }
        return a;
    }

    public static byte max(byte a, byte b, byte c) {
        if (b > a) {
            a = b;
        }
        if (c > a) {
            return c;
        }
        return a;
    }

    public static double max(double a, double b, double c) {
        return Math.max(Math.max(a, b), c);
    }

    public static float max(float a, float b, float c) {
        return Math.max(Math.max(a, b), c);
    }

    public static boolean isDigits(String str) {
        return StringUtils.isNumeric(str);
    }

    @Deprecated
    public static boolean isNumber(String str) {
        return isCreatable(str);
    }

    /* JADX WARN: Code restructure failed: missing block: B:123:0x012e, code lost:
    
        return false;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static boolean isCreatable(java.lang.String r17) {
        /*
            Method dump skipped, instruction units count: 357
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.math.NumberUtils.isCreatable(java.lang.String):boolean");
    }

    public static boolean isParsable(String str) {
        if (StringUtils.isEmpty(str) || str.charAt(str.length() - 1) == '.') {
            return false;
        }
        if (str.charAt(0) == '-') {
            if (str.length() == 1) {
                return false;
            }
            return withDecimalsParsing(str, 1);
        }
        return withDecimalsParsing(str, 0);
    }

    private static boolean withDecimalsParsing(String str, int beginIdx) {
        int decimalPoints = 0;
        for (int i = beginIdx; i < str.length(); i++) {
            boolean isDecimalPoint = str.charAt(i) == '.';
            if (isDecimalPoint) {
                decimalPoints++;
            }
            if (decimalPoints > 1) {
                return false;
            }
            if (!isDecimalPoint && !Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static int compare(int x, int y) {
        if (x == y) {
            return 0;
        }
        return x < y ? -1 : 1;
    }

    public static int compare(long x, long y) {
        if (x == y) {
            return 0;
        }
        return x < y ? -1 : 1;
    }

    public static int compare(short x, short y) {
        if (x == y) {
            return 0;
        }
        return x < y ? -1 : 1;
    }

    public static int compare(byte x, byte y) {
        return x - y;
    }
}
