package com.jakubeeee.core.service;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;

/**
 * Service class for operations related to localized messages operations.
 */
@Service
public class MessageService {

    private final MessageSource messageSource;

    public MessageService(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String getMessage(String messageKey, Locale locale) {
        return messageSource.getMessage(messageKey, null, locale);
    }

}
