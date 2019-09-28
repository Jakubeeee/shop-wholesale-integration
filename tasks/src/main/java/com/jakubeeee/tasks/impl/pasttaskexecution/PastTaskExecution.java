package com.jakubeeee.tasks.impl.pasttaskexecution;

import com.jakubeeee.common.persistence.IdentifiableEntity;
import com.jakubeeee.common.persistence.JsonObjectConverter;
import com.jakubeeee.common.persistence.MultiValueParameter;
import com.jakubeeee.tasks.GenericTask;
import lombok.*;
import org.springframework.data.annotation.Immutable;

import javax.persistence.Column;
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

    @Column(name = "TASK_ID", nullable = false, updatable = false)
    private long taskId;

    @Convert(converter = JsonObjectConverter.class)
    @Column(name = "PARAMS", updatable = false)
    private List<MultiValueParameter> params;

    @Column(name = "FINISH_TIME", nullable = false, updatable = false)
    private LocalDateTime finishTime;

}
