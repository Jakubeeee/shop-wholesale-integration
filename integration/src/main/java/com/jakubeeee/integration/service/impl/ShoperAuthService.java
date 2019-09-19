package com.jakubeeee.integration.service.impl;

import com.jakubeeee.core.service.RestService;
import com.jakubeeee.integration.model.ShoperAuthToken;
import com.jakubeeee.integration.service.AbstractAuthService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import static com.jakubeeee.common.util.DateTimeUtils.*;
import static com.jakubeeee.core.util.RestUtils.generateHeaderWithUsernameAndPassword;
import static java.util.Objects.requireNonNull;

/**
 * Service bean used for operations related to data source authentication in shoper platform.
 */
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class ShoperAuthService extends AbstractAuthService<ShoperAuthToken> {

    private final RestService restService;

    @Value("${shoperAuthUri}")
    String SHOPER_AUTH_URI;
    @Value("${shoperAdminUsername}")
    String SHOPER_USERNAME;
    @Value("${shoperAdminPassword}")
    String SHOPER_PASSWORD;

    public ShoperAuthService(RestService restService) {
        this.restService = restService;
    }

    @Override
    public String getTokenValue() {
        if (token == null || isTokenExpiringSoon()) refreshToken();
        return token.getValue();
    }

    private void refreshToken() {
        HttpHeaders headers = generateHeaderWithUsernameAndPassword(SHOPER_USERNAME, SHOPER_PASSWORD);
        ResponseEntity<ShoperAuthToken> response =
                restService.postJsonObject(SHOPER_AUTH_URI, new HttpEntity<>(headers), ShoperAuthToken.class);
        token = requireNonNull(response.getBody());
    }

    private boolean isTokenExpiringSoon() {
        long timeDifference = getTimeDifferenceInMillis(token.getCreationTime(), getCurrentDateTime());
        return (timeDifference > minutesToMillis(45));
    }

}
