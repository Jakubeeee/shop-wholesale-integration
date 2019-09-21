package com.jakubeeee.tasks.service;

import com.jakubeeee.tasks.enums.TaskStatus;
import com.jakubeeee.tasks.exceptions.InvalidTaskStatusException;
import com.jakubeeee.tasks.model.GenericTask;

/**
 * Interface for service beans used for operations related to task status management.
 */
public interface StatusService {

    void changeStatus(GenericTask task, TaskStatus statusTo) throws InvalidTaskStatusException;

}
