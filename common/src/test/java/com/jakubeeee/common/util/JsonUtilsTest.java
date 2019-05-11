package com.jakubeeee.common.util;

import com.jakubeeee.testutils.marker.BehaviourUnitTest;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.Optional;

import static com.jakubeeee.common.util.JsonUtils.jsonToObject;
import static com.jakubeeee.common.util.JsonUtils.objectToJson;
import static com.jakubeeee.testutils.factory.TestSubjectFactory.getTestSubject;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@Category(BehaviourUnitTest.class)
public class JsonUtilsTest {

    private static String TEST_JSON;

    @BeforeClass
    public static void setUp() {
        TEST_JSON = getTestSubject(1).asJson();
    }

    @Test
    public void objectToJsonTest_shouldConvert() {
        var testSubject = getTestSubject(1);
        Optional<String> resultO = objectToJson(testSubject);
        assertThat(resultO, is(equalTo(Optional.of(TEST_JSON))));
    }

    @Test
    public void jsonToObjectTest_shouldConvert() {
        var expectedResult = getTestSubject(1).asMap();
        Optional<Object> resultO = jsonToObject(TEST_JSON);
        assertThat(resultO, is(equalTo(Optional.of(expectedResult))));
    }

}
