package com.jakubeeee.tasks.service.impl;

import com.jakubeeee.tasks.exceptions.ProgressTrackerNotActiveException;
import com.jakubeeee.tasks.model.GenericTask;
import com.jakubeeee.tasks.service.*;
import org.springframework.stereotype.Service;

import static com.jakubeeee.common.util.ThreadUtils.sleep;

/**
 * Dummy service bean used as an imitation of a real task provider.
 */
@Service
public class DefaultDummyTaskProvider extends AbstractGenericTaskProvider<GenericTask> implements DummyTaskProvider<GenericTask> {

    public DefaultDummyTaskProvider(TaskService taskService, ProgressTrackingService progressTrackingService,
                                    LoggingService loggingService) {
        super(taskService, progressTrackingService, loggingService);
    }

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
