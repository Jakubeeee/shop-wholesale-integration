package com.jakubeeee.core;

import lombok.experimental.UtilityClass;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;

/**
 * Utility class providing useful static methods related to e-mail communication.
 */
@UtilityClass
public final class EmailUtils {

    @Value("${spring.mail.username}")
    private static String SENDER_EMAIL_ADDRESS;

    public static SimpleMailMessage createMailMessage(String emailAddress, String emailContent, String emailSubject) {
        var mailMessage = new SimpleMailMessage();
        mailMessage.setTo(emailAddress);
        mailMessage.setSubject(emailSubject);
        mailMessage.setText(emailContent);
        mailMessage.setFrom(SENDER_EMAIL_ADDRESS);
        return mailMessage;
    }

}
