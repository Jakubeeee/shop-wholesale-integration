package com.jakubeeee.security.controllers;

import com.jakubeeee.security.exceptions.ValidationException;
import com.jakubeeee.security.model.User;
import com.jakubeeee.security.service.PasswordResetService;
import com.jakubeeee.security.service.SecurityService;
import com.jakubeeee.security.service.UserService;
import com.jakubeeee.security.validation.forms.ChangePasswordForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import static java.util.Objects.isNull;

@Slf4j
@RestController
public class SecurityController {

    @Autowired
    SecurityService securityService;

    @Autowired
    UserService userService;

    @Autowired
    PasswordResetService passwordResetService;

    @GetMapping("/isAuthenticated")
    public boolean isAuthenticated() {
        return securityService.isAuthenticated();
    }

    @GetMapping("/activeUser")
    public String getActiveUsername(@AuthenticationPrincipal User activeUser) {
        if (!isNull(activeUser))
            return activeUser.getUsername();
        else return "";
    }

    @PostMapping(path = "/forgotMyPassword", consumes = "text/plain")
    public void handleForgotMyPasswordRequest(
            @RequestBody String email, @RequestHeader("Accept-language") String localeCode, HttpServletRequest request) {
        String origin = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        passwordResetService.handleForgotMyPasswordProcess(email, origin, localeCode);
    }

    @PostMapping("/changePassword")
    public void handleChangePasswordRequest(@Validated @RequestBody ChangePasswordForm form) throws ValidationException {
        passwordResetService.changePassword(form);
    }

}
