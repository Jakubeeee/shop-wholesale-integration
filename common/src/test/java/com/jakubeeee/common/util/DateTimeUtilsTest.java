package com.jakubeeee.common.util;

import com.jakubeeee.testutils.marker.BehavioralUnitTestCategory;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.time.LocalDateTime;

import static com.jakubeeee.common.util.DateTimeUtils.*;
import static com.jakubeeee.testutils.constant.DateTimeTestConstants.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@Category(BehavioralUnitTestCategory.class)
@RunWith(PowerMockRunner.class)
@PrepareForTest(DateTimeUtils.class)
public class DateTimeUtilsTest {

    @Test
    public void stringToDateTimeTest_shouldParse() {
        LocalDateTime result = parseStringToDateTime(TEST_DATE_TIME_AS_STRING);
        assertThat(result, is(equalTo(TEST_DATE_TIME)));
    }

    @Test
    public void roundTimeToNextFullMinute_shouldRound() {
        LocalDateTime result = roundTimeToNextFullMinute(TEST_DATE_TIME_HALF_MINUTE_EARLIER);
        assertThat(result, is(equalTo(TEST_DATE_TIME)));
    }

    @Test
    public void isTimeAfterTest_shouldReturnTrue() {
        boolean result = isTimeAfter(TEST_DATE_TIME, TEST_DATE_TIME_FIVE_HOURS_EARLIER);
        assertThat(result, is((true)));
    }

    @Test
    public void isTimeAfterTest_shouldReturnFalse() {
        boolean result = isTimeAfter(TEST_DATE_TIME, TEST_DATE_TIME_FIVE_HOURS_LATER);
        assertThat(result, is((false)));
    }

    @Test
    public void getTimeDifferenceInMillsTest_shouldCalculateProperly_1() {
        long result = getTimeDifferenceInMillis(TEST_DATE_TIME_HALF_MINUTE_EARLIER, TEST_DATE_TIME);
        assertThat(result, is(equalTo(30000L)));
    }

    @Test
    public void getTimeDifferenceInMillsTest_shouldCalculateProperly_2() {
        long result = getTimeDifferenceInMillis(TEST_DATE_TIME_FIVE_HOURS_EARLIER, TEST_DATE_TIME);
        assertThat(result, is(equalTo(18000000L)));
    }

    @Test
    public void minutesToMillisTest_shouldCalculateProperly() {
        assertThat(minutesToMillis(15), is(equalTo(900000L)));
    }

    @Test
    public void millisToMinutesTest_shouldCalculateProperly() {
        assertThat(millisToMinutes(900000L), is(equalTo(15)));
    }

    @Test
    public void millisUntilNextFullMinuteTest_shouldCalculateProperly() {
        mockStatic(LocalDateTime.class);
        when(getCurrentDateTime()).thenReturn(TEST_DATE_TIME_HALF_MINUTE_EARLIER);
        assertThat(millisUnitNextFullMinute(), is(equalTo(30000L)));
    }

    @Test
    public void formatDateTimeTest_shouldReturnFormattedString() {
        assertThat(formatDateTime(TEST_DATE_TIME), is(equalTo(TEST_DATE_TIME_AS_STRING)));
    }

    @Test
    public void formatDateTimeTestWithNanos_shouldReturnFormattedString() {
        assertThat(formatDateTimeWithNanos(TEST_DATE_TIME_WITH_NANOS),
                is(equalTo(TEST_DATE_TIME_AS_STRING_WITH_NANOS)));
    }

}
