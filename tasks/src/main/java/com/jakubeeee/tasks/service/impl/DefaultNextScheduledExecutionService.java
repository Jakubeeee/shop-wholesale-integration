package com.jakubeeee.tasks.service.impl;

import com.jakubeeee.tasks.model.GenericTask;
import com.jakubeeee.tasks.publishers.TaskPublisher;
import com.jakubeeee.tasks.service.NextScheduledExecutionService;
import com.jakubeeee.tasks.service.TaskStoreService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.jakubeeee.common.DateTimeUtils.*;

/**
 * Default service bean used for operations related to management of next scheduled task executions.
 */
@RequiredArgsConstructor
@Service
public class DefaultNextScheduledExecutionService implements NextScheduledExecutionService {

    private final TaskPublisher taskPublisher;

    private final TaskStoreService taskStoreService;

    @Getter
    private Map<Long, String> nextScheduledTasksExecutions = new HashMap<>();

    @Override
    public void updateNextScheduledTaskExecution(Long taskId) {
        nextScheduledTasksExecutions.put(taskId, calculateNextScheduledTaskExecution(taskId));
        var nextScheduledTaskExecutionMap = new HashMap<Long, String>();
        nextScheduledTaskExecutionMap.put(taskId, nextScheduledTasksExecutions.get(taskId));
        taskPublisher.publishSingleNextScheduledTaskExecution(nextScheduledTaskExecutionMap);
    }

    @Override
    public String calculateFirstScheduledTaskExecution(Long taskId) {
        Optional<GenericTask> taskO = taskStoreService.getTask(taskId);
        return taskO.map(task -> {
            if (!task.isScheduledable())
                return "TASKNOSCHED";
            long delay = task.getDelayInMillis();
            return formatDateTime(roundTimeToNextFullMinute(getCurrentDateTime().plusMinutes(millisToMinutes(delay))));
        }).orElse("");
    }

    private String calculateNextScheduledTaskExecution(Long taskId) {
        Optional<GenericTask> taskO = taskStoreService.getTask(taskId);
        return taskO.map(task -> {
            Optional<LocalDateTime> previousExecutionTimeO =
                    task.getLastTaskExecutionInfo().getLastScheduledStartedExecutionTime();
            long interval = task.getIntervalInMillis();
            return previousExecutionTimeO.map(
                    previousExecutionTime -> formatDateTime(previousExecutionTime.plusMinutes(millisToMinutes(interval))))
                    .orElse(nextScheduledTasksExecutions.get(taskId));
        }).orElse("");
    }

}
