package com.jakubeeee.integration.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:integration.properties")
public class IntegrationConfig {

    @Bean
    public CommandLineRunner registerTasks(IntegrationTasksConfig service) {
        return args -> service.initializeTasks();
    }

}
