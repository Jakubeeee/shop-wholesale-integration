package com.jakubeeee.tasks.service;

import com.jakubeeee.common.exception.UnexpectedClassStructureException;
import com.jakubeeee.tasks.annotations.InitialTaskValidator;
import com.jakubeeee.tasks.enums.TaskStatus;
import com.jakubeeee.tasks.exceptions.InvalidTaskDefinitionException;
import com.jakubeeee.tasks.exceptions.InvalidTaskStatusException;
import com.jakubeeee.tasks.model.GenericTask;
import com.jakubeeee.tasks.model.PastTaskExecution;
import com.jakubeeee.tasks.model.StatusTransition;
import com.jakubeeee.tasks.publishers.TaskPublisher;
import com.jakubeeee.tasks.repositories.PastTaskExecutionRepository;
import com.jakubeeee.tasks.validators.TaskValidator;
import lombok.Getter;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;

import static com.jakubeeee.common.util.CollectionUtils.filterList;
import static com.jakubeeee.common.util.DateTimeUtils.*;
import static com.jakubeeee.common.util.ReflectUtils.getFieldValue;
import static com.jakubeeee.tasks.enums.TaskStatus.*;
import static java.time.LocalDateTime.now;
import static java.util.Objects.isNull;
import static org.apache.commons.lang3.reflect.FieldUtils.getFieldsListWithAnnotation;

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
        String exceptionMessageFirstPart = "Couldn't register a new task: \"" + task.getCode() + "\". ";
        String exceptionMessageSecondPart = "Detailed exception message: ";
        try {
            validateTaskDefinitionCorrectness(task);
            registeredTasks.add(task);
            progressTrackingService.registerTracker(task.getId(), task.getTracker());
            nextScheduledTasksExecutions.put(task.getId(), calculateFirstScheduledTaskExecution(task.getId()));
            LOG.info("Registered successfully a new task: \"" + task.getCode() + "\". ");
        } catch (InvalidTaskDefinitionException e) {
            LOG.error(exceptionMessageFirstPart +
                    "There where errors in it's configuration. " +
                    exceptionMessageSecondPart + "\"" + e.getClass() + "\": " + e.getMessage());
        } catch (UnexpectedClassStructureException e2) {
            LOG.error(exceptionMessageFirstPart +
                    "There where errors in class structure. " +
                    exceptionMessageSecondPart + "\"" + e2.getClass() + "\": " + e2.getMessage());
        } catch (Exception e3) {
            LOG.error(exceptionMessageFirstPart +
                    "There where unknown errors. " +
                    exceptionMessageSecondPart + "\"" + e3.getClass() + "\": " + e3.getMessage());
        }
    }

    private void validateTaskDefinitionCorrectness(GenericTask task)
            throws InvalidTaskDefinitionException, UnexpectedClassStructureException, IllegalAccessException {
        validateUsingGenericTaskValidator(task);
        validateUsingSpecificTaskValidators(task);
    }

    private void validateUsingGenericTaskValidator(GenericTask task) throws InvalidTaskDefinitionException {
        if (task.getId() <= 0)
            throw new InvalidTaskDefinitionException("Task id must be a positive number");
        if (!isTaskIdUnique(task.getId()))
            throw new InvalidTaskDefinitionException("Another task is already registered with given id: " + task.getId());
        if (isNull(task.getCode()))
            throw new InvalidTaskDefinitionException("Task code must not be null");
        if (!isTaskCodeUnique(task.getCode()))
            throw new InvalidTaskDefinitionException("Another task is already registered with given code: " + task.getCode());
        if (isNull(task.getMode()))
            throw new InvalidTaskDefinitionException("Task mode must not be null");
        if (task.getIntervalInMillis() < 0 || task.getDelayInMillis() < 0)
            throw new InvalidTaskDefinitionException("Task interval and delay must be either 0 or positive");
        if (task.getIntervalInMillis() == 0 && task.getDelayInMillis() > 0)
            throw new InvalidTaskDefinitionException("If task interval is 0, its delay must also be 0");
        if (isNull(task.getTaskProvider()))
            throw new InvalidTaskDefinitionException("Task provider must not be null");
    }

    public void validateUsingSpecificTaskValidators(GenericTask task)
            throws InvalidTaskDefinitionException, UnexpectedClassStructureException, IllegalAccessException {
        List<Field> taskValidatorFields = getFieldsListWithAnnotation(task.getClass(), InitialTaskValidator.class);
        if (!taskValidatorFields.isEmpty()) {
            for (var taskValidatorField : taskValidatorFields) {
                TaskValidator taskSpecificCorrectnessValidator = getFieldValue(taskValidatorField, task, TaskValidator.class);
                taskSpecificCorrectnessValidator.validate(task);
            }
        } else {
            // skip additional validation if this task does not have any specific validator
        }
    }

    private boolean isTaskIdUnique(long taskId) {
        return !getTask(taskId).isPresent();
    }

    private boolean isTaskCodeUnique(String taskCode) {
        return !getTask(taskCode).isPresent();
    }

    Optional<GenericTask> getTask(long taskId) {
        GenericTask foundTask = null;
        for (var taskInLoop : registeredTasks) {
            if (taskInLoop.getId() == taskId)
                foundTask = taskInLoop;
        }
        return Optional.ofNullable(foundTask);
    }

    Optional<GenericTask> getTask(String taskCode) {
        GenericTask foundTask = null;
        for (var taskInLoop : registeredTasks) {
            if (taskInLoop.getCode().equals(taskCode))
                foundTask = taskInLoop;
        }
        return Optional.ofNullable(foundTask);
    }

    public void changeStatus(GenericTask task, TaskStatus statusTo) throws InvalidTaskStatusException {
        TaskStatus statusFrom = task.getStatus();
        var statusTransition = new StatusTransition(statusFrom, statusTo);
        if (isTransitionAllowed(statusTransition))
            task.setStatus(statusTo);
        else
            throw new InvalidTaskStatusException("Invalid status transition. From " + statusFrom + " to " + statusTo);
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
    void registerNewPastTaskExecution(PastTaskExecution pastTaskExecution) {
        pastTaskExecutionRepository.save(pastTaskExecution);
        taskPublisher.publishPastTaskExecutions(getPastTaskExecutions());
    }

    public List<PastTaskExecution> getPastTaskExecutions() {
        return (List<PastTaskExecution>) pastTaskExecutionRepository.findAll();
    }

    @Synchronized
    @Transactional
    @Scheduled(fixedRate = 30000)
    public void removeUnnecessaryPastTasksExecutions() {
        LocalDateTime timeHoursAgo = getCurrentDateTime().minusHours(PAST_TASKS_EXECUTIONS_REMOVAL_HOURS_THRESHOLD);
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
            return formatDateTime(roundTimeToNextFullMinute(getCurrentDateTime().plusMinutes(millisToMinutes(delay))));
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
