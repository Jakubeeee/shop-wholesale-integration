package com.jakubeeee.tasks.service;

import com.jakubeeee.tasks.exceptions.ProgressTrackerNotActiveException;
import com.jakubeeee.tasks.model.GenericTask;
import org.springframework.stereotype.Service;

import static com.jakubeeee.common.util.ThreadUtils.sleep;

@Service
public class DummyTaskProvider extends AbstractGenericTaskProvider {

    @Override
    public void executeTask(GenericTask caller) throws ProgressTrackerNotActiveException {
        progressTrackingService.setMaxProgress(caller, 100);
        for (int i = 1; i <= 100; i++) {
            loggingService.update(caller.getId(), "Test log " + i);
            sleep(250);
            progressTrackingService.advanceProgress(caller);
        }
    }

    @Override
    public String getProviderName() {
        return "DUMMY_TASK_PROVIDER";
    }
}
