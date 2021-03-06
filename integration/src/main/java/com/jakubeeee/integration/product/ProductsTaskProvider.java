package com.jakubeeee.integration.product;

import com.jakubeeee.common.ChangeRegistry;
import com.jakubeeee.core.*;
import com.jakubeeee.integration.datasource.DataSource;
import com.jakubeeee.integration.datasource.DummyDataSource;
import com.jakubeeee.integration.datasource.DummyUpdatableDataSource;
import com.jakubeeee.integration.datasource.UpdatableDataSource;
import com.jakubeeee.tasks.LockingService;
import com.jakubeeee.tasks.TaskRegistryService;
import com.jakubeeee.tasks.logging.LogParam;
import com.jakubeeee.tasks.logging.LoggingService;
import com.jakubeeee.tasks.pasttaskexecution.PastTaskExecutionService;
import com.jakubeeee.tasks.progresstracking.ProgressTrackerNotActiveException;
import com.jakubeeee.tasks.progresstracking.ProgressTrackingService;
import com.jakubeeee.tasks.provider.AbstractGenericTaskProvider;
import com.jakubeeee.tasks.status.InvalidTaskStatusException;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.diffplug.common.base.Errors.rethrow;
import static com.jakubeeee.common.CollectionUtils.*;
import static com.jakubeeee.integration.product.ProductMappingKey.CODE;
import static com.jakubeeee.integration.product.ProductMappingKey.NAME;
import static com.jakubeeee.integration.product.ProductsTask.UpdatableProperty;
import static com.jakubeeee.integration.product.ProductsTask.UpdatableProperty.*;
import static com.jakubeeee.tasks.TaskMode.TESTING;
import static com.jakubeeee.tasks.logging.LogParamsUtils.toLogParam;
import static java.util.Collections.sort;

/**
 * Service bean used for providing business logic for tasks related to manipulation of products data between two
 * independent platforms.
 */
@Slf4j
@Service
public class ProductsTaskProvider extends AbstractGenericTaskProvider<ProductsTask> implements Reloadable {

    private static final String PROVIDER_NAME = "PRODUCTS_TASK_PROVIDER";

    private final ImplementationSwitcherService implementationSwitcher;

    private UpdatableDataSource updatableDataSource;

    private DataSource dataSource;

    public ProductsTaskProvider(TaskRegistryService taskRegistryService, LockingService lockingService,
                                ProgressTrackingService progressTrackingService, LoggingService loggingService,
                                PastTaskExecutionService pastTaskExecutionService,
                                @Lazy ImplementationSwitcherService implementationSwitcher) {
        super(taskRegistryService, lockingService, progressTrackingService, loggingService, pastTaskExecutionService);
        this.implementationSwitcher = implementationSwitcher;
    }

    @PostConstruct
    void initialize() {
        updatableDataSource = new DummyUpdatableDataSource();
        dataSource = new DummyDataSource();
    }

    @Override
    public void beforeTask(ProductsTask caller) throws DummyServiceException, InvalidTaskStatusException {
        super.beforeTask(caller);
        switchImplementations(
                caller.getUpdatableDataSourceImplementation(),
                caller.getDataSourceImplementation());
        if (updatableDataSource instanceof DummyService || dataSource instanceof DummyService)
            throw new DummyServiceException(
                    "One of " + this.getClass().getName() + " dependencies has a dummy service implementation");
    }

    @Synchronized
    @Override
    public void executeTask(ProductsTask caller) throws ProgressTrackerNotActiveException {
        updateProducts(caller);
    }

    @Override
    public void afterTask(ProductsTask caller) {
        switchImplementations(DummyUpdatableDataSource.class, DummyDataSource.class);
        super.afterTask(caller);
    }

    private void switchImplementations(Class<? extends Switchable> updatableDataSource,
                                       Class<? extends Switchable> dataSource) {
        var implementationTypesMap = new HashMap<String, Class<? extends Switchable>>();
        implementationTypesMap.put("UPDATABLE_DATA_SOURCE", updatableDataSource);
        implementationTypesMap.put("DATA_SOURCE", dataSource);
        implementationSwitcher.switchImplementations(PROVIDER_NAME, implementationTypesMap);
    }

    @Override
    public void reloadImplementations(String reloaderCode, Map<String, ? extends Switchable> implementationsMap) {
        if (reloaderCode.equals(PROVIDER_NAME)) {
            updatableDataSource = (UpdatableDataSource) implementationsMap.get("UPDATABLE_DATA_SOURCE");
            dataSource = (DataSource) implementationsMap.get("DATA_SOURCE");
        }
    }

