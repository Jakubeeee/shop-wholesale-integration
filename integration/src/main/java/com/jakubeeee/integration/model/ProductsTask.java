package com.jakubeeee.integration.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jakubeeee.integration.service.DataSource;
import com.jakubeeee.integration.service.UpdatableDataSource;
import com.jakubeeee.tasks.enums.TaskMode;
import com.jakubeeee.tasks.model.GenericTask;
import com.jakubeeee.tasks.service.TaskProviderInterface;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class ProductsTask extends GenericTask {

    @AllArgsConstructor
    public enum UpdatableProperty {
        STOCK("STOCKPROP"), PRICE("PRICEPROP"), EAN("EANPROP");
        @Getter
        String code;
    }

    @JsonIgnore
    Class<? extends DataSource> dataSourceImplementation;

    @JsonIgnore
    Class<? extends UpdatableDataSource> updatableDataSourceImplementation;

    @JsonIgnore
    @Getter
    List<UpdatableProperty> updatableProperties;

    public ProductsTask(long id, String code, TaskMode mode, long interval, long delay, TaskProviderInterface taskService,
                        Class<? extends DataSource> dataSourceImplementation,
                        Class<? extends UpdatableDataSource> updatableDataSourceImplementation,
                        List<UpdatableProperty> updatableProperties) {
        super(id, code, mode, interval, delay, taskService);
        this.dataSourceImplementation = dataSourceImplementation;
        this.updatableDataSourceImplementation = updatableDataSourceImplementation;
        this.updatableProperties = updatableProperties;
    }
}
