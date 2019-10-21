package com.jakubeeee.security.impl.config;

import com.jakubeeee.security.impl.role.RoleService;
import com.jakubeeee.security.impl.role.RoleType;
import com.jakubeeee.security.impl.role.RoleValue;
import com.jakubeeee.security.impl.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.EnumSet;

import static com.jakubeeee.security.impl.role.RoleType.*;

/**
 * Component used to initially populate the database with data required by the security module.
 */
@RequiredArgsConstructor
@Component
public class InitialSecurityDataLoader implements ApplicationRunner {

    @Value("${spring.profiles.active}")
    private String activeProfile;

    private final UserService userService;

    private final RoleService roleService;

    @Override
    public void run(ApplicationArguments args) {
        insertDataCommonForAllProfiles();
        if (activeProfile.equals("local"))
            insertDataForLocalProfile();
    }

    private void insertDataCommonForAllProfiles() {
        insertRole(BASIC_USER);
        insertRole(PRO_USER);
        insertRole(ADMIN);
    }

    private void insertRole(RoleType roleType) {
        if (roleService.findOneOptionalByType(roleType).isEmpty())
            roleService.save(RoleValue.of(roleType));
    }

    private void insertDataForLocalProfile() {
        userService.createUser("test1", "Password1", "testmail1@mail.com");
        userService.createUser("test2", "Password2", "testmail2@mail.com");
        userService.createUser("test3", "Password3", "testmail3@mail.com", EnumSet.of(PRO_USER));
        userService.createUser("test4", "Password4", "testmail4@mail.com", EnumSet.of(PRO_USER));
        userService.createUser("test5", "Password5", "testmail5@mail.com", EnumSet.of(ADMIN));
    }

}
