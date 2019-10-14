package com.jakubeeee.security.impl.passwordreset;

import com.jakubeeee.common.persistence.BaseEntityFactory;
import com.jakubeeee.security.impl.user.UserFactory;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * Entity factory used for production of {@link PasswordResetToken} from {@link PasswordResetTokenValue} and vice versa.
 */
@NoArgsConstructor(staticName = "getInstance")
public class PasswordResetTokenFactory extends BaseEntityFactory<PasswordResetToken, PasswordResetTokenValue> {

    private static final UserFactory userFactory = UserFactory.getInstance();

    @Override
    public PasswordResetToken createEntity(@NonNull PasswordResetTokenValue value) {
        var passwordResetToken = new PasswordResetToken();
        passwordResetToken.setId(value.getDatabaseId());
        passwordResetToken.setValue(value.getValue());
        passwordResetToken.setExpiryDate(value.getExpiryDate());
        passwordResetToken.setUser(userFactory.createEntity(value.getUser()));
        return passwordResetToken;
    }

    @Override
    public PasswordResetTokenValue createValue(@NonNull PasswordResetToken entity) {
        return new PasswordResetTokenValue(entity.getId(), entity.getValue(), entity.getExpiryDate(),
                userFactory.createValue(entity.getUser()));
    }

}
