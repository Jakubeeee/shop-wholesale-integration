package com.jakubeeee.tasks.service;

/**
 * Interface for service beans used for operations related to locking task providers that cannot run in parallel.
 */
public interface LockingService {

    void lockTaskProvider(String taskProviderName);

    void unlockTaskProvider(String taskProviderName);

    boolean isTaskProviderLocked(String taskProviderName);

}
