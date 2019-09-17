package com.jakubeeee.tasks.controllers;

import com.jakubeeee.tasks.exceptions.NoTaskWithGivenIdException;
import com.jakubeeee.tasks.model.GenericTask;
import com.jakubeeee.tasks.model.LogMessage;
import com.jakubeeee.tasks.model.PastTaskExecution;
import com.jakubeeee.tasks.model.ProgressTracker;
import com.jakubeeee.tasks.publishers.TaskPublisher;
import com.jakubeeee.tasks.service.LoggingService;
import com.jakubeeee.tasks.service.ProgressTrackingService;
import com.jakubeeee.tasks.service.SchedulingService;
import com.jakubeeee.tasks.service.TaskService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class TaskController {

    private final TaskService taskService;

    private final LoggingService loggingService;

    private final ProgressTrackingService progressTrackingService;

    private final TaskPublisher taskPublisher;

    private final SchedulingService schedulingService;

    public TaskController(TaskService taskService, LoggingService loggingService,
                          ProgressTrackingService progressTrackingService, TaskPublisher taskPublisher,
                          SchedulingService schedulingService) {
        this.taskService = taskService;
        this.loggingService = loggingService;
        this.progressTrackingService = progressTrackingService;
        this.taskPublisher = taskPublisher;
        this.schedulingService = schedulingService;
    }

    @GetMapping("tasks")
    public List<GenericTask> getTasks() {
        return taskService.getRegisteredTasks();
    }

    @GetMapping("tasksLogs")
    public List<LogMessage> getTasksLogs() {
        return loggingService.getAllCachedLogs();
    }

    @GetMapping("nextScheduledTasksExecutions")
    public Map<Long, String> getNextScheduledTasksExecutions() {
        return taskService.getNextScheduledTasksExecutions();
    }

    @GetMapping("tasksProgress")
    public Map<Long, ProgressTracker> getTasksProgress() {
        return progressTrackingService.getProgressTrackers();
    }

    @GetMapping("pastTasksExecutions")
    public List<PastTaskExecution> getPastTaskExecutions() {
        return taskService.getPastTaskExecutions();
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
