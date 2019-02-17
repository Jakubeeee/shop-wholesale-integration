package com.jakubeeee.tasks.validators;

import com.jakubeeee.common.exceptions.IncorrectClassStructureException;
import com.jakubeeee.tasks.exceptions.InvalidTaskDefinitionException;
import com.jakubeeee.tasks.model.GenericTask;

@FunctionalInterface
public interface TaskValidator {
    void validate(GenericTask validatedTask) throws InvalidTaskDefinitionException, IncorrectClassStructureException;
}