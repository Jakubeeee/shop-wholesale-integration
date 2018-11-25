package com.jakubeeee.tasks.service;

import com.jakubeeee.common.exceptions.DummyServiceException;
import com.jakubeeee.tasks.aspects.TaskAspect;
import com.jakubeeee.tasks.exceptions.InvalidParametersException;
import com.jakubeeee.tasks.exceptions.InvalidTaskStatusException;
import com.jakubeeee.tasks.exceptions.ProgressTrackerNotActiveException;
import com.jakubeeee.tasks.model.GenericTask;

/**
 * An interface implemented by service classes used to provide business logic for tasks
 *
 * @see GenericTask
 * @see TaskAspect
 */
public interface TaskProviderInterface<T extends GenericTask> {

    void beforeTask(T caller) throws DummyServiceException, InvalidParametersException, InvalidTaskStatusException;

    void executeTask(T caller) throws ProgressTrackerNotActiveException;

    void afterTask(T caller);

    String getProviderName();

}
