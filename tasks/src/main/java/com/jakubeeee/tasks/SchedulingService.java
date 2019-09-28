package com.jakubeeee.tasks;

/**
 * Interface for service beans used for operations related to task scheduling.
 */
public interface SchedulingService {

    void launchTaskImmediately(long taskId) throws NoTaskWithGivenIdException;

    void scheduleTask(GenericTask task);

}
