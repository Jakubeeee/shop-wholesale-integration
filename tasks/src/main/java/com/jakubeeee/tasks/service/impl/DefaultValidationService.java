package com.jakubeeee.tasks.service.impl;

import com.jakubeeee.common.exception.UnexpectedClassStructureException;
import com.jakubeeee.tasks.annotations.InitialTaskValidator;
import com.jakubeeee.tasks.exceptions.InvalidTaskDefinitionException;
import com.jakubeeee.tasks.model.GenericTask;
import com.jakubeeee.tasks.service.TaskRegistryService;
import com.jakubeeee.tasks.service.ValidationService;
import com.jakubeeee.tasks.validators.TaskValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.List;

import static com.jakubeeee.common.util.ReflectUtils.getFieldValue;
import static java.util.Objects.isNull;
import static org.apache.commons.lang3.reflect.FieldUtils.getFieldsListWithAnnotation;

/**
 * Default service bean used for operations related to initial task validation.
 */
@RequiredArgsConstructor
@Service
public class DefaultValidationService implements ValidationService {

    private final TaskRegistryService taskRegistryService;

    @Override
    public void validateUsingGenericTaskValidator(GenericTask task) throws InvalidTaskDefinitionException {
        if (task.getId() <= 0)
            throw new InvalidTaskDefinitionException("Task id must be a positive number");
        if (!taskRegistryService.isTaskIdUnique(task.getId()))
            throw new InvalidTaskDefinitionException("Another task is already registered with given id: " + task.getId());
        if (isNull(task.getCode()))
            throw new InvalidTaskDefinitionException("Task code must not be null");
        if (!taskRegistryService.isTaskCodeUnique(task.getCode()))
            throw new InvalidTaskDefinitionException("Another task is already registered with given code: " + task.getCode());
        if (isNull(task.getMode()))
            throw new InvalidTaskDefinitionException("Task mode must not be null");
        if (task.getIntervalInMillis() < 0 || task.getDelayInMillis() < 0)
            throw new InvalidTaskDefinitionException("Task interval and delay must be either 0 or positive");
        if (task.getIntervalInMillis() == 0 && task.getDelayInMillis() > 0)
            throw new InvalidTaskDefinitionException("If task interval is 0, its delay must also be 0");
        if (isNull(task.getTaskProvider()))
            throw new InvalidTaskDefinitionException("Task provider must not be null");
    }

    @Override
    public void validateUsingSpecificTaskValidators(GenericTask task)
            throws InvalidTaskDefinitionException, UnexpectedClassStructureException, IllegalAccessException {
        List<Field> taskValidatorFields = getFieldsListWithAnnotation(task.getClass(), InitialTaskValidator.class);
        if (!taskValidatorFields.isEmpty()) {
            for (var taskValidatorField : taskValidatorFields) {
                TaskValidator taskSpecificCorrectnessValidator = getFieldValue(taskValidatorField, task,
                        TaskValidator.class);
                taskSpecificCorrectnessValidator.validate(task);
            }
        } else {
            // skip additional validation if this task does not have any specific validator
        }
    }

}
