package com.jakubeeee.integration.controllers;

import com.jakubeeee.integration.model.ProgressMonitor;
import com.jakubeeee.integration.service.ProgressMonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import static com.jakubeeee.common.utils.JsonUtils.objectToJsonString;

@RestController
public class ProgressController {

    @Autowired
    ProgressMonitorService progressMonitorService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @GetMapping("/progress")
    public ProgressMonitor getMaxProgress() {
        return progressMonitorService.getProgressMonitor();
    }

    @Scheduled(fixedRate = 2000)
    public void publishProgress() {
        Optional<String> logsListAsJsonO = objectToJsonString(progressMonitorService.getProgressMonitor());
        logsListAsJsonO.ifPresent((logsListAsJson) -> messagingTemplate.convertAndSend("/topic/progress", logsListAsJson));
    }
}
