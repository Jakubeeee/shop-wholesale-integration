package com.jakubeeee.integration.service;

import com.jakubeeee.common.service.RestService;
import com.jakubeeee.integration.model.AuthenticationToken;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class AuthenticationService {

    @Value("${shopAuthUri}")
    String SHOP_AUTH_URI;
    @Value("${adminUsername}")
    String USERNAME;
    @Value("${adminPassword}")
    String PASSWORD;

    String token;

    @Autowired
    RestService restService;

    public String getToken() {
        return token != null ? token : getNewToken();
    }

    private String getNewToken() {
        HttpHeaders headers = restService.generateHeaderWithUsernameAndPassword(USERNAME, PASSWORD);
        HttpEntity<?> entity = new HttpEntity<>(headers);
        ResponseEntity<AuthenticationToken> response =
                restService.postJsonObject(SHOP_AUTH_URI, entity, AuthenticationToken.class);
        token = response.getBody().getToken();
        return token;
    }

}
