package com.jakubeeee.integration.service;

import com.jakubeeee.integration.model.ProgressMonitor;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class ProgressMonitorService {

    @Autowired
    ProgressMonitor progressMonitor;

    public void trackProgress() {
        progressMonitor.setIsUpdating(1);
    }

    public void advanceProgress() {
        progressMonitor.setCurrentProgress(progressMonitor.getCurrentProgress() + 1);
    }

    public void resetProgress() {
        progressMonitor.setIsUpdating(0);
        progressMonitor.setCurrentProgress(0);
        progressMonitor.setMaxProgress(0);
    }

}
