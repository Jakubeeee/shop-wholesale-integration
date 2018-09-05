package com.jakubeeee.common.utils;

import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class DateTimeUtils {

    private final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static LocalDateTime parse(String time) {
        return LocalDateTime.parse(time.replace(" ", "T"));
    }

    public static boolean isAfter(LocalDateTime time1, LocalDateTime time2) {
        return time1.compareTo(time2) > 0;
    }

    public static String format(LocalDateTime time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
        return time.format(formatter);
    }

}
