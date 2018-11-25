package com.jakubeeee.tasks.service;

import com.jakubeeee.tasks.exceptions.ProgressTrackerNotActiveException;
import com.jakubeeee.tasks.model.GenericTask;
import com.jakubeeee.tasks.model.ProgressTracker;
import com.jakubeeee.tasks.publishers.TaskPublisher;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ProgressTrackingService {

    @Getter
    private Map<Long, ProgressTracker> progressTrackers = new HashMap<>();

    @Autowired
    TaskPublisher taskPublisher;

    public void startTrackingProgress(GenericTask task) {
        task.getTracker().setActive(true);
        taskPublisher.publishTasksProgress(progressTrackers);
    }

    public void setMaxProgress(GenericTask task, long maxProgress) throws ProgressTrackerNotActiveException {
        validateIfProgressTrackerIsActive(task);
        task.getTracker().setMaxProgress(maxProgress);
    }

    public void advanceProgress(GenericTask task) throws ProgressTrackerNotActiveException {
        validateIfProgressTrackerIsActive(task);
        long currentProgress = task.getTracker().getCurrentProgress();
        task.getTracker().setCurrentProgress(currentProgress + 1);
        taskPublisher.publishTasksProgress(progressTrackers);
    }

    public void resetProgress(GenericTask task) {
        task.getTracker().setActive(false);
        task.getTracker().setCurrentProgress(0);
        task.getTracker().setMaxProgress(1);
        taskPublisher.publishTasksProgress(progressTrackers);
    }

    public void registerTracker(long key, ProgressTracker tracker) {
        progressTrackers.put(key, tracker);
    }

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
