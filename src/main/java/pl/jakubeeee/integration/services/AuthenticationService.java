package pl.jakubeeee.integration.services;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import pl.jakubeeee.integration.domain.AuthenticationToken;

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
    private RestService restService;

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
