package com.jakubeeee.common.converter;

import com.jakubeeee.testutils.marker.BehaviourUnitTest;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.time.LocalDateTime;

import static com.jakubeeee.testutils.constant.DateTimeTestConstants.TEST_DATE_TIME;
import static com.jakubeeee.testutils.constant.DateTimeTestConstants.TEST_DATE_TIME_AS_STRING;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@Category(BehaviourUnitTest.class)
public class LocalDateTimeConverterTest {

    private static LocalDateTimeConverter CONVERTER;

    @BeforeClass
    public static void setUp() {
        CONVERTER = new LocalDateTimeConverter();
    }

    @Test
    public void convertToDatabaseColumnTest_shouldConvert() {
        String result = CONVERTER.convertToDatabaseColumn(TEST_DATE_TIME);
        assertThat(result, is(equalTo(TEST_DATE_TIME_AS_STRING)));
    }

    @Test
    public void convertToEntityAttributeTest_shouldConvert() {
        LocalDateTime result = CONVERTER.convertToEntityAttribute(TEST_DATE_TIME_AS_STRING);
        assertThat(result, is(equalTo(TEST_DATE_TIME)));
    }

}