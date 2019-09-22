package com.jakubeeee.core.service;

import com.jakubeeee.core.service.impl.DefaultEmailService;
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

import static com.jakubeeee.common.reflection.ReflectUtils.getFieldValue;
import static com.jakubeeee.core.util.EmailUtils.createMailMessage;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@Category(FlowControlUnitTestCategory.class)
@RunWith(SpringRunner.class)
public class DefaultEmailServiceTest {

    @TestConfiguration
    static class TestContextConfig {

        @MockBean
        private MailSender mailSender;

        @Bean
        public EmailService emailService() {
            return new DefaultEmailService(mailSender);
        }

    }

    @Autowired
    private EmailService emailService;

    @Test
    public void sendMailMessageTest() throws IllegalAccessException {
        SimpleMailMessage message = createMailMessage("test@test.com", "test content", "test subject");
        emailService.sendMailMessage(message);
        MailSender mailSender = getFieldValue(emailService, MailSender.class);
        verify(mailSender, times(1)).send(message);
    }

}
