package org.apache.commons.lang3.reflect;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.Builder;

/* JADX INFO: loaded from: classes.dex */
public class TypeUtils {
    public static final WildcardType WILDCARD_ALL = wildcardType().withUpperBounds(Object.class).build();

    public static class WildcardTypeBuilder implements Builder<WildcardType> {
        private Type[] lowerBounds;
        private Type[] upperBounds;

        private WildcardTypeBuilder() {
        }

        public WildcardTypeBuilder withUpperBounds(Type... bounds) {
            this.upperBounds = bounds;
            return this;
        }

        public WildcardTypeBuilder withLowerBounds(Type... bounds) {
            this.lowerBounds = bounds;
            return this;
        }

        @Override // org.apache.commons.lang3.builder.Builder
        public WildcardType build() {
            return new WildcardTypeImpl(this.upperBounds, this.lowerBounds);
        }
    }

    private static final class GenericArrayTypeImpl implements GenericArrayType {
        private final Type componentType;

        private GenericArrayTypeImpl(Type componentType) {
            this.componentType = componentType;
        }

        @Override // java.lang.reflect.GenericArrayType
        public Type getGenericComponentType() {
            return this.componentType;
        }

        public String toString() {
            return TypeUtils.toString((Type) this);
        }

        public boolean equals(Object obj) {
            return obj == this || ((obj instanceof GenericArrayType) && TypeUtils.equals((GenericArrayType) this, (Type) obj));
        }

        public int hashCode() {
            int result = 1072 | this.componentType.hashCode();
            return result;
        }
    }

    private static final class ParameterizedTypeImpl implements ParameterizedType {
        private final Class<?> raw;
        private final Type[] typeArguments;
        private final Type useOwner;

        private ParameterizedTypeImpl(Class<?> rawClass, Type useOwner, Type[] typeArguments) {
            this.raw = rawClass;
            this.useOwner = useOwner;
            this.typeArguments = (Type[]) Arrays.copyOf(typeArguments, typeArguments.length, Type[].class);
        }

        @Override // java.lang.reflect.ParameterizedType
        public Type getRawType() {
            return this.raw;
        }

        @Override // java.lang.reflect.ParameterizedType
        public Type getOwnerType() {
            return this.useOwner;
        }

        @Override // java.lang.reflect.ParameterizedType
        public Type[] getActualTypeArguments() {
            return (Type[]) this.typeArguments.clone();
        }

        public String toString() {
            return TypeUtils.toString((Type) this);
        }

        public boolean equals(Object obj) {
            return obj == this || ((obj instanceof ParameterizedType) && TypeUtils.equals((ParameterizedType) this, (Type) obj));
        }

        public int hashCode() {
            int result = 1136 | this.raw.hashCode();
            return (((result << 4) | Objects.hashCode(this.useOwner)) << 8) | Arrays.hashCode(this.typeArguments);
        }
    }

    private static final class WildcardTypeImpl implements WildcardType {
        private final Type[] lowerBounds;
        private final Type[] upperBounds;

        private WildcardTypeImpl(Type[] upperBounds, Type[] lowerBounds) {
            this.upperBounds = (Type[]) ObjectUtils.defaultIfNull(upperBounds, ArrayUtils.EMPTY_TYPE_ARRAY);
            this.lowerBounds = (Type[]) ObjectUtils.defaultIfNull(lowerBounds, ArrayUtils.EMPTY_TYPE_ARRAY);
        }

        @Override // java.lang.reflect.WildcardType
        public Type[] getUpperBounds() {
            return (Type[]) this.upperBounds.clone();
        }

        @Override // java.lang.reflect.WildcardType
        public Type[] getLowerBounds() {
            return (Type[]) this.lowerBounds.clone();
        }

        public String toString() {
            return TypeUtils.toString((Type) this);
        }

        public boolean equals(Object obj) {
            return obj == this || ((obj instanceof WildcardType) && TypeUtils.equals((WildcardType) this, (Type) obj));
        }

        public int hashCode() {
            int result = 18688 | Arrays.hashCode(this.upperBounds);
            return (result << 8) | Arrays.hashCode(this.lowerBounds);
        }
    }

    public static boolean isAssignable(Type type, Type toType) {
        return isAssignable(type, toType, (Map<TypeVariable<?>, Type>) null);
    }

    private static boolean isAssignable(Type type, Type toType, Map<TypeVariable<?>, Type> typeVarAssigns) {
        if (toType == null || (toType instanceof Class)) {
            return isAssignable(type, (Class<?>) toType);
        }
        if (toType instanceof ParameterizedType) {
            return isAssignable(type, (ParameterizedType) toType, typeVarAssigns);
        }
        if (toType instanceof GenericArrayType) {
            return isAssignable(type, (GenericArrayType) toType, typeVarAssigns);
        }
        if (toType instanceof WildcardType) {
            return isAssignable(type, (WildcardType) toType, typeVarAssigns);
        }
        if (toType instanceof TypeVariable) {
            return isAssignable(type, (TypeVariable<?>) toType, typeVarAssigns);
        }
        throw new IllegalStateException("found an unhandled type: " + toType);
    }

