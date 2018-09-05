package com.jakubeeee.common.utils;

import lombok.experimental.UtilityClass;
import org.springframework.lang.Nullable;

import java.util.Arrays;
import java.util.List;

@UtilityClass
public class LangUtils {

    public static <T> T nvl(@Nullable T nullableObject, T reserveObject) {
        return nullableObject != null ? nullableObject : reserveObject;
    }

    @SafeVarargs
    public static <T> List<T> list(T... objects) {
        return Arrays.asList(objects);
    }

}


