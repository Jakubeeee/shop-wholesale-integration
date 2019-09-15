package com.jakubeeee.security.controller;

import com.jakubeeee.security.model.ChangePasswordForm;
import com.jakubeeee.security.service.PasswordResetService;
import com.jakubeeee.security.service.SecurityService;
import com.jakubeeee.testutils.marker.SpringSliceTestCategory;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static com.jakubeeee.security.controller.SecurityControllerTestUtils.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Category(SpringSliceTestCategory.class)
@RunWith(SpringRunner.class)
@WebMvcTest(SecurityController.class)
public class SecurityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SecurityService securityService;

    @MockBean
    private PasswordResetService passwordResetService;

    @Test
    public void isAuthenticatedTest_shouldReturn200() throws Exception {
        mockMvc.perform(get("/is-authenticated")).andExpect(status().isOk());
        verify(securityService, times(1)).isAuthenticated();
    }

    @Test
    public void getCurrentUsernameTest_shouldReturn200() throws Exception {
        mockMvc.perform(get("/get-current-username")).andExpect(status().isOk());
        verify(securityService, times(1)).getCurrentUsername();
    }

    @Test
    public void handleForgotMyPasswordRequestTest_shouldReturn200() throws Exception {
        performForgotMyPasswordRequest(mockMvc, "en");
        verify(passwordResetService, times(1)).handleForgotMyPasswordProcess(eq(TEST_EMAIL_ADDRESS),
                any(String.class), eq("en"));
    }

    @Test
    public void handleChangePasswordRequest_shouldReturn200() throws Exception {
        Long testUserId = 1L;
        String requestBody = getChangePasswordRequestBody(VALID_TEST_PASSWORD_1, testUserId, TEST_TOKEN);
        performChangePasswordRequest(mockMvc, requestBody).andExpect(status().isOk());
        var testForm = new ChangePasswordForm(VALID_TEST_PASSWORD_1, VALID_TEST_PASSWORD_1, testUserId, TEST_TOKEN);
        verify(passwordResetService, times(1)).changePassword(testForm);
    }

    @Test
    public void handleChangePasswordRequest_passwordsNotMatching_shouldReturn400() throws Exception {
        String requestBody = getChangePasswordRequestBody(VALID_TEST_PASSWORD_1, VALID_TEST_PASSWORD_2, 1L, TEST_TOKEN);
        performChangePasswordRequest(mockMvc, requestBody).andExpect(status().isBadRequest());
        verifyZeroInteractions(passwordResetService);
    }

    @Test
    public void handleChangePasswordRequest_invalidPasswordWithoutDigit_shouldReturn400() throws Exception {
        String requestBody = getChangePasswordRequestBody(INVALID_TEST_PASSWORD_WITHOUT_DIGIT, 1L, TEST_TOKEN);
        performChangePasswordRequest(mockMvc, requestBody).andExpect(status().isBadRequest());
        verifyZeroInteractions(passwordResetService);
    }

    @Test
    public void handleChangePasswordRequest_invalidPasswordWithoutCapitalLetter_shouldReturn400() throws Exception {
        String requestBody = getChangePasswordRequestBody(INVALID_TEST_PASSWORD_WITHOUT_CAPITAL_LETTER, 1L, TEST_TOKEN);
        performChangePasswordRequest(mockMvc, requestBody).andExpect(status().isBadRequest());
        verifyZeroInteractions(passwordResetService);
    }

    @Test
    public void handleChangePasswordRequest_invalidPasswordWithoutLowerCase_shouldReturn400() throws Exception {
        String requestBody = getChangePasswordRequestBody(INVALID_TEST_PASSWORD_WITHOUT_LOWER_CASE, 1L, TEST_TOKEN);
        performChangePasswordRequest(mockMvc, requestBody).andExpect(status().isBadRequest());
        verifyZeroInteractions(passwordResetService);
    }

    @Test
    public void handleChangePasswordRequest_invalidPasswordTooShort_shouldReturn400() throws Exception {
        String requestBody = getChangePasswordRequestBody(INVALID_TEST_PASSWORD_TOO_SHORT, 1L, TEST_TOKEN);
        performChangePasswordRequest(mockMvc, requestBody).andExpect(status().isBadRequest());
        verifyZeroInteractions(passwordResetService);
    }

    @Test
    public void handleChangePasswordRequest_invalidPasswordTooLong_shouldReturn400() throws Exception {
        String requestBody = getChangePasswordRequestBody(INVALID_TEST_PASSWORD_TOO_LONG, 1L, TEST_TOKEN);
        performChangePasswordRequest(mockMvc, requestBody).andExpect(status().isBadRequest());
        verifyZeroInteractions(passwordResetService);
    }

    @Test
    public void handleChangePasswordRequest_missingUserId_shouldReturn400() throws Exception {
        String requestBody = getChangePasswordRequestBody(VALID_TEST_PASSWORD_1, null, TEST_TOKEN);
        performChangePasswordRequest(mockMvc, requestBody).andExpect(status().isBadRequest());
        verifyZeroInteractions(passwordResetService);
    }

    @Test
    public void handleChangePasswordRequest_missingResetToken_shouldReturn400() throws Exception {
        String requestBody = getChangePasswordRequestBody(VALID_TEST_PASSWORD_1, 1L, null);
        performChangePasswordRequest(mockMvc, requestBody).andExpect(status().isBadRequest());
        verifyZeroInteractions(passwordResetService);
    }

    @Test
    public void handleChangePasswordRequest_missingPassword_shouldReturn400() throws Exception {
        String requestBody = getChangePasswordRequestBody(null, VALID_TEST_PASSWORD_1, 1L, TEST_TOKEN);
        performChangePasswordRequest(mockMvc, requestBody).andExpect(status().isBadRequest());
        verifyZeroInteractions(passwordResetService);
    }

    @Test
    public void handleChangePasswordRequest_missingPasswordConfirm_shouldReturn400() throws Exception {
        String requestBody = getChangePasswordRequestBody(VALID_TEST_PASSWORD_1, null, 1L, TEST_TOKEN);
        performChangePasswordRequest(mockMvc, requestBody).andExpect(status().isBadRequest());
        verifyZeroInteractions(passwordResetService);
    }

    @Test
    public void checkUsernameUniqueness_shouldReturn200() throws Exception {
        String testUsername = "testUsername";
        mockMvc.perform(post("/is-username-unique")
                .content(testUsername)
                .contentType(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk());
        verify(securityService, times(1)).isUsernameUnique(testUsername);
    }

    @Test
    public void checkEmailUniqueness_shouldReturn200() throws Exception {
        mockMvc.perform(post("/is-email-unique")
                .content(TEST_EMAIL_ADDRESS)
                .contentType(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk());
        verify(securityService, times(1)).isEmailUnique(TEST_EMAIL_ADDRESS);
    }

}
