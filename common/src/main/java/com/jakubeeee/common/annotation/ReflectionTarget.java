package com.jakubeeee.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * Annotation used on classes and their members that are designed to be used with the reflection mechanism.
 * Suppresses the deceptive compiler warning that the class or its member is not in use.
 */
@SuppressWarnings("unused")
@Target({TYPE, METHOD, FIELD})
@Retention(SOURCE)
@Documented
public @interface ReflectionTarget {

}
