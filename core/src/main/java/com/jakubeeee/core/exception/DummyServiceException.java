package com.jakubeeee.core.exception;

import com.jakubeeee.core.service.DummyService;

/**
 * Exception thrown when an implementation of {@link DummyService} is found in place of a real implementation.
 */
public class DummyServiceException extends Exception {

    public DummyServiceException(String message) {
        super(message);
    }

}
