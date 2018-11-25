package com.jakubeeee.tasks.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.jakubeeee.common.converters.JsonObjectConverter;
import com.jakubeeee.common.converters.LocalDateTimeConverter;
import com.jakubeeee.common.misc.Parameterizable;
import com.jakubeeee.common.serializers.LocalDateTimeSerializer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static java.time.LocalDateTime.now;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@Entity
@Table(name = "past_task_executions")
public class PastTaskExecution implements Parameterizable {

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    long taskId;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @Convert(converter = LocalDateTimeConverter.class)
    LocalDateTime executionFinishTime;

    @Convert(converter = JsonObjectConverter.class)
    Map<String, Object> params;

    public PastTaskExecution(long taskId) {
        this.taskId = taskId;
        this.params = new HashMap<>();
        this.executionFinishTime = now();
    }

}
