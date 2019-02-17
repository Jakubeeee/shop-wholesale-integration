package com.jakubeeee.common.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * Annotation used on classes, methods or fields that are designed to use with the reflection mechanism
 */

@SuppressWarnings("unused")
@Target({TYPE, METHOD, FIELD})
@Retention(SOURCE)
public @interface ReflectionTarget {
}
