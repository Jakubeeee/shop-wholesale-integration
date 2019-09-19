package com.jakubeeee.core.service;

import java.util.Locale;

/**
 * Interface for service beans used for operations related to localized messages operations.
 */
public interface MessageService {

    String getMessage(String messageKey, Locale locale);

}
