package com.jakubeeee.integration.service;

import com.jakubeeee.common.exceptions.DummyServiceException;
import com.jakubeeee.common.misc.Reloadable;
import com.jakubeeee.common.misc.Switchable;
import com.jakubeeee.common.model.ChangeRegistry;
import com.jakubeeee.common.service.DummyService;
import com.jakubeeee.common.service.ImplementationSwitcher;
import com.jakubeeee.integration.model.CommonProduct;
import com.jakubeeee.integration.model.ExternalProduct;
import com.jakubeeee.integration.model.ProductMatchingResult;
import com.jakubeeee.integration.model.ProductsTask;
import com.jakubeeee.tasks.exceptions.InvalidTaskStatusException;
import com.jakubeeee.tasks.exceptions.ProgressTrackerNotActiveException;
import com.jakubeeee.tasks.service.AbstractGenericTaskProvider;
import lombok.AccessLevel;
import lombok.Synchronized;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.diffplug.common.base.Errors.rethrow;
import static com.jakubeeee.common.utils.LangUtils.*;
import static com.jakubeeee.common.utils.ThreadUtils.sleep;
import static com.jakubeeee.integration.model.ProductsTask.UpdatableProperty;
import static com.jakubeeee.integration.model.ProductsTask.UpdatableProperty.*;
import static com.jakubeeee.tasks.enums.TaskMode.TESTING;
import static java.util.Collections.sort;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class ProductsTaskProvider extends AbstractGenericTaskProvider<ProductsTask> implements Reloadable {

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
    @SuppressWarnings("unchecked")
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
        super.afterTask(caller);
        switchImplementations(DummyUpdatableDataSource.class, DummyDataSource.class);
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

    @SuppressWarnings("unchecked")
    private void updateProducts(ProductsTask caller) throws ProgressTrackerNotActiveException {
        loggingService.info(caller.getId(), "DOWNPRODINFO", toList(updatableDataSource.getServiceName()));
        List<? extends ExternalProduct> externalShopProducts = updatableDataSource.getExternalProducts();
        List<CommonProduct> commonShopProducts = updatableDataSource.convertToCommonProducts(externalShopProducts);
        loggingService.info(caller.getId(), "DOWNPRODAMNT", toList(String.valueOf(commonShopProducts.size()),
                updatableDataSource.getServiceName()));
        executionParams.put("UPDS_PRODUCT_AMOUNT", toList(updatableDataSource.getServiceName(), commonShopProducts.size()));
        loggingService.info(caller.getId(), "DOWNPRODINFO", toList(dataSource.getServiceName()));
        List<? extends ExternalProduct> externalDataSourceProducts = dataSource.getExternalProducts();
        List<CommonProduct> commonDataSourceProducts = dataSource.convertToCommonProducts(externalDataSourceProducts);
        loggingService.info(caller.getId(), "DOWNPRODAMNT", toList(String.valueOf(commonDataSourceProducts.size()),
                dataSource.getServiceName()));
        executionParams.put("DS_PRODUCT_AMOUNT", toList(dataSource.getServiceName(), commonDataSourceProducts.size()));
        loggingService.info(caller.getId(), "STARTMATCHPRD", toList(updatableDataSource.getServiceName(),
                dataSource.getServiceName()));
        ProductMatchingResult result = matchProducts(commonShopProducts, commonDataSourceProducts);
        executionParams.put("PRODUCTS_MATCHED", toList(result.getMatchedProductsRegistry().size()));
        result.getUnmatchedUpdatableDataSourceProducts().forEach(product ->
                loggingService.warn(caller.getId(), "PRODNOTINDS", toList(product.getCode(),
                        updatableDataSource.getServiceName(), dataSource.getServiceName())));
        executionParams.put("NOT_MATCHED_UPDS_PRODUCTS_AMOUNT", toList(updatableDataSource.getServiceName(),
                result.getUnmatchedUpdatableDataSourceProducts().size()));
        result.getUnmatchedDataSourceProducts().forEach(product ->
                loggingService.warn(caller.getId(), "PRODNOTINDS", toList(product.getCode(),
                        dataSource.getServiceName(), updatableDataSource.getServiceName())));
        executionParams.put("NOT_MATCHED_DS_PRODUCTS_AMOUNT", toList(dataSource.getServiceName(),
                result.getUnmatchedDataSourceProducts().size()));
        progressTrackingService.setMaxProgress(caller, result.getMatchedProductsRegistry().size());
        result.getMatchedProductsRegistry().forEach(rethrow().wrap(registryElement -> {
            CommonProduct oldProduct = registryElement.getOldObject();
            CommonProduct updatedProduct = registryElement.getNewObject();
            List<UpdatableProperty> propertiesToUpdate =
                    getPreparedPropertiesToUpdate(cloneList(caller.getUpdatableProperties()), oldProduct, updatedProduct, caller.getId());
            if (!propertiesToUpdate.isEmpty()) {
                boolean isTesting = false;
                if (caller.getMode() == TESTING) isTesting = true;
                updatableDataSource.updateSingleProduct(updatedProduct, propertiesToUpdate, isTesting);
                List<String> logParameters = getPreparedUpdateLogParameters(propertiesToUpdate, oldProduct, updatedProduct);
                loggingService.update(caller.getId(), "PRODUPD", logParameters);
            }
            progressTrackingService.advanceProgress(caller);
            sleep(50);
        }));
    }

    private List<UpdatableProperty> getPreparedPropertiesToUpdate(
            List<UpdatableProperty> propertiesToUpdate, CommonProduct oldProduct, CommonProduct updatedProduct, Long taskId) {
        preparePropertyToUpdate(propertiesToUpdate, STOCK, oldProduct.getStock(), updatedProduct.getStock(),
                updatedProduct.getName(), taskId);
        preparePropertyToUpdate(propertiesToUpdate, PRICE, oldProduct.getPrice(), updatedProduct.getPrice(),
                updatedProduct.getName(), taskId);
        preparePropertyToUpdate(propertiesToUpdate, EAN, oldProduct.getEan(), updatedProduct.getEan(),
                updatedProduct.getName(), taskId);
        return propertiesToUpdate;
    }

    private void preparePropertyToUpdate(List<UpdatableProperty> allProperties, UpdatableProperty property,
                                         String oldValue, String newValue, String productName, Long taskId) {
        if (allProperties.contains(property) && oldValue.equals(newValue)) {
            loggingService.debug(taskId, "PROPNOTCHNG", toList(productName, property.getCode(), newValue));
            allProperties.remove(property);
        }
    }

    private List<String> getPreparedUpdateLogParameters(
            List<UpdatableProperty> propertiesToUpdate, CommonProduct oldProduct, CommonProduct updatedProduct) {
        var logParameters = new ArrayList<String>();
        logParameters.add(updatedProduct.getName());
        if (propertiesToUpdate.contains(STOCK)) {
            logParameters.add(STOCK.getCode());
            logParameters.add(oldProduct.getStock());
            logParameters.add(updatedProduct.getStock());
        }
        if (propertiesToUpdate.contains(PRICE)) {
            logParameters.add(PRICE.getCode());
            logParameters.add(oldProduct.getPrice());
            logParameters.add(updatedProduct.getPrice());
        }
        if (propertiesToUpdate.contains(EAN)) {
            logParameters.add(EAN.getCode());
            logParameters.add(oldProduct.getEan());
            logParameters.add(updatedProduct.getEan());
        }
        return logParameters;
    }

    public ProductMatchingResult matchProducts(
            List<CommonProduct> updatableDataSourceProducts, List<CommonProduct> dataSourceProducts) {
        var result = new ProductMatchingResult();
        sort(dataSourceProducts);
        for (var updatableDataSourceProduct : updatableDataSourceProducts) {
            findMatchInList(dataSourceProducts, updatableDataSourceProduct).ifPresent(dataSourceProduct -> {
                updatableDataSourceProduct.addProperty("MATCHED");
                CommonProduct mergedProduct = CommonProduct.builder()
                        .code(updatableDataSourceProduct.getCode())
                        .name(dataSourceProduct.getName())
                        .stock(dataSourceProduct.getStock())
                        .price(dataSourceProduct.getPrice())
                        .ean(dataSourceProduct.getEan())
                        .params(updatableDataSourceProduct.getMergedParams(dataSourceProduct))
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

    @Override
    public String getProviderName() {
        return PROVIDER_NAME;
    }
}
