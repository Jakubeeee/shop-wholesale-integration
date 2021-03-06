package com.jakubeeee.security.impl.role;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

/**
 * Spring data jpa repository for crud operations on {@link Role} objects.
 */
@Repository
public interface RoleRepository extends CrudRepository<Role, Long> {

    Optional<Role> findByType(RoleType roleType);

    Set<Role> findByTypeIn(Set<RoleType> roleTypes);

}
