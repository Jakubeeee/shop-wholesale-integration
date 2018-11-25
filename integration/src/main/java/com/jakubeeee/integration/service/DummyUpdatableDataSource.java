package com.jakubeeee.integration.service;

import com.jakubeeee.common.service.DummyService;
import com.jakubeeee.integration.model.CommonProduct;
import com.jakubeeee.integration.model.DummyProduct;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.jakubeeee.integration.model.ProductsTask.UpdatableProperty;

@Service
public class DummyUpdatableDataSource implements UpdatableDataSource<DummyProduct>, DummyService {

    @Override
    public void updateSingleProduct(CommonProduct commonProducts, List<UpdatableProperty> properties, boolean isTesting){

    }

    @Override
    public List<DummyProduct> getExternalProducts() {
        return null;
    }

    @Override
    public List<CommonProduct> convertToCommonProducts(List<DummyProduct> externalProducts) {
        return null;
    }

    @Override
    public String getServiceName() {
        return "DUMMY_UPDS";
    }
}
