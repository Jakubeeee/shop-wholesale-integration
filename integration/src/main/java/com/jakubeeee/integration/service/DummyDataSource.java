package com.jakubeeee.integration.service;

import com.jakubeeee.common.service.DummyService;
import com.jakubeeee.integration.model.CommonProduct;
import com.jakubeeee.integration.model.DummyProduct;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DummyDataSource implements DataSource<DummyProduct>, DummyService {

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
        return "DUMMY_DS";
    }
}
