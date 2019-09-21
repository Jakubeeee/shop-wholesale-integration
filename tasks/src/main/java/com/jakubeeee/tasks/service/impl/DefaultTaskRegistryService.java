package com.jakubeeee.tasks.service.impl;

import com.jakubeeee.common.exception.UnexpectedClassStructureException;
import com.jakubeeee.tasks.exceptions.InvalidTaskDefinitionException;
import com.jakubeeee.tasks.model.GenericTask;
import com.jakubeeee.tasks.service.NextScheduledExecutionService;
import com.jakubeeee.tasks.service.ProgressTrackingService;
import com.jakubeeee.tasks.service.TaskRegistryService;
import com.jakubeeee.tasks.service.ValidationService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Default service bean used for operations related to task registration and retrieval.
 */
@Slf4j
@Service
public class DefaultTaskRegistryService implements TaskRegistryService {

    private final ValidationService validationService;

    private final ProgressTrackingService progressTrackingService;

    private final NextScheduledExecutionService nextScheduledExecutionService;

    @Getter
    private List<GenericTask> registeredTasks = new ArrayList<>();

    public DefaultTaskRegistryService(@Lazy ValidationService validationService,
                                      ProgressTrackingService progressTrackingService,
                                      @Lazy NextScheduledExecutionService nextScheduledExecutionService) {
        this.validationService = validationService;
        this.progressTrackingService = progressTrackingService;
        this.nextScheduledExecutionService = nextScheduledExecutionService;
    }

    @Override
    public void registerTask(GenericTask task) {
        String exceptionMessageFirstPart = "Couldn't register a new task: \"" + task.getCode() + "\". ";
        String exceptionMessageSecondPart = "Detailed exception message: ";
        try {
            validateTaskDefinitionCorrectness(task);
            registeredTasks.add(task);
            progressTrackingService.registerTracker(task.getId(), task.getTracker());
            nextScheduledExecutionService.getNextScheduledTasksExecutions().put(task.getId(),
                    nextScheduledExecutionService.calculateFirstScheduledTaskExecution(task.getId()));
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
        validationService.validateUsingGenericTaskValidator(task);
        validationService.validateUsingSpecificTaskValidators(task);
    }

    @Override
    public boolean isTaskIdUnique(long taskId) {
        return getTask(taskId).isEmpty();
    }

    @Override
    public boolean isTaskCodeUnique(String taskCode) {
        return getTask(taskCode).isEmpty();
    }

    @Override
    public Optional<GenericTask> getTask(long taskId) {
        GenericTask foundTask = null;
        for (var taskInLoop : registeredTasks) {
            if (taskInLoop.getId() == taskId)
                foundTask = taskInLoop;
        }
        return Optional.ofNullable(foundTask);
    }

    @Override
    public Optional<GenericTask> getTask(String taskCode) {
        GenericTask foundTask = null;
        for (var taskInLoop : registeredTasks) {
            if (taskInLoop.getCode().equals(taskCode))
                foundTask = taskInLoop;
        }
        return Optional.ofNullable(foundTask);
    }

}
