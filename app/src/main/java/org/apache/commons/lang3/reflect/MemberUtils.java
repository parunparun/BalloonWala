package org.apache.commons.lang3.reflect;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.apache.commons.lang3.ClassUtils;

/* JADX INFO: loaded from: classes.dex */
abstract class MemberUtils {
    private static final int ACCESS_TEST = 7;
    private static final Class<?>[] ORDERED_PRIMITIVE_TYPES = {Byte.TYPE, Short.TYPE, Character.TYPE, Integer.TYPE, Long.TYPE, Float.TYPE, Double.TYPE};

    MemberUtils() {
    }

    /* JADX WARN: Multi-variable type inference failed */
    static boolean setAccessibleWorkaround(AccessibleObject accessibleObject) {
        if (accessibleObject == 0 || accessibleObject.isAccessible()) {
            return false;
        }
        Member m = (Member) accessibleObject;
        if (!accessibleObject.isAccessible() && Modifier.isPublic(m.getModifiers()) && isPackageAccess(m.getDeclaringClass().getModifiers())) {
            try {
                accessibleObject.setAccessible(true);
                return true;
            } catch (SecurityException e) {
            }
        }
        return false;
    }

    static boolean isPackageAccess(int modifiers) {
        return (modifiers & 7) == 0;
    }

    static boolean isAccessible(Member m) {
        return (m == null || !Modifier.isPublic(m.getModifiers()) || m.isSynthetic()) ? false : true;
    }

    static int compareConstructorFit(Constructor<?> left, Constructor<?> right, Class<?>[] actual) {
        return compareParameterTypes(Executable.of(left), Executable.of(right), actual);
    }

    static int compareMethodFit(Method left, Method right, Class<?>[] actual) {
        return compareParameterTypes(Executable.of(left), Executable.of(right), actual);
    }

    private static int compareParameterTypes(Executable left, Executable right, Class<?>[] actual) {
        float leftCost = getTotalTransformationCost(actual, left);
        float rightCost = getTotalTransformationCost(actual, right);
        return Float.compare(leftCost, rightCost);
    }

    private static float getTotalTransformationCost(Class<?>[] srcArgs, Executable executable) {
        Class<?>[] destArgs = executable.getParameterTypes();
        boolean isVarArgs = executable.isVarArgs();
        float totalCost = 0.0f;
        int length = destArgs.length;
        if (isVarArgs) {
            length--;
        }
        long normalArgsLen = length;
        if (srcArgs.length < normalArgsLen) {
            return Float.MAX_VALUE;
        }
        for (int i = 0; i < normalArgsLen; i++) {
            totalCost += getObjectTransformationCost(srcArgs[i], destArgs[i]);
        }
        if (isVarArgs) {
            boolean z = false;
            boolean noVarArgsPassed = srcArgs.length < destArgs.length;
            if (srcArgs.length == destArgs.length && srcArgs[srcArgs.length - 1] != null && srcArgs[srcArgs.length - 1].isArray()) {
                z = true;
            }
            boolean explicitArrayForVarags = z;
            Class<?> destClass = destArgs[destArgs.length - 1].getComponentType();
            if (noVarArgsPassed) {
                return totalCost + getObjectTransformationCost(destClass, Object.class) + 0.001f;
            }
            if (explicitArrayForVarags) {
                Class<?> sourceClass = srcArgs[srcArgs.length - 1].getComponentType();
                return totalCost + getObjectTransformationCost(sourceClass, destClass) + 0.001f;
            }
            for (int i2 = destArgs.length - 1; i2 < srcArgs.length; i2++) {
                Class<?> srcClass = srcArgs[i2];
                totalCost += getObjectTransformationCost(srcClass, destClass) + 0.001f;
            }
            return totalCost;
        }
        return totalCost;
    }

    private static float getObjectTransformationCost(Class<?> srcClass, Class<?> destClass) {
        if (destClass.isPrimitive()) {
            return getPrimitivePromotionCost(srcClass, destClass);
        }
        float cost = 0.0f;
        while (true) {
            if (srcClass != null && !destClass.equals(srcClass)) {
                if (destClass.isInterface() && ClassUtils.isAssignable(srcClass, destClass)) {
                    cost += 0.25f;
                    break;
                }
                cost += 1.0f;
                srcClass = srcClass.getSuperclass();
            } else {
                break;
            }
        }
        if (srcClass == null) {
            return cost + 1.5f;
        }
        return cost;
    }

    private static float getPrimitivePromotionCost(Class<?> srcClass, Class<?> destClass) {
        if (srcClass == null) {
            return 1.5f;
        }
        float cost = 0.0f;
        Class<?> cls = srcClass;
        if (!cls.isPrimitive()) {
            cost = 0.0f + 0.1f;
            cls = ClassUtils.wrapperToPrimitive(cls);
        }
        int i = 0;
        while (cls != destClass) {
            Class<?>[] clsArr = ORDERED_PRIMITIVE_TYPES;
            if (i >= clsArr.length) {
                break;
            }
            if (cls == clsArr[i]) {
                cost += 0.1f;
                if (i < clsArr.length - 1) {
                    cls = clsArr[i + 1];
                }
            }
            i++;
        }
        return cost;
    }

    static boolean isMatchingMethod(Method method, Class<?>[] parameterTypes) {
        return isMatchingExecutable(Executable.of(method), parameterTypes);
    }

    static boolean isMatchingConstructor(Constructor<?> method, Class<?>[] parameterTypes) {
        return isMatchingExecutable(Executable.of(method), parameterTypes);
    }

    private static boolean isMatchingExecutable(Executable method, Class<?>[] parameterTypes) {
        Class<?>[] methodParameterTypes = method.getParameterTypes();
        if (ClassUtils.isAssignable(parameterTypes, methodParameterTypes, true)) {
            return true;
        }
        if (!method.isVarArgs()) {
            return false;
        }
        int i = 0;
        while (i < methodParameterTypes.length - 1 && i < parameterTypes.length) {
            if (!ClassUtils.isAssignable(parameterTypes[i], methodParameterTypes[i], true)) {
                return false;
            }
            i++;
        }
        Class<?> varArgParameterType = methodParameterTypes[methodParameterTypes.length - 1].getComponentType();
        while (i < parameterTypes.length) {
            if (!ClassUtils.isAssignable(parameterTypes[i], varArgParameterType, true)) {
                return false;
            }
            i++;
        }
        return true;
    }

    private static final class Executable {
        private final boolean isVarArgs;
        private final Class<?>[] parameterTypes;

        /* JADX INFO: Access modifiers changed from: private */
        public static Executable of(Method method) {
            return new Executable(method);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static Executable of(Constructor<?> constructor) {
            return new Executable(constructor);
        }

        private Executable(Method method) {
            this.parameterTypes = method.getParameterTypes();
            this.isVarArgs = method.isVarArgs();
        }

        private Executable(Constructor<?> constructor) {
            this.parameterTypes = constructor.getParameterTypes();
            this.isVarArgs = constructor.isVarArgs();
        }

        public Class<?>[] getParameterTypes() {
            return this.parameterTypes;
        }

        public boolean isVarArgs() {
            return this.isVarArgs;
        }
    }
}
