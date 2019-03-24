package com.jakubeeee.tasks.service;

import com.jakubeeee.core.exceptions.DummyServiceException;
import com.jakubeeee.tasks.exceptions.InvalidTaskStatusException;
import com.jakubeeee.tasks.model.GenericTask;
import com.jakubeeee.tasks.model.PastTaskExecution;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jakubeeee.tasks.utils.LogParamsUtils.toLogParam;
import static java.time.LocalDateTime.now;

@FieldDefaults(level = AccessLevel.PROTECTED)
public abstract class AbstractGenericTaskProvider<T extends GenericTask> implements TaskProvider<T> {

    @Autowired
    TaskService taskService;

    @Autowired
    ProgressTrackingService progressTrackingService;

    @Autowired
    LoggingService loggingService;

    Map<String, Object> executionParams = new HashMap<>();

    @Override
    public void beforeTask(T caller) throws DummyServiceException, InvalidTaskStatusException {
        taskService.lockTaskProvider(getProviderName());
        caller.getLastTaskExecutionInfo().setLastStartedExecutionTime(now());
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
        caller.getLastTaskExecutionInfo().setLastFinishedExecutionTime(now());
        taskService.unlockTaskProvider(getProviderName());
    }

}
