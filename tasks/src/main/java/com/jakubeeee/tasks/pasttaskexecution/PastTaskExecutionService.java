package com.jakubeeee.tasks.pasttaskexecution;

import java.util.List;

/**
 * Interface for service beans used for operations related to archivization of past task executions.
 */
public interface PastTaskExecutionService {

    void registerNewPastTaskExecution(PastTaskExecutionValue pastTaskExecution);

    List<PastTaskExecutionValue> getPastTaskExecutions();

    void removeUnnecessaryPastTasksExecutions();

}
