package pl.jakubeeee.integration;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import pl.jakubeeee.integration.services.ProductService;
import pl.jakubeeee.integration.services.TimerService;

@SpringBootApplication
public class IntegrationApplication {

    private final long UPDATE_DELAY = 1000 * 5;
    private final long UPDATE_INTERVAL = 1000 * 60 * 60;

    public static void main(String[] args) {
        SpringApplication.run(IntegrationApplication.class, args);
    }

    @Bean
    public CommandLineRunner demo(ProductService productService, TimerService timerService) {
        return (args) ->
                timerService.setRecurrentTask
                        (productService::updateAllProductsStocks, UPDATE_DELAY, UPDATE_INTERVAL);
    }
}
