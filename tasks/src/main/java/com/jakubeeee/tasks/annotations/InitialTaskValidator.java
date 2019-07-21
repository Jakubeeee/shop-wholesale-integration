package com.jakubeeee.tasks.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation used on definitions of task validation functions that allows to call them when registering a new task.
 *
 * @see com.jakubeeee.tasks.validators.TaskValidator
 * @see com.jakubeeee.tasks.service.TaskService#validateUsingSpecificTaskValidators
 */
@Target(FIELD)
@Retention(RUNTIME)
@Documented
public @interface InitialTaskValidator {
}
