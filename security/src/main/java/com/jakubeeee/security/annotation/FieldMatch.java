package com.jakubeeee.security.annotation;

import com.jakubeeee.security.validator.FieldMatchValidator;
import org.springframework.validation.annotation.Validated;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation used on data transfer classes, whose objects are validated using
 * {@link Validated} annotation. It enables a check that two given fields have
 * the same value.
 */
@Target({TYPE, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = FieldMatchValidator.class)
public @interface FieldMatch {

    String message() default "{constraints.fieldmatch}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String first();

    String second();

    @Target({TYPE, ANNOTATION_TYPE})
    @Retention(RUNTIME)
    @interface List {

        FieldMatch[] value();

    }

}