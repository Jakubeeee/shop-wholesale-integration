package com.jakubeeee.tasks.service;

import java.util.Map;

/**
 * Interface for service beans used for operations related to management of next scheduled task executions.
 */
public interface NextScheduledExecutionService {

    Map<Long, String> getNextScheduledTasksExecutions();

    void updateNextScheduledTaskExecution(Long taskId);

    String calculateFirstScheduledTaskExecution(Long taskId);

}
