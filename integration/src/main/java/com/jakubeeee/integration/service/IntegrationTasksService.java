package com.jakubeeee.integration.service;

import com.jakubeeee.integration.model.ProductsTask;
import com.jakubeeee.tasks.model.GenericTask;
import com.jakubeeee.tasks.service.DummyTaskProvider;
import com.jakubeeee.tasks.service.SchedulingService;
import com.jakubeeee.tasks.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import static com.jakubeeee.common.utils.LangUtils.toList;
import static com.jakubeeee.integration.model.ProductsTask.UpdatableProperty.*;
import static com.jakubeeee.tasks.enums.TaskMode.TESTING;

@Service
public class IntegrationTasksService {

    @Autowired
    TaskService taskService;

    @Autowired
    SchedulingService schedulingService;

    @Autowired
    ProductsTaskProvider productsTaskProvider;

    @Autowired
    DummyTaskProvider dummyTaskProvider;

    @Autowired
    @Qualifier("dummyProductsService")
    BasicXmlService dummyProductsService;

    public void initializeTasks() {
        var dummyProductsUpdateTask = new ProductsTask(1, "DUMMY_PRODUCTS_UPDATE_TASK", TESTING, 0, 0,
                productsTaskProvider, dummyProductsService.getClass(), ShoperService.class, toList(STOCK));
        taskService.registerTask(dummyProductsUpdateTask);

        var dummyProductsUpdateTask2 = new ProductsTask(2, "DUMMY_PRODUCTS_UPDATE_TASK", TESTING, 0, 0,
                productsTaskProvider, dummyProductsService.getClass(), ShoperService.class, toList(STOCK, PRICE, EAN));
        taskService.registerTask(dummyProductsUpdateTask2);

        var dummyGenericTask = new GenericTask(3, "DUMMY_GENERIC_TASK", TESTING, 15, 60, dummyTaskProvider);
        taskService.registerTask(dummyGenericTask);

        var dummyGenericTask2 = new GenericTask(4, "DUMMY_GENERIC_TASK", TESTING, 30, 20, dummyTaskProvider);
        taskService.registerTask(dummyGenericTask2);
    }

}
