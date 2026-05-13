package org.apache.commons.lang3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class EnumUtils {
    private static final String CANNOT_STORE_S_S_VALUES_IN_S_BITS = "Cannot store %s %s values in %s bits";
    private static final String ENUM_CLASS_MUST_BE_DEFINED = "EnumClass must be defined.";
    private static final String NULL_ELEMENTS_NOT_PERMITTED = "null elements not permitted";
    private static final String S_DOES_NOT_SEEM_TO_BE_AN_ENUM_TYPE = "%s does not seem to be an Enum type";

    private static <E extends Enum<E>> Class<E> asEnum(Class<E> enumClass) {
        Validate.notNull(enumClass, ENUM_CLASS_MUST_BE_DEFINED, new Object[0]);
        Validate.isTrue(enumClass.isEnum(), S_DOES_NOT_SEEM_TO_BE_AN_ENUM_TYPE, enumClass);
        return enumClass;
    }

    private static <E extends Enum<E>> Class<E> checkBitVectorable(Class<E> enumClass) {
        Enum[] enumArr = (Enum[]) asEnum(enumClass).getEnumConstants();
        Validate.isTrue(enumArr.length <= 64, CANNOT_STORE_S_S_VALUES_IN_S_BITS, Integer.valueOf(enumArr.length), enumClass.getSimpleName(), 64);
        return enumClass;
    }

    @SafeVarargs
    public static <E extends Enum<E>> long generateBitVector(Class<E> enumClass, E... values) {
        Validate.noNullElements(values);
        return generateBitVector(enumClass, Arrays.asList(values));
    }

    public static <E extends Enum<E>> long generateBitVector(Class<E> enumClass, Iterable<? extends E> values) {
        checkBitVectorable(enumClass);
        Validate.notNull(values);
        long total = 0;
        for (E constant : values) {
            Validate.notNull(constant, NULL_ELEMENTS_NOT_PERMITTED, new Object[0]);
            total |= 1 << constant.ordinal();
        }
        return total;
    }

    @SafeVarargs
    public static <E extends Enum<E>> long[] generateBitVectors(Class<E> enumClass, E... values) {
        asEnum(enumClass);
        Validate.noNullElements(values);
        EnumSet<E> condensed = EnumSet.noneOf(enumClass);
        Collections.addAll(condensed, values);
        long[] result = new long[((enumClass.getEnumConstants().length - 1) / 64) + 1];
        for (E e : condensed) {
            int iOrdinal = e.ordinal() / 64;
            result[iOrdinal] = result[iOrdinal] | (1 << (e.ordinal() % 64));
        }
        ArrayUtils.reverse(result);
        return result;
    }

    public static <E extends Enum<E>> long[] generateBitVectors(Class<E> enumClass, Iterable<? extends E> values) {
        asEnum(enumClass);
        Validate.notNull(values);
        EnumSet<E> condensed = EnumSet.noneOf(enumClass);
        for (E constant : values) {
            Validate.notNull(constant, NULL_ELEMENTS_NOT_PERMITTED, new Object[0]);
            condensed.add(constant);
        }
        long[] result = new long[((enumClass.getEnumConstants().length - 1) / 64) + 1];
        for (E e : condensed) {
            int iOrdinal = e.ordinal() / 64;
            result[iOrdinal] = result[iOrdinal] | (1 << (e.ordinal() % 64));
        }
        ArrayUtils.reverse(result);
        return result;
    }

    public static <E extends Enum<E>> E getEnum(Class<E> cls, String str) {
        return (E) getEnum(cls, str, null);
    }

    public static <E extends Enum<E>> E getEnum(Class<E> cls, String str, E e) {
        if (str == null) {
            return e;
        }
        try {
            return (E) Enum.valueOf(cls, str);
        } catch (IllegalArgumentException e2) {
            return e;
        }
    }

    public static <E extends Enum<E>> E getEnumIgnoreCase(Class<E> cls, String str) {
        return (E) getEnumIgnoreCase(cls, str, null);
    }

    public static <E extends Enum<E>> E getEnumIgnoreCase(Class<E> enumClass, String enumName, E defaultEnum) {
        if (enumName == null || !enumClass.isEnum()) {
            return defaultEnum;
        }
        for (E each : enumClass.getEnumConstants()) {
            if (each.name().equalsIgnoreCase(enumName)) {
                return each;
            }
        }
        return defaultEnum;
    }

    public static <E extends Enum<E>> List<E> getEnumList(Class<E> enumClass) {
        return new ArrayList(Arrays.asList(enumClass.getEnumConstants()));
    }

    public static <E extends Enum<E>> Map<String, E> getEnumMap(Class<E> enumClass) {
        Map<String, E> map = new LinkedHashMap<>();
        for (E e : enumClass.getEnumConstants()) {
            map.put(e.name(), e);
        }
        return map;
    }

    public static <E extends Enum<E>> boolean isValidEnum(Class<E> enumClass, String enumName) {
        return getEnum(enumClass, enumName) != null;
    }

    public static <E extends Enum<E>> boolean isValidEnumIgnoreCase(Class<E> enumClass, String enumName) {
        return getEnumIgnoreCase(enumClass, enumName) != null;
    }

    public static <E extends Enum<E>> EnumSet<E> processBitVector(Class<E> enumClass, long value) {
        checkBitVectorable(enumClass).getEnumConstants();
        return processBitVectors(enumClass, value);
    }

    public static <E extends Enum<E>> EnumSet<E> processBitVectors(Class<E> enumClass, long... values) {
        EnumSet<E> results = EnumSet.noneOf(asEnum(enumClass));
        long[] lvalues = ArrayUtils.clone((long[]) Validate.notNull(values));
        ArrayUtils.reverse(lvalues);
        for (E constant : enumClass.getEnumConstants()) {
            int block = constant.ordinal() / 64;
            if (block < lvalues.length && (lvalues[block] & (1 << (constant.ordinal() % 64))) != 0) {
                results.add(constant);
            }
        }
        return results;
    }
}
