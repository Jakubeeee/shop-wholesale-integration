package com.jakubeeee.integration.impl.validator;

import com.jakubeeee.integration.product.ProductsTask;
import com.jakubeeee.tasks.GenericTask;
import com.jakubeeee.tasks.validation.InvalidTaskDefinitionException;
import com.jakubeeee.tasks.validation.TaskValidator;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static java.util.Objects.isNull;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductsTaskBasicParametersValidator implements TaskValidator {

    @Getter
    static ProductsTaskBasicParametersValidator instance = new ProductsTaskBasicParametersValidator();

    @Override
    public void validate(GenericTask validatedTask) throws InvalidTaskDefinitionException {
        ProductsTask validatedProductsTask = (ProductsTask) validatedTask;
        if (isNull(validatedProductsTask.getDataSourceImplementation()))
            throw new InvalidTaskDefinitionException("Task data source must not be null");
        if (isNull(validatedProductsTask.getUpdatableDataSourceImplementation()))
            throw new InvalidTaskDefinitionException("Task updatable data source must not be null");
        if (isNull(validatedProductsTask.getUpdatableProperties()) || validatedProductsTask.getUpdatableProperties().isEmpty())
            throw new InvalidTaskDefinitionException("Task updatable properties list must not be null nor empty");
    }

}
