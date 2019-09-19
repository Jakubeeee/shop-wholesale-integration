package com.jakubeeee.core.service.impl;

import com.jakubeeee.core.service.EmailService;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

/**
 * Default service bean used for operations related to e-mail communication.
 */
@Service
public class DefaultEmailService implements EmailService {

    private final MailSender mailSender;

    public DefaultEmailService(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendMailMessage(SimpleMailMessage message) {
        mailSender.send(message);
    }

}
