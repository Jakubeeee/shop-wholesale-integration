package com.jakubeeee.common.persistence;

/**
 * Exception thrown when the database query unexpectedly returns empty result.
 */
public class DatabaseResultEmptyException extends RuntimeException {

    public DatabaseResultEmptyException(String message) {
        super(message);
    }

}
