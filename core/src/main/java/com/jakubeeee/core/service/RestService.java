package com.jakubeeee.core.service;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Service class for operations related to rest communication.
 */
@Service
public class RestService {

    private final RestTemplate template;

    public RestService(RestTemplateBuilder restTemplateBuilder) {
        this.template = restTemplateBuilder.build();
    }

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

}
