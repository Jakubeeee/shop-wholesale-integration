package com.jakubeeee.security.service;

import com.jakubeeee.common.exception.DatabaseResultEmptyException;
import com.jakubeeee.security.entity.Role;
import com.jakubeeee.security.entity.User;
import com.jakubeeee.security.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.util.stream.Collectors.toCollection;

/**
 * Service class for operations related to user roles and privileges.
 */
@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public void grantRoles(User user, Set<Role> roles) {
        roles = attachLowerRoles(roles);
        user.setRoles(roles);
    }

    private Set<Role> attachLowerRoles(Set<Role> currentRoles) {
        Set<Role> extendedRoles = new HashSet<>(currentRoles);
        if (isRoleTypePresentInSet(currentRoles, Role.Type.ADMIN))
            addRolesToSetIfMissing(extendedRoles, Set.of(Role.Type.BASIC_USER, Role.Type.PRO_USER));
        else if (isRoleTypePresentInSet(currentRoles, Role.Type.PRO_USER))
            addRolesToSetIfMissing(extendedRoles, Set.of(Role.Type.BASIC_USER));
        return extendedRoles;
    }

    private boolean isRoleTypePresentInSet(Set<Role> roles, Role.Type roleType) {
        return roles.stream().map(Role::getType).anyMatch(typeInSet -> typeInSet.equals(roleType));
    }

    private void addRolesToSetIfMissing(Set<Role> roles, Set<Role.Type> roleTypes) {
        Set<Role.Type> typesOfMissingRoles =
                roleTypes.stream().filter(type -> !isRoleTypePresentInSet(roles, type)).collect(toCollection(HashSet::new));
        if (!typesOfMissingRoles.isEmpty()) {
            Set<Role> additionalRoles = findAllByTypes(typesOfMissingRoles);
            roles.addAll(additionalRoles);
        }
    }


    public Role findOneByType(Role.Type roleType) {
        Optional<Role> roleO = roleRepository.findByType(roleType);
        return roleO.orElseThrow(() -> new DatabaseResultEmptyException("Role with type " + roleType + " not found in" +
                " " +
                "the database"));
    }

    public Optional<Role> findOneOptionalByType(Role.Type roleType) {
        return roleRepository.findByType(roleType);
    }

    public Set<Role> findAllByTypes(Set<Role.Type> roleTypes) {
        Set<Role> roles = roleRepository.findByTypeIn(roleTypes);
        if (roles.isEmpty()) throw new DatabaseResultEmptyException("Roles with types " + roleTypes + " " +
                "not found in the database");
        return roles;
    }

    public Set<Role> findAll() {
        Set<Role> roles = new HashSet<>((List<Role>) roleRepository.findAll());
        if (roles.isEmpty()) throw new DatabaseResultEmptyException("No roles found in the database");
        return roles;
    }

    public void save(Role role) {
        roleRepository.save(role);
    }

}
