package com.jakubeeee.integration.model;

import lombok.Data;

import java.time.LocalDateTime;

import static java.time.LocalDateTime.now;

@Data
public abstract class AbstractAuthToken {

    String value;

    LocalDateTime creationTime;

    public AbstractAuthToken() {
        this.creationTime = now();
    }

    public AbstractAuthToken(String value) {
        this.value = value;
        this.creationTime = now();
    }


}
