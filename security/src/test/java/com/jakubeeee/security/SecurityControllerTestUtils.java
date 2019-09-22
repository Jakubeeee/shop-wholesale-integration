package com.jakubeeee.security;

import lombok.experimental.UtilityClass;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static com.jakubeeee.common.StringUtils.removeLastChar;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * Utility class providing useful static methods and constants related to security and user management.
 * Those methods are used in tests only.
 */
@UtilityClass
final class SecurityControllerTestUtils {

    static final String VALID_TEST_PASSWORD_1 = "testPassword1";
    static final String VALID_TEST_PASSWORD_2 = "testPassword2";
    static final String INVALID_TEST_PASSWORD_WITHOUT_DIGIT = "testPassword";
    static final String INVALID_TEST_PASSWORD_WITHOUT_CAPITAL_LETTER = "testpassword1";
    static final String INVALID_TEST_PASSWORD_WITHOUT_LOWER_CASE = "PASSWORD1";
    static final String INVALID_TEST_PASSWORD_TOO_SHORT = "pass";
    static final String INVALID_TEST_PASSWORD_TOO_LONG = "testPasswordtestPasswordtestPassword";
    static final String UPDATED_TEST_PASSWORD = "updatedTestPassword1";
    static final String TEST_TOKEN = "testToken";
    static final String TEST_EMAIL_ADDRESS = "testmail1@mail.com";

    static ResultActions performForgotMyPasswordRequest(MockMvc mockMvc, String language) throws Exception {
        return mockMvc.perform(post("/forgot-my-password")
                .content(TEST_EMAIL_ADDRESS)
                .contentType(MediaType.TEXT_PLAIN)
                .header("Accept-language", language));
    }

    static ResultActions performChangePasswordRequest(MockMvc mockMvc, String requestBody) throws Exception {
        return mockMvc.perform(post("/change-password")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON));
    }

    static String getChangePasswordRequestBody(String password, Long userId, String resetToken) {
        return getChangePasswordRequestBody(password, password, userId, resetToken);
    }

    static String getChangePasswordRequestBody(String password, String passwordConfirm, Long userId,
                                               String resetToken) {
        var builder = new StringBuilder("{ ");
        if (userId != null)
            builder.append("\"userId\": ").append(userId).append(",");
        if (resetToken != null)
            builder.append("\"resetToken\": ").append("\"").append(resetToken).append("\"").append(",");
        if (password != null)
            builder.append("\"password\": ").append("\"").append(password).append("\"").append(",");
        if (passwordConfirm != null)
            builder.append("\"passwordConfirm\": ").append("\"").append(passwordConfirm).append("\"").append(",");
        builder = removeLastChar(builder);
        return builder.append(" }").toString();
    }

}
