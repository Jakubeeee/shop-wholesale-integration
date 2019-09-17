package com.jakubeeee.tasks.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.jakubeeee.common.converter.JsonObjectConverter;
import com.jakubeeee.common.mixin.Parameterizable;
import com.jakubeeee.common.serializer.LocalDateTimeSerializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static com.jakubeeee.common.util.DateTimeUtils.getCurrentDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "past_task_executions")
public class PastTaskExecution implements Parameterizable {

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private long taskId;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime executionFinishTime;

    @Convert(converter = JsonObjectConverter.class)
    private Map<String, Object> params;

    public PastTaskExecution(long taskId) {
        this.taskId = taskId;
        this.params = new HashMap<>();
        this.executionFinishTime = getCurrentDateTime();
    }

}
