package com.jakubeeee.security.impl.passwordreset;

import com.jakubeeee.common.persistence.AbstractEntityFactory;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * Entity factory used for production of {@link PasswordResetToken} from {@link PasswordResetTokenValue} and vice versa.
 */
@NoArgsConstructor(staticName = "getInstance")
public class PasswordResetTokenFactory extends AbstractEntityFactory<PasswordResetToken, PasswordResetTokenValue> {

    @Override
    public PasswordResetToken createEntity(@NonNull PasswordResetTokenValue value) {
        var passwordResetToken = new PasswordResetToken();
        passwordResetToken.setValue(value.getValue());
        passwordResetToken.setExpiryDate(value.getExpiryDate());
        passwordResetToken.setUser(value.getUser());
        return passwordResetToken;
    }

    @Override
    public PasswordResetTokenValue createValue(@NonNull PasswordResetToken entity) {
        return new PasswordResetTokenValue(entity.getValue(), entity.getExpiryDate(), entity.getUser());
    }

}
