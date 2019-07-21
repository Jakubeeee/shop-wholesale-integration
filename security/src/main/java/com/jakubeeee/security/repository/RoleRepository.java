package com.jakubeeee.security.repository;

import com.jakubeeee.security.entity.Role;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

/**
 * Spring data jpa repository for crud operations on {@link Role} objects.
 */
@Repository
public interface RoleRepository extends CrudRepository<Role, Long> {

    Optional<Role> findByType(Role.Type roleType);

    Set<Role> findByTypeIn(Set<Role.Type> roleTypes);

}
