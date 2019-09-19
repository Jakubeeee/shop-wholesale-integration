package com.jakubeeee.integration.service.impl;

import com.jakubeeee.core.service.DummyService;
import com.jakubeeee.integration.enums.DataSourceType;
import com.jakubeeee.integration.enums.ProductMappingKey;
import com.jakubeeee.integration.model.CommonProduct;
import com.jakubeeee.integration.model.DummyProduct;
import com.jakubeeee.integration.service.DataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

import static com.jakubeeee.integration.enums.DataSourceType.WAREHOUSE;
import static com.jakubeeee.integration.enums.ProductMappingKey.NAME;
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
        return Set.of(NAME);
    }
}
