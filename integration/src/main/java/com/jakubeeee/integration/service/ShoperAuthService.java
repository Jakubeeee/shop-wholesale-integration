package com.jakubeeee.integration.service;

import com.jakubeeee.core.service.RestService;
import com.jakubeeee.integration.model.ShoperAuthToken;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import static com.jakubeeee.common.util.DateTimeUtils.getTimeDifferenceInMillis;
import static com.jakubeeee.common.util.DateTimeUtils.minutesToMillis;
import static com.jakubeeee.core.util.RestUtils.*;
import static java.time.LocalDateTime.now;
import static java.util.Objects.requireNonNull;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class ShoperAuthService extends AbstractAuthService<ShoperAuthToken> {

    @Value("${shoperAuthUri}")
    String SHOPER_AUTH_URI;
    @Value("${shoperAdminUsername}")
    String SHOPER_USERNAME;
    @Value("${shoperAdminPassword}")
    String SHOPER_PASSWORD;

    @Autowired
    RestService restService;

    @Override
    protected String getTokenValue() {
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
        long timeDifference = getTimeDifferenceInMillis(token.getCreationTime(), now());
        return (timeDifference > minutesToMillis(45));
    }

}
