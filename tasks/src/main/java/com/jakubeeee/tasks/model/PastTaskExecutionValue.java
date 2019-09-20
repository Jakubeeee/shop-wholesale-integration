package com.jakubeeee.tasks.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.jakubeeee.common.model.ImmutableValue;
import com.jakubeeee.common.serializer.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.Map;

import static com.jakubeeee.common.util.DateTimeUtils.getCurrentDateTime;

/**
 * Immutable value object for {@link PastTaskExecution} data transfer.
 */
@Value
@AllArgsConstructor
public final class PastTaskExecutionValue implements ImmutableValue<PastTaskExecution> {

    private final long taskId;

    private final Map<String, Object> params;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private final LocalDateTime executionFinishTime;

    public PastTaskExecutionValue(long taskId, Map<String, Object> params) {
        this.taskId = taskId;
        this.params = params;
        this.executionFinishTime = getCurrentDateTime();
    }

}