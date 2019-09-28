package com.jakubeeee.tasks.impl;

import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Optional;

@Setter
public class LastTaskExecutionInfo {

    LocalDateTime lastStartedExecutionTime;

    LocalDateTime lastFinishedExecutionTime;

    LocalDateTime lastScheduledStartedExecutionTime;

    LocalDateTime lastScheduledFinishedExecutionTime;

    public Optional<LocalDateTime> getLastStartedExecutionTime() {
        return Optional.ofNullable(lastStartedExecutionTime);
    }

    public Optional<LocalDateTime> getLastFinishedExecutionTime() {
        return Optional.ofNullable(lastFinishedExecutionTime);
    }

    public Optional<LocalDateTime> getLastScheduledStartedExecutionTime() {
        return Optional.ofNullable(lastScheduledStartedExecutionTime);
    }

    public Optional<LocalDateTime> getLastScheduledFinishedExecutionTime() {
        return Optional.ofNullable(lastScheduledFinishedExecutionTime);
    }
}
