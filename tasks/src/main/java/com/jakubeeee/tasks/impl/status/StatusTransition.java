package com.jakubeeee.tasks.impl.status;

import com.jakubeeee.tasks.status.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StatusTransition {

    TaskStatus statusFrom;
    TaskStatus statusTo;

}
