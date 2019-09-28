package com.jakubeeee.tasks.validation;

import com.jakubeeee.tasks.GenericTask;

@FunctionalInterface
public interface TaskValidator {

    void validate(GenericTask validatedTask) throws InvalidTaskDefinitionException;

}
