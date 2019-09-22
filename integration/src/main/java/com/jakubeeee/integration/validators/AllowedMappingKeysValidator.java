package com.jakubeeee.integration.validators;

import com.jakubeeee.common.reflection.UnexpectedClassStructureException;
import com.jakubeeee.integration.enums.ProductMappingKey;
import com.jakubeeee.integration.model.ProductsTask;
import com.jakubeeee.integration.service.DataSource;
import com.jakubeeee.integration.service.UpdatableDataSource;
import com.jakubeeee.tasks.exceptions.InvalidTaskDefinitionException;
import com.jakubeeee.tasks.model.GenericTask;
import com.jakubeeee.tasks.validators.TaskValidator;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.Set;

import static com.jakubeeee.common.reflection.ReflectUtils.getMethod;
import static com.jakubeeee.core.BeanUtils.getBean;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AllowedMappingKeysValidator implements TaskValidator {

    @Getter
    static AllowedMappingKeysValidator instance = new AllowedMappingKeysValidator();

    @Override
    public void validate(GenericTask validatedTask) throws InvalidTaskDefinitionException {
        ProductsTask validatedProductsTask = (ProductsTask) validatedTask;
        Class<? extends DataSource> dataSource = validatedProductsTask.getDataSourceImplementation();
        Class<? extends UpdatableDataSource> updatableDataSource =
                validatedProductsTask.getUpdatableDataSourceImplementation();
        try {
            Method dataSourceAllowedMappingKeysGetter = getMethod(dataSource, "getAllowedProductMappingKeys");
            Method updatableDataSourceAllowedMappingKeysGetter = getMethod(updatableDataSource,
                    "getAllowedProductMappingKeys");
            var dataSourceAllowedMappingKeys = (Set) dataSourceAllowedMappingKeysGetter.invoke(getBean(dataSource),
                    null);
            var updatableDataSourceAllowedMappingKeys =
                    (Set) updatableDataSourceAllowedMappingKeysGetter.invoke(getBean(updatableDataSource), null);
            ProductMappingKey validatedMappingKey = validatedProductsTask.getMappingKey();
            if (!dataSourceAllowedMappingKeys.contains(validatedMappingKey))
                throw new InvalidTaskDefinitionException(
                        "Data source \"" + dataSource + "\" does not allow to use mapping key: " + validatedMappingKey);
            if (!updatableDataSourceAllowedMappingKeys.contains(validatedMappingKey))
                throw new InvalidTaskDefinitionException(
                        "Updatable data source \"" + updatableDataSource + "\" does not allow to use mapping key: " + validatedMappingKey);
        } catch (ReflectiveOperationException e) {
            throw new UnexpectedClassStructureException("Either \"" + dataSource + "\" or \"" + updatableDataSource + "\" " +
                    "It is impossible to access its set of allowed mapping keys. " +
                    "Detailed message: \"" + e.getClass() + "\": \"" + e.getMessage());
        }
    }
}
