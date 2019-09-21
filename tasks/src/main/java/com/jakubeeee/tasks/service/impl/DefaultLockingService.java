package com.jakubeeee.tasks.service.impl;

import com.jakubeeee.tasks.service.LockingService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Synchronized;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * Default service bean used for operations related to locking task providers that cannot run in parallel.
 */
@RequiredArgsConstructor
@Service
public class DefaultLockingService implements LockingService {

    @Getter
    private Set<String> lockedTaskProvidersNames = new HashSet<>();

    @Synchronized
    @Override
    public void lockTaskProvider(String taskProviderName) {
        lockedTaskProvidersNames.add(taskProviderName);
    }

    @Synchronized
    @Override
    public void unlockTaskProvider(String taskProviderName) {
        lockedTaskProvidersNames.remove(taskProviderName);
    }

    @Synchronized
    @Override
    public boolean isTaskProviderLocked(String taskProviderName) {
        return lockedTaskProvidersNames.contains(taskProviderName);
    }

}
