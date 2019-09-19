package com.jakubeeee.core.service.impl;

import com.jakubeeee.core.service.TimerService;
import org.springframework.stereotype.Service;

import java.util.Timer;
import java.util.TimerTask;
import java.util.function.BooleanSupplier;

/**
 * Default service bean used for operations related to scheduling recurrent jobs.
 */
@Service
public class DefaultTimerService implements TimerService {

    @Override
    public void setRecurrentJob(Runnable job, long delay, long interval) {
        var timerTask = new TimerTask() {
            @Override
            public void run() {
                job.run();
            }
        };
        new Timer().scheduleAtFixedRate(timerTask, delay, interval);
    }

    @Override
    public void setRecurrentJob(Runnable job, long delay, long interval, BooleanSupplier cancelCondition,
                                Runnable afterCancellation, boolean shouldExecuteLastTime) {
        var timerTask = new TimerTask() {
            @Override
            public void run() {
                boolean isCanceled = cancelCondition.getAsBoolean();
                if (isCanceled) {
                    this.cancel();
                    if (!shouldExecuteLastTime) {
                        afterCancellation.run();
                        return;
                    }
                }
                job.run();
                if (isCanceled) afterCancellation.run();
            }
        };
        new Timer().scheduleAtFixedRate(timerTask, delay, interval);
    }

}

