package com.jakubeeee.core.service.impl;

import com.jakubeeee.core.service.RestService;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Default service bean used for operations related to rest communication.
 */
@Service
public class DefaultRestService implements RestService {

    private final RestTemplate template;

    public DefaultRestService(RestTemplateBuilder restTemplateBuilder) {
        this.template = restTemplateBuilder.build();
    }

    @Override
    public String getString(String uri) {
        return template.getForObject(uri, String.class);
    }

    @Override
    public <T> ResponseEntity<T> getJsonObject(String uri, HttpEntity<?> entity, Class<T> responseType) {
        return template.exchange(
                uri,
                HttpMethod.GET,
                entity,
                responseType);
    }

    @Override
    public <T> ResponseEntity<T> postJsonObject(String uri, HttpEntity<?> entity, Class<T> responseType) {
        return template.exchange(
                uri,
                HttpMethod.POST,
                entity,
                responseType);
    }

    @Override
    public <T> ResponseEntity<T> putJsonObject(String uri, HttpEntity<?> entity, Class<T> responseType) {
        return template.exchange(
                uri,
                HttpMethod.PUT,
                entity,
                responseType);
    }

}
