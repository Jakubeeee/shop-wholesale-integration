package com.jakubeeee.testutils;

import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;

/**
 * Utility class providing useful constants used in date and time related tests.
 */
@UtilityClass
public final class DateTimeTestConstants {

    public static final LocalDateTime TEST_DATE_TIME =
            LocalDateTime.of(2020, 7, 3, 12, 0, 0);

    public static final LocalDateTime TEST_DATE_TIME_WITH_NANOS =
            LocalDateTime.of(2020, 7, 3, 12, 0, 0, 123456789);

    public static final String TEST_DATE_TIME_AS_STRING = "2020-07-03 12:00:00";

    public static final String TEST_DATE_TIME_AS_STRING_WITH_NANOS = "2020-07-03 12:00:00.12345678";

    public static final LocalDateTime TEST_DATE_TIME_FIVE_HOURS_LATER =
            LocalDateTime.of(2020, 7, 3, 17, 0, 0);

    public static final LocalDateTime TEST_DATE_TIME_FIVE_HOURS_EARLIER =
            LocalDateTime.of(2020, 7, 3, 7, 0, 0);

    public static final LocalDateTime TEST_DATE_TIME_HALF_MINUTE_EARLIER =
            LocalDateTime.of(2020, 7, 3, 11, 59, 30);

}
