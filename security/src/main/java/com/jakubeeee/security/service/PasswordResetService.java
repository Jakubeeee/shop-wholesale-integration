package com.jakubeeee.security.service;

import com.jakubeeee.security.entity.PasswordResetToken;
import com.jakubeeee.security.model.ChangePasswordForm;

/**
 * Interface for service beans used for operations related to resetting user password.
 */
public interface PasswordResetService {

    void handleForgotMyPasswordProcess(String email, String origin, String localeCode);

    void changePassword(ChangePasswordForm form);

    PasswordResetToken findByValue(String value);

}
