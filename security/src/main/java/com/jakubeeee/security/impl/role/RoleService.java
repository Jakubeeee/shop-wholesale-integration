package com.jakubeeee.security.impl.role;

import java.util.Optional;
import java.util.Set;

/**
 * Interface for service beans used for operations related to user roles and privileges.
 */
public interface RoleService {

    Set<Role> resolveRolesToAssign(Set<Role.Type> roleTypes);

    Role findOneByType(Role.Type roleType);

    Optional<Role> findOneOptionalByType(Role.Type roleType);

    Set<Role> findAllByTypes(Set<Role.Type> roleTypes);

    Set<Role> findAll();

    void save(Role role);

}
