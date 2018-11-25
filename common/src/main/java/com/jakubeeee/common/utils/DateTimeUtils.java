package com.jakubeeee.common.utils;

import lombok.experimental.UtilityClass;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static java.time.LocalDateTime.now;
import static java.time.LocalDateTime.parse;
import static java.time.temporal.ChronoUnit.MILLIS;

@UtilityClass
public class DateTimeUtils {

    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static final String DATE_TIME_FORMAT_WITH_MILLIS = "yyyy-MM-dd HH:mm:ss.SSS";

    public static final String DATE_TIME_FORMAT_WITH_NANOS = "yyyy-MM-dd HH:mm:ss.SSSSSSSS";

    public static final String DATE_FORMAT = "yyyy-MM-dd";

    public static final String TIME_FORMAT = "HH:mm:ss";

    public static final String TIME_FORMAT_WITH_MILLIS = "HH:mm:ss.SSS";

    public static LocalDateTime stringToTime(String timeAsString) {
        return parse(timeAsString.replace(" ", "T"));
    }

    public static LocalDateTime roundTimeToNextFullMinute(LocalDateTime time) {
        return time.plusMinutes(1).minusNanos(1).withSecond(0).withNano(0);
    }

    public static LocalDateTime roundTimeToNextFullHour(LocalDateTime time) {
        return time.plusHours(1).minusNanos(1).withMinute(0).withSecond(0).withNano(0);
    }

    public static boolean isTimeAfter(LocalDateTime laterTime, LocalDateTime earlierTime) {
        return laterTime.compareTo(earlierTime) > 0;
    }

    public static boolean isTimeAfterOrEqual(LocalDateTime laterTime, LocalDateTime earlierTime) {
        return laterTime.compareTo(earlierTime) >= 0;
    }

    public static long getTimeDifferenceInMillis(LocalDateTime earlierTime, LocalDateTime laterTime) {
        return earlierTime.until(laterTime, MILLIS);
    }

    public static long minutesToMillis(int minutes) {
        return minutes * 60000;
    }

    public static int millisToMinutes(long millis) {
        return (int) (millis / 60000);
    }

    public static long millisUnitNextFullMinute() {
        LocalDateTime now = now();
        return Duration.between(now, roundTimeToNextFullMinute(now)).toMillis();
    }

    public static long millisUntilNextFullHour() {
        LocalDateTime now = now();
        return Duration.between(now, roundTimeToNextFullHour(now)).toMillis();
    }

    public static String formatDateTime(LocalDateTime time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
        return time.format(formatter);
    }

    public static String formatDateTimeWithMillis(LocalDateTime time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT_WITH_MILLIS);
        return time.format(formatter);
    }

    public static String formatDateTimeWithNanos(LocalDateTime time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT_WITH_NANOS);
        return time.format(formatter);
    }

    public static String formatDate(LocalDateTime time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
        return time.format(formatter);
    }

    public static String formatTime(LocalDateTime time) {
        return formatTime(time, false);
    }

    public static String formatTime(LocalDateTime time, boolean withMillis) {
        String timeFormat = withMillis ? TIME_FORMAT_WITH_MILLIS : TIME_FORMAT;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(timeFormat);
        return time.format(formatter);
    }

}
