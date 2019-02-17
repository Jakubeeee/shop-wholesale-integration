package com.jakubeeee.common.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.cxf.common.util.Base64Utility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class RestService {

    @Autowired
    RestTemplate template;

    public String getString(String uri) {
        return template.getForObject(uri, String.class);
    }

    public <T> ResponseEntity<T> getJsonObject(String uri, HttpEntity<?> entity, Class<T> responseType) {
        return template.exchange(
                uri,
                HttpMethod.GET,
                entity,
                responseType);
    }

    public <T> ResponseEntity<T> postJsonObject(String uri, HttpEntity<?> entity, Class<T> responseType) {
        return template.exchange(
                uri,
                HttpMethod.POST,
                entity,
                responseType);
    }

    public <T> ResponseEntity<T> putJsonObject(String uri, HttpEntity<?> entity, Class<T> responseType) {
        return template.exchange(
                uri,
                HttpMethod.PUT,
                entity,
                responseType);
    }

    public HttpHeaders generateHeaderWithAuthToken(String tokenValue) {
        var headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + tokenValue);
        return headers;
    }

    public HttpHeaders generateHeaderWithUsernameAndPassword(String username, String password) {
        var headers = new HttpHeaders();
        String userAndPass = username + ":" + password;
        headers.add("Authorization", "Basic " + Base64Utility.encode(userAndPass.getBytes()));
        return headers;
    }
}
