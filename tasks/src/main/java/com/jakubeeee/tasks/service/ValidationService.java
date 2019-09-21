package com.jakubeeee.tasks.service;

import com.jakubeeee.common.exception.UnexpectedClassStructureException;
import com.jakubeeee.tasks.exceptions.InvalidTaskDefinitionException;
import com.jakubeeee.tasks.model.GenericTask;

/**
 * Interface for service beans used for operations related to initial task validation.
 */
public interface ValidationService {

    void validateUsingGenericTaskValidator(GenericTask task) throws InvalidTaskDefinitionException;

    void validateUsingSpecificTaskValidators(GenericTask task) throws InvalidTaskDefinitionException,
            UnexpectedClassStructureException, IllegalAccessException;

}
