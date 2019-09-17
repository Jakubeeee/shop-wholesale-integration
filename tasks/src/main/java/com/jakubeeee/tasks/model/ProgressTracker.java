package com.jakubeeee.tasks.model;

import lombok.Data;

@Data
public class ProgressTracker {

    private boolean isActive = false;
    private long currentProgress = 0;
    private long maxProgress = 1;

}
