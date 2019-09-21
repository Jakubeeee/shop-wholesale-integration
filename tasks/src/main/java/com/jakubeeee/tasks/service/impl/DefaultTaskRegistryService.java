package com.jakubeeee.tasks.service.impl;

import com.jakubeeee.common.exception.UnexpectedClassStructureException;
import com.jakubeeee.tasks.exceptions.InvalidTaskDefinitionException;
import com.jakubeeee.tasks.model.GenericTask;
import com.jakubeeee.tasks.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Default service bean used for operations related to task registration and retrieval.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class DefaultTaskRegistryService implements TaskRegistryService {

    private final TaskStoreService taskStoreService;

    private final ValidationService validationService;

    private final ProgressTrackingService progressTrackingService;

    private final NextScheduledExecutionService nextScheduledExecutionService;

    @Override
    public void registerTask(GenericTask task) {
        String exceptionMessageFirstPart = "Couldn't register a new task: \"" + task.getCode() + "\". ";
        String exceptionMessageSecondPart = "Detailed exception message: ";
        try {
            validateTaskDefinitionCorrectness(task);
            taskStoreService.getStoredTasks().add(task);
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

}