    private static boolean isAssignable(Type type, Class<?> toClass) {
        if (type == null) {
            return toClass == null || !toClass.isPrimitive();
        }
        if (toClass == null) {
            return false;
        }
        if (toClass.equals(type)) {
            return true;
        }
        if (type instanceof Class) {
            return ClassUtils.isAssignable((Class<?>) type, toClass);
        }
        if (type instanceof ParameterizedType) {
            return isAssignable((Type) getRawType((ParameterizedType) type), toClass);
        }
        if (type instanceof TypeVariable) {
            for (Type bound : ((TypeVariable) type).getBounds()) {
                if (isAssignable(bound, toClass)) {
                    return true;
                }
            }
            return false;
        }
        if (type instanceof GenericArrayType) {
            if (toClass.equals(Object.class)) {
                return true;
            }
            return toClass.isArray() && isAssignable(((GenericArrayType) type).getGenericComponentType(), toClass.getComponentType());
        }
        if (type instanceof WildcardType) {
            return false;
        }
        throw new IllegalStateException("found an unhandled type: " + type);
    }

    private static boolean isAssignable(Type type, ParameterizedType toParameterizedType, Map<TypeVariable<?>, Type> typeVarAssigns) {
        if (type == null) {
            return true;
        }
        if (toParameterizedType == null) {
            return false;
        }
        if (toParameterizedType.equals(type)) {
            return true;
        }
        Class<?> toClass = getRawType(toParameterizedType);
        Map<TypeVariable<?>, Type> fromTypeVarAssigns = getTypeArguments(type, toClass, (Map<TypeVariable<?>, Type>) null);
        if (fromTypeVarAssigns == null) {
            return false;
        }
        if (fromTypeVarAssigns.isEmpty()) {
            return true;
        }
        Map<TypeVariable<?>, Type> toTypeVarAssigns = getTypeArguments(toParameterizedType, toClass, typeVarAssigns);
        for (TypeVariable<?> var : toTypeVarAssigns.keySet()) {
            Type toTypeArg = unrollVariableAssignments(var, toTypeVarAssigns);
            Type fromTypeArg = unrollVariableAssignments(var, fromTypeVarAssigns);
            if (toTypeArg != null || !(fromTypeArg instanceof Class)) {
                if (fromTypeArg != null && !toTypeArg.equals(fromTypeArg) && (!(toTypeArg instanceof WildcardType) || !isAssignable(fromTypeArg, toTypeArg, typeVarAssigns))) {
                    return false;
                }
            }
        }
        return true;
    }

    private static Type unrollVariableAssignments(TypeVariable<?> typeVariable, Map<TypeVariable<?>, Type> typeVarAssigns) {
        Type result;
        while (true) {
            result = typeVarAssigns.get(typeVariable);
            if (!(result instanceof TypeVariable) || result.equals(typeVariable)) {
                break;
            }
            typeVariable = (TypeVariable) result;
        }
        return result;
    }

    private static boolean isAssignable(Type type, GenericArrayType toGenericArrayType, Map<TypeVariable<?>, Type> typeVarAssigns) {
        if (type == null) {
            return true;
        }
        if (toGenericArrayType == null) {
            return false;
        }
        if (toGenericArrayType.equals(type)) {
            return true;
        }
        Type toComponentType = toGenericArrayType.getGenericComponentType();
        if (type instanceof Class) {
            Class<?> cls = (Class) type;
            if (cls.isArray() && isAssignable(cls.getComponentType(), toComponentType, typeVarAssigns)) {
                return true;
            }
            return false;
        }
        if (type instanceof GenericArrayType) {
            return isAssignable(((GenericArrayType) type).getGenericComponentType(), toComponentType, typeVarAssigns);
        }
        if (type instanceof WildcardType) {
            for (Type bound : getImplicitUpperBounds((WildcardType) type)) {
                if (isAssignable(bound, toGenericArrayType)) {
                    return true;
                }
            }
            return false;
        }
        if (type instanceof TypeVariable) {
            for (Type bound2 : getImplicitBounds((TypeVariable) type)) {
                if (isAssignable(bound2, toGenericArrayType)) {
                    return true;
                }
            }
            return false;
        }
        if (type instanceof ParameterizedType) {
            return false;
        }
        throw new IllegalStateException("found an unhandled type: " + type);
    }

