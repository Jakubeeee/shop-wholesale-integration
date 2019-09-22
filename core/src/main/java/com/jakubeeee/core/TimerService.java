package com.jakubeeee.core;

import java.util.function.BooleanSupplier;

/**
 * Interface for service beans used for operations related to scheduling recurrent jobs.
 */
public interface TimerService {

    void setRecurrentJob(Runnable job, long delay, long interval);

    void setRecurrentJob(Runnable job, long delay, long interval, BooleanSupplier cancelCondition,
                         Runnable afterCancellation, boolean shouldExecuteLastTime);

}
