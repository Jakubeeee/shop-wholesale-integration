package pl.jakubeeee.integration.exceptions;

public class AuthTokenExpiredException extends Exception {

    public AuthTokenExpiredException(String message) {
        super(message);
    }

}
