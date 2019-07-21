package com.jakubeeee.security.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when provided email is not unique when it should be.
 */
@ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "Provided email is not unique")
public class EmailNotUniqueException extends RuntimeException {

    public EmailNotUniqueException(String message) {
        super(message);
    }

}
