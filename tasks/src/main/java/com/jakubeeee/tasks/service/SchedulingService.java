package com.jakubeeee.tasks.service;

import com.jakubeeee.core.service.TimerService;
import com.jakubeeee.tasks.exceptions.NoTaskWithGivenIdException;
import com.jakubeeee.tasks.model.GenericTask;
import org.springframework.stereotype.Service;

import static com.jakubeeee.common.util.DateTimeUtils.getCurrentDateTime;
import static com.jakubeeee.common.util.DateTimeUtils.millisUnitNextFullMinute;

@Service
public class SchedulingService {

    private final TaskService taskService;

    private final TimerService timerService;

    public SchedulingService(TaskService taskService, TimerService timerService) {
        this.taskService = taskService;
        this.timerService = timerService;
    }

    public void launchTaskImmediately(long taskId) throws NoTaskWithGivenIdException {
        GenericTask task = taskService.getTask(taskId).orElseThrow(
                () -> new NoTaskWithGivenIdException("There is no registered task with id: " + taskId));
        task.getTaskFunction().run();
    }

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
