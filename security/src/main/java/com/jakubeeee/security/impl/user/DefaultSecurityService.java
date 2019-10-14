package com.jakubeeee.security.impl.user;

import com.jakubeeee.common.persistence.DatabaseResultEmptyException;
import com.jakubeeee.security.impl.role.Role;
import com.jakubeeee.security.impl.role.RoleService;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

import static com.jakubeeee.security.impl.role.Role.Type.BASIC_USER;

/**
 * Default service bean used for operations related to security and user management.
 */
@Service
public class DefaultSecurityService implements SecurityService {

    private static final UserFactory userFactory = UserFactory.getInstance();

    private final RoleService roleService;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public DefaultSecurityService(RoleService roleService, UserRepository userRepository,
                                  @Lazy PasswordEncoder passwordEncoder) {
        this.roleService = roleService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    @Override
    public void createUser(String username, String password, String email) {
        createUser(username, password, email, EnumSet.of(BASIC_USER));
    }

    @Transactional
    @Override
    public void createUser(String username, String password, String email, Set<Role.Type> roleTypes) {
        validateUsernameAndEmailUniqueness(username, email);
        Set<Role> roles = roleService.resolveRolesToAssign(roleTypes);
        var user = new UserValue(null, username, encodePassword(password), email, true, roles);
        userRepository.save(userFactory.createEntity(user));
        authenticateUser(user);
    }

    private void validateUsernameAndEmailUniqueness(String username, String email) {
        if (!isUsernameUnique(username))
            throw new UsernameNotUniqueException("Provided username is not unique");
        if (!isEmailUnique(email))
            throw new EmailNotUniqueException("Provided email is not unique");
    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    private void authenticateUser(UserValue user) {
        Authentication auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Transactional
    @Override
    public void updateUserPassword(long userId, String password) {
        password = passwordEncoder.encode(password);
        userRepository.updateUserPassword(userId, password);
    }

    @Override
    public boolean isAuthenticated() {
        return isAuthenticationResolvable(getCurrentAuthentication());
    }

    @Override
    public String getCurrentUsername() throws UsernameNotResolvableException {
        Authentication auth = getCurrentAuthentication();
        if (!isAuthenticationResolvable(auth)) {
            throw new UsernameNotResolvableException("Current username cannot be retrieved");
        }
        return auth.getName();
    }

    private Authentication getCurrentAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    private boolean isAuthenticationResolvable(Authentication auth) {
        return auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken);
    }

    @Override
    public boolean isUsernameUnique(String username) {
        Optional<User> userO = userRepository.findByUsername(username);
        return userO.isEmpty();
    }

    @Override
    public boolean isEmailUnique(String email) {
        Optional<User> userO = userRepository.findByEmail(email);
        return userO.isEmpty();
    }

    @Override
    public UserValue findByUsername(String username) {
        Optional<User> userO = userRepository.findByUsername(username);
        return userFactory.createValue(userO.orElseThrow(() ->
                new DatabaseResultEmptyException("User with username " + username + " not found in the database")));
    }

    @Override
    public UserValue findByEmail(String email) {
        Optional<User> userO = userRepository.findByEmail(email);
        return userFactory.createValue(userO.orElseThrow(() ->
                new DatabaseResultEmptyException("User with email " + email + "not found in the database")));
    }

}
