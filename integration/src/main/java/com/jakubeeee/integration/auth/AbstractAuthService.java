package com.jakubeeee.integration.auth;

/**
 * Base for service beans used for operations related to data source authentication.
 */
public abstract class AbstractAuthService<T extends AbstractAuthToken> implements AuthService {

    protected T token;

    public abstract String getTokenValue();

}
