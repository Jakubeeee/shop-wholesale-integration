package com.jakubeeee.integration;

import com.jakubeeee.integration.datasource.BasicXmlDataSource;
import com.jakubeeee.integration.impl.plugin.shoper.ShoperDataSource;
import com.jakubeeee.integration.product.ProductsTask;
import com.jakubeeee.integration.product.ProductsTaskProvider;
import com.jakubeeee.tasks.GenericTask;
import com.jakubeeee.tasks.SchedulingService;
import com.jakubeeee.tasks.TaskRegistryService;
import com.jakubeeee.tasks.provider.DummyTaskProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;

import java.util.List;

import static com.jakubeeee.integration.product.ProductMappingKey.NAME;
import static com.jakubeeee.integration.product.ProductsTask.UpdatableProperty.STOCK;
import static com.jakubeeee.tasks.TaskMode.TESTING;

@Configuration
public class IntegrationTasksConfig {

    private final TaskRegistryService taskRegistryService;

    private final SchedulingService schedulingService;

    private final ProductsTaskProvider productsTaskProvider;

    private final DummyTaskProvider dummyTaskProvider;

    private final BasicXmlDataSource dummyBasicXmlDataSource;

    public IntegrationTasksConfig(TaskRegistryService taskRegistryService, SchedulingService schedulingService,
                                  ProductsTaskProvider productsTaskProvider, DummyTaskProvider dummyTaskProvider,
                                  @Qualifier("dummyBasicXmlDataSource") BasicXmlDataSource dummyBasicXmlDataSource) {
        this.taskRegistryService = taskRegistryService;
        this.schedulingService = schedulingService;
        this.productsTaskProvider = productsTaskProvider;
        this.dummyTaskProvider = dummyTaskProvider;
        this.dummyBasicXmlDataSource = dummyBasicXmlDataSource;
    }

    public void initializeTasks() {
        var dummyProductsUpdateTask = new ProductsTask(1, "DUMMY_PRODUCTS_UPDATE_TASK", TESTING, 0, 0,
                productsTaskProvider, NAME, dummyBasicXmlDataSource.getClass(), ShoperDataSource.class, List.of(STOCK));
        taskRegistryService.registerTask(dummyProductsUpdateTask);

        var dummyGenericTask = new GenericTask(3, "DUMMY_GENERIC_TASK", TESTING, 15, 60, dummyTaskProvider);
        schedulingService.scheduleTask(dummyGenericTask);
        taskRegistryService.registerTask(dummyGenericTask);

        var dummyGenericTask2 = new GenericTask(4, "DUMMY_GENERIC_TASK2", TESTING, 30, 20, dummyTaskProvider);
        schedulingService.scheduleTask(dummyGenericTask2);
        taskRegistryService.registerTask(dummyGenericTask2);

    }


}
