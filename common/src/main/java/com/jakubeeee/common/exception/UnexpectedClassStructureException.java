package com.jakubeeee.common.exception;

/**
 * Exception thrown when the structure of the class to which access is
 * obtained by means of the reflection mechanism is different than expected.
 */
public class UnexpectedClassStructureException extends Exception {

    public UnexpectedClassStructureException(String message) {
        super(message);
    }

}
