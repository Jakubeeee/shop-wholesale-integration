package com.jakubeeee.security.impl.passwordreset;

import com.jakubeeee.common.persistence.IdentifiableEntityValue;
import com.jakubeeee.security.impl.user.User;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import lombok.Value;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;

/**
 * Immutable value object for {@link PasswordResetToken} data transfer.
 */
@EqualsAndHashCode(callSuper = false)
@ToString
@Value
public final class PasswordResetTokenValue extends IdentifiableEntityValue<PasswordResetToken> {

    private final String value;

    private final LocalDateTime expiryDate;

    private final User user;

    public PasswordResetTokenValue(@Nullable Long databaseId,
                                   @NonNull String value,
                                   @NonNull LocalDateTime expiryDate,
                                   @NonNull User user) {
        super(databaseId);
        this.value = value;
        this.expiryDate = expiryDate;
        this.user = user;
    }

}
