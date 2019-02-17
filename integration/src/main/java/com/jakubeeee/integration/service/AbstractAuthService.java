package com.jakubeeee.integration.service;

import com.jakubeeee.integration.model.AbstractAuthToken;

public abstract class AbstractAuthService<T extends AbstractAuthToken> {

    T token;

    protected abstract String getTokenValue();

}
