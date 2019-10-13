package com.jakubeeee.security;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.jakubeeee.security.impl.passwordreset.PasswordResetService;
import com.jakubeeee.security.impl.passwordreset.PasswordResetToken;
import com.jakubeeee.security.impl.user.SecurityService;
import com.jakubeeee.security.impl.user.User;
import com.jakubeeee.testcore.AbstractIntegrationTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.internet.MimeMessage;
import javax.persistence.EntityManager;
import java.util.Set;

import static com.github.springtestdbunit.annotation.DatabaseOperation.DELETE_ALL;
import static com.github.springtestdbunit.annotation.DatabaseOperation.INSERT;
import static com.jakubeeee.security.SecurityControllerTestUtils.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@TestPropertySource(locations = "classpath:security-test.properties")
@DatabaseSetup(value = "/dbunit/insert_test_users.xml", type = INSERT)
@DatabaseTearDown(value = "/dbunit/delete_test_users.xml", type = DELETE_ALL)
public class SecurityControllerIT extends AbstractIntegrationTest {

    @Autowired
    SecurityService securityService;

    @Autowired
    PasswordResetService passwordResetService;

    @Autowired
    EntityManager entityManager;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    @Transactional
    public void handleForgotMyPasswordRequestIntegrationTest_tokenEqualityCheck() throws Exception {
        performForgotMyPasswordRequest(mockMvc, "en").andExpect(status().isOk());
        User user = securityService.findByEmail(TEST_EMAIL_ADDRESS);

        Set<PasswordResetToken> tokens = passwordResetService.findAllByUser(user);
        assertThat(tokens.size(), is(equalTo(1)));
        PasswordResetToken tokenFromUser = tokens.iterator().next();

        PasswordResetToken tokenFromDatabase = passwordResetService.findByValue(tokenFromUser.getValue());
        assertThat(tokenFromDatabase, is(equalTo(tokenFromUser)));
    }

    @Test
    @Transactional
    public void handleForgotMyPasswordRequestIntegrationTest_receivedMessagesAmountCheck() throws Exception {
        performForgotMyPasswordRequest(mockMvc, "en").andExpect(status().isOk());
        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();

        assertThat(receivedMessages.length, is(equalTo(1)));
    }

    @Test
    @Transactional
    public void handleForgotMyPasswordRequestIntegrationTest_englishLanguage_subjectCheck() throws Exception {
        performForgotMyPasswordRequest(mockMvc, "en").andExpect(status().isOk());
        MimeMessage receivedMessage = greenMail.getReceivedMessages()[0];

        String expectedSubject = "Password reset (integrator)";
        assertThat(receivedMessage.getSubject(), is(equalTo(expectedSubject)));
    }

    @Test
    @Transactional
    public void handleForgotMyPasswordRequestIntegrationTest_polishLanguage_subjectCheck() throws Exception {
        performForgotMyPasswordRequest(mockMvc, "pl").andExpect(status().isOk());
        MimeMessage receivedMessage = greenMail.getReceivedMessages()[0];

        String expectedSubject = "Resetowanie has\\u0142a (integrator)";
        assertThat(receivedMessage.getSubject(), is(equalTo(expectedSubject)));
    }

    @Test
    @Transactional
    public void handleForgotMyPasswordRequestIntegrationTest_englishLanguage_contentCheck() throws Exception {
        performForgotMyPasswordRequest(mockMvc, "en").andExpect(status().isOk());
        MimeMessage receivedMessage = greenMail.getReceivedMessages()[0];
        User user = securityService.findByEmail(TEST_EMAIL_ADDRESS);
        Set<PasswordResetToken> tokens = passwordResetService.findAllByUser(user);
        PasswordResetToken tokenFromUser = tokens.iterator().next();

        String expectedContent = "Click the link below to reset your password:\r\n" +
                "http://localhost:80/#/change-password?id=1&token=" + tokenFromUser.getValue() + "\r\n";
        assertThat(receivedMessage.getContent(), is(equalTo(expectedContent)));
    }

    @Test
    @Transactional
    public void handleForgotMyPasswordRequestIntegrationTest_polishLanguage_contentCheck() throws Exception {
        performForgotMyPasswordRequest(mockMvc, "pl").andExpect(status().isOk());
        MimeMessage receivedMessage = greenMail.getReceivedMessages()[0];
        User user = securityService.findByEmail(TEST_EMAIL_ADDRESS);
        Set<PasswordResetToken> tokens = passwordResetService.findAllByUser(user);
        PasswordResetToken tokenFromUser = tokens.iterator().next();

        String expectedContent = "Kliknij poni\\u017Cszy link aby zresetowa\\u0107 has\\u0142o:\r\n" +
                "http://localhost:80/#/change-password?id=1&token=" + tokenFromUser.getValue() + "\r\n";
        assertThat(receivedMessage.getContent(), is(equalTo(expectedContent)));
    }

    @Test
    @Transactional
    public void handleForgotMyPasswordRequestIntegrationTest_contentTypeCheck() throws Exception {
        performForgotMyPasswordRequest(mockMvc, "en").andExpect(status().isOk());
        MimeMessage receivedMessage = greenMail.getReceivedMessages()[0];

        String expectedContentType = "text/plain; charset=UTF-8";
        assertThat(receivedMessage.getContentType(), is(equalTo(expectedContentType)));
    }

    @Test
    @Transactional
    public void handleForgotMyPasswordRequestIntegrationTest_receiverCheck() throws Exception {
        performForgotMyPasswordRequest(mockMvc, "en").andExpect(status().isOk());
        MimeMessage receivedMessage = greenMail.getReceivedMessages()[0];

        assertThat(receivedMessage.getAllRecipients().length, is(equalTo(1)));
        assertThat(receivedMessage.getAllRecipients()[0].toString(), is(equalTo((TEST_EMAIL_ADDRESS))));
    }

    @Test
    @Transactional
    public void handleChangePasswordRequestIntegrationTest() throws Exception {
        performForgotMyPasswordRequest(mockMvc, "en").andExpect(status().isOk());
        User user = securityService.findByEmail(TEST_EMAIL_ADDRESS);
        Set<PasswordResetToken> tokens = passwordResetService.findAllByUser(user);
        PasswordResetToken tokenFromUser = tokens.iterator().next();
        performChangePasswordRequest(mockMvc,
                getChangePasswordRequestBody(UPDATED_TEST_PASSWORD, 1L, tokenFromUser.getValue()))
                .andExpect(status().isOk());
        entityManager.refresh(user);
        assertThat(passwordEncoder.matches(UPDATED_TEST_PASSWORD, user.getPassword()), is(equalTo(true)));
    }

    @Test
    public void checkUsernameUniquenessIntegrationTest_unique_shouldReturnTrue() throws Exception {
        mockMvc.perform(post("/is-username-unique")
                .content("testUsername4")
                .contentType(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    public void checkUsernameUniquenessIntegrationTest_nonUnique_shouldReturnFalse() throws Exception {
        mockMvc.perform(post("/is-username-unique")
                .content("testUsername1")
                .contentType(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }


    @Test
    public void checkEmailUniquenessIntegrationTest_unique_shouldReturnTrue() throws Exception {
        mockMvc.perform(post("/is-email-unique")
                .content("testmail4@mail.com")
                .contentType(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    public void checkEmailUniquenessIntegrationTest_nonUnique_shouldReturnFalse() throws Exception {
        mockMvc.perform(post("/is-email-unique")
                .content("testmail1@mail.com")
                .contentType(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

}