package com.jakubeeee.security.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when provided username is not unique when it should be.
 */
@ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "Provided username is not unique")
public class UsernameNotUniqueException extends RuntimeException {

    public UsernameNotUniqueException(String message) {
        super(message);
    }

}
