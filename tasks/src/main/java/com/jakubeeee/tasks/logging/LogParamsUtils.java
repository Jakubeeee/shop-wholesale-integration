package com.jakubeeee.tasks.logging;

import lombok.experimental.UtilityClass;

import static com.jakubeeee.tasks.logging.LogParam.Type.CODE;
import static com.jakubeeee.tasks.logging.LogParam.Type.TEXT;

@UtilityClass
public class LogParamsUtils {

    public static LogParam toLogParam(String value) {
        return new LogParam(value, TEXT);
    }

    public static LogParam toLogParam(String value, boolean isCode) {
        return new LogParam(value, isCode ? CODE : TEXT);
    }

}
