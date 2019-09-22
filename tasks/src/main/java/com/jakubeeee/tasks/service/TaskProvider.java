package com.jakubeeee.tasks.service;

import com.jakubeeee.core.DummyServiceException;
import com.jakubeeee.tasks.aspects.TaskAspect;
import com.jakubeeee.tasks.exceptions.InvalidTaskStatusException;
import com.jakubeeee.tasks.exceptions.ProgressTrackerNotActiveException;
import com.jakubeeee.tasks.model.GenericTask;

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
