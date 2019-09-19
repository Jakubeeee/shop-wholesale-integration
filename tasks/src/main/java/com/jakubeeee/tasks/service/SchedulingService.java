package com.jakubeeee.tasks.service;

import com.jakubeeee.tasks.exceptions.NoTaskWithGivenIdException;
import com.jakubeeee.tasks.model.GenericTask;

/**
 * Interface for service beans used for operations related to task scheduling.
 */
public interface SchedulingService {

    void launchTaskImmediately(long taskId) throws NoTaskWithGivenIdException;

    void scheduleTask(GenericTask task);

}
