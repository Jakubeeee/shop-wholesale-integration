package com.jakubeeee.core.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;

/**
 * Interface for service beans used for operations related to rest communication.
 */
public interface RestService {

    String getString(String uri);

    <T> ResponseEntity<T> getJsonObject(String uri, HttpEntity<?> entity, Class<T> responseType);

    <T> ResponseEntity<T> postJsonObject(String uri, HttpEntity<?> entity, Class<T> responseType);

    <T> ResponseEntity<T> putJsonObject(String uri, HttpEntity<?> entity, Class<T> responseType);

}
