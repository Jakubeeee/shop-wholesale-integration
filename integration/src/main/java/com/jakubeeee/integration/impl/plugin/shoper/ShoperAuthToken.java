package com.jakubeeee.integration.impl.plugin.shoper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jakubeeee.integration.auth.AbstractAuthToken;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShoperAuthToken extends AbstractAuthToken {

    @JsonProperty(value = "access_token")
    String value;

}
