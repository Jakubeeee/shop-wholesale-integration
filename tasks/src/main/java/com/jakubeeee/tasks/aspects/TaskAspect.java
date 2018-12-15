package com.jakubeeee.tasks.aspects;

import com.jakubeeee.tasks.model.GenericTask;
import com.jakubeeee.tasks.service.LoggingService;
import com.jakubeeee.tasks.service.TaskProvider;
import com.jakubeeee.tasks.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;

import static com.jakubeeee.common.utils.LangUtils.toList;
import static com.jakubeeee.tasks.enums.TaskStatus.*;
import static com.jakubeeee.tasks.utils.LogParamsUtils.toLogParam;

@Slf4j
@Aspect
@Component
public class TaskAspect {

    @Autowired
    TaskService taskService;

    @Autowired
    LoggingService loggingService;

    @SuppressWarnings("unchecked")
    @Around("execution( void com.jakubeeee..*.AbstractGenericTaskProvider.executeTask(..))")
    public void aroundTaskExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        var taskProvider = (TaskProvider) joinPoint.getThis();
        var caller = (GenericTask) joinPoint.getArgs()[0];
        if (!taskService.isTaskProviderLocked(caller.getTaskProvider().getProviderName())) {
            if (!caller.getTracker().isActive()) {
                taskService.changeStatus(caller, LAUNCHED);
                taskProvider.beforeTask(caller);
                taskService.changeStatus(caller, PREPARED);
                boolean isExecutedSuccessfully = false;
                try {
                    joinPoint.proceed();
                    isExecutedSuccessfully = true;
                } catch (ResourceAccessException e) {
                    LOG.error("ResourceAccessException has occurred", e);
                    loggingService.error(caller.getId(), "CONNPROB");
                } catch (Exception e2) {
                    LOG.error("Unknown Exception has occurred", e2);
                    loggingService.error(caller.getId(), "UNKNOWNPROB");
                } finally {
                    if (isExecutedSuccessfully) {
                        taskService.changeStatus(caller, EXECUTED);
                        loggingService.info(caller.getId(), "TASKEXECSUCC",
                                toList(toLogParam(caller.getCode(), true), toLogParam(String.valueOf(caller.getId()))));
                    } else {
                        taskService.changeStatus(caller, ABORTED);
                        loggingService.info(caller.getId(), "TASKEXECABORT",
                                toList(toLogParam(caller.getCode(), true), toLogParam(String.valueOf(caller.getId()))));
                    }
                }
                taskProvider.afterTask(caller);
                taskService.changeStatus(caller, WAITING);
            } else {
                loggingService.warn(caller.getId(), "TASKALRLAUNCH",
                        toList(toLogParam(caller.getCode(), true), toLogParam(String.valueOf(caller.getId()))));
                LOG.warn("Tried to launch " + caller.getCode() + " but this task is currently running");
            }
        } else {
            loggingService.warn(caller.getId(), "PROVALRINUSE",
                    toList(toLogParam(caller.getCode(), true), toLogParam(String.valueOf(caller.getId()))));
            LOG.warn("Tried to launch " + caller.getCode() + " but another task already locked it's provider");
        }
        taskService.updateNextScheduledTaskExecution(caller.getId());
    }

}