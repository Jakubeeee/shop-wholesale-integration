package com.jakubeeee.common.persistence;

import com.jakubeeee.testutils.marker.BehavioralUnitTestCategory;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static com.jakubeeee.testutils.TestSubjectFactory.getTestSubject;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@Category(BehavioralUnitTestCategory.class)
public class JsonObjectConverterTest {

    private static JsonObjectConverter CONVERTER;

    private static String TEST_JSON;

    @BeforeClass
    public static void setUp() {
        CONVERTER = new JsonObjectConverter();
        TEST_JSON = getTestSubject(1).asJson();
    }

    @Test
    public void convertToDatabaseColumnTest_shouldConvert() {
        var testSubject = getTestSubject(1);
        String result = CONVERTER.convertToDatabaseColumn(testSubject);
        assertThat(result, is(equalTo(TEST_JSON)));
    }

    @Test
    public void convertToEntityAttributeTest_shouldConvert() {
        var expectedResult = getTestSubject(1).asMap();
        Object result = CONVERTER.convertToEntityAttribute(TEST_JSON);
        assertThat(result, is(equalTo(expectedResult)));
    }

    @Test(expected = ConversionException.class)
    public void convertToEntityAttributeTest_shouldThrowException() {
        String malformedTestJson = TEST_JSON.replace("}", "{");
        CONVERTER.convertToEntityAttribute(malformedTestJson);
    }

}