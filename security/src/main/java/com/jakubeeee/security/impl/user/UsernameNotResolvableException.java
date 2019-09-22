package com.jakubeeee.security.impl.user;

/**
 * Exception thrown when username cannot be resolved.
 */
public class UsernameNotResolvableException extends Exception {

    public UsernameNotResolvableException(String message) {
        super(message);
    }

}
