package com.jakubeeee.security.impl.role;

import com.jakubeeee.common.persistence.DatabaseResultEmptyException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.util.stream.Collectors.toCollection;

/**
 * Default service bean used for operations related to user roles and privileges.
 */
@RequiredArgsConstructor
@Service
public class DefaultRoleService implements RoleService {

    private static final RoleFactory roleFactory = RoleFactory.getInstance();

    private final RoleRepository roleRepository;

    @Override
    public Set<RoleValue> resolveRolesToAssign(Set<RoleType> roleTypes) {
        Set<RoleValue> roles = findAllByTypes(roleTypes);
        roles = attachLowerRoles(roles);
        return roles;
    }

    private Set<RoleValue> attachLowerRoles(Set<RoleValue> currentRoles) {
        var extendedRoles = new HashSet<>(currentRoles);
        if (isRoleTypePresentInSet(currentRoles, RoleType.ADMIN))
            addRolesToSetIfMissing(extendedRoles, Set.of(RoleType.BASIC_USER, RoleType.PRO_USER));
        else if (isRoleTypePresentInSet(currentRoles, RoleType.PRO_USER))
            addRolesToSetIfMissing(extendedRoles, Set.of(RoleType.BASIC_USER));
        return extendedRoles;
    }

    private boolean isRoleTypePresentInSet(Set<RoleValue> roles, RoleType roleType) {
        return roles.stream().map(RoleValue::getType).anyMatch(typeInSet -> typeInSet.equals(roleType));
    }

    private void addRolesToSetIfMissing(Set<RoleValue> roles, Set<RoleType> roleTypes) {
        Set<RoleType> typesOfMissingRoles =
                roleTypes.stream().filter(type -> !isRoleTypePresentInSet(roles, type)).collect(toCollection(HashSet::new));
        if (!typesOfMissingRoles.isEmpty()) {
            Set<RoleValue> additionalRoles = findAllByTypes(typesOfMissingRoles);
            roles.addAll(additionalRoles);
        }
    }

    @Override
    public RoleValue findOneByType(RoleType roleType) {
        Optional<Role> roleO = roleRepository.findByType(roleType);
        return roleFactory.createValue(roleO.orElseThrow(() -> new DatabaseResultEmptyException("Role with type " + roleType
                + " not found in the database")));
    }

    @Override
    public Optional<RoleValue> findOneOptionalByType(RoleType roleType) {
        return roleRepository.findByType(roleType).map(roleFactory::createValue);
    }

    @Override
    public Set<RoleValue> findAllByTypes(Set<RoleType> roleTypes) {
        Set<Role> roles = roleRepository.findByTypeIn(roleTypes);
        if (roles.isEmpty()) throw new DatabaseResultEmptyException("Roles with types " + roleTypes + " " +
                "not found in the database");
        return roleFactory.createValues(roles);
    }

    @Override
    public Set<RoleValue> findAll() {
        Set<Role> roles = new HashSet<>((List<Role>) roleRepository.findAll());
        if (roles.isEmpty()) throw new DatabaseResultEmptyException("No roles found in the database");
        return roleFactory.createValues(roles);
    }

    @Override
    public void save(RoleValue role) {
        roleRepository.save(roleFactory.createEntity(role));
    }

}
