package pl.jakubeeee.integration.services;

import org.springframework.boot.SpringApplication;
import org.springframework.stereotype.Service;
import pl.jakubeeee.integration.IntegrationApplication;

@Service
public class ShutdownService {

    public void initiateShutdown() {
        SpringApplication.run(IntegrationApplication.class).close();
    }
}