    private static boolean isAssignable(Type type, WildcardType toWildcardType, Map<TypeVariable<?>, Type> typeVarAssigns) {
        if (type == null) {
            return true;
        }
        if (toWildcardType == null) {
            return false;
        }
        if (toWildcardType.equals(type)) {
            return true;
        }
        Type[] toUpperBounds = getImplicitUpperBounds(toWildcardType);
        Type[] toLowerBounds = getImplicitLowerBounds(toWildcardType);
        if (type instanceof WildcardType) {
            WildcardType wildcardType = (WildcardType) type;
            Type[] upperBounds = getImplicitUpperBounds(wildcardType);
            Type[] lowerBounds = getImplicitLowerBounds(wildcardType);
            for (Type toBound : toUpperBounds) {
                Type toBound2 = substituteTypeVariables(toBound, typeVarAssigns);
                for (Type bound : upperBounds) {
                    if (!isAssignable(bound, toBound2, typeVarAssigns)) {
                        return false;
                    }
                }
            }
            for (Type toBound3 : toLowerBounds) {
                Type toBound4 = substituteTypeVariables(toBound3, typeVarAssigns);
                for (Type bound2 : lowerBounds) {
                    if (!isAssignable(toBound4, bound2, typeVarAssigns)) {
                        return false;
                    }
                }
            }
            return true;
        }
        for (Type toBound5 : toUpperBounds) {
            if (!isAssignable(type, substituteTypeVariables(toBound5, typeVarAssigns), typeVarAssigns)) {
                return false;
            }
        }
        for (Type toBound6 : toLowerBounds) {
            if (!isAssignable(substituteTypeVariables(toBound6, typeVarAssigns), type, typeVarAssigns)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isAssignable(Type type, TypeVariable<?> toTypeVariable, Map<TypeVariable<?>, Type> typeVarAssigns) {
        if (type == null) {
            return true;
        }
        if (toTypeVariable == null) {
            return false;
        }
        if (toTypeVariable.equals(type)) {
            return true;
        }
        if (type instanceof TypeVariable) {
            Type[] bounds = getImplicitBounds((TypeVariable) type);
            for (Type bound : bounds) {
                if (isAssignable(bound, toTypeVariable, typeVarAssigns)) {
                    return true;
                }
            }
        }
        if ((type instanceof Class) || (type instanceof ParameterizedType) || (type instanceof GenericArrayType) || (type instanceof WildcardType)) {
            return false;
        }
        throw new IllegalStateException("found an unhandled type: " + type);
    }

    private static Type substituteTypeVariables(Type type, Map<TypeVariable<?>, Type> typeVarAssigns) {
        if ((type instanceof TypeVariable) && typeVarAssigns != null) {
            Type replacementType = typeVarAssigns.get(type);
            if (replacementType == null) {
                throw new IllegalArgumentException("missing assignment type for type variable " + type);
            }
            return replacementType;
        }
        return type;
    }

    public static Map<TypeVariable<?>, Type> getTypeArguments(ParameterizedType type) {
        return getTypeArguments(type, getRawType(type), (Map<TypeVariable<?>, Type>) null);
    }

    public static Map<TypeVariable<?>, Type> getTypeArguments(Type type, Class<?> toClass) {
        return getTypeArguments(type, toClass, (Map<TypeVariable<?>, Type>) null);
    }

    private static Map<TypeVariable<?>, Type> getTypeArguments(Type type, Class<?> toClass, Map<TypeVariable<?>, Type> subtypeVarAssigns) {
        if (type instanceof Class) {
            return getTypeArguments((Class<?>) type, toClass, subtypeVarAssigns);
        }
        if (type instanceof ParameterizedType) {
            return getTypeArguments((ParameterizedType) type, toClass, subtypeVarAssigns);
        }
        if (type instanceof GenericArrayType) {
            return getTypeArguments(((GenericArrayType) type).getGenericComponentType(), toClass.isArray() ? toClass.getComponentType() : toClass, subtypeVarAssigns);
        }
        int i = 0;
        if (type instanceof WildcardType) {
            Type[] implicitUpperBounds = getImplicitUpperBounds((WildcardType) type);
            int length = implicitUpperBounds.length;
            while (i < length) {
                Type bound = implicitUpperBounds[i];
                if (!isAssignable(bound, toClass)) {
                    i++;
                } else {
                    return getTypeArguments(bound, toClass, subtypeVarAssigns);
                }
            }
            return null;
        }
        if (type instanceof TypeVariable) {
            Type[] implicitBounds = getImplicitBounds((TypeVariable) type);
            int length2 = implicitBounds.length;
            while (i < length2) {
                Type bound2 = implicitBounds[i];
                if (!isAssignable(bound2, toClass)) {
                    i++;
                } else {
                    return getTypeArguments(bound2, toClass, subtypeVarAssigns);
                }
            }
            return null;
        }
        throw new IllegalStateException("found an unhandled type: " + type);
    }

    private static Map<TypeVariable<?>, Type> getTypeArguments(ParameterizedType parameterizedType, Class<?> cls, Map<TypeVariable<?>, Type> map) {
        HashMap map2;
        Map<TypeVariable<?>, Type> typeArguments;
        Class<?> rawType = getRawType(parameterizedType);
        if (!isAssignable((Type) rawType, cls)) {
            return null;
        }
        Type ownerType = parameterizedType.getOwnerType();
        if (ownerType instanceof ParameterizedType) {
            ParameterizedType parameterizedType2 = (ParameterizedType) ownerType;
            typeArguments = getTypeArguments(parameterizedType2, getRawType(parameterizedType2), map);
        } else if (map == null) {
            typeArguments = map2;
            map2 = new HashMap();
        } else {
            typeArguments = map2;
            map2 = new HashMap(map);
        }
        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        TypeVariable<Class<?>>[] typeParameters = rawType.getTypeParameters();
        for (int i = 0; i < typeParameters.length; i++) {
            Type type = actualTypeArguments[i];
            typeArguments.put(typeParameters[i], typeArguments.containsKey(type) ? typeArguments.get(type) : type);
        }
        if (cls.equals(rawType)) {
            return typeArguments;
        }
        return getTypeArguments(getClosestParentType(rawType, cls), cls, typeArguments);
    }

    private static Map<TypeVariable<?>, Type> getTypeArguments(Class<?> cls, Class<?> toClass, Map<TypeVariable<?>, Type> subtypeVarAssigns) {
        if (!isAssignable((Type) cls, toClass)) {
            return null;
        }
        if (cls.isPrimitive()) {
            if (toClass.isPrimitive()) {
                return new HashMap();
            }
            cls = ClassUtils.primitiveToWrapper(cls);
        }
        HashMap<TypeVariable<?>, Type> typeVarAssigns = subtypeVarAssigns == null ? new HashMap<>() : new HashMap<>(subtypeVarAssigns);
        if (toClass.equals(cls)) {
            return typeVarAssigns;
        }
        return getTypeArguments(getClosestParentType(cls, toClass), toClass, typeVarAssigns);
    }

    public static Map<TypeVariable<?>, Type> determineTypeArguments(Class<?> cls, ParameterizedType superType) {
        Validate.notNull(cls, "cls is null", new Object[0]);
        Validate.notNull(superType, "superType is null", new Object[0]);
        Class<?> superClass = getRawType(superType);
        if (!isAssignable((Type) cls, superClass)) {
            return null;
        }
        if (cls.equals(superClass)) {
            return getTypeArguments(superType, superClass, (Map<TypeVariable<?>, Type>) null);
        }
        Type midType = getClosestParentType(cls, superClass);
        if (midType instanceof Class) {
            return determineTypeArguments((Class) midType, superType);
        }
        ParameterizedType midParameterizedType = (ParameterizedType) midType;
        Class<?> midClass = getRawType(midParameterizedType);
        Map<TypeVariable<?>, Type> typeVarAssigns = determineTypeArguments(midClass, superType);
        mapTypeVariablesToArguments(cls, midParameterizedType, typeVarAssigns);
        return typeVarAssigns;
    }

    private static <T> void mapTypeVariablesToArguments(Class<T> cls, ParameterizedType parameterizedType, Map<TypeVariable<?>, Type> typeVarAssigns) {
        Type ownerType = parameterizedType.getOwnerType();
        if (ownerType instanceof ParameterizedType) {
            mapTypeVariablesToArguments(cls, (ParameterizedType) ownerType, typeVarAssigns);
        }
        Type[] typeArgs = parameterizedType.getActualTypeArguments();
        TypeVariable<?>[] typeVars = getRawType(parameterizedType).getTypeParameters();
        List<TypeVariable<Class<T>>> typeVarList = Arrays.asList(cls.getTypeParameters());
        for (int i = 0; i < typeArgs.length; i++) {
            TypeVariable<?> typeVar = typeVars[i];
            Type typeArg = typeArgs[i];
            if (typeVarList.contains(typeArg) && typeVarAssigns.containsKey(typeVar)) {
                typeVarAssigns.put((TypeVariable) typeArg, typeVarAssigns.get(typeVar));
            }
        }
    }

    private static Type getClosestParentType(Class<?> cls, Class<?> superClass) {
        Class<?> midClass;
        if (superClass.isInterface()) {
            Type[] interfaceTypes = cls.getGenericInterfaces();
            Type genericInterface = null;
            for (Type midType : interfaceTypes) {
                if (midType instanceof ParameterizedType) {
                    midClass = getRawType((ParameterizedType) midType);
                } else if (midType instanceof Class) {
                    midClass = (Class) midType;
                } else {
                    throw new IllegalStateException("Unexpected generic interface type found: " + midType);
                }
                if (isAssignable((Type) midClass, superClass) && isAssignable(genericInterface, (Type) midClass)) {
                    genericInterface = midType;
                }
            }
            if (genericInterface != null) {
                return genericInterface;
            }
        }
        return cls.getGenericSuperclass();
    }

    public static boolean isInstance(Object value, Type type) {
        if (type == null) {
            return false;
        }
        if (value == null) {
            return ((type instanceof Class) && ((Class) type).isPrimitive()) ? false : true;
        }
        return isAssignable(value.getClass(), type, (Map<TypeVariable<?>, Type>) null);
    }

    public static Type[] normalizeUpperBounds(Type[] bounds) {
        Validate.notNull(bounds, "null value specified for bounds array", new Object[0]);
        if (bounds.length < 2) {
            return bounds;
        }
        Set<Type> types = new HashSet<>(bounds.length);
        for (Type type1 : bounds) {
            boolean subtypeFound = false;
            int length = bounds.length;
            int i = 0;
            while (true) {
                if (i >= length) {
                    break;
                }
                Type type2 = bounds[i];
                if (type1 == type2 || !isAssignable(type2, type1, (Map<TypeVariable<?>, Type>) null)) {
                    i++;
                } else {
                    subtypeFound = true;
                    break;
                }
            }
            if (!subtypeFound) {
                types.add(type1);
            }
        }
        return (Type[]) types.toArray(ArrayUtils.EMPTY_TYPE_ARRAY);
    }

    public static Type[] getImplicitBounds(TypeVariable<?> typeVariable) {
        Validate.notNull(typeVariable, "typeVariable is null", new Object[0]);
        Type[] bounds = typeVariable.getBounds();
        return bounds.length == 0 ? new Type[]{Object.class} : normalizeUpperBounds(bounds);
    }

    public static Type[] getImplicitUpperBounds(WildcardType wildcardType) {
        Validate.notNull(wildcardType, "wildcardType is null", new Object[0]);
        Type[] bounds = wildcardType.getUpperBounds();
        return bounds.length == 0 ? new Type[]{Object.class} : normalizeUpperBounds(bounds);
    }

    public static Type[] getImplicitLowerBounds(WildcardType wildcardType) {
        Validate.notNull(wildcardType, "wildcardType is null", new Object[0]);
        Type[] bounds = wildcardType.getLowerBounds();
        return bounds.length == 0 ? new Type[]{null} : bounds;
    }

    public static boolean typesSatisfyVariables(Map<TypeVariable<?>, Type> typeVarAssigns) {
        Validate.notNull(typeVarAssigns, "typeVarAssigns is null", new Object[0]);
        for (Map.Entry<TypeVariable<?>, Type> entry : typeVarAssigns.entrySet()) {
            TypeVariable<?> typeVar = entry.getKey();
            Type type = entry.getValue();
            for (Type bound : getImplicitBounds(typeVar)) {
                if (!isAssignable(type, substituteTypeVariables(bound, typeVarAssigns), typeVarAssigns)) {
                    return false;
                }
            }
        }
        return true;
    }

    private static Class<?> getRawType(ParameterizedType parameterizedType) {
        Type rawType = parameterizedType.getRawType();
        if (!(rawType instanceof Class)) {
            throw new IllegalStateException("Wait... What!? Type of rawType: " + rawType);
        }
        return (Class) rawType;
    }

    public static Class<?> getRawType(Type type, Type assigningType) {
        Map<TypeVariable<?>, Type> typeVarAssigns;
        Type typeArgument;
        if (type instanceof Class) {
            return (Class) type;
        }
        if (type instanceof ParameterizedType) {
            return getRawType((ParameterizedType) type);
        }
        if (type instanceof TypeVariable) {
            if (assigningType == null) {
                return null;
            }
            Object genericDeclaration = ((TypeVariable) type).getGenericDeclaration();
            if (!(genericDeclaration instanceof Class) || (typeVarAssigns = getTypeArguments(assigningType, (Class) genericDeclaration)) == null || (typeArgument = typeVarAssigns.get(type)) == null) {
                return null;
            }
            return getRawType(typeArgument, assigningType);
        }
        if (type instanceof GenericArrayType) {
            Class<?> rawComponentType = getRawType(((GenericArrayType) type).getGenericComponentType(), assigningType);
            return Array.newInstance(rawComponentType, 0).getClass();
        }
        if (type instanceof WildcardType) {
            return null;
        }
        throw new IllegalArgumentException("unknown type: " + type);
    }

    public static boolean isArrayType(Type type) {
        return (type instanceof GenericArrayType) || ((type instanceof Class) && ((Class) type).isArray());
    }

    public static Type getArrayComponentType(Type type) {
        if (type instanceof Class) {
            Class<?> cls = (Class) type;
            if (cls.isArray()) {
                return cls.getComponentType();
            }
            return null;
        }
        if (type instanceof GenericArrayType) {
            return ((GenericArrayType) type).getGenericComponentType();
        }
        return null;
    }

    public static Type unrollVariables(Map<TypeVariable<?>, Type> typeArguments, Type type) {
        Map<TypeVariable<?>, Type> parameterizedTypeArguments;
        if (typeArguments == null) {
            typeArguments = Collections.emptyMap();
        }
        if (containsTypeVariables(type)) {
            if (type instanceof TypeVariable) {
                return unrollVariables(typeArguments, typeArguments.get(type));
            }
            if (type instanceof ParameterizedType) {
                ParameterizedType p = (ParameterizedType) type;
                if (p.getOwnerType() == null) {
                    parameterizedTypeArguments = typeArguments;
                } else {
                    parameterizedTypeArguments = new HashMap<>(typeArguments);
                    parameterizedTypeArguments.putAll(getTypeArguments(p));
                }
                Type[] args = p.getActualTypeArguments();
                for (int i = 0; i < args.length; i++) {
                    Type unrolled = unrollVariables(parameterizedTypeArguments, args[i]);
                    if (unrolled != null) {
                        args[i] = unrolled;
                    }
                }
                return parameterizeWithOwner(p.getOwnerType(), (Class<?>) p.getRawType(), args);
            }
            if (type instanceof WildcardType) {
                WildcardType wild = (WildcardType) type;
                return wildcardType().withUpperBounds(unrollBounds(typeArguments, wild.getUpperBounds())).withLowerBounds(unrollBounds(typeArguments, wild.getLowerBounds())).build();
            }
        }
        return type;
    }

    private static Type[] unrollBounds(Map<TypeVariable<?>, Type> typeArguments, Type[] bounds) {
        Type[] result = bounds;
        int i = 0;
        while (i < result.length) {
            Type unrolled = unrollVariables(typeArguments, result[i]);
            if (unrolled == null) {
                result = (Type[]) ArrayUtils.remove((Object[]) result, i);
                i--;
            } else {
                result[i] = unrolled;
            }
            i++;
        }
        return result;
    }

    public static boolean containsTypeVariables(Type type) {
        if (type instanceof TypeVariable) {
            return true;
        }
        if (type instanceof Class) {
            return ((Class) type).getTypeParameters().length > 0;
        }
        if (type instanceof ParameterizedType) {
            for (Type arg : ((ParameterizedType) type).getActualTypeArguments()) {
                if (containsTypeVariables(arg)) {
                    return true;
                }
            }
            return false;
        }
        if (!(type instanceof WildcardType)) {
            return false;
        }
        WildcardType wild = (WildcardType) type;
        return containsTypeVariables(getImplicitLowerBounds(wild)[0]) || containsTypeVariables(getImplicitUpperBounds(wild)[0]);
    }

    public static final ParameterizedType parameterize(Class<?> rawClass, Type... typeArguments) {
        return parameterizeWithOwner((Type) null, rawClass, typeArguments);
    }

    public static final ParameterizedType parameterize(Class<?> rawClass, Map<TypeVariable<?>, Type> typeArgMappings) {
        Validate.notNull(rawClass, "raw class is null", new Object[0]);
        Validate.notNull(typeArgMappings, "typeArgMappings is null", new Object[0]);
        return parameterizeWithOwner((Type) null, rawClass, extractTypeArgumentsFrom(typeArgMappings, rawClass.getTypeParameters()));
    }

    public static final ParameterizedType parameterizeWithOwner(Type owner, Class<?> rawClass, Type... typeArguments) {
        Type useOwner;
        Validate.notNull(rawClass, "raw class is null", new Object[0]);
        if (rawClass.getEnclosingClass() == null) {
            Validate.isTrue(owner == null, "no owner allowed for top-level %s", rawClass);
            useOwner = null;
        } else if (owner == null) {
            useOwner = rawClass.getEnclosingClass();
        } else {
            Validate.isTrue(isAssignable(owner, rawClass.getEnclosingClass()), "%s is invalid owner type for parameterized %s", owner, rawClass);
            useOwner = owner;
        }
        Validate.noNullElements(typeArguments, "null type argument at index %s", new Object[0]);
        Validate.isTrue(rawClass.getTypeParameters().length == typeArguments.length, "invalid number of type parameters specified: expected %d, got %d", Integer.valueOf(rawClass.getTypeParameters().length), Integer.valueOf(typeArguments.length));
        return new ParameterizedTypeImpl(rawClass, useOwner, typeArguments);
    }

    public static final ParameterizedType parameterizeWithOwner(Type owner, Class<?> rawClass, Map<TypeVariable<?>, Type> typeArgMappings) {
        Validate.notNull(rawClass, "raw class is null", new Object[0]);
        Validate.notNull(typeArgMappings, "typeArgMappings is null", new Object[0]);
        return parameterizeWithOwner(owner, rawClass, extractTypeArgumentsFrom(typeArgMappings, rawClass.getTypeParameters()));
    }

    private static Type[] extractTypeArgumentsFrom(Map<TypeVariable<?>, Type> mappings, TypeVariable<?>[] variables) {
        Type[] result = new Type[variables.length];
        int index = 0;
        int length = variables.length;
        int i = 0;
        while (i < length) {
            TypeVariable<?> var = variables[i];
            Validate.isTrue(mappings.containsKey(var), "missing argument mapping for %s", toString((Type) var));
            result[index] = mappings.get(var);
            i++;
            index++;
        }
        return result;
    }

    public static WildcardTypeBuilder wildcardType() {
        return new WildcardTypeBuilder();
    }

    public static GenericArrayType genericArrayType(Type componentType) {
        return new GenericArrayTypeImpl((Type) Validate.notNull(componentType, "componentType is null", new Object[0]));
    }

    public static boolean equals(Type type1, Type type2) {
        if (Objects.equals(type1, type2)) {
            return true;
        }
        if (type1 instanceof ParameterizedType) {
            return equals((ParameterizedType) type1, type2);
        }
        if (type1 instanceof GenericArrayType) {
            return equals((GenericArrayType) type1, type2);
        }
        if (type1 instanceof WildcardType) {
            return equals((WildcardType) type1, type2);
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean equals(ParameterizedType parameterizedType, Type type) {
        if (type instanceof ParameterizedType) {
            ParameterizedType other = (ParameterizedType) type;
            if (equals(parameterizedType.getRawType(), other.getRawType()) && equals(parameterizedType.getOwnerType(), other.getOwnerType())) {
                return equals(parameterizedType.getActualTypeArguments(), other.getActualTypeArguments());
            }
            return false;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean equals(GenericArrayType genericArrayType, Type type) {
        return (type instanceof GenericArrayType) && equals(genericArrayType.getGenericComponentType(), ((GenericArrayType) type).getGenericComponentType());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean equals(WildcardType wildcardType, Type type) {
        if (!(type instanceof WildcardType)) {
            return false;
        }
        WildcardType other = (WildcardType) type;
        return equals(getImplicitLowerBounds(wildcardType), getImplicitLowerBounds(other)) && equals(getImplicitUpperBounds(wildcardType), getImplicitUpperBounds(other));
    }

    private static boolean equals(Type[] type1, Type[] type2) {
        if (type1.length != type2.length) {
            return false;
        }
        for (int i = 0; i < type1.length; i++) {
            if (!equals(type1[i], type2[i])) {
                return false;
            }
        }
        return true;
    }

    public static String toString(Type type) {
        Validate.notNull(type);
        if (type instanceof Class) {
            return classToString((Class) type);
        }
        if (type instanceof ParameterizedType) {
            return parameterizedTypeToString((ParameterizedType) type);
        }
        if (type instanceof WildcardType) {
            return wildcardTypeToString((WildcardType) type);
        }
        if (type instanceof TypeVariable) {
            return typeVariableToString((TypeVariable) type);
        }
        if (type instanceof GenericArrayType) {
            return genericArrayTypeToString((GenericArrayType) type);
        }
        throw new IllegalArgumentException(ObjectUtils.identityToString(type));
    }

    public static String toLongString(TypeVariable<?> var) {
        Validate.notNull(var, "var is null", new Object[0]);
        StringBuilder buf = new StringBuilder();
        GenericDeclaration d = var.getGenericDeclaration();
        if (d instanceof Class) {
            Class<?> c = (Class) d;
            while (c.getEnclosingClass() != null) {
                buf.insert(0, c.getSimpleName()).insert(0, ClassUtils.PACKAGE_SEPARATOR_CHAR);
                c = c.getEnclosingClass();
            }
            buf.insert(0, c.getName());
        } else if (d instanceof Type) {
            buf.append(toString((Type) d));
        } else {
            buf.append(d);
        }
        buf.append(':');
        buf.append(typeVariableToString(var));
        return buf.toString();
    }

    static /* synthetic */ Type lambda$wrap$0(Type type) {
        return type;
    }

    public static <T> Typed<T> wrap(final Type type) {
        return new Typed() { // from class: org.apache.commons.lang3.reflect.-$$Lambda$TypeUtils$3OYZq2XY2x18cPBsG6TdvImhazQ
            @Override // org.apache.commons.lang3.reflect.Typed
            public final Type getType() {
                return TypeUtils.lambda$wrap$0(type);
            }
        };
    }

    public static <T> Typed<T> wrap(Class<T> type) {
        return wrap((Type) type);
    }

    private static String classToString(Class<?> cls) {
        if (cls.isArray()) {
            return toString((Type) cls.getComponentType()) + "[]";
        }
        StringBuilder buf = new StringBuilder();
        if (cls.getEnclosingClass() != null) {
            buf.append(classToString(cls.getEnclosingClass()));
            buf.append(ClassUtils.PACKAGE_SEPARATOR_CHAR);
            buf.append(cls.getSimpleName());
        } else {
            buf.append(cls.getName());
        }
        if (cls.getTypeParameters().length > 0) {
            buf.append('<');
            appendAllTo(buf, ", ", cls.getTypeParameters());
            buf.append('>');
        }
        return buf.toString();
    }

    private static String typeVariableToString(TypeVariable<?> typeVariable) {
        StringBuilder buf = new StringBuilder(typeVariable.getName());
        Type[] bounds = typeVariable.getBounds();
        if (bounds.length > 0 && (bounds.length != 1 || !Object.class.equals(bounds[0]))) {
            buf.append(" extends ");
            appendAllTo(buf, " & ", typeVariable.getBounds());
        }
        return buf.toString();
    }

    private static String parameterizedTypeToString(ParameterizedType parameterizedType) {
        StringBuilder builder = new StringBuilder();
        Type useOwner = parameterizedType.getOwnerType();
        Class<?> raw = (Class) parameterizedType.getRawType();
        if (useOwner == null) {
            builder.append(raw.getName());
        } else {
            if (useOwner instanceof Class) {
                builder.append(((Class) useOwner).getName());
            } else {
                builder.append(useOwner.toString());
            }
            builder.append(ClassUtils.PACKAGE_SEPARATOR_CHAR);
            builder.append(raw.getSimpleName());
        }
        int[] recursiveTypeIndexes = findRecursiveTypes(parameterizedType);
        if (recursiveTypeIndexes.length > 0) {
            appendRecursiveTypes(builder, recursiveTypeIndexes, parameterizedType.getActualTypeArguments());
        } else {
            builder.append('<');
            appendAllTo(builder, ", ", parameterizedType.getActualTypeArguments()).append('>');
        }
        return builder.toString();
    }

    private static void appendRecursiveTypes(StringBuilder builder, int[] recursiveTypeIndexes, Type[] argumentTypes) {
        for (int i = 0; i < recursiveTypeIndexes.length; i++) {
            builder.append('<');
            appendAllTo(builder, ", ", argumentTypes[i].toString()).append('>');
        }
        Type[] argumentsFiltered = (Type[]) ArrayUtils.removeAll((Object[]) argumentTypes, recursiveTypeIndexes);
        if (argumentsFiltered.length > 0) {
            builder.append('<');
            appendAllTo(builder, ", ", argumentsFiltered).append('>');
        }
    }

    private static int[] findRecursiveTypes(ParameterizedType parameterizedType) {
        Type[] filteredArgumentTypes = (Type[]) Arrays.copyOf(parameterizedType.getActualTypeArguments(), parameterizedType.getActualTypeArguments().length);
        int[] indexesToRemove = new int[0];
        for (int i = 0; i < filteredArgumentTypes.length; i++) {
            if ((filteredArgumentTypes[i] instanceof TypeVariable) && containsVariableTypeSameParametrizedTypeBound((TypeVariable) filteredArgumentTypes[i], parameterizedType)) {
                indexesToRemove = ArrayUtils.add(indexesToRemove, i);
            }
        }
        return indexesToRemove;
    }

    private static boolean containsVariableTypeSameParametrizedTypeBound(TypeVariable<?> typeVariable, ParameterizedType parameterizedType) {
        return ArrayUtils.contains(typeVariable.getBounds(), parameterizedType);
    }

    private static String wildcardTypeToString(WildcardType wildcardType) {
        StringBuilder buf = new StringBuilder().append('?');
        Type[] lowerBounds = wildcardType.getLowerBounds();
        Type[] upperBounds = wildcardType.getUpperBounds();
        if (lowerBounds.length > 1 || (lowerBounds.length == 1 && lowerBounds[0] != null)) {
            buf.append(" super ");
            appendAllTo(buf, " & ", lowerBounds);
        } else if (upperBounds.length > 1 || (upperBounds.length == 1 && !Object.class.equals(upperBounds[0]))) {
            buf.append(" extends ");
            appendAllTo(buf, " & ", upperBounds);
        }
        return buf.toString();
    }

    private static String genericArrayTypeToString(GenericArrayType genericArrayType) {
        return String.format("%s[]", toString(genericArrayType.getGenericComponentType()));
    }

    private static <T> StringBuilder appendAllTo(StringBuilder builder, String sep, T... types) {
        Validate.notEmpty(Validate.noNullElements(types));
        if (types.length > 0) {
            builder.append(toString(types[0]));
            for (int i = 1; i < types.length; i++) {
                builder.append(sep);
                builder.append(toString(types[i]));
            }
        }
        return builder;
    }

    private static <T> String toString(T object) {
        return object instanceof Type ? toString((Type) object) : object.toString();
    }
}
