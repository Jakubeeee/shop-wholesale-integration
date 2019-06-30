package com.jakubeeee.common.util;

import lombok.experimental.UtilityClass;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static java.time.LocalDateTime.now;
import static java.time.LocalDateTime.parse;
import static java.time.temporal.ChronoUnit.MILLIS;

/**
 * Utility class providing useful static methods related to dates and time.
 */
@UtilityClass
public final class DateTimeUtils {

    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static final String DATE_TIME_FORMAT_WITH_NANOS = "yyyy-MM-dd HH:mm:ss.SSSSSSSS";

    public static LocalDateTime getCurrentDateTime() {
        return now().truncatedTo(MILLIS);
    }

    public static LocalDateTime parseStringToDateTime(String dateTimeAsString) {
        return parse(dateTimeAsString.replace(" ", "T"));
    }

    public static LocalDateTime roundTimeToNextFullMinute(LocalDateTime dateTime) {
        return dateTime.plusMinutes(1).minusNanos(1).withSecond(0).withNano(0);
    }

    public static boolean isTimeAfter(LocalDateTime laterTime, LocalDateTime earlierDateTime) {
        return laterTime.compareTo(earlierDateTime) > 0;
    }

    public static long getTimeDifferenceInMillis(LocalDateTime earlierTime, LocalDateTime laterDateTime) {
        return earlierTime.until(laterDateTime, MILLIS);
    }

    public static long minutesToMillis(int minutes) {
        return (long) (minutes * 60000);
    }

    public static int millisToMinutes(long millis) {
        return (int) (millis / 60000);
    }

    public static long millisUnitNextFullMinute() {
        LocalDateTime now = getCurrentDateTime();
        return Duration.between(now, roundTimeToNextFullMinute(now)).toMillis();
    }

    public static String formatDateTime(LocalDateTime dateTime) {
        var formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
        return dateTime.format(formatter);
    }

    public static String formatDateTimeWithNanos(LocalDateTime dateTime) {
        var formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT_WITH_NANOS);
        return dateTime.format(formatter);
    }

}
