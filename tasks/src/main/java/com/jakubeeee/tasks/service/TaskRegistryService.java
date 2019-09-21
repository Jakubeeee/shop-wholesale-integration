package com.jakubeeee.tasks.service;

import com.jakubeeee.tasks.model.GenericTask;

/**
 * Interface for service beans used for operations related to task registration.
 */
public interface TaskRegistryService {

    void registerTask(GenericTask task);

}
