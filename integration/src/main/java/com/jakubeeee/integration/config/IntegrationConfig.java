package com.jakubeeee.integration.config;

import com.jakubeeee.integration.service.SchedulerService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:integration.properties")
public class IntegrationConfig {

    @Bean
    public CommandLineRunner setSchedulerTasks(SchedulerService schedulerService) {
        return (args) -> schedulerService.scheduleUpdates();
    }

}
