package com.jakubeeee.security.model;

import com.jakubeeee.security.annotation.FieldMatch;
import lombok.AllArgsConstructor;
import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * Immutable data access object for password changing functionality.
 */
@Value
@AllArgsConstructor
@FieldMatch(first = "password", second = "passwordConfirm")
public class ChangePasswordForm {

    private static final String PASSWORD_REGEX = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,25}$";

    @NotBlank
    @Size(min = 8, max = 25)
    @Pattern(regexp = PASSWORD_REGEX)
    private final String password;

    @NotBlank
    @Size(min = 8, max = 25)
    @Pattern(regexp = PASSWORD_REGEX)
    private final String passwordConfirm;

    @NotNull
    private final Long userId;

    @NotBlank
    private final String resetToken;

}
