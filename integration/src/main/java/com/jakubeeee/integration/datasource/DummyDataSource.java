package com.jakubeeee.integration.datasource;

import com.jakubeeee.core.DummyService;
import com.jakubeeee.integration.product.CommonProduct;
import com.jakubeeee.integration.product.DummyProduct;
import com.jakubeeee.integration.product.ProductMappingKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import static com.jakubeeee.integration.datasource.DataSourceType.WAREHOUSE;
import static com.jakubeeee.integration.product.ProductMappingKey.NAME;
import static java.util.Collections.emptyList;

/**
 * Dummy service bean used as imitation of real data source.
 */
@RequiredArgsConstructor
@Service
public class DummyDataSource implements DataSource<DummyProduct>, DummyService {

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
        return "DUMMY_DATA_SOURCE";
    }

    @Override
    public DataSourceType getType() {
        return WAREHOUSE;
    }

    @Override
    public Set<ProductMappingKey> getAllowedProductMappingKeys() {
        return EnumSet.of(NAME);
    }
}
