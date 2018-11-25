package com.jakubeeee.integration.service;

import com.jakubeeee.common.service.RestService;
import com.jakubeeee.integration.model.CommonProduct;
import com.jakubeeee.integration.model.ShoperProduct;
import com.jakubeeee.integration.model.ShoperProductsCollection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jakubeeee.integration.model.ProductsTask.UpdatableProperty;
import static java.util.Objects.requireNonNull;

@Slf4j
@Service
public class ShoperService implements UpdatableDataSource<ShoperProduct> {

    final String SERVICE_NAME = "SHOPER";

    @Autowired
    RestService restService;

    @Autowired
    ShoperAuthenticationService authenticationService;

    @Value("${shoperProductStockUri}")
    String SHOPER_PRODUCT_STOCK_URI;

    @Value("${shoperAllProductsUri}")
    String SHOPER_ALL_PRODUCTS_URI;

    @Override
    public List<ShoperProduct> getExternalProducts() {
        return getProductsFromAllPages(getPageAmount());
    }

    private List<ShoperProduct> getProductsFromAllPages(int pageAmount) {
        List<ShoperProduct> productList = new ArrayList<>();
        for (int i = 1; i <= pageAmount; i++) {
            productList.addAll(getAllProductsFromPage(i));
        }
        return productList;
    }

    private List<ShoperProduct> getAllProductsFromPage(int page) {
        HttpHeaders headers = restService.generateHeaderWithAuthToken(authenticationService.getTokenValue());
        ResponseEntity<ShoperProductsCollection> response = restService.getJsonObject(
                SHOPER_ALL_PRODUCTS_URI + "?limit=50&page=" + page,
                new HttpEntity<>(headers), ShoperProductsCollection.class);
        return requireNonNull(response.getBody()).getList();
    }

    private int getPageAmount() {
        HttpHeaders headers = restService.generateHeaderWithAuthToken(authenticationService.getTokenValue());
        ResponseEntity<ShoperProductsCollection> response = restService.getJsonObject(
                SHOPER_ALL_PRODUCTS_URI + "?limit=50",
                new HttpEntity<>(headers), ShoperProductsCollection.class);
        return requireNonNull(response.getBody()).getPages();
    }

    @Override
    public List<CommonProduct> convertToCommonProducts(List<ShoperProduct> externalProducts) {
        var commonProducts = new ArrayList<CommonProduct>();
        externalProducts.forEach(externalProduct -> {
            CommonProduct commonProduct = CommonProduct.builder()
                    .code(externalProduct.getCode())
                    .stock(externalProduct.getProductStock())
                    .price(externalProduct.getProductPrice())
                    .ean(externalProduct.getEan())
                    .build();
            commonProduct.addParam("SHOPER_ID", externalProduct.getId());
            commonProducts.add(commonProduct);
        });
        return commonProducts;
    }

    @Override
    public void updateSingleProduct(CommonProduct commonProduct, List<UpdatableProperty> properties, boolean isTesting) {
        if (!isTesting) {
            HttpHeaders headers = restService.generateHeaderWithAuthToken(authenticationService.getTokenValue());
            var requestParams = getRequestParametersForUpdate(commonProduct, properties);
            restService.putJsonObject(
                    SHOPER_PRODUCT_STOCK_URI + "/" + commonProduct.getParam("SHOPER_ID"),
                    new HttpEntity<>(requestParams, headers), Void.class);
        }
    }

    private Map<String, String> getRequestParametersForUpdate(CommonProduct commonProduct, List<UpdatableProperty> properties) {
        var requestParams = new HashMap<String, String>();
        for (var property : properties) {
            switch (property) {
                case STOCK:
                    requestParams.put("stock", commonProduct.getStock());
                case PRICE:
                    requestParams.put("price", commonProduct.getPrice());
                case EAN:
                    requestParams.put("ean", commonProduct.getEan());
            }
        }
        return requestParams;
    }

    @Override
    public String getServiceName() {
        return SERVICE_NAME;
    }
}
