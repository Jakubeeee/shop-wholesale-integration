package com.jakubeeee.security.impl.passwordreset;

import com.jakubeeee.security.ChangePasswordForm;
import com.jakubeeee.security.impl.user.User;

import java.util.Set;

/**
 * Interface for service beans used for operations related to resetting user password.
 */
public interface PasswordResetService {

    void handleForgotMyPasswordProcess(String email, String origin, String localeCode);

    void changePassword(ChangePasswordForm form);

    PasswordResetTokenValue findByValue(String value);

    Set<PasswordResetTokenValue> findAllByUser(User user);

}
