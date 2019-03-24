package com.jakubeeee.common.util;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Utility class providing useful static methods related to reflection mechanism.
 */
@UtilityClass
public final class ReflectUtils {

    @SuppressWarnings({"unchecked", "unused"})
    public static <T> T getFieldValue(Field field, Object fieldOwnerObject, @NonNull Class<T> fieldValueType)
            throws IllegalAccessException {
        return (T) FieldUtils.readField(field, fieldOwnerObject, true);
    }

    @SuppressWarnings({"unchecked"})
    public static Method getMethod(Class clazz, String name) throws NoSuchMethodException {
        Method method = clazz.getMethod(name);
        method.setAccessible(true);
        return method;
    }

}
