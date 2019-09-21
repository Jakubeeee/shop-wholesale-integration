package com.jakubeeee.tasks.aspects;

import com.jakubeeee.tasks.model.GenericTask;
import com.jakubeeee.tasks.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;

import java.util.List;

import static com.jakubeeee.tasks.enums.TaskStatus.*;
import static com.jakubeeee.tasks.utils.LogParamsUtils.toLogParam;

@Slf4j
@RequiredArgsConstructor
@Aspect
@Component
public class TaskAspect {

    private final TaskRegistryService taskRegistryService;

    private final NextScheduledExecutionService nextScheduledExecutionService;

    private final StatusService statusService;

    private final LockingService lockingService;

    private final LoggingService loggingService;

    @SuppressWarnings("unchecked")
    @Around("execution( void com.jakubeeee..*.AbstractGenericTaskProvider.executeTask(..))")
    public void aroundTaskExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        var taskProvider = (TaskProvider) joinPoint.getThis();
        var caller = (GenericTask) joinPoint.getArgs()[0];
        if (!lockingService.isTaskProviderLocked(caller.getTaskProvider().getProviderName())) {
            if (!caller.getTracker().isActive()) {
                statusService.changeStatus(caller, LAUNCHED);
                taskProvider.beforeTask(caller);
                statusService.changeStatus(caller, PREPARED);
                boolean isExecutedSuccessfully = false;
                try {
                    joinPoint.proceed();
                    isExecutedSuccessfully = true;
                } catch (ResourceAccessException e) {
                    LOG.error("ResourceAccessException has occurred", e);
                    loggingService.error(caller.getId(), "CONNPROB");
                } catch (Throwable e2) {
                    LOG.error("Unknown Exception has occurred", e2);
                    loggingService.error(caller.getId(), "UNKNOWNPROB");
                } finally {
                    if (isExecutedSuccessfully) {
                        statusService.changeStatus(caller, EXECUTED);
                        loggingService.info(caller.getId(), "TASKEXECSUCC",
                                List.of(toLogParam(caller.getCode(), true),
                                        toLogParam(String.valueOf(caller.getId()))));
                    } else {
                        statusService.changeStatus(caller, ABORTED);
                        loggingService.info(caller.getId(), "TASKEXECABORT",
                                List.of(toLogParam(caller.getCode(), true),
                                        toLogParam(String.valueOf(caller.getId()))));
                    }
                    taskProvider.afterTask(caller);
                    statusService.changeStatus(caller, WAITING);
                }
            } else {
                loggingService.warn(caller.getId(), "TASKALRLAUNCH",
                        List.of(toLogParam(caller.getCode(), true), toLogParam(String.valueOf(caller.getId()))));
                LOG.warn("Tried to launch " + caller.getCode() + " but this task is currently running");
            }
        } else {
            loggingService.warn(caller.getId(), "PROVALRINUSE",
                    List.of(toLogParam(caller.getCode(), true), toLogParam(String.valueOf(caller.getId()))));
            LOG.warn("Tried to launch " + caller.getCode() + " but another task already locked it's provider");
        }
        nextScheduledExecutionService.updateNextScheduledTaskExecution(caller.getId());
    }

}