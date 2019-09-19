package com.jakubeeee.core.service.impl;

import com.jakubeeee.core.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;

/**
 * Default service bean used for operations related to localized messages operations.
 */
@RequiredArgsConstructor
@Service
public class DefaultMessageService implements MessageService {

    private final MessageSource messageSource;

    @Override
    public String getMessage(String messageKey, Locale locale) {
        return messageSource.getMessage(messageKey, null, locale);
    }

}
