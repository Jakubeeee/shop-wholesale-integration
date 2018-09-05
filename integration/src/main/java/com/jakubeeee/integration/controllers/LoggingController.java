package com.jakubeeee.integration.controllers;

import com.jakubeeee.integration.service.LoggingService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import static com.jakubeeee.common.utils.JsonUtils.objectToJsonString;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@RestController
public class LoggingController {

    @Autowired
    LoggingService loggingService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Scheduled(fixedRate = 2000)
    public void publishLogs() {
        loggingService.removeOldLogs();
        Optional<String> logsListAsJsonO = objectToJsonString(loggingService.getLogList());
        logsListAsJsonO.ifPresent((logsListAsJson) -> messagingTemplate.convertAndSend("/topic/logs", logsListAsJson));
    }

    @PostMapping("/clearLogs")
    public void clearLogs() {
        loggingService.clearLogList();
        publishLogs();
    }

}
