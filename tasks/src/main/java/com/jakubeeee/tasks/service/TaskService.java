package com.jakubeeee.tasks.service;

import com.jakubeeee.common.exception.UnexpectedClassStructureException;
import com.jakubeeee.tasks.enums.TaskStatus;
import com.jakubeeee.tasks.exceptions.InvalidTaskDefinitionException;
import com.jakubeeee.tasks.exceptions.InvalidTaskStatusException;
import com.jakubeeee.tasks.model.GenericTask;
import com.jakubeeee.tasks.model.PastTaskExecution;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Interface for service beans used for general operations related to tasks.
 */
public interface TaskService {

    void registerTask(GenericTask task);

    void validateUsingSpecificTaskValidators(GenericTask task) throws InvalidTaskDefinitionException,
            UnexpectedClassStructureException, IllegalAccessException;

    Optional<GenericTask> getTask(long taskId);

    Optional<GenericTask> getTask(String taskCode);

    void changeStatus(GenericTask task, TaskStatus statusTo) throws InvalidTaskStatusException;

    List<GenericTask> getRegisteredTasks();

    void registerNewPastTaskExecution(PastTaskExecution pastTaskExecution);

    List<PastTaskExecution> getPastTaskExecutions();

    void removeUnnecessaryPastTasksExecutions();

    Map<Long, String> getNextScheduledTasksExecutions();

    void updateNextScheduledTaskExecution(Long taskId);

    void lockTaskProvider(String taskProviderName);

    void unlockTaskProvider(String taskProviderName);

    boolean isTaskProviderLocked(String taskProviderName);

}
