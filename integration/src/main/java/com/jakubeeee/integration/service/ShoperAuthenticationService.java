package com.jakubeeee.integration.service;

import com.jakubeeee.common.service.RestService;
import com.jakubeeee.integration.model.ShoperAuthenticationToken;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import static com.jakubeeee.common.utils.DateTimeUtils.getTimeDifferenceInMillis;
import static com.jakubeeee.common.utils.DateTimeUtils.minutesToMillis;
import static java.time.LocalDateTime.now;
import static java.util.Objects.requireNonNull;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class ShoperAuthenticationService {

    @Value("${shoperAuthUri}")
    String SHOPER_AUTH_URI;
    @Value("${adminUsername}")
    String SHOPER_USERNAME;
    @Value("${adminPassword}")
    String SHOPER_PASSWORD;

    ShoperAuthenticationToken token;
    @Autowired
    RestService restService;

    String getTokenValue() {
        if (token == null || isTokenExpiringSoon()) refreshToken();
        return token.getValue();
    }

    private void refreshToken() {
        HttpHeaders headers = restService.generateHeaderWithUsernameAndPassword(SHOPER_USERNAME, SHOPER_PASSWORD);
        ResponseEntity<ShoperAuthenticationToken> response =
                restService.postJsonObject(SHOPER_AUTH_URI, new HttpEntity<>(headers), ShoperAuthenticationToken.class);
        token = requireNonNull(response.getBody());
    }

    private boolean isTokenExpiringSoon() {
        long timeDifference = getTimeDifferenceInMillis(token.getCreationTime(), now());
        return (timeDifference > minutesToMillis(45));
    }

}
