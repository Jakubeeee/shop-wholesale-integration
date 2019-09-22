package com.jakubeeee.core;

/**
 * Exception thrown when an implementation of {@link DummyService} is found in place of a real implementation.
 */
public class DummyServiceException extends Exception {

    public DummyServiceException(String message) {
        super(message);
    }

}