    private void updateProducts(ProductsTask caller) throws ProgressTrackerNotActiveException {
        List<CommonProduct> commonUpdatableDataSourceProducts = getCommonProducts(caller.getId(), updatableDataSource);
        List<CommonProduct> commonDataSourceProducts = getCommonProducts(caller.getId(), dataSource);
        loggingService.info(caller.getId(), "STARTMATCHPRD",
                List.of(toLogParam(updatableDataSource.getServiceName()), toLogParam(dataSource.getServiceName())));
        ProductMatchingResult result = matchProducts(commonUpdatableDataSourceProducts, commonDataSourceProducts,
                caller.getMappingKey());
        addExecutionParam("PRODUCTS_MATCHED", String.valueOf(result.getMatchedProductsRegistry().size()));
        result.getUnmatchedUpdatableDataSourceProducts().forEach(product ->
                loggingService.warn(caller.getId(), "PRODNOTINDS", List.of(toLogParam(product.getMappingKeyValue()),
                        toLogParam(updatableDataSource.getServiceName()), toLogParam(dataSource.getServiceName()))));
        addExecutionParam("NOT_MATCHED_UPDS_PRODUCTS_AMOUNT", updatableDataSource.getServiceName(),
                String.valueOf(result.getUnmatchedUpdatableDataSourceProducts().size()));
        result.getUnmatchedDataSourceProducts().forEach(product ->
                loggingService.warn(caller.getId(), "PRODNOTINDS", List.of(toLogParam(product.getMappingKeyValue()),
                        toLogParam(dataSource.getServiceName()), toLogParam(updatableDataSource.getServiceName()))));
        addExecutionParam("NOT_MATCHED_DS_PRODUCTS_AMOUNT", dataSource.getServiceName(),
                String.valueOf(result.getUnmatchedDataSourceProducts().size()));
        progressTrackingService.setMaxProgress(caller, result.getMatchedProductsRegistry().size());

        result.getMatchedProductsRegistry().forEach(rethrow().wrap(registryElement -> {
            CommonProduct oldProduct = registryElement.getOldObject();
            CommonProduct updatedProduct = registryElement.getNewObject();
            List<UpdatableProperty> propertiesToUpdate =
                    getPreparedPropertiesToUpdate(shallowCloneList(caller.getUpdatableProperties()),
                            oldProduct, updatedProduct, caller.getId());
            if (!propertiesToUpdate.isEmpty()) {
                boolean isTesting = false;
                if (caller.getMode() == TESTING) isTesting = true;
                updatableDataSource.handleSingleProductUpdate(updatedProduct, propertiesToUpdate, isTesting);
                List<LogParam> logParameters = getPreparedUpdateLogParams(propertiesToUpdate, oldProduct,
                        updatedProduct);
                int updateLogDynamicParts = propertiesToUpdate.size() - 1;
                loggingService.update(caller.getId(), "PRODUPD", logParameters, updateLogDynamicParts);
            }
            progressTrackingService.advanceProgress(caller);
        }));
        updatableDataSource.afterHandlingFinishedAction();
    }

    @SuppressWarnings("unchecked")
    private List<CommonProduct> getCommonProducts(Long taskId, DataSource dataSource) {
        loggingService.info(taskId, "DOWNPRODINFO", List.of(toLogParam(dataSource.getServiceName())));
        List<? extends ExternalProduct> externalDataSourceProducts = dataSource.getExternalProducts();
        List<CommonProduct> commonDataSourceProducts = dataSource.convertToCommonProducts(externalDataSourceProducts);
        loggingService.info(taskId, "DOWNPRODAMNT",
                List.of(toLogParam(String.valueOf(commonDataSourceProducts.size())),
                        toLogParam(dataSource.getServiceName())));
        String executionParamCode = dataSource instanceof UpdatableDataSource ? "UPDS_PRODUCT_AMOUNT" :
                "DS_PRODUCT_AMOUNT";
        addExecutionParam(executionParamCode, dataSource.getServiceName(),
                String.valueOf(commonDataSourceProducts.size()));
        return commonDataSourceProducts;
    }

