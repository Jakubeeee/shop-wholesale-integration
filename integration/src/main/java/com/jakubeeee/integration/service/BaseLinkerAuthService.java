package com.jakubeeee.integration.service;

import com.jakubeeee.integration.model.BaseLinkerAuthToken;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class BaseLinkerAuthService extends AbstractAuthService<BaseLinkerAuthToken> {

    @Value("${baseLinkerAuthTokenValue}")
    String BASE_LINKER_TOKEN_VALUE;

    @PostConstruct
    void initialize() {
        token = new BaseLinkerAuthToken(BASE_LINKER_TOKEN_VALUE);
    }

    @Override
    protected String getTokenValue() {
        return token.getValue();
    }

}
