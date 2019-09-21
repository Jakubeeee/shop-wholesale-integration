package com.jakubeeee.tasks.service;

import com.jakubeeee.tasks.model.GenericTask;

import java.util.List;
import java.util.Optional;

/**
 * Interface for service beans used for operations related to task registration and retrieval.
 */
public interface TaskRegistryService {

    void registerTask(GenericTask task);

    boolean isTaskIdUnique(long taskId);

    boolean isTaskCodeUnique(String taskCode);

    Optional<GenericTask> getTask(long taskId);

    Optional<GenericTask> getTask(String taskCode);

    List<GenericTask> getRegisteredTasks();

}
