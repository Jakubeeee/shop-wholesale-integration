package com.jakubeeee.common.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Calendar;

import static com.jakubeeee.common.utils.DateTimeUtils.*;
import static java.time.LocalDateTime.now;
import static java.time.temporal.ChronoUnit.HOURS;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(SpringRunner.class)
public class DateTimeUtilsTest {

    @Test
    public void stringToTimeTest() {
        String timeAsString = "2018-01-01 12:30:00";
        var calendar = Calendar.getInstance();
        calendar.set(2018, Calendar.JANUARY, 1, 12, 30, 0);
        LocalDateTime expectedResult = new Timestamp(calendar.getTime().getTime()).toLocalDateTime().withNano(0);
        LocalDateTime result = stringToTime(timeAsString);
        assertThat(result, is(equalTo(expectedResult)));
    }

    @Test
    public void isTimeAfterTest() {
        var laterTime = now();
        var earlierTime = laterTime.minus(5, HOURS);
        boolean result = isTimeAfter(laterTime, earlierTime);
        assertThat(result, is(equalTo(true)));
    }

    @Test
    public void getTimeDifferenceInMillsTest() {
        var laterTime = now();
        var earlierTime = laterTime.minus(5, HOURS);
        long result = getTimeDifferenceInMillis(earlierTime, laterTime);
        assertThat(result, is(equalTo(18000000L)));
    }

    @Test
    public void minutesToMillisTest() {
        assertThat(minutesToMillis(15), is(equalTo(900000L)));
    }

}
