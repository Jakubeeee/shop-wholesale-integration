package com.jakubeeee.tasks.service.impl;

import com.jakubeeee.tasks.enums.TaskStatus;
import com.jakubeeee.tasks.exceptions.InvalidTaskStatusException;
import com.jakubeeee.tasks.model.GenericTask;
import com.jakubeeee.tasks.model.StatusTransition;
import com.jakubeeee.tasks.service.StatusService;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

import static com.jakubeeee.tasks.enums.TaskStatus.*;

/**
 * Default service bean used for operations related to task status management.
 */
@Service
public class DefaultStatusService implements StatusService {

    private static Set<StatusTransition> allowedStatusTransitions = new HashSet<>();

    static {
        allowedStatusTransitions.add(new StatusTransition(WAITING, LAUNCHED));
        allowedStatusTransitions.add(new StatusTransition(LAUNCHED, PREPARED));
        allowedStatusTransitions.add(new StatusTransition(PREPARED, EXECUTED));
        allowedStatusTransitions.add(new StatusTransition(PREPARED, ABORTED));
        allowedStatusTransitions.add(new StatusTransition(EXECUTED, WAITING));
        allowedStatusTransitions.add(new StatusTransition(ABORTED, WAITING));
    }

    @Override
    public void changeStatus(GenericTask task, TaskStatus statusTo) throws InvalidTaskStatusException {
        TaskStatus statusFrom = task.getStatus();
        var statusTransition = new StatusTransition(statusFrom, statusTo);
        if (isTransitionAllowed(statusTransition))
            task.setStatus(statusTo);
        else
            throw new InvalidTaskStatusException("Invalid status transition. From " + statusFrom + " to " + statusTo);
    }

    private boolean isTransitionAllowed(StatusTransition statusTransition) {
        for (var allowedStatusTransition : allowedStatusTransitions) {
            if (statusTransition.getStatusFrom() == allowedStatusTransition.getStatusFrom()
                    && statusTransition.getStatusTo() == allowedStatusTransition.getStatusTo())
                return true;
        }
        return false;
    }

}
