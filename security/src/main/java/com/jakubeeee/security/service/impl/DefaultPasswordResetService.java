package com.jakubeeee.security.service.impl;

import com.jakubeeee.common.exception.DatabaseResultEmptyException;
import com.jakubeeee.core.service.EmailService;
import com.jakubeeee.core.service.MessageService;
import com.jakubeeee.security.entity.PasswordResetToken;
import com.jakubeeee.security.entity.User;
import com.jakubeeee.security.exception.DifferentPasswordResetTokenOwnerException;
import com.jakubeeee.security.exception.PasswordResetTokenExpiredException;
import com.jakubeeee.security.model.ChangePasswordForm;
import com.jakubeeee.security.repository.PasswordResetTokenRepository;
import com.jakubeeee.security.service.PasswordResetService;
import com.jakubeeee.security.service.SecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static com.jakubeeee.common.util.DateTimeUtils.getCurrentDateTime;
import static com.jakubeeee.common.util.DateTimeUtils.isTimeAfter;
import static com.jakubeeee.core.util.EmailUtils.createMailMessage;

/**
 * Default service bean used for operations related to resetting user password.
 */
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class DefaultPasswordResetService implements PasswordResetService {

    private final SecurityService securityService;

    private final EmailService emailService;

    private final MessageService messageService;

    private final PasswordResetTokenRepository passwordResetTokenRepository;

    @Value("${reset.token.lifetime.in.minutes}")
    private int TOKEN_LIFETIME_IN_MINUTES;

    @Transactional
    @Override
    public void handleForgotMyPasswordProcess(String email, String origin, String localeCode) {
        PasswordResetToken resetToken = createPasswordResetToken(email);
        User tokenOwner = resetToken.getUser();
        String resetPasswordUrl = createResetPasswordUrl(origin, tokenOwner.getId(), resetToken.getValue());
        sendEmailWithResetToken(tokenOwner, resetPasswordUrl, new Locale(localeCode));
    }

    private PasswordResetToken createPasswordResetToken(String email) {
        User user = securityService.findByEmail(email);
        var resetTokenValue = UUID.randomUUID().toString();
        LocalDateTime now = getCurrentDateTime();
        var resetToken = new PasswordResetToken(resetTokenValue, user, now, TOKEN_LIFETIME_IN_MINUTES);
        passwordResetTokenRepository.save(resetToken);
        return resetToken;
    }

    private String createResetPasswordUrl(String origin, long userId, String token) {
        return origin
                + "/#/change-password"
                + "?id="
                + userId
                + "&token="
                + token;
    }

    private void sendEmailWithResetToken(User user, String resetPasswordUrl, Locale locale) {
        String emailContent = messageService.getMessage("passwordResetEmailContent", locale)
                + "\r\n" + resetPasswordUrl;
        String emailSubject = messageService.getMessage("passwordResetEmailSubject", locale);
        SimpleMailMessage mailMessage = createMailMessage(user.getEmail(), emailContent, emailSubject);
        emailService.sendMailMessage(mailMessage);
    }

    @Override
    public void changePassword(ChangePasswordForm form) {
        PasswordResetToken passwordResetToken = findByValue(form.getResetToken());
        if (isTimeAfter(getCurrentDateTime(), passwordResetToken.getExpiryDate()))
            throw new PasswordResetTokenExpiredException("Password reset token has expired");
        User tokenOwner = passwordResetToken.getUser();
        if (!Objects.equals(tokenOwner.getId(), form.getUserId()))
            throw new DifferentPasswordResetTokenOwnerException("Password reset token owner is different");
        securityService.updateUserPassword(tokenOwner.getId(), form.getPassword());
    }

    @Override
    public PasswordResetToken findByValue(String value) {
        Optional<PasswordResetToken> tokenO = passwordResetTokenRepository.findByValue(value);
        return tokenO.orElseThrow(() -> new DatabaseResultEmptyException("Token with value " + value + " not found in" +
                " the database"));
    }

}
