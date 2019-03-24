package com.jakubeeee.common.exception;

/**
 * Error thrown when custom JPA converter cannot convert entity attribute to database column or vice versa.
 */
public class ConversionError extends Error {

    public ConversionError(String message) {
        super(message);
    }

}
