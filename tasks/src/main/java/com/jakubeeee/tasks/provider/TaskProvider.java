package com.jakubeeee.tasks.provider;

import com.jakubeeee.core.DummyServiceException;
import com.jakubeeee.tasks.GenericTask;
import com.jakubeeee.tasks.impl.TaskAspect;
import com.jakubeeee.tasks.progresstracking.ProgressTrackerNotActiveException;
import com.jakubeeee.tasks.status.InvalidTaskStatusException;

/**
 * Interface for service beans used for providing tasks with executable logic.
 *
 * @see GenericTask
 * @see TaskAspect
 */
public interface TaskProvider<T extends GenericTask> {

    void beforeTask(T caller) throws DummyServiceException, InvalidTaskStatusException;

    void executeTask(T caller) throws ProgressTrackerNotActiveException;

    void afterTask(T caller);

    String getProviderName();

}
