package com.jakubeeee.tasks;

import com.jakubeeee.tasks.impl.TaskPublisher;
import com.jakubeeee.tasks.impl.progresstracking.ProgressTracker;
import com.jakubeeee.tasks.logging.LogMessage;
import com.jakubeeee.tasks.logging.LoggingService;
import com.jakubeeee.tasks.pasttaskexecution.PastTaskExecutionService;
import com.jakubeeee.tasks.pasttaskexecution.PastTaskExecutionValue;
import com.jakubeeee.tasks.progresstracking.ProgressTrackingService;
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

    private final TaskStoreService taskStoreService;

    private final LoggingService loggingService;

    private final ProgressTrackingService progressTrackingService;

    private final NextScheduledExecutionService nextScheduledExecutionService;

    private final PastTaskExecutionService pastTaskExecutionService;

    private final TaskPublisher taskPublisher;

    private final SchedulingService schedulingService;

    @GetMapping("tasks")
    public List<GenericTask> getTasks() {
        return taskStoreService.getStoredTasks();
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
