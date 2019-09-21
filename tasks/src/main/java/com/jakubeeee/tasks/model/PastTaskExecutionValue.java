package com.jakubeeee.tasks.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.jakubeeee.common.model.ImmutableValue;
import com.jakubeeee.common.model.MultiValueParameter;
import com.jakubeeee.common.serializer.LocalDateTimeSerializer;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.List;

import static com.jakubeeee.common.util.DateTimeUtils.getCurrentDateTime;

/**
 * Immutable value object for {@link PastTaskExecution} data transfer.
 */
@Value
public final class PastTaskExecutionValue implements ImmutableValue<PastTaskExecution> {

    private final long taskId;

    private final List<MultiValueParameter> params;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private final LocalDateTime executionFinishTime;

    public PastTaskExecutionValue(long taskId, List<MultiValueParameter> params) {
        this(taskId, params, getCurrentDateTime());
    }

    public PastTaskExecutionValue(long taskId, List<MultiValueParameter> params, LocalDateTime executionFinishTime) {
        this.taskId = taskId;
        this.params = List.copyOf(params);
        this.executionFinishTime = executionFinishTime;
    }

}