package com.jakubeeee.core.service;

import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

/**
 * Service class for operations related to e-mail communication.
 */
@Service
public class EmailService {

    private final MailSender mailSender;

    public EmailService(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendMailMessage(SimpleMailMessage message) {
        mailSender.send(message);
    }

}
