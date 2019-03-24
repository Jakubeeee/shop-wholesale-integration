package com.jakubeeee.integration.service;

import com.jakubeeee.core.service.RestService;
import com.jakubeeee.integration.enums.DataSourceType;
import com.jakubeeee.integration.enums.ProductMappingKey;
import com.jakubeeee.integration.model.BaseLinkerProduct;
import com.jakubeeee.integration.model.BaseLinkerProductsCollection;
import com.jakubeeee.integration.model.CommonProduct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.jakubeeee.common.util.StringUtils.removeLastChar;
import static com.jakubeeee.integration.enums.DataSourceType.SHOP_PLATFORM;
import static com.jakubeeee.integration.enums.ProductMappingKey.NAME;
import static com.jakubeeee.integration.model.ProductsTask.UpdatableProperty;
import static com.jakubeeee.integration.model.ProductsTask.UpdatableProperty.STOCK;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;
import static org.springframework.util.CollectionUtils.isEmpty;

@Slf4j
@Service
public class BaseLinkerDataSource implements UpdatableDataSource<BaseLinkerProduct> {

    @Autowired
    RestService restService;

    @Autowired
    BaseLinkerAuthService authService;

    @Value("${baseLinkerRequestUri}")
    String BASE_LINKER_REQUEST_URI;

    private int MAX_PRODUCTS_IN_UPDATE_REQUEST = 1000;

    private List<String> cachedProductStockJsonArrays = new ArrayList<>();

    @Getter
    private Set<ProductMappingKey> allowedProductMappingKeys;

    @Getter
    private Set<UpdatableProperty> allowedUpdatableProperties;

    @PostConstruct
    void initialize() {
        allowedProductMappingKeys = Set.of(NAME);
        allowedUpdatableProperties = Set.of(STOCK);
    }

    @Override
    public List<BaseLinkerProduct> getExternalProducts() {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        ResponseEntity<BaseLinkerProductsCollection> response = restService.postJsonObject(
                BASE_LINKER_REQUEST_URI,
                new HttpEntity<>(getPreparedRequestParams("getProductsList", "{\"storage_id\": 1}"), headers),
                BaseLinkerProductsCollection.class);
        return requireNonNull(response.getBody()).getProducts();
    }

    @Override
    public List<CommonProduct> convertToCommonProducts(List<BaseLinkerProduct> externalProducts) {
        var commonProducts = new ArrayList<CommonProduct>();
        externalProducts.forEach(externalProduct -> {
            CommonProduct commonProduct = CommonProduct.builder()
                    .mappingKeyValue(externalProduct.getName())
                    .name(externalProduct.getName())
                    .stock(externalProduct.getQuantity())
                    .priceBrutto(externalProduct.getPriceBrutto())
                    .ean(externalProduct.getEan())
                    .build();
            commonProduct.addParam("BASE_LINKER_ID", externalProduct.getId());
            commonProducts.add(commonProduct);
        });
        return commonProducts;
    }

    @Override
    public void handleSingleProductUpdate(CommonProduct commonProduct, List<UpdatableProperty> properties, boolean isTesting) {
        if (!isTesting) {
            for (var property : properties) {
                if (property == STOCK)
                    cacheProductStockJsonArray(commonProduct);
                else
                    sendProductUpdateRequest(property);
            }
        }
    }

    private void cacheProductStockJsonArray(CommonProduct commonProduct) {
        cachedProductStockJsonArrays.add("[" +
                commonProduct.getParam("BASE_LINKER_ID") +
                ", " +
                "0" +
                ", " +
                getPropertyValue(commonProduct, STOCK) +
                "]");
    }

    private String getPropertyValue(CommonProduct commonProduct, UpdatableProperty property) {
        switch (property) {
            case STOCK:
                return commonProduct.getStock();
            default:
                throw new IllegalArgumentException("Tried to use product property that is invalid: " + property);
        }
    }

    private void sendProductUpdateRequest(UpdatableProperty property) {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        restService.postJsonObject(BASE_LINKER_REQUEST_URI,
                new HttpEntity<>(getPreparedRequestParams(resolveBaseLinkerWebApiMethod(property),
                        getPreparedJsonParams(property)), headers), Void.class);
    }

    private LinkedMultiValueMap<String, String> getPreparedRequestParams(String webApiMethod, @Nullable String jsonParams) {
        var requestParams = new LinkedMultiValueMap<String, String>();
        requestParams.add("token", authService.getTokenValue());
        requestParams.add("method", webApiMethod);
        if (nonNull(jsonParams)) requestParams.add("parameters", jsonParams);
        return requestParams;
    }

    private String resolveBaseLinkerWebApiMethod(UpdatableProperty property) {
        switch (property) {
            case STOCK:
                return "updateProductsQuantity";
            default:
                throw new IllegalArgumentException("Tried to use product property that is invalid: " + property);
        }
    }

    private String getPreparedJsonParams(UpdatableProperty property) {
        switch (property) {
            case STOCK:
                var jsonParams = new StringBuilder("{\"storage_id\": 1, \"products\": [");
                for (var productStockJsonArray : cachedProductStockJsonArrays.stream()
                        .limit(MAX_PRODUCTS_IN_UPDATE_REQUEST).collect(Collectors.toList()))
                    jsonParams.append(productStockJsonArray).append(",");
                jsonParams = new StringBuilder(removeLastChar(jsonParams.toString()));
                jsonParams.append("]}");
                cachedProductStockJsonArrays.removeIf(
                        jsonArray -> cachedProductStockJsonArrays.indexOf(jsonArray) < MAX_PRODUCTS_IN_UPDATE_REQUEST);
                return jsonParams.toString();
            default:
                throw new IllegalArgumentException("Tried to use product property that is invalid: " + property);
        }
    }

    @Override
    public void afterHandlingFinishedAction() {
        if (!isEmpty(cachedProductStockJsonArrays))
            updateCachedProductsStocks();
    }

    private void updateCachedProductsStocks() {
        while (!isEmpty(cachedProductStockJsonArrays))
            sendProductUpdateRequest(STOCK);
    }

    @Override
    public String getServiceName() {
        return "BASE_LINKER";
    }

    @Override
    public DataSourceType getType() {
        return SHOP_PLATFORM;
    }
}
