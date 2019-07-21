package com.jakubeeee.security.exception;

import com.jakubeeee.security.entity.PasswordResetToken;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when {@link PasswordResetToken}s owner id and provided users id do not match.
 */
@ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "Password reset token owner is different")
public class DifferentPasswordResetTokenOwnerException extends RuntimeException {

    public DifferentPasswordResetTokenOwnerException(String message) {
        super(message);
    }

}
