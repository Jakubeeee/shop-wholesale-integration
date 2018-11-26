package com.jakubeeee.security.config;

import com.jakubeeee.security.model.Role;
import com.jakubeeee.security.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InitialDataLoader implements ApplicationRunner {

    @Autowired
    private RoleRepository roleRepository;

    public void run(ApplicationArguments args) {
        var roles = (List<Role>) roleRepository.findAll();
        if (roles.isEmpty()) {
            roleRepository.save(new Role((long) 1, "BASIC_USER"));
            roleRepository.save(new Role((long) 2, "PRO_USER"));
            roleRepository.save(new Role((long) 3, "ADMIN"));
        }
    }

}
