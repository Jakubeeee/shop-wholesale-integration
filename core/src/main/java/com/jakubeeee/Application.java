package com.jakubeeee;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Class that serves as the entry point of the application.
 */
@SpringBootApplication(scanBasePackages = "com.jakubeeee")
@EntityScan(basePackages = "com.jakubeeee")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
