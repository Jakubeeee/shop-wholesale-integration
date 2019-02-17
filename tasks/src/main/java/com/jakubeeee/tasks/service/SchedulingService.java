package com.jakubeeee.tasks.service;

import com.jakubeeee.common.service.TimerService;
import com.jakubeeee.tasks.exceptions.NoTaskWithGivenIdException;
import com.jakubeeee.tasks.model.GenericTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.jakubeeee.common.utils.DateTimeUtils.millisUnitNextFullMinute;
import static java.time.LocalDateTime.now;

@Service
public class SchedulingService {

    @Autowired
    TaskService taskService;

    @Autowired
    TimerService timerService;

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
            task.getLastTaskExecutionInfo().setLastScheduledStartedExecutionTime(now());
            task.getTaskFunction().run();
            task.getLastTaskExecutionInfo().setLastScheduledFinishedExecutionTime(now());
        };
    }

}
