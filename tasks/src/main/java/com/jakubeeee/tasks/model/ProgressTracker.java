package com.jakubeeee.tasks.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProgressTracker {

    boolean isActive = false;
    long currentProgress = 0;
    long maxProgress = 1;

}
