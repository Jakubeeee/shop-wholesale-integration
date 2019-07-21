package com.jakubeeee.security.validator;

import com.jakubeeee.security.annotation.FieldMatch;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static org.apache.commons.beanutils.BeanUtils.getProperty;

/**
 * Class providing validation logic enabled by {@link FieldMatch} annotation placed
 * in data transfer class. It checks that two given fields have the same value.
 */
public class FieldMatchValidator implements ConstraintValidator<FieldMatch, Object> {

    private String firstFieldName;
    private String secondFieldName;

    @Override
    public void initialize(final FieldMatch constraintAnnotation) {
        firstFieldName = constraintAnnotation.first();
        secondFieldName = constraintAnnotation.second();
    }

    @Override
    public boolean isValid(final Object value, final ConstraintValidatorContext context) {
        try {
            final Object firstProperty = getProperty(value, firstFieldName);
            final Object secondProperty = getProperty(value, secondFieldName);

            return firstProperty == null && secondProperty == null ||
                    firstProperty != null && firstProperty.equals(secondProperty);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Data transfer object does not contain proper accessor. " +
                    "Details: " + e.getCause());
        }
    }

}