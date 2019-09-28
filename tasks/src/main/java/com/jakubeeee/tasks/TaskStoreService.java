package com.jakubeeee.tasks;

import java.util.List;
import java.util.Optional;

/**
 * Interface for service beans used for operations related to task storing and retrieval.
 */
public interface TaskStoreService {

    boolean isTaskIdUnique(long taskId);

    boolean isTaskCodeUnique(String taskCode);

    Optional<GenericTask> getTask(long taskId);

    Optional<GenericTask> getTask(String taskCode);

    List<GenericTask> getStoredTasks();

}
