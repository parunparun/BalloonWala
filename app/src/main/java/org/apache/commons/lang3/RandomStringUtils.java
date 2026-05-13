package org.apache.commons.lang3;

import java.util.Random;

/* JADX INFO: loaded from: classes.dex */
public class RandomStringUtils {
    private static final Random RANDOM = new Random();

    public static String random(int count) {
        return random(count, false, false);
    }

    public static String randomAscii(int count) {
        return random(count, 32, 127, false, false);
    }

    public static String randomAscii(int minLengthInclusive, int maxLengthExclusive) {
        return randomAscii(RandomUtils.nextInt(minLengthInclusive, maxLengthExclusive));
    }

    public static String randomAlphabetic(int count) {
        return random(count, true, false);
    }

    public static String randomAlphabetic(int minLengthInclusive, int maxLengthExclusive) {
        return randomAlphabetic(RandomUtils.nextInt(minLengthInclusive, maxLengthExclusive));
    }

    public static String randomAlphanumeric(int count) {
        return random(count, true, true);
    }

    public static String randomAlphanumeric(int minLengthInclusive, int maxLengthExclusive) {
        return randomAlphanumeric(RandomUtils.nextInt(minLengthInclusive, maxLengthExclusive));
    }

    public static String randomGraph(int count) {
        return random(count, 33, 126, false, false);
    }

    public static String randomGraph(int minLengthInclusive, int maxLengthExclusive) {
        return randomGraph(RandomUtils.nextInt(minLengthInclusive, maxLengthExclusive));
    }

    public static String randomNumeric(int count) {
        return random(count, false, true);
    }

    public static String randomNumeric(int minLengthInclusive, int maxLengthExclusive) {
        return randomNumeric(RandomUtils.nextInt(minLengthInclusive, maxLengthExclusive));
    }

    public static String randomPrint(int count) {
        return random(count, 32, 126, false, false);
    }

    public static String randomPrint(int minLengthInclusive, int maxLengthExclusive) {
        return randomPrint(RandomUtils.nextInt(minLengthInclusive, maxLengthExclusive));
    }

    public static String random(int count, boolean letters, boolean numbers) {
        return random(count, 0, 0, letters, numbers);
    }

    public static String random(int count, int start, int end, boolean letters, boolean numbers) {
        return random(count, start, end, letters, numbers, null, RANDOM);
    }

    public static String random(int count, int start, int end, boolean letters, boolean numbers, char... chars) {
        return random(count, start, end, letters, numbers, chars, RANDOM);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static String random(int codePoint, int start, int end, boolean letters, boolean numbers, char[] cArr, Random random) {
        char cNextInt;
        if (codePoint == 0) {
            return "";
        }
        if (codePoint < 0) {
            throw new IllegalArgumentException("Requested random string length " + codePoint + " is less than 0.");
        }
        if (cArr != 0 && cArr.length == 0) {
            throw new IllegalArgumentException("The chars array must not be empty");
        }
        if (start == 0 && end == 0) {
            if (cArr != 0) {
                end = cArr.length;
            } else if (!letters && !numbers) {
                end = 1114111;
            } else {
                end = 123;
                start = 32;
            }
        } else if (end <= start) {
            throw new IllegalArgumentException("Parameter end (" + end + ") must be greater than start (" + start + ")");
        }
        if (cArr == 0 && ((numbers && end <= 48) || (letters && end <= 65))) {
            throw new IllegalArgumentException("Parameter end (" + end + ") must be greater then (48) for generating digits or greater then (65) for generating letters.");
        }
        StringBuilder sb = new StringBuilder(codePoint);
        int gap = end - start;
        while (true) {
            int count = codePoint - 1;
            if (codePoint != 0) {
                if (cArr == 0) {
                    cNextInt = random.nextInt(gap) + start;
                    int type = Character.getType(cNextInt);
                    if (type == 0 || type == 18 || type == 19) {
                        codePoint = count + 1;
                    }
                } else {
                    cNextInt = cArr[random.nextInt(gap) + start];
                }
                int numberOfChars = Character.charCount(cNextInt);
                if (count == 0 && numberOfChars > 1) {
                    codePoint = count + 1;
                } else if ((letters && Character.isLetter(cNextInt)) || ((numbers && Character.isDigit(cNextInt)) || (!letters && !numbers))) {
                    sb.appendCodePoint(cNextInt);
                    if (numberOfChars != 2) {
                        codePoint = count;
                    } else {
                        codePoint = count - 1;
                    }
                } else {
                    codePoint = count + 1;
                }
            } else {
                return sb.toString();
            }
        }
    }

    public static String random(int count, String chars) {
        if (chars == null) {
            return random(count, 0, 0, false, false, null, RANDOM);
        }
        return random(count, chars.toCharArray());
    }

    public static String random(int count, char... chars) {
        if (chars == null) {
            return random(count, 0, 0, false, false, null, RANDOM);
        }
        return random(count, 0, chars.length, false, false, chars, RANDOM);
    }
}
