package com.jakubeeee.integration.validators;

import com.jakubeeee.common.exception.UnexpectedClassStructureException;
import com.jakubeeee.integration.enums.DataSourceType;
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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static com.jakubeeee.common.util.ReflectUtils.getMethod;
import static com.jakubeeee.core.util.BeanUtils.getBean;
import static com.jakubeeee.integration.enums.DataSourceType.SHOP_PLATFORM;
import static com.jakubeeee.integration.enums.DataSourceType.WAREHOUSE;
import static com.jakubeeee.integration.model.ProductsTask.UpdatableProperty.PRICE;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AllowedPriceUpdateConfigValidator implements TaskValidator {

    @Getter
    static AllowedPriceUpdateConfigValidator instance = new AllowedPriceUpdateConfigValidator();

    @Override
    public void validate(GenericTask validatedTask) throws InvalidTaskDefinitionException, UnexpectedClassStructureException {
        ProductsTask validatedProductsTask = (ProductsTask) validatedTask;
        if (validatedProductsTask.getUpdatableProperties().contains(PRICE)) {
            Class<? extends DataSource> dataSource = validatedProductsTask.getDataSourceImplementation();
            Class<? extends UpdatableDataSource> updatableDataSource = validatedProductsTask.getUpdatableDataSourceImplementation();
            try {
                Method dataSourceTypeGetter = getMethod(dataSource, "getType");
                Method updatableDataSourceTypeGetter = getMethod(updatableDataSource, "getType");
                var dataSourceType = (DataSourceType) dataSourceTypeGetter.invoke(getBean(dataSource), null);
                var updatableDataSourceType = (DataSourceType) updatableDataSourceTypeGetter.invoke(getBean(updatableDataSource), null);
                if (dataSourceType == WAREHOUSE && updatableDataSourceType == SHOP_PLATFORM)
                    throw new InvalidTaskDefinitionException("Price update from datasource that is a warehouse to " +
                            "datasource that is a shop platform is not supported. " +
                            "Incorrect configuration consists of following classes: \"" + dataSource + "\" -> \"" + updatableDataSource + "\"");
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                throw new UnexpectedClassStructureException("Either \"" + dataSource + "\" or \"" + updatableDataSource + "\" " +
                        "class structure is incorrect. It is impossible to access its types. " +
                        "Detailed message: \"" + e.getClass() + "\": " + e.getMessage());
            }
        }
    }

}
