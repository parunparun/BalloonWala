package org.apache.commons.lang3.exception;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

/* JADX INFO: loaded from: classes.dex */
public class ExceptionUtils {
    private static final String[] CAUSE_METHOD_NAMES = {"getCause", "getNextException", "getTargetException", "getException", "getSourceException", "getRootCause", "getCausedByException", "getNested", "getLinkedException", "getNestedException", "getLinkedCause", "getThrowable"};
    private static final int NOT_FOUND = -1;
    static final String WRAPPED_MARKER = " [wrapped] ";

    @Deprecated
    public static Throwable getCause(Throwable throwable) {
        return getCause(throwable, null);
    }

    @Deprecated
    public static Throwable getCause(Throwable throwable, String[] methodNames) {
        Throwable legacyCause;
        if (throwable == null) {
            return null;
        }
        if (methodNames == null) {
            Throwable cause = throwable.getCause();
            if (cause != null) {
                return cause;
            }
            methodNames = CAUSE_METHOD_NAMES;
        }
        for (String methodName : methodNames) {
            if (methodName != null && (legacyCause = getCauseUsingMethodName(throwable, methodName)) != null) {
                return legacyCause;
            }
        }
        return null;
    }

    private static Throwable getCauseUsingMethodName(Throwable throwable, String methodName) throws NoSuchMethodException {
        Method method = null;
        try {
            method = throwable.getClass().getMethod(methodName, new Class[0]);
        } catch (NoSuchMethodException e) {
        } catch (SecurityException e2) {
        }
        if (method != null && Throwable.class.isAssignableFrom(method.getReturnType())) {
            try {
                return (Throwable) method.invoke(throwable, new Object[0]);
            } catch (IllegalAccessException e3) {
                return null;
            } catch (IllegalArgumentException e4) {
                return null;
            } catch (InvocationTargetException e5) {
                return null;
            }
        }
        return null;
    }

    @Deprecated
    public static String[] getDefaultCauseMethodNames() {
        return (String[]) ArrayUtils.clone(CAUSE_METHOD_NAMES);
    }

    public static String getMessage(Throwable th) {
        if (th == null) {
            return "";
        }
        String clsName = ClassUtils.getShortClassName(th, null);
        String msg = th.getMessage();
        return clsName + ": " + StringUtils.defaultString(msg);
    }

    public static Throwable getRootCause(Throwable throwable) {
        List<Throwable> list = getThrowableList(throwable);
        if (list.isEmpty()) {
            return null;
        }
        return list.get(list.size() - 1);
    }

    public static String getRootCauseMessage(Throwable th) {
        Throwable root = getRootCause(th);
        return getMessage(root == null ? th : root);
    }

