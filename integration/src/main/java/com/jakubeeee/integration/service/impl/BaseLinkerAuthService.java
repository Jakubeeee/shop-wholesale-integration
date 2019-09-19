package com.jakubeeee.integration.service.impl;

import com.jakubeeee.integration.model.BaseLinkerAuthToken;
import com.jakubeeee.integration.service.AbstractAuthService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Service bean used for operations related to data source authentication in base linker platform.
 */
@Service
public class BaseLinkerAuthService extends AbstractAuthService<BaseLinkerAuthToken> {

    @Value("${baseLinkerAuthTokenValue}")
    private String BASE_LINKER_TOKEN_VALUE;

    @PostConstruct
    void initialize() {
        token = new BaseLinkerAuthToken(BASE_LINKER_TOKEN_VALUE);
    }

    @Override
    public String getTokenValue() {
        return token.getValue();
    }

}
