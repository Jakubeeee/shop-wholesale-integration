package com.jakubeeee.tasks.status;

import com.jakubeeee.tasks.GenericTask;

/**
 * Interface for service beans used for operations related to task status management.
 */
public interface StatusService {

    void changeStatus(GenericTask task, TaskStatus statusTo) throws InvalidTaskStatusException;

}
