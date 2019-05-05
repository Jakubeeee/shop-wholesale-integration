package com.jakubeeee.integration.validators;

import com.jakubeeee.common.exception.UnexpectedClassStructureException;
import com.jakubeeee.integration.model.ProductsTask;
import com.jakubeeee.integration.service.UpdatableDataSource;
import com.jakubeeee.tasks.exceptions.InvalidTaskDefinitionException;
import com.jakubeeee.tasks.model.GenericTask;
import com.jakubeeee.tasks.validators.TaskValidator;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

import static com.jakubeeee.common.util.ReflectUtils.getMethod;
import static com.jakubeeee.core.util.BeanUtils.getBean;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AllowedUpdatablePropertiesValidator implements TaskValidator {

    @Getter
    static AllowedUpdatablePropertiesValidator instance = new AllowedUpdatablePropertiesValidator();

    @Override
    public void validate(GenericTask validatedTask) throws InvalidTaskDefinitionException, UnexpectedClassStructureException {
        ProductsTask validatedProductsTask = (ProductsTask) validatedTask;
        Class<? extends UpdatableDataSource> updatableDataSource = validatedProductsTask.getUpdatableDataSourceImplementation();
        try {
            Method allowedUpdatablePropertiesGetter = getMethod(updatableDataSource, "getAllowedUpdatableProperties");
            var allowedUpdatableProperties = (Set) allowedUpdatablePropertiesGetter.invoke(getBean(updatableDataSource), null);
            for (var property : validatedProductsTask.getUpdatableProperties())
                if (!allowedUpdatableProperties.contains(property))
                    throw new InvalidTaskDefinitionException("Updatable data source \"" +
                            updatableDataSource + "\" does not allow to update property: " + property);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new UnexpectedClassStructureException("The \"" + updatableDataSource + "\" class structure is incorrect. " +
                    "It is impossible to access its set of allowed updatable properties. " +
                    "Detailed message: \"" + e.getClass() + "\": \"" + e.getMessage());
        }
    }
}
