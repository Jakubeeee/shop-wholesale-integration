package com.jakubeeee.integration.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jakubeeee.integration.enums.ProductMappingKey;
import com.jakubeeee.integration.service.DataSource;
import com.jakubeeee.integration.service.UpdatableDataSource;
import com.jakubeeee.integration.validators.AllowedMappingKeysValidator;
import com.jakubeeee.integration.validators.AllowedPriceUpdateConfigValidator;
import com.jakubeeee.integration.validators.AllowedUpdatablePropertiesValidator;
import com.jakubeeee.integration.validators.ProductsTaskBasicParametersValidator;
import com.jakubeeee.tasks.GenericTask;
import com.jakubeeee.tasks.TaskMode;
import com.jakubeeee.tasks.provider.TaskProvider;
import com.jakubeeee.tasks.validation.InitialTaskValidator;
import com.jakubeeee.tasks.validation.TaskValidator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Slf4j
public class ProductsTask extends GenericTask {

    @AllArgsConstructor
    public enum UpdatableProperty {
        STOCK("STOCKPROP"), PRICE("PRICEPROP"), EAN("EANPROP");
        @Getter
        String code;
    }

    @JsonIgnore
    @Getter
    ProductMappingKey mappingKey;

    @JsonIgnore
    Class<? extends DataSource> dataSourceImplementation;

    @JsonIgnore
    Class<? extends UpdatableDataSource> updatableDataSourceImplementation;

    @JsonIgnore
    @Getter
    List<UpdatableProperty> updatableProperties;

    @JsonIgnore
    @InitialTaskValidator
    TaskValidator productsTaskBasicParametersValidator = ProductsTaskBasicParametersValidator.getInstance();

    @JsonIgnore
    @InitialTaskValidator
    TaskValidator allowedMappingKeysValidator = AllowedMappingKeysValidator.getInstance();

    @JsonIgnore
    @InitialTaskValidator
    TaskValidator allowedUpdatablePropertiesValidator = AllowedUpdatablePropertiesValidator.getInstance();

    @JsonIgnore
    @InitialTaskValidator
    TaskValidator allowedPriceUpdateConfigValidator = AllowedPriceUpdateConfigValidator.getInstance();

    public ProductsTask(long id, String code, TaskMode mode, long interval, long delay, TaskProvider taskService,
                        ProductMappingKey mappingKey,
                        Class<? extends DataSource> dataSourceImplementation,
                        Class<? extends UpdatableDataSource> updatableDataSourceImplementation,
                        List<UpdatableProperty> updatableProperties) {
        super(id, code, mode, interval, delay, taskService);
        this.mappingKey = mappingKey;
        this.dataSourceImplementation = dataSourceImplementation;
        this.updatableDataSourceImplementation = updatableDataSourceImplementation;
        this.updatableProperties = updatableProperties;
    }
}
