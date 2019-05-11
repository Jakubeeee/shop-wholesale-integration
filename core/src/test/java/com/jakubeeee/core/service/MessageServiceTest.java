package com.jakubeeee.core.service;

import com.jakubeeee.testutils.marker.FlowControlUnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import static com.jakubeeee.common.util.ReflectUtils.getFieldValue;
import static java.util.Locale.ENGLISH;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@Category(FlowControlUnitTest.class)
@RunWith(SpringRunner.class)
public class MessageServiceTest {

    @TestConfiguration
    static class TestContextConfig {

        @MockBean
        private MessageSource messageSource;

        @Bean
        public MessageService messageService() {
            return new MessageService(messageSource);
        }

    }

    @Autowired
    private MessageService messageService;

    @Test
    public void getMessageTest() throws IllegalAccessException {
        String testMessageKey = "testMessageKey";
        messageService.getMessage(testMessageKey, ENGLISH);
        MessageSource messageSource = getFieldValue("messageSource", messageService, MessageSource.class);
        verify(messageSource, times(1)).getMessage(testMessageKey, null, ENGLISH);
    }

}
