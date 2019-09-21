package com.jakubeeee.tasks.service.impl;

import com.jakubeeee.core.service.TimerService;
import com.jakubeeee.tasks.exceptions.NoTaskWithGivenIdException;
import com.jakubeeee.tasks.model.GenericTask;
import com.jakubeeee.tasks.service.SchedulingService;
import com.jakubeeee.tasks.service.TaskRegistryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.jakubeeee.common.util.DateTimeUtils.getCurrentDateTime;
import static com.jakubeeee.common.util.DateTimeUtils.millisUnitNextFullMinute;

/**
 * Default service bean used for operations related to task scheduling.
 */
@RequiredArgsConstructor
@Service
public class DefaultSchedulingService implements SchedulingService {

    private final TaskRegistryService taskRegistryService;

    private final TimerService timerService;

    @Override
    public void launchTaskImmediately(long taskId) throws NoTaskWithGivenIdException {
        GenericTask task = taskRegistryService.getTask(taskId).orElseThrow(
                () -> new NoTaskWithGivenIdException("There is no registered task with id: " + taskId));
        task.getTaskFunction().run();
    }

    @Override
    public void scheduleTask(GenericTask task) {
        long delay = task.getDelayInMillis();
        long interval = task.getIntervalInMillis();
        timerService.setRecurrentJob(getPreparedTaskFunction(task), delay + millisUnitNextFullMinute(), interval);
    }

    private Runnable getPreparedTaskFunction(GenericTask task) {
        return () -> {
            task.getLastTaskExecutionInfo().setLastScheduledStartedExecutionTime(getCurrentDateTime());
            task.getTaskFunction().run();
            task.getLastTaskExecutionInfo().setLastScheduledFinishedExecutionTime(getCurrentDateTime());
        };
    }

}