    public static String[] getRootCauseStackTrace(Throwable throwable) {
        if (throwable == null) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }
        Throwable[] throwables = getThrowables(throwable);
        int count = throwables.length;
        List<String> frames = new ArrayList<>();
        List<String> nextTrace = getStackFrameList(throwables[count - 1]);
        int i = count;
        while (true) {
            i--;
            if (i >= 0) {
                List<String> trace = nextTrace;
                if (i != 0) {
                    nextTrace = getStackFrameList(throwables[i - 1]);
                    removeCommonFrames(trace, nextTrace);
                }
                if (i == count - 1) {
                    frames.add(throwables[i].toString());
                } else {
                    frames.add(WRAPPED_MARKER + throwables[i].toString());
                }
                frames.addAll(trace);
            } else {
                return (String[]) frames.toArray(ArrayUtils.EMPTY_STRING_ARRAY);
            }
        }
    }

    static List<String> getStackFrameList(Throwable t) {
        String stackTrace = getStackTrace(t);
        String linebreak = System.lineSeparator();
        StringTokenizer frames = new StringTokenizer(stackTrace, linebreak);
        List<String> list = new ArrayList<>();
        boolean traceStarted = false;
        while (frames.hasMoreTokens()) {
            String token = frames.nextToken();
            int at = token.indexOf("at");
            if (at != -1 && token.substring(0, at).trim().isEmpty()) {
                traceStarted = true;
                list.add(token);
            } else if (traceStarted) {
                break;
            }
        }
        return list;
    }

    static String[] getStackFrames(String stackTrace) {
        String linebreak = System.lineSeparator();
        StringTokenizer frames = new StringTokenizer(stackTrace, linebreak);
        List<String> list = new ArrayList<>();
        while (frames.hasMoreTokens()) {
            list.add(frames.nextToken());
        }
        return (String[]) list.toArray(ArrayUtils.EMPTY_STRING_ARRAY);
    }

    public static String[] getStackFrames(Throwable throwable) {
        if (throwable == null) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }
        return getStackFrames(getStackTrace(throwable));
    }

    public static String getStackTrace(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter((Writer) sw, true);
        throwable.printStackTrace(pw);
        return sw.getBuffer().toString();
    }

    public static int getThrowableCount(Throwable throwable) {
        return getThrowableList(throwable).size();
    }

    public static List<Throwable> getThrowableList(Throwable throwable) {
        List<Throwable> list = new ArrayList<>();
        while (throwable != null && !list.contains(throwable)) {
            list.add(throwable);
            throwable = throwable.getCause();
        }
        return list;
    }

    public static Throwable[] getThrowables(Throwable throwable) {
        List<Throwable> list = getThrowableList(throwable);
        return (Throwable[]) list.toArray(ArrayUtils.EMPTY_THROWABLE_ARRAY);
    }

    public static boolean hasCause(Throwable chain, Class<? extends Throwable> type) {
        if (chain instanceof UndeclaredThrowableException) {
            chain = chain.getCause();
        }
        return type.isInstance(chain);
    }

    private static int indexOf(Throwable throwable, Class<? extends Throwable> type, int fromIndex, boolean subclass) {
        if (throwable == null || type == null) {
            return -1;
        }
        if (fromIndex < 0) {
            fromIndex = 0;
        }
        Throwable[] throwables = getThrowables(throwable);
        if (fromIndex >= throwables.length) {
            return -1;
        }
        if (subclass) {
            for (int i = fromIndex; i < throwables.length; i++) {
                if (type.isAssignableFrom(throwables[i].getClass())) {
                    return i;
                }
            }
        } else {
            for (int i2 = fromIndex; i2 < throwables.length; i2++) {
                if (type.equals(throwables[i2].getClass())) {
                    return i2;
                }
            }
        }
        return -1;
    }

    public static int indexOfThrowable(Throwable throwable, Class<? extends Throwable> clazz) {
        return indexOf(throwable, clazz, 0, false);
    }

    public static int indexOfThrowable(Throwable throwable, Class<? extends Throwable> clazz, int fromIndex) {
        return indexOf(throwable, clazz, fromIndex, false);
    }

    public static int indexOfType(Throwable throwable, Class<? extends Throwable> type) {
        return indexOf(throwable, type, 0, true);
    }

    public static int indexOfType(Throwable throwable, Class<? extends Throwable> type, int fromIndex) {
        return indexOf(throwable, type, fromIndex, true);
    }

    public static void printRootCauseStackTrace(Throwable throwable) {
        printRootCauseStackTrace(throwable, System.err);
    }

    public static void printRootCauseStackTrace(Throwable throwable, PrintStream stream) {
        if (throwable == null) {
            return;
        }
        Validate.notNull(stream, "The PrintStream must not be null", new Object[0]);
        String[] trace = getRootCauseStackTrace(throwable);
        for (String element : trace) {
            stream.println(element);
        }
        stream.flush();
    }

    public static void printRootCauseStackTrace(Throwable throwable, PrintWriter writer) {
        if (throwable == null) {
            return;
        }
        Validate.notNull(writer, "The PrintWriter must not be null", new Object[0]);
        String[] trace = getRootCauseStackTrace(throwable);
        for (String element : trace) {
            writer.println(element);
        }
        writer.flush();
    }

    public static void removeCommonFrames(List<String> causeFrames, List<String> wrapperFrames) {
        if (causeFrames == null || wrapperFrames == null) {
            throw new IllegalArgumentException("The List must not be null");
        }
        int causeFrameIndex = causeFrames.size() - 1;
        for (int wrapperFrameIndex = wrapperFrames.size() - 1; causeFrameIndex >= 0 && wrapperFrameIndex >= 0; wrapperFrameIndex--) {
            String causeFrame = causeFrames.get(causeFrameIndex);
            String wrapperFrame = wrapperFrames.get(wrapperFrameIndex);
            if (causeFrame.equals(wrapperFrame)) {
                causeFrames.remove(causeFrameIndex);
            }
            causeFrameIndex--;
        }
    }

    public static <R> R rethrow(Throwable th) {
        return (R) typeErasure(th);
    }

    private static <T extends Throwable> T throwableOf(Throwable throwable, Class<T> type, int fromIndex, boolean subclass) {
        if (throwable == null || type == null) {
            return null;
        }
        if (fromIndex < 0) {
            fromIndex = 0;
        }
        Throwable[] throwables = getThrowables(throwable);
        if (fromIndex >= throwables.length) {
            return null;
        }
        if (subclass) {
            for (int i = fromIndex; i < throwables.length; i++) {
                if (type.isAssignableFrom(throwables[i].getClass())) {
                    return type.cast(throwables[i]);
                }
            }
        } else {
            for (int i2 = fromIndex; i2 < throwables.length; i2++) {
                if (type.equals(throwables[i2].getClass())) {
                    return type.cast(throwables[i2]);
                }
            }
        }
        return null;
    }

    public static <T extends Throwable> T throwableOfThrowable(Throwable th, Class<T> cls) {
        return (T) throwableOf(th, cls, 0, false);
    }

    public static <T extends Throwable> T throwableOfThrowable(Throwable th, Class<T> cls, int i) {
        return (T) throwableOf(th, cls, i, false);
    }

    public static <T extends Throwable> T throwableOfType(Throwable th, Class<T> cls) {
        return (T) throwableOf(th, cls, 0, true);
    }

    public static <T extends Throwable> T throwableOfType(Throwable th, Class<T> cls, int i) {
        return (T) throwableOf(th, cls, i, true);
    }

    private static <R, T extends Throwable> R typeErasure(Throwable throwable) throws Throwable {
        throw throwable;
    }

    public static <R> R wrapAndThrow(Throwable throwable) {
        if (throwable instanceof RuntimeException) {
            throw ((RuntimeException) throwable);
        }
        if (throwable instanceof Error) {
            throw ((Error) throwable);
        }
        throw new UndeclaredThrowableException(throwable);
    }
}
