package com.jakubeeee.integration.config;

import com.jakubeeee.integration.service.IntegrationTasksService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:integration.properties")
public class IntegrationConfig {

    @Bean
    public CommandLineRunner registerTasks(IntegrationTasksService service) {
        return args -> service.initializeTasks();
    }

}
