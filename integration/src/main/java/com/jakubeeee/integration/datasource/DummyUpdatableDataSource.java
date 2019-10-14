package com.jakubeeee.integration.datasource;

import com.jakubeeee.core.DummyService;
import com.jakubeeee.integration.product.CommonProduct;
import com.jakubeeee.integration.product.DummyProduct;
import com.jakubeeee.integration.product.ProductMappingKey;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.jakubeeee.integration.datasource.DataSourceType.SHOP_PLATFORM;
import static com.jakubeeee.integration.product.ProductMappingKey.NAME;
import static com.jakubeeee.integration.product.ProductsTask.UpdatableProperty;
import static com.jakubeeee.integration.product.ProductsTask.UpdatableProperty.STOCK;
import static java.util.Collections.emptyList;

/**
 * Dummy service bean used as imitation of real updatable data source.
 */
@RequiredArgsConstructor
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
        return EnumSet.of(NAME);
    }
}
