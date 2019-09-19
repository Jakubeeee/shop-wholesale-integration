package com.jakubeeee.tasks.service;

import com.jakubeeee.core.exception.DummyServiceException;
import com.jakubeeee.tasks.exceptions.InvalidTaskStatusException;
import com.jakubeeee.tasks.model.GenericTask;
import com.jakubeeee.tasks.model.PastTaskExecution;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jakubeeee.common.util.DateTimeUtils.getCurrentDateTime;
import static com.jakubeeee.tasks.utils.LogParamsUtils.toLogParam;

/**
 * Base for service beans used for providing tasks with executable logic.
 */
public abstract class AbstractGenericTaskProvider<T extends GenericTask> implements TaskProvider<T> {

    protected final TaskService taskService;

    protected final ProgressTrackingService progressTrackingService;

    protected final LoggingService loggingService;

    public AbstractGenericTaskProvider(TaskService taskService, ProgressTrackingService progressTrackingService,
                                       LoggingService loggingService) {
        this.taskService = taskService;
        this.progressTrackingService = progressTrackingService;
        this.loggingService = loggingService;
    }

    protected Map<String, Object> executionParams = new HashMap<>();

    @Override
    public void beforeTask(T caller) throws DummyServiceException, InvalidTaskStatusException {
        taskService.lockTaskProvider(getProviderName());
        caller.getLastTaskExecutionInfo().setLastStartedExecutionTime(getCurrentDateTime());
        progressTrackingService.startTrackingProgress(caller);
        loggingService.startPublishingLogs();
        loggingService.info(caller.getId(), "TASKEXECSTART",
                List.of(toLogParam(caller.getCode(), true), toLogParam(String.valueOf(caller.getId()))));
    }

    @Override
    public void afterTask(T caller) {
        var pastTaskExecution = new PastTaskExecution(caller.getId());
        pastTaskExecution.setParams(new HashMap<>(executionParams));
        taskService.registerNewPastTaskExecution(pastTaskExecution);
        executionParams.clear();
        loggingService.removeUnnecessaryLogs();
        progressTrackingService.resetProgress(caller);
        caller.getLastTaskExecutionInfo().setLastFinishedExecutionTime(getCurrentDateTime());
        taskService.unlockTaskProvider(getProviderName());
    }

}
