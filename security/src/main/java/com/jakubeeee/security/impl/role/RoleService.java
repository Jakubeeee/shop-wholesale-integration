package com.jakubeeee.security.impl.role;

import java.util.Optional;
import java.util.Set;

/**
 * Interface for service beans used for operations related to user roles and privileges.
 */
public interface RoleService {

    Set<RoleValue> resolveRolesToAssign(Set<RoleType> roleTypes);

    RoleValue findOneByType(RoleType roleType);

    Optional<RoleValue> findOneOptionalByType(RoleType roleType);

    Set<RoleValue> findAllByTypes(Set<RoleType> roleTypes);

    Set<RoleValue> findAll();

    void save(RoleValue role);

}
