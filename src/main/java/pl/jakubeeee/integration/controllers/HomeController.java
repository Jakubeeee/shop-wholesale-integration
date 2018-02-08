package pl.jakubeeee.integration.controllers;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import pl.jakubeeee.integration.services.ProductService;
import pl.jakubeeee.integration.services.ShutdownService;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Controller
public class HomeController {

    @Autowired
    ProductService productService;

    @Autowired
    ShutdownService shutdownService;

    @GetMapping("/")
    public String getHomePage() {
        return "home";
    }

    @PostMapping("/updateStocks")
    public String updateStock() {
        productService.updateAllProductsStocks();
        return "home";
    }

    @PostMapping("/exit")
    public void exit() {
        shutdownService.initiateShutdown();
    }
}
