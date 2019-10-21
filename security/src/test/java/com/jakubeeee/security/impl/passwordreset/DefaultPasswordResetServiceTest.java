package com.jakubeeee.security.impl.passwordreset;

import com.jakubeeee.common.DateTimeUtils;
import com.jakubeeee.common.persistence.DatabaseResultEmptyException;
import com.jakubeeee.core.EmailService;
import com.jakubeeee.core.MessageService;
import com.jakubeeee.security.ChangePasswordForm;
import com.jakubeeee.security.impl.role.RoleValue;
import com.jakubeeee.security.impl.user.SecurityService;
import com.jakubeeee.security.impl.user.User;
import com.jakubeeee.security.impl.user.UserFactory;
import com.jakubeeee.security.impl.user.UserValue;
import com.jakubeeee.testutils.marker.FlowControlUnitTestCategory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static com.jakubeeee.common.DateTimeUtils.getCurrentDateTime;
import static com.jakubeeee.common.DateTimeUtils.isTimeAfter;
import static com.jakubeeee.common.reflection.ReflectUtils.getFieldValue;
import static com.jakubeeee.security.impl.role.RoleType.BASIC_USER;
import static com.jakubeeee.testutils.DateTimeTestConstants.*;
import static java.util.UUID.randomUUID;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@SuppressWarnings("all")
@Category(FlowControlUnitTestCategory.class)
@TestPropertySource(locations = "classpath:security-test.properties")
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
@PrepareForTest({DefaultPasswordResetService.class, DateTimeUtils.class, UUID.class})
// @PowerMockIgnore annotation added because of a bug in power mock library (version: 2.0.0):
// https://github.com/powermock/powermock/issues/864
// to be removed in case it is fixed in future versions of power mock.
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.dom.*"})
public class DefaultPasswordResetServiceTest {

    @TestConfiguration
    static class TestContextConfig {

        @MockBean
        private SecurityService securityService;

        @MockBean
        private EmailService emailService;

        @MockBean
        private MessageService messageService;

        @MockBean
        private PasswordResetTokenRepository passwordResetTokenRepository;

        @Bean
        public PasswordResetService passwordResetService() {
            return new DefaultPasswordResetService(securityService, emailService,
                    messageService, passwordResetTokenRepository);
        }

    }

    @Autowired
    private PasswordResetService passwordResetService;

    @SpyBean
    private PasswordResetService passwordResetServiceSpy;

    private SecurityService securityService;
    private EmailService emailService;
    private MessageService messageService;
    private PasswordResetTokenRepository passwordResetTokenRepository;

    private Long testUserId;
    private String testEmail;
    private User testUser;
    private UserValue testUserValue;
    private String testValue1;
    private String testValue2;
    private PasswordResetToken testPasswordResetToken1;
    private PasswordResetToken testPasswordResetToken2;
    private PasswordResetTokenValue testPasswordResetTokenValue1;
    private PasswordResetTokenValue testPasswordResetTokenValue2;
    private ChangePasswordForm changePasswordForm;

    @Before
    public void setUpForEveryTest() throws Exception {
        securityService = getFieldValue(passwordResetService, SecurityService.class);
        emailService = getFieldValue(passwordResetService, EmailService.class);
        messageService = getFieldValue(passwordResetService, MessageService.class);
        passwordResetTokenRepository = getFieldValue(passwordResetService, PasswordResetTokenRepository.class);
        testUserId = 1L;
        testEmail = "testEmail";
        testUserValue = new UserValue(testUserId, "testUsername", "testPassword", testEmail, true,
                Set.of(RoleValue.of(BASIC_USER)));
        testUser = UserFactory.getInstance().createEntity(testUserValue);
        setField(testUser, "id", testUserId);
        testValue1 = "testValue1";
        testValue2 = "testValue2";
        testPasswordResetToken1 = new PasswordResetToken(testValue1, testUser, TEST_DATE_TIME, 240);
        testPasswordResetToken2 = new PasswordResetToken(testValue2, testUser, TEST_DATE_TIME_FIVE_HOURS_LATER, 240);
        testPasswordResetTokenValue1 = new PasswordResetTokenValue(null, testValue1, TEST_DATE_TIME.plusMinutes(240), testUserValue);
        testPasswordResetTokenValue2 = new PasswordResetTokenValue(null, testValue2, TEST_DATE_TIME_FIVE_HOURS_LATER.plusMinutes(240), testUserValue);
        changePasswordForm = new ChangePasswordForm("testPassword1", "testPassword1", testUserId, testValue1);
    }

