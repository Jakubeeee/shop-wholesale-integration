package com.jakubeeee.security.exception;

import com.jakubeeee.security.entity.PasswordResetToken;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when used {@link PasswordResetToken} object has expired.
 */
@ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "Password reset token has expired")
public class PasswordResetTokenExpiredException extends RuntimeException {

    public PasswordResetTokenExpiredException(String message) {
        super(message);
    }

}
