package pl.jakubeeee.integration.controllers;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import pl.jakubeeee.integration.domain.LogMessage;
import pl.jakubeeee.integration.services.LoggingService;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Controller
public class LoggingController {

    @Autowired
    LoggingService loggingService;

    @GetMapping("/logs")
    @ResponseBody
    public List<LogMessage> getLogs() {
        loggingService.removeOldLogs();
        return loggingService.getLogList();
    }

    @PostMapping("/clearLogs")
    public String clearLogs() {
        loggingService.clearLogList();
        return "home";
    }

}
