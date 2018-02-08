package pl.jakubeeee.integration.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.jakubeeee.integration.domain.ProgressMonitor;
import pl.jakubeeee.integration.services.ProgressMonitorService;

@RestController
public class ProgressController {

    @Autowired
    ProgressMonitorService progressMonitorService;

    @GetMapping("/progress")
    public ProgressMonitor getMaxProgress() {
        return progressMonitorService.getProgressMonitor();
    }
}
