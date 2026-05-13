package org.apache.commons.lang3;

import androidx.core.internal.view.SupportMenu;
import java.util.UUID;

/* JADX INFO: loaded from: classes.dex */
public class Conversion {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static final boolean[] TTTT = {true, true, true, true};
    private static final boolean[] FTTT = {false, true, true, true};
    private static final boolean[] TFTT = {true, false, true, true};
    private static final boolean[] FFTT = {false, false, true, true};
    private static final boolean[] TTFT = {true, true, false, true};
    private static final boolean[] FTFT = {false, true, false, true};
    private static final boolean[] TFFT = {true, false, false, true};
    private static final boolean[] FFFT = {false, false, false, true};
    private static final boolean[] TTTF = {true, true, true, false};
    private static final boolean[] FTTF = {false, true, true, false};
    private static final boolean[] TFTF = {true, false, true, false};
    private static final boolean[] FFTF = {false, false, true, false};
    private static final boolean[] TTFF = {true, true, false, false};
    private static final boolean[] FTFF = {false, true, false, false};
    private static final boolean[] TFFF = {true, false, false, false};
    private static final boolean[] FFFF = {false, false, false, false};

    public static int hexDigitToInt(char hexDigit) {
        int digit = Character.digit(hexDigit, 16);
        if (digit < 0) {
            throw new IllegalArgumentException("Cannot interpret '" + hexDigit + "' as a hexadecimal digit");
        }
        return digit;
    }

