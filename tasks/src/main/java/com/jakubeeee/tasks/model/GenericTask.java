package com.jakubeeee.tasks.model;

import com.diffplug.common.base.Errors;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jakubeeee.tasks.enums.TaskMode;
import com.jakubeeee.tasks.enums.TaskStatus;
import com.jakubeeee.tasks.service.TaskProvider;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import static com.jakubeeee.tasks.enums.TaskStatus.WAITING;

/**
 * Class whose instance represent a single task that can be scheduled and executed by this application
 */
@NoArgsConstructor
public class GenericTask {

    @Getter
    @Setter
    private long id;

    @Getter
    @NonNull
    private String code;

    @JsonIgnore
    @Getter
    private TaskProvider taskProvider;

    @Getter
    private TaskMode mode;

    @JsonIgnore
    @Getter
    @Setter
    private TaskStatus status = WAITING;

    private long interval;

    @JsonIgnore
    private long delay;

    @JsonIgnore
    @Getter
    @SuppressWarnings("unchecked")
    private Runnable taskFunction = Errors.rethrow().wrap(() -> taskProvider.executeTask(this));

    @JsonIgnore
    @Getter
    private ProgressTracker tracker;

    @JsonIgnore
    @Getter
    private LastTaskExecutionInfo lastTaskExecutionInfo;

    public GenericTask(long id, String code, TaskMode mode, long interval, long delay, TaskProvider taskProvider) {
        this.id = id;
        this.code = code;
        this.mode = mode;
        this.interval = interval;
        this.delay = delay;
        this.taskProvider = taskProvider;
        this.tracker = new ProgressTracker();
        this.lastTaskExecutionInfo = new LastTaskExecutionInfo();
    }

    public long getDelayInMillis() {
        return delay * 60 * 1000;
    }

    public long getIntervalInMillis() {
        return interval * 60 * 1000;
    }

    public boolean isScheduledable() {
        return interval != 0;
    }

}
