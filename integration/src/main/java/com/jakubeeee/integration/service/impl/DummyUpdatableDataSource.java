package com.jakubeeee.integration.service.impl;

import com.jakubeeee.core.service.DummyService;
import com.jakubeeee.integration.enums.DataSourceType;
import com.jakubeeee.integration.enums.ProductMappingKey;
import com.jakubeeee.integration.model.CommonProduct;
import com.jakubeeee.integration.model.DummyProduct;
import com.jakubeeee.integration.service.UpdatableDataSource;
import lombok.Getter;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.jakubeeee.integration.enums.DataSourceType.SHOP_PLATFORM;
import static com.jakubeeee.integration.enums.ProductMappingKey.NAME;
import static com.jakubeeee.integration.model.ProductsTask.UpdatableProperty;
import static com.jakubeeee.integration.model.ProductsTask.UpdatableProperty.STOCK;
import static java.util.Collections.emptyList;

/**
 * Dummy service bean used as imitation of real updatable data source.
 */
@Service
public class DummyUpdatableDataSource implements UpdatableDataSource<DummyProduct>, DummyService {

    @Getter
    private Set<UpdatableProperty> allowedUpdatableProperties = new HashSet<>();

    @PostConstruct
    void initialize() {
        allowedUpdatableProperties.add(STOCK);
    }

    @Override
    public void handleSingleProductUpdate(CommonProduct commonProducts, List<UpdatableProperty> properties, boolean isTesting) {
        // dummy stub with no implementation
    }

    @Override
    public void afterHandlingFinishedAction() {
        // dummy stub with no implementation
    }

    @Override
    public List<DummyProduct> getExternalProducts() {
        return emptyList();
    }

    @Override
    public List<CommonProduct> convertToCommonProducts(List<DummyProduct> externalProducts) {
        return emptyList();
    }

    @Override
    public String getServiceName() {
        return "DUMMY_UPDATABLE_DATA_SOURCE";
    }

    @Override
    public DataSourceType getType() {
        return SHOP_PLATFORM;
    }

    @Override
    public Set<ProductMappingKey> getAllowedProductMappingKeys() {
        return Set.of(NAME);
    }
}
