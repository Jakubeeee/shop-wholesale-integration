package com.jakubeeee.security.service;

import com.jakubeeee.security.entity.Role;
import com.jakubeeee.security.entity.User;

import java.util.Optional;
import java.util.Set;

/**
 * Interface for service beans used for operations related to user roles and privileges.
 */
public interface RoleService {

    void grantRoles(User user, Set<Role> roles);

    Role findOneByType(Role.Type roleType);

    Optional<Role> findOneOptionalByType(Role.Type roleType);

    Set<Role> findAllByTypes(Set<Role.Type> roleTypes);

    Set<Role> findAll();

    void save(Role role);

}