    public static int hexDigitMsb0ToInt(char hexDigit) {
        switch (hexDigit) {
            case '0':
                return 0;
            case '1':
                return 8;
            case '2':
                return 4;
            case '3':
                return 12;
            case '4':
                return 2;
            case '5':
                return 10;
            case '6':
                return 6;
            case '7':
                return 14;
            case '8':
                return 1;
            case '9':
                return 9;
            default:
                switch (hexDigit) {
                    case 'A':
                        return 5;
                    case 'B':
                        return 13;
                    case 'C':
                        return 3;
                    case 'D':
                        return 11;
                    case 'E':
                        return 7;
                    case 'F':
                        return 15;
                    default:
                        switch (hexDigit) {
                            case 'a':
                                return 5;
                            case 'b':
                                return 13;
                            case 'c':
                                return 3;
                            case 'd':
                                return 11;
                            case 'e':
                                return 7;
                            case 'f':
                                return 15;
                            default:
                                throw new IllegalArgumentException("Cannot interpret '" + hexDigit + "' as a hexadecimal digit");
                        }
                }
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:11:0x0037  */
    /* JADX WARN: Removed duplicated region for block: B:13:0x0040  */
    /* JADX WARN: Removed duplicated region for block: B:15:0x0049  */
    /* JADX WARN: Removed duplicated region for block: B:17:0x0052  */
    /* JADX WARN: Removed duplicated region for block: B:7:0x0025  */
    /* JADX WARN: Removed duplicated region for block: B:9:0x002e  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static boolean[] hexDigitToBinary(char r3) {
        /*
            Method dump skipped, instruction units count: 238
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.Conversion.hexDigitToBinary(char):boolean[]");
    }

    /* JADX WARN: Removed duplicated region for block: B:11:0x0037  */
    /* JADX WARN: Removed duplicated region for block: B:13:0x0040  */
    /* JADX WARN: Removed duplicated region for block: B:15:0x0049  */
    /* JADX WARN: Removed duplicated region for block: B:17:0x0052  */
    /* JADX WARN: Removed duplicated region for block: B:7:0x0025  */
    /* JADX WARN: Removed duplicated region for block: B:9:0x002e  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static boolean[] hexDigitMsb0ToBinary(char r3) {
        /*
            Method dump skipped, instruction units count: 238
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.Conversion.hexDigitMsb0ToBinary(char):boolean[]");
    }

    public static char binaryToHexDigit(boolean[] src) {
        return binaryToHexDigit(src, 0);
    }

    public static char binaryToHexDigit(boolean[] src, int srcPos) {
        if (src.length != 0) {
            return (src.length <= srcPos + 3 || !src[srcPos + 3]) ? (src.length <= srcPos + 2 || !src[srcPos + 2]) ? (src.length <= srcPos + 1 || !src[srcPos + 1]) ? src[srcPos] ? '1' : '0' : src[srcPos] ? '3' : '2' : src[srcPos + 1] ? src[srcPos] ? '7' : '6' : src[srcPos] ? '5' : '4' : src[srcPos + 2] ? src[srcPos + 1] ? src[srcPos] ? 'f' : 'e' : src[srcPos] ? 'd' : 'c' : src[srcPos + 1] ? src[srcPos] ? 'b' : 'a' : src[srcPos] ? '9' : '8';
        }
        throw new IllegalArgumentException("Cannot convert an empty array.");
    }

    public static char binaryToHexDigitMsb0_4bits(boolean[] src) {
        return binaryToHexDigitMsb0_4bits(src, 0);
    }

    public static char binaryToHexDigitMsb0_4bits(boolean[] src, int srcPos) {
        if (src.length > 8) {
            throw new IllegalArgumentException("src.length>8: src.length=" + src.length);
        }
        if (src.length - srcPos >= 4) {
            return src[srcPos + 3] ? src[srcPos + 2] ? src[srcPos + 1] ? src[srcPos] ? 'f' : '7' : src[srcPos] ? 'b' : '3' : src[srcPos + 1] ? src[srcPos] ? 'd' : '5' : src[srcPos] ? '9' : '1' : src[srcPos + 2] ? src[srcPos + 1] ? src[srcPos] ? 'e' : '6' : src[srcPos] ? 'a' : '2' : src[srcPos + 1] ? src[srcPos] ? 'c' : '4' : src[srcPos] ? '8' : '0';
        }
        throw new IllegalArgumentException("src.length-srcPos<4: src.length=" + src.length + ", srcPos=" + srcPos);
    }

    public static char binaryBeMsb0ToHexDigit(boolean[] src) {
        return binaryBeMsb0ToHexDigit(src, 0);
    }

    public static char binaryBeMsb0ToHexDigit(boolean[] src, int srcPos) {
        if (src.length == 0) {
            throw new IllegalArgumentException("Cannot convert an empty array.");
        }
        int beSrcPos = (src.length - 1) - srcPos;
        int srcLen = Math.min(4, beSrcPos + 1);
        boolean[] paddedSrc = new boolean[4];
        System.arraycopy(src, (beSrcPos + 1) - srcLen, paddedSrc, 4 - srcLen, srcLen);
        return paddedSrc[0] ? (paddedSrc.length <= 0 + 1 || !paddedSrc[0 + 1]) ? (paddedSrc.length <= 0 + 2 || !paddedSrc[0 + 2]) ? (paddedSrc.length <= 0 + 3 || !paddedSrc[0 + 3]) ? '8' : '9' : (paddedSrc.length <= 0 + 3 || !paddedSrc[0 + 3]) ? 'a' : 'b' : (paddedSrc.length <= 0 + 2 || !paddedSrc[0 + 2]) ? (paddedSrc.length <= 0 + 3 || !paddedSrc[0 + 3]) ? 'c' : 'd' : (paddedSrc.length <= 0 + 3 || !paddedSrc[0 + 3]) ? 'e' : 'f' : (paddedSrc.length <= 0 + 1 || !paddedSrc[0 + 1]) ? (paddedSrc.length <= 0 + 2 || !paddedSrc[0 + 2]) ? (paddedSrc.length <= 0 + 3 || !paddedSrc[0 + 3]) ? '0' : '1' : (paddedSrc.length <= 0 + 3 || !paddedSrc[0 + 3]) ? '2' : '3' : (paddedSrc.length <= 0 + 2 || !paddedSrc[0 + 2]) ? (paddedSrc.length <= 0 + 3 || !paddedSrc[0 + 3]) ? '4' : '5' : (paddedSrc.length <= 0 + 3 || !paddedSrc[0 + 3]) ? '6' : '7';
    }

    public static char intToHexDigit(int nibble) {
        char c = Character.forDigit(nibble, 16);
        if (c == 0) {
            throw new IllegalArgumentException("nibble value not between 0 and 15: " + nibble);
        }
        return c;
    }

    public static char intToHexDigitMsb0(int nibble) {
        switch (nibble) {
            case 0:
                return '0';
            case 1:
                return '8';
            case 2:
                return '4';
            case 3:
                return 'c';
            case 4:
                return '2';
            case 5:
                return 'a';
            case 6:
                return '6';
            case 7:
                return 'e';
            case 8:
                return '1';
            case 9:
                return '9';
            case 10:
                return '5';
            case 11:
                return 'd';
            case 12:
                return '3';
            case 13:
                return 'b';
            case 14:
                return '7';
            case 15:
                return 'f';
            default:
                throw new IllegalArgumentException("nibble value not between 0 and 15: " + nibble);
        }
    }

    public static long intArrayToLong(int[] src, int srcPos, long dstInit, int dstPos, int nInts) {
        if ((src.length == 0 && srcPos == 0) || nInts == 0) {
            return dstInit;
        }
        if (((nInts - 1) * 32) + dstPos >= 64) {
            throw new IllegalArgumentException("(nInts-1)*32+dstPos is greater or equal to than 64");
        }
        long out = dstInit;
        for (int i = 0; i < nInts; i++) {
            int shift = (i * 32) + dstPos;
            long bits = (((long) src[i + srcPos]) & 4294967295L) << shift;
            long mask = 4294967295 << shift;
            out = ((~mask) & out) | bits;
        }
        return out;
    }

    public static long shortArrayToLong(short[] src, int srcPos, long dstInit, int dstPos, int nShorts) {
        if ((src.length == 0 && srcPos == 0) || nShorts == 0) {
            return dstInit;
        }
        if (((nShorts - 1) * 16) + dstPos >= 64) {
            throw new IllegalArgumentException("(nShorts-1)*16+dstPos is greater or equal to than 64");
        }
        long out = dstInit;
        for (int i = 0; i < nShorts; i++) {
            int shift = (i * 16) + dstPos;
            long bits = (((long) src[i + srcPos]) & 65535) << shift;
            long mask = 65535 << shift;
            out = ((~mask) & out) | bits;
        }
        return out;
    }

    public static int shortArrayToInt(short[] src, int srcPos, int dstInit, int dstPos, int nShorts) {
        if ((src.length == 0 && srcPos == 0) || nShorts == 0) {
            return dstInit;
        }
        if (((nShorts - 1) * 16) + dstPos >= 32) {
            throw new IllegalArgumentException("(nShorts-1)*16+dstPos is greater or equal to than 32");
        }
        int out = dstInit;
        for (int i = 0; i < nShorts; i++) {
            int shift = (i * 16) + dstPos;
            int bits = (src[i + srcPos] & 65535) << shift;
            int mask = SupportMenu.USER_MASK << shift;
            out = ((~mask) & out) | bits;
        }
        return out;
    }

    public static long byteArrayToLong(byte[] src, int srcPos, long dstInit, int dstPos, int nBytes) {
        if ((src.length == 0 && srcPos == 0) || nBytes == 0) {
            return dstInit;
        }
        if (((nBytes - 1) * 8) + dstPos >= 64) {
            throw new IllegalArgumentException("(nBytes-1)*8+dstPos is greater or equal to than 64");
        }
        long out = dstInit;
        for (int i = 0; i < nBytes; i++) {
            int shift = (i * 8) + dstPos;
            long bits = (((long) src[i + srcPos]) & 255) << shift;
            long mask = 255 << shift;
            out = ((~mask) & out) | bits;
        }
        return out;
    }

    public static int byteArrayToInt(byte[] src, int srcPos, int dstInit, int dstPos, int nBytes) {
        if ((src.length == 0 && srcPos == 0) || nBytes == 0) {
            return dstInit;
        }
        if (((nBytes - 1) * 8) + dstPos >= 32) {
            throw new IllegalArgumentException("(nBytes-1)*8+dstPos is greater or equal to than 32");
        }
        int out = dstInit;
        for (int i = 0; i < nBytes; i++) {
            int shift = (i * 8) + dstPos;
            int bits = (src[i + srcPos] & 255) << shift;
            int mask = 255 << shift;
            out = ((~mask) & out) | bits;
        }
        return out;
    }

    public static short byteArrayToShort(byte[] src, int srcPos, short dstInit, int dstPos, int nBytes) {
        if ((src.length == 0 && srcPos == 0) || nBytes == 0) {
            return dstInit;
        }
        if (((nBytes - 1) * 8) + dstPos >= 16) {
            throw new IllegalArgumentException("(nBytes-1)*8+dstPos is greater or equal to than 16");
        }
        short out = dstInit;
        for (int i = 0; i < nBytes; i++) {
            int shift = (i * 8) + dstPos;
            int bits = (src[i + srcPos] & 255) << shift;
            int mask = 255 << shift;
            out = (short) (((~mask) & out) | bits);
        }
        return out;
    }

    public static long hexToLong(String src, int srcPos, long dstInit, int dstPos, int nHex) {
        if (nHex == 0) {
            return dstInit;
        }
        if (((nHex - 1) * 4) + dstPos >= 64) {
            throw new IllegalArgumentException("(nHexs-1)*4+dstPos is greater or equal to than 64");
        }
        long out = dstInit;
        for (int i = 0; i < nHex; i++) {
            int shift = (i * 4) + dstPos;
            long bits = (((long) hexDigitToInt(src.charAt(i + srcPos))) & 15) << shift;
            long mask = 15 << shift;
            out = ((~mask) & out) | bits;
        }
        return out;
    }

    public static int hexToInt(String src, int srcPos, int dstInit, int dstPos, int nHex) {
        if (nHex == 0) {
            return dstInit;
        }
        if (((nHex - 1) * 4) + dstPos >= 32) {
            throw new IllegalArgumentException("(nHexs-1)*4+dstPos is greater or equal to than 32");
        }
        int out = dstInit;
        for (int i = 0; i < nHex; i++) {
            int shift = (i * 4) + dstPos;
            int bits = (hexDigitToInt(src.charAt(i + srcPos)) & 15) << shift;
            int mask = 15 << shift;
            out = ((~mask) & out) | bits;
        }
        return out;
    }

    public static short hexToShort(String src, int srcPos, short dstInit, int dstPos, int nHex) {
        if (nHex == 0) {
            return dstInit;
        }
        if (((nHex - 1) * 4) + dstPos >= 16) {
            throw new IllegalArgumentException("(nHexs-1)*4+dstPos is greater or equal to than 16");
        }
        short out = dstInit;
        for (int i = 0; i < nHex; i++) {
            int shift = (i * 4) + dstPos;
            int bits = (hexDigitToInt(src.charAt(i + srcPos)) & 15) << shift;
            int mask = 15 << shift;
            out = (short) (((~mask) & out) | bits);
        }
        return out;
    }

    public static byte hexToByte(String src, int srcPos, byte dstInit, int dstPos, int nHex) {
        if (nHex == 0) {
            return dstInit;
        }
        if (((nHex - 1) * 4) + dstPos >= 8) {
            throw new IllegalArgumentException("(nHexs-1)*4+dstPos is greater or equal to than 8");
        }
        byte out = dstInit;
        for (int i = 0; i < nHex; i++) {
            int shift = (i * 4) + dstPos;
            int bits = (hexDigitToInt(src.charAt(i + srcPos)) & 15) << shift;
            int mask = 15 << shift;
            out = (byte) (((~mask) & out) | bits);
        }
        return out;
    }

    public static long binaryToLong(boolean[] src, int srcPos, long dstInit, int dstPos, int nBools) {
        if ((src.length == 0 && srcPos == 0) || nBools == 0) {
            return dstInit;
        }
        if ((nBools - 1) + dstPos >= 64) {
            throw new IllegalArgumentException("nBools-1+dstPos is greater or equal to than 64");
        }
        long out = dstInit;
        for (int i = 0; i < nBools; i++) {
            int shift = i + dstPos;
            long bits = (src[i + srcPos] ? 1L : 0L) << shift;
            long mask = 1 << shift;
            out = ((~mask) & out) | bits;
        }
        return out;
    }

    public static int binaryToInt(boolean[] zArr, int i, int i2, int i3, int i4) {
        if ((zArr.length == 0 && i == 0) || i4 == 0) {
            return i2;
        }
        if ((i4 - 1) + i3 >= 32) {
            throw new IllegalArgumentException("nBools-1+dstPos is greater or equal to than 32");
        }
        int i5 = i2;
        for (int i6 = 0; i6 < i4; i6++) {
            int i7 = i6 + i3;
            i5 = ((~(1 << i7)) & i5) | ((zArr[i6 + i] ? 1 : 0) << i7);
        }
        return i5;
    }

    public static short binaryToShort(boolean[] zArr, int i, short s, int i2, int i3) {
        if ((zArr.length == 0 && i == 0) || i3 == 0) {
            return s;
        }
        if ((i3 - 1) + i2 >= 16) {
            throw new IllegalArgumentException("nBools-1+dstPos is greater or equal to than 16");
        }
        short s2 = s;
        for (int i4 = 0; i4 < i3; i4++) {
            int i5 = i4 + i2;
            s2 = (short) (((~(1 << i5)) & s2) | ((zArr[i4 + i] ? 1 : 0) << i5));
        }
        return s2;
    }

    public static byte binaryToByte(boolean[] zArr, int i, byte b, int i2, int i3) {
        if ((zArr.length == 0 && i == 0) || i3 == 0) {
            return b;
        }
        if ((i3 - 1) + i2 >= 8) {
            throw new IllegalArgumentException("nBools-1+dstPos is greater or equal to than 8");
        }
        byte b2 = b;
        for (int i4 = 0; i4 < i3; i4++) {
            int i5 = i4 + i2;
            b2 = (byte) (((~(1 << i5)) & b2) | ((zArr[i4 + i] ? 1 : 0) << i5));
        }
        return b2;
    }

    public static int[] longToIntArray(long src, int srcPos, int[] dst, int dstPos, int nInts) {
        if (nInts == 0) {
            return dst;
        }
        if (((nInts - 1) * 32) + srcPos >= 64) {
            throw new IllegalArgumentException("(nInts-1)*32+srcPos is greater or equal to than 64");
        }
        for (int i = 0; i < nInts; i++) {
            int shift = (i * 32) + srcPos;
            dst[dstPos + i] = (int) ((-1) & (src >> shift));
        }
        return dst;
    }

    public static short[] longToShortArray(long src, int srcPos, short[] dst, int dstPos, int nShorts) {
        if (nShorts == 0) {
            return dst;
        }
        if (((nShorts - 1) * 16) + srcPos >= 64) {
            throw new IllegalArgumentException("(nShorts-1)*16+srcPos is greater or equal to than 64");
        }
        for (int i = 0; i < nShorts; i++) {
            int shift = (i * 16) + srcPos;
            dst[dstPos + i] = (short) (65535 & (src >> shift));
        }
        return dst;
    }

    public static short[] intToShortArray(int src, int srcPos, short[] dst, int dstPos, int nShorts) {
        if (nShorts == 0) {
            return dst;
        }
        if (((nShorts - 1) * 16) + srcPos >= 32) {
            throw new IllegalArgumentException("(nShorts-1)*16+srcPos is greater or equal to than 32");
        }
        for (int i = 0; i < nShorts; i++) {
            int shift = (i * 16) + srcPos;
            dst[dstPos + i] = (short) (65535 & (src >> shift));
        }
        return dst;
    }

    public static byte[] longToByteArray(long src, int srcPos, byte[] dst, int dstPos, int nBytes) {
        if (nBytes == 0) {
            return dst;
        }
        if (((nBytes - 1) * 8) + srcPos >= 64) {
            throw new IllegalArgumentException("(nBytes-1)*8+srcPos is greater or equal to than 64");
        }
        for (int i = 0; i < nBytes; i++) {
            int shift = (i * 8) + srcPos;
            dst[dstPos + i] = (byte) (255 & (src >> shift));
        }
        return dst;
    }

    public static byte[] intToByteArray(int src, int srcPos, byte[] dst, int dstPos, int nBytes) {
        if (nBytes == 0) {
            return dst;
        }
        if (((nBytes - 1) * 8) + srcPos >= 32) {
            throw new IllegalArgumentException("(nBytes-1)*8+srcPos is greater or equal to than 32");
        }
        for (int i = 0; i < nBytes; i++) {
            int shift = (i * 8) + srcPos;
            dst[dstPos + i] = (byte) ((src >> shift) & 255);
        }
        return dst;
    }

    public static byte[] shortToByteArray(short src, int srcPos, byte[] dst, int dstPos, int nBytes) {
        if (nBytes == 0) {
            return dst;
        }
        if (((nBytes - 1) * 8) + srcPos >= 16) {
            throw new IllegalArgumentException("(nBytes-1)*8+srcPos is greater or equal to than 16");
        }
        for (int i = 0; i < nBytes; i++) {
            int shift = (i * 8) + srcPos;
            dst[dstPos + i] = (byte) ((src >> shift) & 255);
        }
        return dst;
    }

    public static String longToHex(long src, int srcPos, String dstInit, int dstPos, int nHexs) {
        if (nHexs == 0) {
            return dstInit;
        }
        if (((nHexs - 1) * 4) + srcPos >= 64) {
            throw new IllegalArgumentException("(nHexs-1)*4+srcPos is greater or equal to than 64");
        }
        StringBuilder sb = new StringBuilder(dstInit);
        int append = sb.length();
        for (int i = 0; i < nHexs; i++) {
            int shift = (i * 4) + srcPos;
            int bits = (int) (15 & (src >> shift));
            if (dstPos + i == append) {
                append++;
                sb.append(intToHexDigit(bits));
            } else {
                sb.setCharAt(dstPos + i, intToHexDigit(bits));
            }
        }
        return sb.toString();
    }

    public static String intToHex(int src, int srcPos, String dstInit, int dstPos, int nHexs) {
        if (nHexs == 0) {
            return dstInit;
        }
        if (((nHexs - 1) * 4) + srcPos >= 32) {
            throw new IllegalArgumentException("(nHexs-1)*4+srcPos is greater or equal to than 32");
        }
        StringBuilder sb = new StringBuilder(dstInit);
        int append = sb.length();
        for (int i = 0; i < nHexs; i++) {
            int shift = (i * 4) + srcPos;
            int bits = (src >> shift) & 15;
            if (dstPos + i == append) {
                append++;
                sb.append(intToHexDigit(bits));
            } else {
                sb.setCharAt(dstPos + i, intToHexDigit(bits));
            }
        }
        return sb.toString();
    }

    public static String shortToHex(short src, int srcPos, String dstInit, int dstPos, int nHexs) {
        if (nHexs == 0) {
            return dstInit;
        }
        if (((nHexs - 1) * 4) + srcPos >= 16) {
            throw new IllegalArgumentException("(nHexs-1)*4+srcPos is greater or equal to than 16");
        }
        StringBuilder sb = new StringBuilder(dstInit);
        int append = sb.length();
        for (int i = 0; i < nHexs; i++) {
            int shift = (i * 4) + srcPos;
            int bits = (src >> shift) & 15;
            if (dstPos + i == append) {
                append++;
                sb.append(intToHexDigit(bits));
            } else {
                sb.setCharAt(dstPos + i, intToHexDigit(bits));
            }
        }
        return sb.toString();
    }

    public static String byteToHex(byte src, int srcPos, String dstInit, int dstPos, int nHexs) {
        if (nHexs == 0) {
            return dstInit;
        }
        if (((nHexs - 1) * 4) + srcPos >= 8) {
            throw new IllegalArgumentException("(nHexs-1)*4+srcPos is greater or equal to than 8");
        }
        StringBuilder sb = new StringBuilder(dstInit);
        int append = sb.length();
        for (int i = 0; i < nHexs; i++) {
            int shift = (i * 4) + srcPos;
            int bits = (src >> shift) & 15;
            if (dstPos + i == append) {
                append++;
                sb.append(intToHexDigit(bits));
            } else {
                sb.setCharAt(dstPos + i, intToHexDigit(bits));
            }
        }
        return sb.toString();
    }

    public static boolean[] longToBinary(long src, int srcPos, boolean[] dst, int dstPos, int nBools) {
        if (nBools == 0) {
            return dst;
        }
        if ((nBools - 1) + srcPos >= 64) {
            throw new IllegalArgumentException("nBools-1+srcPos is greater or equal to than 64");
        }
        for (int i = 0; i < nBools; i++) {
            int shift = i + srcPos;
            dst[dstPos + i] = (1 & (src >> shift)) != 0;
        }
        return dst;
    }

    public static boolean[] intToBinary(int src, int srcPos, boolean[] dst, int dstPos, int nBools) {
        if (nBools == 0) {
            return dst;
        }
        if ((nBools - 1) + srcPos >= 32) {
            throw new IllegalArgumentException("nBools-1+srcPos is greater or equal to than 32");
        }
        for (int i = 0; i < nBools; i++) {
            int shift = i + srcPos;
            int i2 = dstPos + i;
            boolean z = true;
            if (((src >> shift) & 1) == 0) {
                z = false;
            }
            dst[i2] = z;
        }
        return dst;
    }

    public static boolean[] shortToBinary(short src, int srcPos, boolean[] dst, int dstPos, int nBools) {
        if (nBools == 0) {
            return dst;
        }
        if ((nBools - 1) + srcPos >= 16) {
            throw new IllegalArgumentException("nBools-1+srcPos is greater or equal to than 16");
        }
        for (int i = 0; i < nBools; i++) {
            int shift = i + srcPos;
            int i2 = dstPos + i;
            boolean z = true;
            if (((src >> shift) & 1) == 0) {
                z = false;
            }
            dst[i2] = z;
        }
        return dst;
    }

    public static boolean[] byteToBinary(byte src, int srcPos, boolean[] dst, int dstPos, int nBools) {
        if (nBools == 0) {
            return dst;
        }
        if ((nBools - 1) + srcPos >= 8) {
            throw new IllegalArgumentException("nBools-1+srcPos is greater or equal to than 8");
        }
        for (int i = 0; i < nBools; i++) {
            int shift = i + srcPos;
            int i2 = dstPos + i;
            boolean z = true;
            if (((src >> shift) & 1) == 0) {
                z = false;
            }
            dst[i2] = z;
        }
        return dst;
    }

    public static byte[] uuidToByteArray(UUID src, byte[] dst, int dstPos, int nBytes) {
        if (nBytes == 0) {
            return dst;
        }
        if (nBytes > 16) {
            throw new IllegalArgumentException("nBytes is greater than 16");
        }
        longToByteArray(src.getMostSignificantBits(), 0, dst, dstPos, nBytes > 8 ? 8 : nBytes);
        if (nBytes >= 8) {
            longToByteArray(src.getLeastSignificantBits(), 0, dst, dstPos + 8, nBytes - 8);
        }
        return dst;
    }

    public static UUID byteArrayToUuid(byte[] src, int srcPos) {
        if (src.length - srcPos < 16) {
            throw new IllegalArgumentException("Need at least 16 bytes for UUID");
        }
        return new UUID(byteArrayToLong(src, srcPos, 0L, 0, 8), byteArrayToLong(src, srcPos + 8, 0L, 0, 8));
    }
}
