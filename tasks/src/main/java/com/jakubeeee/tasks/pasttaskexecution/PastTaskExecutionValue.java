package com.jakubeeee.tasks.pasttaskexecution;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.jakubeeee.common.persistence.IdentifiableEntityValue;
import com.jakubeeee.common.persistence.MultiValueParameter;
import com.jakubeeee.common.serialization.LocalDateTimeSerializer;
import com.jakubeeee.tasks.impl.pasttaskexecution.PastTaskExecution;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import lombok.Value;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
import java.util.List;

import static com.jakubeeee.common.DateTimeUtils.getCurrentDateTime;

/**
 * Immutable value object for {@link PastTaskExecution} data transfer.
 */
@EqualsAndHashCode(callSuper = false)
@ToString
@Value
public final class PastTaskExecutionValue extends IdentifiableEntityValue<PastTaskExecution> {

    private final long taskId;

    private final List<MultiValueParameter> params;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private final LocalDateTime executionFinishTime;

    public PastTaskExecutionValue(long taskId,
                                  @NonNull List<MultiValueParameter> params) {
        this(null, taskId, params);
    }

    public PastTaskExecutionValue(@Nullable Long databaseId,
                                  @NonNull long taskId,
                                  @NonNull List<MultiValueParameter> params) {
        this(databaseId, taskId, params, getCurrentDateTime());
    }

    public PastTaskExecutionValue(@Nullable Long databaseId,
                                  long taskId,
                                  @NonNull List<MultiValueParameter> params,
                                  @NonNull LocalDateTime executionFinishTime) {
        super(databaseId);
        this.taskId = taskId;
        this.params = List.copyOf(params);
        this.executionFinishTime = executionFinishTime;
    }

    public List<MultiValueParameter> getParams() {
        return List.copyOf(params);
    }

}