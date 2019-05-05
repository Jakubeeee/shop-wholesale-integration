package com.jakubeeee.integration.service;

import com.jakubeeee.common.model.ChangeRegistry;
import com.jakubeeee.core.exception.DummyServiceException;
import com.jakubeeee.core.mixin.Reloadable;
import com.jakubeeee.core.mixin.Switchable;
import com.jakubeeee.core.service.DummyService;
import com.jakubeeee.core.service.ImplementationSwitcher;
import com.jakubeeee.integration.enums.ProductMappingKey;
import com.jakubeeee.integration.model.CommonProduct;
import com.jakubeeee.integration.model.ExternalProduct;
import com.jakubeeee.integration.model.ProductMatchingResult;
import com.jakubeeee.integration.model.ProductsTask;
import com.jakubeeee.tasks.exceptions.InvalidTaskStatusException;
import com.jakubeeee.tasks.exceptions.ProgressTrackerNotActiveException;
import com.jakubeeee.tasks.model.LogParam;
import com.jakubeeee.tasks.service.AbstractGenericTaskProvider;
import lombok.AccessLevel;
import lombok.Synchronized;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.diffplug.common.base.Errors.rethrow;
import static com.jakubeeee.common.util.CollectionUtils.*;
import static com.jakubeeee.integration.enums.ProductMappingKey.CODE;
import static com.jakubeeee.integration.enums.ProductMappingKey.NAME;
import static com.jakubeeee.integration.model.ProductsTask.UpdatableProperty;
import static com.jakubeeee.integration.model.ProductsTask.UpdatableProperty.*;
import static com.jakubeeee.tasks.enums.TaskMode.TESTING;
import static com.jakubeeee.tasks.utils.LogParamsUtils.toLogParam;
import static java.util.Collections.sort;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class ProductsTaskProvider extends AbstractGenericTaskProvider<ProductsTask> implements Reloadable {

    @Lazy
    @Autowired
    ImplementationSwitcher implementationSwitcher;

    UpdatableDataSource updatableDataSource;

    DataSource dataSource;

    private static final String PROVIDER_NAME = "PRODUCTS_TASK_PROVIDER";

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
        ProductMatchingResult result = matchProducts(commonUpdatableDataSourceProducts, commonDataSourceProducts, caller.getMappingKey());
        executionParams.put("PRODUCTS_MATCHED", List.of(result.getMatchedProductsRegistry().size()));
        result.getUnmatchedUpdatableDataSourceProducts().forEach(product ->
                loggingService.warn(caller.getId(), "PRODNOTINDS", List.of(toLogParam(product.getMappingKeyValue()),
                        toLogParam(updatableDataSource.getServiceName()), toLogParam(dataSource.getServiceName()))));
        executionParams.put("NOT_MATCHED_UPDS_PRODUCTS_AMOUNT", List.of(updatableDataSource.getServiceName(),
                result.getUnmatchedUpdatableDataSourceProducts().size()));
        result.getUnmatchedDataSourceProducts().forEach(product ->
                loggingService.warn(caller.getId(), "PRODNOTINDS", List.of(toLogParam(product.getMappingKeyValue()),
                        toLogParam(dataSource.getServiceName()), toLogParam(updatableDataSource.getServiceName()))));
        executionParams.put("NOT_MATCHED_DS_PRODUCTS_AMOUNT", List.of(dataSource.getServiceName(),
                result.getUnmatchedDataSourceProducts().size()));
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
                List<LogParam> logParameters = getPreparedUpdateLogParams(propertiesToUpdate, oldProduct, updatedProduct);
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
                List.of(toLogParam(String.valueOf(commonDataSourceProducts.size())), toLogParam(dataSource.getServiceName())));
        String executionParamCode = dataSource instanceof UpdatableDataSource ? "UPDS_PRODUCT_AMOUNT" : "DS_PRODUCT_AMOUNT";
        executionParams.put(executionParamCode, List.of(dataSource.getServiceName(), commonDataSourceProducts.size()));
        return commonDataSourceProducts;
    }

    private List<UpdatableProperty> getPreparedPropertiesToUpdate(
            List<UpdatableProperty> propertiesToUpdate, CommonProduct oldProduct, CommonProduct updatedProduct, Long taskId) {
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
            List<CommonProduct> updatableDataSourceProducts, List<CommonProduct> dataSourceProducts, ProductMappingKey mappingKey) {
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
                result.getMatchedProductsRegistry().add(new ChangeRegistry<>(updatableDataSourceProduct, mergedProduct));
                dataSourceProducts.remove(dataSourceProduct);
            });
        }
        updatableDataSourceProducts = filterList(updatableDataSourceProducts, (product -> (!product.hasProperty("MATCHED"))));
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
