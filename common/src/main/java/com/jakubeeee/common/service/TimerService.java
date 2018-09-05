package com.jakubeeee.common.service;

import org.springframework.stereotype.Service;

import java.util.Timer;
import java.util.TimerTask;

@Service
public class TimerService {

    public void setRecurrentTask(Runnable runnable, long delay, long interval) {
        Timer timer = new Timer();
        TimerTask hourlyTask = new TimerTask() {
            @Override
            public void run() {
                runnable.run();
            }
        };
        timer.scheduleAtFixedRate(hourlyTask, delay, interval);
    }

}

