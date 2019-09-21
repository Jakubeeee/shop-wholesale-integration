package com.jakubeeee.tasks.model;

import com.jakubeeee.common.converter.JsonObjectConverter;
import com.jakubeeee.common.entity.IdentifiableEntity;
import com.jakubeeee.common.model.MultiValueParameter;
import lombok.*;
import org.springframework.data.annotation.Immutable;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Entity representing an execution of {@link GenericTask} that happened in the past and carrying useful information
 * about it.
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ToString
@NoArgsConstructor
@Immutable
@Entity
@Table(name = "PAST_TASK_EXECUTIONS")
public class PastTaskExecution extends IdentifiableEntity {

    private long taskId;

    @Convert(converter = JsonObjectConverter.class)
    private List<MultiValueParameter> params;

    private LocalDateTime executionFinishTime;

}
