package com.jakubeeee.core.service.impl;

import com.jakubeeee.core.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

/**
 * Default service bean used for operations related to e-mail communication.
 */
@RequiredArgsConstructor
@Service
public class DefaultEmailService implements EmailService {

    private final MailSender mailSender;

    @Override
    public void sendMailMessage(SimpleMailMessage message) {
        mailSender.send(message);
    }

}
