package org.apache.commons.lang3;

/* JADX INFO: loaded from: classes.dex */
public class CharSetUtils {
    public static boolean containsAny(String str, String... set) {
        if (StringUtils.isEmpty(str) || deepEmpty(set)) {
            return false;
        }
        CharSet chars = CharSet.getInstance(set);
        for (char c : str.toCharArray()) {
            if (chars.contains(c)) {
                return true;
            }
        }
        return false;
    }

    public static int count(String str, String... set) {
        if (StringUtils.isEmpty(str) || deepEmpty(set)) {
            return 0;
        }
        CharSet chars = CharSet.getInstance(set);
        int count = 0;
        for (char c : str.toCharArray()) {
            if (chars.contains(c)) {
                count++;
            }
        }
        return count;
    }

    private static boolean deepEmpty(String[] strings) {
        if (strings != null) {
            for (String s : strings) {
                if (StringUtils.isNotEmpty(s)) {
                    return false;
                }
            }
            return true;
        }
        return true;
    }

    public static String delete(String str, String... set) {
        if (StringUtils.isEmpty(str) || deepEmpty(set)) {
            return str;
        }
        return modify(str, set, false);
    }

    public static String keep(String str, String... set) {
        if (str == null) {
            return null;
        }
        if (str.isEmpty() || deepEmpty(set)) {
            return "";
        }
        return modify(str, set, true);
    }

    private static String modify(String str, String[] set, boolean expect) {
        CharSet chars = CharSet.getInstance(set);
        StringBuilder buffer = new StringBuilder(str.length());
        char[] chrs = str.toCharArray();
        for (char chr : chrs) {
            if (chars.contains(chr) == expect) {
                buffer.append(chr);
            }
        }
        return buffer.toString();
    }

    /* JADX WARN: Removed duplicated region for block: B:22:0x0050 A[PHI: r7
  0x0050: PHI (r7v2 'notInChars' java.lang.Character) = 
  (r7v1 'notInChars' java.lang.Character)
  (r7v4 'notInChars' java.lang.Character)
  (r7v1 'notInChars' java.lang.Character)
 binds: [B:10:0x002e, B:21:0x004c, B:17:0x003f] A[DONT_GENERATE, DONT_INLINE]] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static java.lang.String squeeze(java.lang.String r10, java.lang.String... r11) {
        /*
            boolean r0 = org.apache.commons.lang3.StringUtils.isEmpty(r10)
            if (r0 != 0) goto L5c
            boolean r0 = deepEmpty(r11)
            if (r0 == 0) goto Ld
            goto L5c
        Ld:
            org.apache.commons.lang3.CharSet r0 = org.apache.commons.lang3.CharSet.getInstance(r11)
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            int r2 = r10.length()
            r1.<init>(r2)
            char[] r2 = r10.toCharArray()
            int r3 = r2.length
            r4 = 0
            char r4 = r2[r4]
            r5 = 32
            r6 = 0
            r7 = 0
            r1.append(r4)
            r8 = 1
        L2a:
            if (r8 >= r3) goto L57
            char r5 = r2[r8]
            if (r5 != r4) goto L50
            if (r6 == 0) goto L39
            char r9 = r6.charValue()
            if (r5 != r9) goto L39
            goto L54
        L39:
            if (r7 == 0) goto L41
            char r9 = r7.charValue()
            if (r5 == r9) goto L50
        L41:
            boolean r9 = r0.contains(r5)
            if (r9 == 0) goto L4c
            java.lang.Character r6 = java.lang.Character.valueOf(r5)
            goto L54
        L4c:
            java.lang.Character r7 = java.lang.Character.valueOf(r5)
        L50:
            r1.append(r5)
            r4 = r5
        L54:
            int r8 = r8 + 1
            goto L2a
        L57:
            java.lang.String r8 = r1.toString()
            return r8
        L5c:
            return r10
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.CharSetUtils.squeeze(java.lang.String, java.lang.String[]):java.lang.String");
    }
}
