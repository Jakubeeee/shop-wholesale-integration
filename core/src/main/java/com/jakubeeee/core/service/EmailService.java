package com.jakubeeee.core.service;

import org.springframework.mail.SimpleMailMessage;

/**
 * Interface for service beans used for operations related to e-mail communication.
 */
public interface EmailService {

    void sendMailMessage(SimpleMailMessage message);

}
