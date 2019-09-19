package com.jakubeeee.tasks.service.impl;

import com.jakubeeee.tasks.exceptions.ProgressTrackerNotActiveException;
import com.jakubeeee.tasks.model.GenericTask;
import com.jakubeeee.tasks.model.ProgressTracker;
import com.jakubeeee.tasks.publishers.TaskPublisher;
import com.jakubeeee.tasks.service.ProgressTrackingService;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Default service bean used for operations related to task progress tracking.
 */
@Service
public class DefaultProgressTrackingService implements ProgressTrackingService {

    private final TaskPublisher taskPublisher;

    @Getter
    private Map<Long, ProgressTracker> progressTrackers = new HashMap<>();

    public DefaultProgressTrackingService(TaskPublisher taskPublisher) {
        this.taskPublisher = taskPublisher;
    }

    @Override
    public void startTrackingProgress(GenericTask task) {
        task.getTracker().setActive(true);
        taskPublisher.publishTasksProgress(progressTrackers);
    }

    @Override
    public void setMaxProgress(GenericTask task, long maxProgress) throws ProgressTrackerNotActiveException {
        validateIfProgressTrackerIsActive(task);
        task.getTracker().setMaxProgress(maxProgress);
    }

    @Override
    public void advanceProgress(GenericTask task) throws ProgressTrackerNotActiveException {
        validateIfProgressTrackerIsActive(task);
        long currentProgress = task.getTracker().getCurrentProgress();
        task.getTracker().setCurrentProgress(currentProgress + 1);
        taskPublisher.publishTasksProgress(progressTrackers);
    }

    @Override
    public void resetProgress(GenericTask task) {
        task.getTracker().setActive(false);
        task.getTracker().setCurrentProgress(0);
        task.getTracker().setMaxProgress(1);
        taskPublisher.publishTasksProgress(progressTrackers);
    }

    @Override
    public void registerTracker(long key, ProgressTracker tracker) {
        progressTrackers.put(key, tracker);
    }

    @Override
    public boolean isAnyProgressTrackerActive() {
        for (var tracker : progressTrackers.values())
            if (tracker.isActive()) {
                return true;
            }
        return false;
    }

    private void validateIfProgressTrackerIsActive(GenericTask task) throws ProgressTrackerNotActiveException {
        if (!task.getTracker().isActive())
            throw new ProgressTrackerNotActiveException(
                    "Progress tracker that belongs to the task definition with id " + task.getId() + " is not active");
    }

}
