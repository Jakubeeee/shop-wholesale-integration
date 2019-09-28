package com.jakubeeee.tasks.progresstracking;

import com.jakubeeee.tasks.GenericTask;
import com.jakubeeee.tasks.impl.progresstracking.ProgressTracker;

import java.util.Map;

/**
 * Interface for service beans used for operations related to task progress tracking.
 */
public interface ProgressTrackingService {

    void startTrackingProgress(GenericTask task);

    void setMaxProgress(GenericTask task, long maxProgress) throws ProgressTrackerNotActiveException;

    void advanceProgress(GenericTask task) throws ProgressTrackerNotActiveException;

    void resetProgress(GenericTask task);

    void registerTracker(long key, ProgressTracker tracker);

    Map<Long, ProgressTracker> getProgressTrackers();

    boolean isAnyProgressTrackerActive();

}