    @Test
    public void handleForgotMyPasswordProcessTest() {
        when(securityService.findByEmail(testEmail)).thenReturn(testUserValue);
        var mockedUUID = PowerMockito.mock(UUID.class);
        PowerMockito.mockStatic(UUID.class);
        PowerMockito.when(randomUUID()).thenReturn(mockedUUID);
        when(mockedUUID.toString()).thenReturn(testValue1);
        mockGetCurrentTimeInvocation();
        String testEmailContent = "testEmailContent";
        String testEmailContentWithUrl = testEmailContent + "\r\nhttp://localhost:8080/#/change-password?id=1&token" +
                "=testValue1";
        String testEmailSubject = "testEmailSubject";
        when(messageService.getMessage("passwordResetEmailContent", Locale.ENGLISH))
                .thenReturn(testEmailContent);
        when(messageService.getMessage("passwordResetEmailSubject", Locale.ENGLISH))
                .thenReturn(testEmailSubject);
        passwordResetService.handleForgotMyPasswordProcess(testEmail, "http://localhost:8080", "en");
        verify(passwordResetTokenRepository, times(1)).save(testPasswordResetToken1);
        var mailMessage = new SimpleMailMessage();
        mailMessage.setTo(testUserValue.getEmail());
        mailMessage.setSubject(testEmailSubject);
        mailMessage.setText(testEmailContentWithUrl);
        verify(emailService, times(1)).sendMailMessage(mailMessage);
    }

    @Test
    public void changePasswordTest() {
        doReturn(Optional.of(testPasswordResetToken1)).when(passwordResetTokenRepository).findByValue(testValue1);
        mockGetCurrentTimeInvocation();
        useRealIsTimeAfterMethod();
        passwordResetServiceSpy.changePassword(changePasswordForm);
        verify(securityService, times(1)).updateUserPassword(testUserId, "testPassword1");
    }

    @Test(expected = DifferentPasswordResetTokenOwnerException.class)
    public void changePasswordTest_providedUserIdAndTokenOwenrIdDoNotMatch_shouldThrowException() {
        setField(testUser, "id", 2L);
        doReturn(Optional.of(testPasswordResetToken1)).when(passwordResetTokenRepository).findByValue(testValue1);
        mockGetCurrentTimeInvocation();
        useRealIsTimeAfterMethod();
        passwordResetServiceSpy.changePassword(changePasswordForm);
        verify(securityService, times(0)).updateUserPassword(testUserId, "testPassword1");
    }

    @Test(expected = PasswordResetTokenExpiredException.class)
    public void changePasswordTest_tokenHasExpired_shouldThrowException() {
        setField(testPasswordResetToken1, "expiryDate", TEST_DATE_TIME_FIVE_HOURS_EARLIER);
        doReturn(Optional.of(testPasswordResetToken1)).when(passwordResetTokenRepository).findByValue(testValue1);
        mockGetCurrentTimeInvocation();
        useRealIsTimeAfterMethod();
        passwordResetServiceSpy.changePassword(changePasswordForm);
        verify(securityService, times(0)).updateUserPassword(testUserId, "testPassword1");
    }

    @Test
    public void findByValueTest() {
        when(passwordResetTokenRepository.findByValue(testValue1)).thenReturn(Optional.of(testPasswordResetToken1));
        PasswordResetTokenValue result = passwordResetService.findByValue(testValue1);
        Assert.assertThat(result, is(equalTo(testPasswordResetTokenValue1)));
    }

    @Test(expected = DatabaseResultEmptyException.class)
    public void findByValueTest_passwordResetTokenNotFound_shouldThrowException() {
        when(passwordResetTokenRepository.findByValue(testValue1)).thenReturn(Optional.empty());
        passwordResetService.findByValue(testValue1);
    }

    @Test
    public void findAllByUserTest() {
        when(passwordResetTokenRepository.findAllByUser(testUser)).thenReturn(Set.of(testPasswordResetToken1, testPasswordResetToken2));
        Set<PasswordResetTokenValue> resultSet = passwordResetService.findAllByUser(testUserValue);
        assertThat(resultSet,
                hasItems(testPasswordResetTokenValue1, testPasswordResetTokenValue2)
        );
    }

    private void mockGetCurrentTimeInvocation() {
        PowerMockito.mockStatic(DateTimeUtils.class);
        PowerMockito.when(getCurrentDateTime()).thenReturn(TEST_DATE_TIME);
    }

    private void useRealIsTimeAfterMethod() {
        PowerMockito.when(isTimeAfter(TEST_DATE_TIME, TEST_DATE_TIME_FIVE_HOURS_EARLIER)).thenCallRealMethod();
    }

}
