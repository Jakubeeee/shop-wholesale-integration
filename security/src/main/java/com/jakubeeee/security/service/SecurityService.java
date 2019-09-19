package com.jakubeeee.security.service;

import com.jakubeeee.security.entity.Role;
import com.jakubeeee.security.entity.User;
import com.jakubeeee.security.exception.UsernameNotResolvableException;

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

    User findByUsername(String username);

    User findByEmail(String email);

}
