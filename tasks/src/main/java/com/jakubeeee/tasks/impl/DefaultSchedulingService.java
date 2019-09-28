package com.jakubeeee.tasks.impl;

import com.jakubeeee.core.TimerService;
import com.jakubeeee.tasks.GenericTask;
import com.jakubeeee.tasks.NoTaskWithGivenIdException;
import com.jakubeeee.tasks.SchedulingService;
import com.jakubeeee.tasks.TaskStoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.jakubeeee.common.DateTimeUtils.getCurrentDateTime;
import static com.jakubeeee.common.DateTimeUtils.millisUnitNextFullMinute;

/**
 * Default service bean used for operations related to task scheduling.
 */
@RequiredArgsConstructor
@Service
public class DefaultSchedulingService implements SchedulingService {

    private final TaskStoreService taskStoreService;

    private final TimerService timerService;

    @Override
    public void launchTaskImmediately(long taskId) throws NoTaskWithGivenIdException {
        GenericTask task = taskStoreService.getTask(taskId).orElseThrow(
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
