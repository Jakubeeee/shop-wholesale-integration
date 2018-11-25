package com.jakubeeee.integration.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

import static java.time.LocalDateTime.now;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShoperAuthenticationToken {

    @JsonProperty(value = "access_token")
    String value;

    LocalDateTime creationTime;

    public ShoperAuthenticationToken() {
        creationTime = now();
    }

}