    private List<UpdatableProperty> getPreparedPropertiesToUpdate(
            List<UpdatableProperty> propertiesToUpdate, CommonProduct oldProduct, CommonProduct updatedProduct,
            Long taskId) {
        preparePropertyToUpdate(propertiesToUpdate, STOCK, oldProduct.getStock(), updatedProduct.getStock(),
                updatedProduct.getName(), taskId);
        preparePropertyToUpdate(propertiesToUpdate, PRICE, oldProduct.getPriceBrutto(), updatedProduct.getPriceBrutto(),
                updatedProduct.getName(), taskId);
        preparePropertyToUpdate(propertiesToUpdate, EAN, oldProduct.getEan(), updatedProduct.getEan(),
                updatedProduct.getName(), taskId);
        return propertiesToUpdate;
    }

    private void preparePropertyToUpdate(List<UpdatableProperty> allProperties, UpdatableProperty property,
                                         String oldValue, String newValue, String productName, Long taskId) {
        if (allProperties.contains(property) && oldValue.equals(newValue)) {
            loggingService.debug(taskId, "PROPNOTCHNG",
                    List.of(toLogParam(productName), toLogParam(property.getCode(), true), toLogParam(newValue)));
            allProperties.remove(property);
        }
    }

    private List<LogParam> getPreparedUpdateLogParams(
            List<UpdatableProperty> propertiesToUpdate, CommonProduct oldProduct, CommonProduct updatedProduct) {
        var logParameters = new ArrayList<LogParam>();
        logParameters.add(toLogParam(updatedProduct.getName()));
        if (propertiesToUpdate.contains(STOCK)) {
            logParameters.add(toLogParam(STOCK.getCode(), true));
            logParameters.add(toLogParam(oldProduct.getStock()));
            logParameters.add(toLogParam(updatedProduct.getStock()));
        }
        if (propertiesToUpdate.contains(PRICE)) {
            logParameters.add(toLogParam(PRICE.getCode(), true));
            logParameters.add(toLogParam(oldProduct.getPriceBrutto()));
            logParameters.add(toLogParam(updatedProduct.getPriceBrutto()));
        }
        if (propertiesToUpdate.contains(EAN)) {
            logParameters.add(toLogParam(EAN.getCode(), true));
            logParameters.add(toLogParam(oldProduct.getEan()));
            logParameters.add(toLogParam(updatedProduct.getEan()));
        }
        return logParameters;
    }

    private ProductMatchingResult matchProducts(
            List<CommonProduct> updatableDataSourceProducts, List<CommonProduct> dataSourceProducts,
            ProductMappingKey mappingKey) {
        var result = new ProductMatchingResult();
        prepareMappingKeyValues(dataSourceProducts, mappingKey);
        prepareMappingKeyValues(updatableDataSourceProducts, mappingKey);
        sort(dataSourceProducts);
        for (var updatableDataSourceProduct : updatableDataSourceProducts) {
            findMatchInList(dataSourceProducts, updatableDataSourceProduct).ifPresent(dataSourceProduct -> {
                updatableDataSourceProduct.addProperty("MATCHED");
                CommonProduct mergedProduct = CommonProduct.builder()
                        .mappingKeyValue(dataSourceProduct.getMappingKeyValue())
                        .code(dataSourceProduct.getCode())
                        .name(dataSourceProduct.getName())
                        .stock(dataSourceProduct.getStock())
                        .priceBrutto(dataSourceProduct.getPriceBrutto())
                        .ean(dataSourceProduct.getEan())
                        .params(dataSourceProduct.mergeParamsWith(updatableDataSourceProduct))
                        .build();
                result.getMatchedProductsRegistry().add(new ChangeRegistry<>(updatableDataSourceProduct,
                        mergedProduct));
                dataSourceProducts.remove(dataSourceProduct);
            });
        }
        updatableDataSourceProducts = filterList(updatableDataSourceProducts, (product -> (!product.hasProperty(
                "MATCHED"))));
        result.setUnmatchedUpdatableDataSourceProducts(updatableDataSourceProducts);
        result.setUnmatchedDataSourceProducts(dataSourceProducts);
        return result;
    }

    private void prepareMappingKeyValues(List<CommonProduct> products, ProductMappingKey mappingKey) {
        products.forEach(product -> {
            if (mappingKey == CODE)
                product.setMappingKeyValue(product.getCode());
            else if (mappingKey == NAME)
                product.setMappingKeyValue(product.getName());
        });
    }

    @Override
    public String getProviderName() {
        return PROVIDER_NAME;
    }
}
