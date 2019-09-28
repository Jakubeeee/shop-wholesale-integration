package com.jakubeeee.tasks.validation;

import com.jakubeeee.tasks.GenericTask;

/**
 * Interface for service beans used for operations related to initial task validation.
 */
public interface ValidationService {

    void validateUsingGenericTaskValidator(GenericTask task) throws InvalidTaskDefinitionException;

    void validateUsingSpecificTaskValidators(GenericTask task) throws InvalidTaskDefinitionException,
            IllegalAccessException;

}
