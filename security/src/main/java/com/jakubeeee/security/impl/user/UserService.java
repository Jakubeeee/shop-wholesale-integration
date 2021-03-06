package com.jakubeeee.security.impl.user;

import com.jakubeeee.security.impl.role.RoleType;

import java.util.Set;

/**
 * Interface for service beans used for operations related to security and user management.
 */
public interface UserService {

    void createUser(String username, String password, String email);

    void createUser(String username, String password, String email, Set<RoleType> roleTypes);

    void updateUserPassword(long userId, String password);

    boolean isAuthenticated();

    String getCurrentUsername() throws UsernameNotResolvableException;

    boolean isUsernameUnique(String username);

    boolean isEmailUnique(String email);

    UserValue findByUsername(String username);

    UserValue findByEmail(String email);

}
