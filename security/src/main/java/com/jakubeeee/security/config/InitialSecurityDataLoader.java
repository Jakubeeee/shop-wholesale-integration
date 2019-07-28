package com.jakubeeee.security.config;

import com.jakubeeee.security.entity.Role;
import com.jakubeeee.security.service.RoleService;
import com.jakubeeee.security.service.SecurityService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Set;

import static com.jakubeeee.security.entity.Role.Type.*;

/**
 * Component used to initially populate the database with data required by the security module.
 */
@Component
public class InitialSecurityDataLoader implements ApplicationRunner {

    @Value("${spring.profiles.active}")
    private String activeProfile;

    private final SecurityService securityService;

    private final RoleService roleService;

    public InitialSecurityDataLoader(SecurityService securityService, RoleService roleService) {
        this.securityService = securityService;
        this.roleService = roleService;
    }

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

    private void insertRole(Role.Type roleType) {
        if (roleService.findOneOptionalByType(roleType).isEmpty())
            roleService.save(Role.of(roleType));
    }

    private void insertDataForLocalProfile() {
        securityService.createUser("test1", "Password1", "testmail1@mail.com");
        securityService.createUser("test2", "Password2", "testmail2@mail.com");
        securityService.createUser("test3", "Password3", "testmail3@mail.com", Set.of(PRO_USER));
        securityService.createUser("test4", "Password4", "testmail4@mail.com", Set.of(PRO_USER));
        securityService.createUser("test5", "Password5", "testmail5@mail.com", Set.of(ADMIN));
    }

}