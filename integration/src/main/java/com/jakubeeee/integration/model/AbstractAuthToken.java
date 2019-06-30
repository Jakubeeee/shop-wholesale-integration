package com.jakubeeee.integration.model;

import lombok.Data;

import java.time.LocalDateTime;

import static com.jakubeeee.common.util.DateTimeUtils.getCurrentDateTime;

@Data
public abstract class AbstractAuthToken {

    String value;

    LocalDateTime creationTime;

    public AbstractAuthToken() {
        this.creationTime = getCurrentDateTime();
    }

    public AbstractAuthToken(String value) {
        this.value = value;
        this.creationTime = getCurrentDateTime();
    }


}
