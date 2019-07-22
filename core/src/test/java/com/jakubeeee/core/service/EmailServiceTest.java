package com.jakubeeee.core.service;

import com.jakubeeee.testutils.marker.FlowControlUnitTestCategory;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.context.junit4.SpringRunner;

import static com.jakubeeee.common.util.ReflectUtils.*;
import static com.jakubeeee.core.util.EmailUtils.createMailMessage;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@Category(FlowControlUnitTestCategory.class)
@RunWith(SpringRunner.class)
public class EmailServiceTest {

    @TestConfiguration
    static class TestContextConfig {

        @MockBean
        private MailSender mailSender;

        @Bean
        public EmailService emailService() {
            return new EmailService(mailSender);
        }

    }

    @Autowired
    private EmailService emailService;

    @Test
    public void sendMailMessageTest() throws IllegalAccessException {
        SimpleMailMessage message = createMailMessage("test@test.com", "test content", "test subject");
        emailService.sendMailMessage(message);
        MailSender mailSender = getFieldValue( emailService, MailSender.class);
        verify(mailSender, times(1)).send(message);
    }

}
