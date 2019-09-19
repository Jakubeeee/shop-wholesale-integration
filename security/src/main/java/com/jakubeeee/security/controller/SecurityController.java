package com.jakubeeee.security.controller;

import com.jakubeeee.security.exception.UsernameNotResolvableException;
import com.jakubeeee.security.model.ChangePasswordForm;
import com.jakubeeee.security.service.PasswordResetService;
import com.jakubeeee.security.service.SecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * Rest controller for functionalities related to security and user management.
 */
@RequiredArgsConstructor
@RestController
public class SecurityController {

    private final SecurityService securityService;

    private final PasswordResetService passwordResetService;

    @GetMapping("/is-authenticated")
    public boolean isAuthenticated() {
        return securityService.isAuthenticated();
    }

    @GetMapping("/get-current-username")
    public String getCurrentUsername() {
        try {
            return securityService.getCurrentUsername();
        } catch (UsernameNotResolvableException e) {
            return "";
        }
    }

    @PostMapping(path = "/forgot-my-password", consumes = "text/plain")
    public void handleForgotMyPasswordRequest(@RequestBody String email,
                                              @RequestHeader("Accept-language") String localeCode,
                                              HttpServletRequest request) {
        String requestOrigin = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        passwordResetService.handleForgotMyPasswordProcess(email, requestOrigin, localeCode);
    }

    @PostMapping("/change-password")
    public void handleChangePasswordRequest(@Validated @RequestBody ChangePasswordForm form) {
        passwordResetService.changePassword(form);
    }

    @PostMapping(path = "/is-username-unique", consumes = "text/plain")
    public boolean checkUsernameUniqueness(@RequestBody String username) {
        return securityService.isUsernameUnique(username);
    }

    @PostMapping(path = "/is-email-unique", consumes = "text/plain")
    public boolean checkEmailUniqueness(@RequestBody String email) {
        return securityService.isEmailUnique(email);
    }

}
