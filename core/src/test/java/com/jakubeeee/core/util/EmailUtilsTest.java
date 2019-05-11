package com.jakubeeee.core.util;

import com.jakubeeee.testutils.marker.BehaviourUnitTest;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.mail.SimpleMailMessage;

import static com.jakubeeee.core.util.EmailUtils.createMailMessage;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@Category(BehaviourUnitTest.class)
public class EmailUtilsTest {

    private static String TEST_RECEIVER_EMAIL_ADDRESS;
    private static String TEST_SUBJECT;
    private static String TEST_TEXT;
    private static String TEST_SENDER_EMAIL_ADDRESS;

    @BeforeClass
    public static void setUp() {
        TEST_RECEIVER_EMAIL_ADDRESS = "receiver@test.com";
        TEST_SUBJECT = "test subject";
        TEST_TEXT = "test text";
        TEST_SENDER_EMAIL_ADDRESS = "sender@test.com";
    }

    @Test
    public void createMailMessageTest() {
        var expectedResult = new SimpleMailMessage();
        expectedResult.setTo(TEST_RECEIVER_EMAIL_ADDRESS);
        expectedResult.setSubject(TEST_SUBJECT);
        expectedResult.setText(TEST_TEXT);
        expectedResult.setFrom(TEST_SENDER_EMAIL_ADDRESS);
        setField(EmailUtils.class, "SENDER_EMAIL_ADDRESS", TEST_SENDER_EMAIL_ADDRESS);
        SimpleMailMessage result = createMailMessage(TEST_RECEIVER_EMAIL_ADDRESS, TEST_TEXT, TEST_SUBJECT);
        assertThat(result, is(equalTo(expectedResult)));
    }

}
