package com.jakubeeee.tasks.service;

import com.jakubeeee.tasks.enums.TaskStatus;
import com.jakubeeee.tasks.exceptions.InvalidTaskStatusException;
import com.jakubeeee.tasks.model.GenericTask;
import com.jakubeeee.tasks.model.PastTaskExecution;
import com.jakubeeee.tasks.model.StatusTransition;
import com.jakubeeee.tasks.publishers.TaskPublisher;
import com.jakubeeee.tasks.repositories.PastTaskExecutionRepository;
import lombok.Getter;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;

import static com.jakubeeee.common.utils.DateTimeUtils.*;
import static com.jakubeeee.common.utils.LangUtils.filterList;
import static com.jakubeeee.tasks.enums.TaskStatus.*;
import static java.time.LocalDateTime.now;

@Slf4j
@Service
public class TaskService {

    @Autowired
    ProgressTrackingService progressTrackingService;

    @Autowired
    LoggingService loggingService;

    @Autowired
    TaskPublisher taskPublisher;

    @Autowired
    PastTaskExecutionRepository pastTaskExecutionRepository;

    @Autowired
    SchedulingService schedulingService;

    @Getter
    List<GenericTask> registeredTasks = new ArrayList<>();

    @Getter
    Map<Long, String> nextScheduledTasksExecutions = new HashMap<>();

    @Getter
    Set<String> lockedTaskProvidersNames = new HashSet<>();

    static final int PAST_TASKS_EXECUTIONS_REMOVAL_HOURS_THRESHOLD = 72;
    static final int PAST_TASKS_EXECUTIONS_PER_TASK_MAX_SIZE = 20;

    static Set<StatusTransition> allowedStatusTransitions = new HashSet<>();

    static {
        allowedStatusTransitions.add(new StatusTransition(WAITING, LAUNCHED));
        allowedStatusTransitions.add(new StatusTransition(LAUNCHED, PREPARED));
        allowedStatusTransitions.add(new StatusTransition(PREPARED, EXECUTED));
        allowedStatusTransitions.add(new StatusTransition(PREPARED, ABORTED));
        allowedStatusTransitions.add(new StatusTransition(EXECUTED, WAITING));
        allowedStatusTransitions.add(new StatusTransition(ABORTED, WAITING));
    }

    public void registerTask(GenericTask task) {
        registeredTasks.add(task);
        progressTrackingService.registerTracker(task.getId(), task.getTracker());
        nextScheduledTasksExecutions.put(task.getId(), calculateFirstScheduledTaskExecution(task.getId()));
    }

    public Optional<GenericTask> getTask(long taskId) {
        GenericTask foundTask = null;
        for (var taskInLoop : registeredTasks) {
            if (taskInLoop.getId() == taskId)
                foundTask = taskInLoop;
        }
        return Optional.ofNullable(foundTask);
    }

    public void changeStatus(GenericTask task, TaskStatus statusTo) throws InvalidTaskStatusException {
        TaskStatus statusFrom = task.getStatus();
        var statusTransition = new StatusTransition(statusFrom, statusTo);
        if (isTransitionAllowed(statusTransition))
            task.setStatus(statusTo);
        else throw new InvalidTaskStatusException("Invalid status transition. From " + statusFrom + " to " + statusTo);
    }

    private boolean isTransitionAllowed(StatusTransition statusTransition) {
        for (var allowedStatusTransition : allowedStatusTransitions) {
            if (statusTransition.getStatusFrom() == allowedStatusTransition.getStatusFrom()
                    && statusTransition.getStatusTo() == allowedStatusTransition.getStatusTo())
                return true;
        }
        return false;
    }

    @Synchronized
    public void registerNewPastTaskExecution(PastTaskExecution pastTaskExecution) {
        var a = pastTaskExecutionRepository.save(pastTaskExecution);
        taskPublisher.publishPastTaskExecutions(getPastTaskExecutions());
    }

    public List<PastTaskExecution> getPastTaskExecutions() {
        return (List<PastTaskExecution>) pastTaskExecutionRepository.findAll();
    }

    @Synchronized
    @Transactional
    @Scheduled(fixedRate = 30000)
    public void removeUnnecessaryPastTasksExecutions() {
        LocalDateTime timeHoursAgo = now().minusHours(PAST_TASKS_EXECUTIONS_REMOVAL_HOURS_THRESHOLD);
        List<PastTaskExecution> allPastTasksExecutions = getPastTaskExecutions();
        Predicate<PastTaskExecution> obsoletePredicate =
                pastTaskExecution -> isTimeAfter(timeHoursAgo, pastTaskExecution.getExecutionFinishTime());
        filterList(allPastTasksExecutions, obsoletePredicate).forEach(pastTaskExecutionRepository::delete);
        for (var registeredTask : registeredTasks) {
            List<PastTaskExecution> pastTasksExecutionsForSingleTask = filterList(
                    filterList(allPastTasksExecutions, obsoletePredicate.negate()),
                    pastTaskExecution -> pastTaskExecution.getTaskId() == registeredTask.getId());
            if (pastTasksExecutionsForSingleTask.size() > PAST_TASKS_EXECUTIONS_PER_TASK_MAX_SIZE) {
                int excess = pastTasksExecutionsForSingleTask.size() - PAST_TASKS_EXECUTIONS_PER_TASK_MAX_SIZE;
                List<PastTaskExecution> tempList = pastTasksExecutionsForSingleTask.subList(0, excess);
                tempList.forEach(pastTaskExecutionRepository::delete);
            }
        }
        taskPublisher.publishPastTaskExecutions(getPastTaskExecutions());
    }

    public void updateNextScheduledTaskExecution(Long taskId) {
        nextScheduledTasksExecutions.put(taskId, calculateNextScheduledTaskExecution(taskId));
        var nextScheduledTaskExecutionMap = new HashMap<Long, String>();
        nextScheduledTaskExecutionMap.put(taskId, nextScheduledTasksExecutions.get(taskId));
        taskPublisher.publishSingleNextScheduledTaskExecution(nextScheduledTaskExecutionMap);
    }

    private String calculateFirstScheduledTaskExecution(Long taskId) {
        Optional<GenericTask> taskO = getTask(taskId);
        return taskO.map(task -> {
            if (!task.isScheduledable())
                return "TASKNOSCHED";
            long delay = task.getDelayInMillis();
            return formatDateTime(roundTimeToNextFullMinute(now().plusMinutes(millisToMinutes(delay))));
        }).orElse("");
    }

    private String calculateNextScheduledTaskExecution(Long taskId) {
        Optional<GenericTask> taskO = getTask(taskId);
        return taskO.map(task -> {
            Optional<LocalDateTime> previousExecutionTimeO = task.getLastTaskExecutionInfo().getLastScheduledStartedExecutionTime();
            long interval = task.getIntervalInMillis();
            return previousExecutionTimeO.map(
                    previousExecutionTime -> formatDateTime(previousExecutionTime.plusMinutes(millisToMinutes(interval))))
                    .orElse(nextScheduledTasksExecutions.get(taskId));
        }).orElse("");
    }

    @Synchronized
    public void lockTaskProvider(String taskProviderName) {
        lockedTaskProvidersNames.add(taskProviderName);
    }

    @Synchronized
    public void unlockTaskProvider(String taskProviderName) {
        lockedTaskProvidersNames.remove(taskProviderName);
    }

    @Synchronized
    public boolean isTaskProviderLocked(String taskProviderName) {
        return lockedTaskProvidersNames.contains(taskProviderName);
    }

}
