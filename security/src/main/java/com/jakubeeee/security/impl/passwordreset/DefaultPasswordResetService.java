package com.jakubeeee.security.impl.passwordreset;

import com.jakubeeee.common.persistence.DatabaseResultEmptyException;
import com.jakubeeee.core.EmailService;
import com.jakubeeee.core.MessageService;
import com.jakubeeee.security.ChangePasswordForm;
import com.jakubeeee.security.impl.user.SecurityService;
import com.jakubeeee.security.impl.user.UserFactory;
import com.jakubeeee.security.impl.user.UserValue;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

import static com.jakubeeee.common.DateTimeUtils.getCurrentDateTime;
import static com.jakubeeee.common.DateTimeUtils.isTimeAfter;
import static com.jakubeeee.core.EmailUtils.createMailMessage;

/**
 * Default service bean used for operations related to resetting user password.
 */
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class DefaultPasswordResetService implements PasswordResetService {

    private static final PasswordResetTokenFactory passwordResetTokenFactory = PasswordResetTokenFactory.getInstance();
    private static final UserFactory userFactory = UserFactory.getInstance();

    private final SecurityService securityService;

    private final EmailService emailService;

    private final MessageService messageService;

    private final PasswordResetTokenRepository passwordResetTokenRepository;

    @Value("${reset.token.lifetime.in.minutes}")
    private int TOKEN_LIFETIME_IN_MINUTES;

    @Transactional
    @Override
    public void handleForgotMyPasswordProcess(String email, String origin, String localeCode) {
        PasswordResetTokenValue resetToken = createPasswordResetToken(email);
        UserValue tokenOwner = resetToken.getUser();
        String resetPasswordUrl = createResetPasswordUrl(origin, tokenOwner.getDatabaseId(), resetToken.getValue());
        sendEmailWithResetToken(tokenOwner, resetPasswordUrl, new Locale(localeCode));
    }

    private PasswordResetTokenValue createPasswordResetToken(String email) {
        UserValue user = securityService.findByEmail(email);
        var resetTokenValue = UUID.randomUUID().toString();
        LocalDateTime now = getCurrentDateTime();
        var resetToken = new PasswordResetTokenValue(null, resetTokenValue, now.plusMinutes(TOKEN_LIFETIME_IN_MINUTES), user);
        passwordResetTokenRepository.save(passwordResetTokenFactory.createEntity(resetToken));
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

    private void sendEmailWithResetToken(UserValue user, String resetPasswordUrl, Locale locale) {
        String emailContent = messageService.getMessage("passwordResetEmailContent", locale)
                + "\r\n" + resetPasswordUrl;
        String emailSubject = messageService.getMessage("passwordResetEmailSubject", locale);
        SimpleMailMessage mailMessage = createMailMessage(user.getEmail(), emailContent, emailSubject);
        emailService.sendMailMessage(mailMessage);
    }

    @Override
    public void changePassword(ChangePasswordForm form) {
        PasswordResetTokenValue passwordResetToken = findByValue(form.getResetToken());
        if (isTimeAfter(getCurrentDateTime(), passwordResetToken.getExpiryDate()))
            throw new PasswordResetTokenExpiredException("Password reset token has expired");
        UserValue tokenOwner = passwordResetToken.getUser();
        if (!Objects.equals(tokenOwner.getDatabaseId(), form.getUserId()))
            throw new DifferentPasswordResetTokenOwnerException("Password reset token owner is different");
        securityService.updateUserPassword(tokenOwner.getDatabaseId(), form.getPassword());
    }

    @Override
    public PasswordResetTokenValue findByValue(String value) {
        Optional<PasswordResetToken> tokenO = passwordResetTokenRepository.findByValue(value);
        return passwordResetTokenFactory.createValue(tokenO.orElseThrow(() -> new DatabaseResultEmptyException("Token with value " + value
                + " not found in the database")));
    }

    @Override
    public Set<PasswordResetTokenValue> findAllByUser(UserValue user) {
        return passwordResetTokenFactory.createValues(passwordResetTokenRepository.findAllByUser(userFactory.createEntity(user)));
    }

}
