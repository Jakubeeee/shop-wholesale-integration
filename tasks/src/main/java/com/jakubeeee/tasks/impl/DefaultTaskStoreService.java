package com.jakubeeee.tasks.impl;

import com.jakubeeee.tasks.GenericTask;
import com.jakubeeee.tasks.TaskStoreService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Default service bean used for operations related to task storing and retrieval.
 */
@RequiredArgsConstructor
@Service
public class DefaultTaskStoreService implements TaskStoreService {

    @Getter
    private List<GenericTask> storedTasks = new ArrayList<>();

    @Override
    public boolean isTaskIdUnique(long taskId) {
        return getTask(taskId).isEmpty();
    }

    @Override
    public boolean isTaskCodeUnique(String taskCode) {
        return getTask(taskCode).isEmpty();
    }

    @Override
    public Optional<GenericTask> getTask(long taskId) {
        GenericTask foundTask = null;
        for (var taskInLoop : storedTasks) {
            if (taskInLoop.getId() == taskId)
                foundTask = taskInLoop;
        }
        return Optional.ofNullable(foundTask);
    }

    @Override
    public Optional<GenericTask> getTask(String taskCode) {
        GenericTask foundTask = null;
        for (var taskInLoop : storedTasks) {
            if (taskInLoop.getCode().equals(taskCode))
                foundTask = taskInLoop;
        }
        return Optional.ofNullable(foundTask);
    }

}
