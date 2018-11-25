package com.jakubeeee.tasks.model;

import com.jakubeeee.tasks.enums.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StatusTransition {

    TaskStatus statusFrom;
    TaskStatus statusTo;

}
