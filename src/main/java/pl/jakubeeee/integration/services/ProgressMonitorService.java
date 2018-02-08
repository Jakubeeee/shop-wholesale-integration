package pl.jakubeeee.integration.services;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.jakubeeee.integration.domain.ProgressMonitor;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class ProgressMonitorService {

    @Autowired
    ProgressMonitor progressMonitor;

    public void startProgress() {
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
