package com.jakubeeee.security.impl.passwordreset;

import com.jakubeeee.common.persistence.ImmutableValue;
import com.jakubeeee.security.impl.user.User;
import lombok.Value;

import java.time.LocalDateTime;

/**
 * Immutable value object for {@link PasswordResetToken} data transfer.
 */
@Value
public final class PasswordResetTokenValue implements ImmutableValue<PasswordResetToken> {

    private final String value;

    private final LocalDateTime expiryDate;

    private final User user;

    public PasswordResetTokenValue(String value, LocalDateTime expiryDate, User user) {
        this.value = value;
        this.expiryDate = expiryDate;
        this.user = user;
    }

}
