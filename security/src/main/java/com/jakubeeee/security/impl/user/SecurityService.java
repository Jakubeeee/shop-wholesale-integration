package com.jakubeeee.security.impl.user;

import com.jakubeeee.security.impl.role.Role;

import java.util.Set;

/**
 * Interface for service beans used for operations related to security and user management.
 */
public interface SecurityService {

    void createUser(String username, String password, String email);

    void createUser(String username, String password, String email, Set<Role.Type> roleTypes);

    void updateUserPassword(long userId, String password);

    boolean isAuthenticated();

    String getCurrentUsername() throws UsernameNotResolvableException;

    boolean isUsernameUnique(String username);

    boolean isEmailUnique(String email);

    UserValue findByUsername(String username);

    UserValue findByEmail(String email);

}
