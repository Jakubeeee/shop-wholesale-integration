package com.jakubeeee.tasks.controllers;

import com.jakubeeee.tasks.exceptions.NoTaskWithGivenIdException;
import com.jakubeeee.tasks.model.GenericTask;
import com.jakubeeee.tasks.model.LogMessage;
import com.jakubeeee.tasks.model.PastTaskExecutionValue;
import com.jakubeeee.tasks.model.ProgressTracker;
import com.jakubeeee.tasks.publishers.TaskPublisher;
import com.jakubeeee.tasks.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
public class TaskController {

    private final TaskRegistryService taskRegistryService;

    private final LoggingService loggingService;

    private final ProgressTrackingService progressTrackingService;

    private final NextScheduledExecutionService nextScheduledExecutionService;

    private final PastTaskExecutionService pastTaskExecutionService;

    private final TaskPublisher taskPublisher;

    private final SchedulingService schedulingService;

    @GetMapping("tasks")
    public List<GenericTask> getTasks() {
        return taskRegistryService.getRegisteredTasks();
    }

    @GetMapping("tasksLogs")
    public List<LogMessage> getTasksLogs() {
        return loggingService.getAllCachedLogs();
    }

    @GetMapping("nextScheduledTasksExecutions")
    public Map<Long, String> getNextScheduledTasksExecutions() {
        return nextScheduledExecutionService.getNextScheduledTasksExecutions();
    }

    @GetMapping("tasksProgress")
    public Map<Long, ProgressTracker> getTasksProgress() {
        return progressTrackingService.getProgressTrackers();
    }

    @GetMapping("pastTasksExecutions")
    public List<PastTaskExecutionValue> getPastTaskExecutions() {
        return pastTaskExecutionService.getPastTaskExecutions();
    }

    @PostMapping(path = "launchTaskManually", consumes = "text/plain")
    public void launchTaskManually(@RequestBody String taskId) throws NoTaskWithGivenIdException {
        schedulingService.launchTaskImmediately(Long.valueOf(taskId));
        taskPublisher.publishTasksProgress(progressTrackingService.getProgressTrackers());
    }

    @PostMapping("clearLogs")
    public void cleaTasksLogs() {
        loggingService.clearLogList();
        taskPublisher.publishAllTasksLogs(loggingService.getAllCachedLogs());
    }

}
