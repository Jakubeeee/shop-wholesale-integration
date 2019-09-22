package com.jakubeeee.tasks.service;

import com.jakubeeee.common.persistence.MultiValueParameter;
import com.jakubeeee.core.DummyServiceException;
import com.jakubeeee.tasks.exceptions.InvalidTaskStatusException;
import com.jakubeeee.tasks.model.GenericTask;
import com.jakubeeee.tasks.model.PastTaskExecutionValue;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.jakubeeee.common.DateTimeUtils.getCurrentDateTime;
import static com.jakubeeee.tasks.utils.LogParamsUtils.toLogParam;

/**
 * Base for service beans used for providing tasks with executable logic.
 */
@RequiredArgsConstructor
public abstract class AbstractGenericTaskProvider<T extends GenericTask> implements TaskProvider<T> {

    protected final TaskRegistryService taskRegistryService;

    protected final LockingService lockingService;

    protected final ProgressTrackingService progressTrackingService;

    protected final LoggingService loggingService;

    protected final PastTaskExecutionService pastTaskExecutionService;

    protected List<MultiValueParameter> executionParams = new ArrayList<>();

    @Override
    public void beforeTask(T caller) throws DummyServiceException, InvalidTaskStatusException {
        lockingService.lockTaskProvider(getProviderName());
        caller.getLastTaskExecutionInfo().setLastStartedExecutionTime(getCurrentDateTime());
        progressTrackingService.startTrackingProgress(caller);
        loggingService.startPublishingLogs();
        loggingService.info(caller.getId(), "TASKEXECSTART",
                List.of(toLogParam(caller.getCode(), true), toLogParam(String.valueOf(caller.getId()))));
    }

    @Override
    public void afterTask(T caller) {
        var pastTaskExecution = new PastTaskExecutionValue(caller.getId(), executionParams);
        pastTaskExecutionService.registerNewPastTaskExecution(pastTaskExecution);
        executionParams.clear();
        loggingService.removeUnnecessaryLogs();
        progressTrackingService.resetProgress(caller);
        caller.getLastTaskExecutionInfo().setLastFinishedExecutionTime(getCurrentDateTime());
        lockingService.unlockTaskProvider(getProviderName());
    }

    protected void addExecutionParam(String key, String... values) {
        executionParams.add(new MultiValueParameter(key, Arrays.asList(values)));
    }

}
