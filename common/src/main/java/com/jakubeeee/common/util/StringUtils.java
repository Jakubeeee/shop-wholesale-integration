package com.jakubeeee.common.util;

import lombok.experimental.UtilityClass;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * Utility class providing useful static methods for operations on strings.
 */
@UtilityClass
public final class StringUtils {

    public static StringBuilder removeLastChar(StringBuilder stringBuilder) {
        String string = removeLastChar(stringBuilder.toString());
        return new StringBuilder(string);
    }

    public static String removeLastChar(String string) {
        if (isNotEmpty(string))
            return string.substring(0, string.length() - 1);
        else throw new NullPointerException("String cannot be null nor empty");
    }

}
