package com.jakubeeee.security.service;

import com.jakubeeee.security.model.User;
import com.jakubeeee.security.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;

import static com.jakubeeee.security.model.Role.Type.BASIC_USER;

@Service
@Transactional(readOnly = true)
public class UserService {

    @Autowired
    private RoleService roleService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public void createUser(String username, String password, String email) {
        var user = new User(username, password, email);
        roleService.grantRoles(user, BASIC_USER);
        encodePassword(user);
        userRepository.save(user);
        securityService.authenticate(user);
    }

    private void encodePassword(User user) {
        String encryptedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encryptedPassword);
    }

    @Transactional
    public void updateUserPassword(long userId, String password) {
        password = passwordEncoder.encode(password);
        userRepository.updateUserPassword(userId, password);
    }

    public boolean isUsernameUnique(String username) {
        Optional<User> userO = userRepository.findByUsername(username);
        return !userO.isPresent();
    }

    public boolean isEmailUnique(String email) {
        Optional<User> userO = userRepository.findByEmail(email);
        return !userO.isPresent();
    }

    public User findByUsername(String username) {
        Optional<User> userO = userRepository.findByUsername(username);
        return userO.orElseThrow(() -> new NoSuchElementException("User with username " + username + " not found in the database"));
    }

    public User findByEmail(String email) {
        Optional<User> userO = userRepository.findByEmail(email);
        return userO.orElseThrow(() -> new NoSuchElementException("User with email " + email + " not found in the database"));
    }

}
