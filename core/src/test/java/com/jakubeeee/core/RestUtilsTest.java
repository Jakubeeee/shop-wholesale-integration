package com.jakubeeee.core;

import com.jakubeeee.testutils.marker.BehavioralUnitTestCategory;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.http.HttpHeaders;

import static com.jakubeeee.core.RestUtils.generateHeaderWithAuthToken;
import static com.jakubeeee.core.RestUtils.generateHeaderWithUsernameAndPassword;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.util.Base64Utils.encodeToString;

@Category(BehavioralUnitTestCategory.class)
public class RestUtilsTest {

    private static String TEST_TOKEN_VALUE;
    private static String TEST_USERNAME;
    private static String TEST_PASSWORD;
    private static String TEST_USERNAME_AND_PASSWORD_ENCODED;

    @BeforeClass
    public static void setUp() {
        TEST_TOKEN_VALUE = "123456789";
        TEST_USERNAME = "TestUsername";
        TEST_PASSWORD = "TestPassword";
        TEST_USERNAME_AND_PASSWORD_ENCODED = encodeToString((TEST_USERNAME + ":" + TEST_PASSWORD).getBytes());
    }

    @Test
    public void generateHeaderWithAuthTokenTest() {
        var expectedResult = new HttpHeaders();
        expectedResult.add("Authorization", "Bearer " + TEST_TOKEN_VALUE);
        HttpHeaders result = generateHeaderWithAuthToken(TEST_TOKEN_VALUE);
        assertThat(result, is(equalTo(expectedResult)));
    }

    @Test
    public void generateHeaderWithUsernameAndPasswordTest() {
        var expectedResult = new HttpHeaders();
        expectedResult.add("Authorization", "Basic " + TEST_USERNAME_AND_PASSWORD_ENCODED);
        HttpHeaders result = generateHeaderWithUsernameAndPassword(TEST_USERNAME, TEST_PASSWORD);
        assertThat(result, is(equalTo(expectedResult)));
    }

}
