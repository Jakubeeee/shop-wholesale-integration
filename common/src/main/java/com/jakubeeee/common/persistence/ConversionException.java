package com.jakubeeee.common.persistence;

/**
 * Exception thrown when custom JPA converter cannot convert entity attribute to database column or vice versa.
 */
public class ConversionException extends RuntimeException {

    public ConversionException(String message) {
        super(message);
    }

}
