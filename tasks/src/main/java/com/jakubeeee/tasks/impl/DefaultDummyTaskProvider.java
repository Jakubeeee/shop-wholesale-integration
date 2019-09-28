package com.jakubeeee.tasks.impl;

import com.jakubeeee.tasks.GenericTask;
import com.jakubeeee.tasks.LockingService;
import com.jakubeeee.tasks.TaskRegistryService;
import com.jakubeeee.tasks.logging.LoggingService;
import com.jakubeeee.tasks.pasttaskexecution.PastTaskExecutionService;
import com.jakubeeee.tasks.progresstracking.ProgressTrackerNotActiveException;
import com.jakubeeee.tasks.progresstracking.ProgressTrackingService;
import com.jakubeeee.tasks.provider.AbstractGenericTaskProvider;
import com.jakubeeee.tasks.provider.DummyTaskProvider;
import org.springframework.stereotype.Service;

import static com.jakubeeee.common.ThreadUtils.sleep;

/**
 * Dummy service bean used as an imitation of a real task provider.
 */
@Service
public class DefaultDummyTaskProvider extends AbstractGenericTaskProvider<GenericTask> implements DummyTaskProvider<GenericTask> {

    public DefaultDummyTaskProvider(TaskRegistryService taskRegistryService, LockingService lockingService,
                                    ProgressTrackingService progressTrackingService, LoggingService loggingService,
                                    PastTaskExecutionService pastTaskExecutionService) {
        super(taskRegistryService, lockingService, progressTrackingService, loggingService, pastTaskExecutionService);
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
