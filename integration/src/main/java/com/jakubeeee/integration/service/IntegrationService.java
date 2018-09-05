package com.jakubeeee.integration.service;

import com.jakubeeee.common.service.RestService;
import com.jakubeeee.integration.exceptions.ProductNotFoundInTheShopException;
import com.jakubeeee.integration.exceptions.StockNotChangedException;
import com.jakubeeee.integration.model.Product;
import com.jakubeeee.integration.model.ProductsCollection;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Synchronized;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jakubeeee.common.utils.LangUtils.list;
import static com.jakubeeee.common.utils.XmlUtils.*;
import static java.time.LocalDateTime.now;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class IntegrationService {

    @Value("${shopProductStockUri}")
    String SHOP_PRODUCT_STOCK_URI;
    @Value("${shopAllProductsUri}")
    String SHOP_ALL_PRODUCTS_URI;
    @Value("${wholesaleAllProductsUri}")
    String WHOLESALE_PRODUCTS_XML_URI;

    @Autowired
    LoggingService loggingService;

    @Autowired
    ProgressMonitorService progressMonitorService;

    @Autowired
    AdditionalInfoService additionalInfoService;

    @Autowired
    RestService restService;

    @Autowired
    AuthenticationService authenticationService;

    @Getter
    LocalDateTime lastUpdateDateTime;

    @Getter
    int productsUpdatedInLastUpdate;

    private List<Product> getAllProducts() {
        List<Product> productList = new ArrayList<>();

        for (int i = 1; i <= getPageAmount(); i++)
            productList.addAll(getAllProductsFromPage(i));

        return productList;
    }

    private List<Product> getAllProductsFromPage(int page) {
        HttpHeaders headers = restService.generateHeaderWithAuthToken(authenticationService.getToken());
        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<ProductsCollection> response = restService.getJsonObject(
                SHOP_ALL_PRODUCTS_URI + "?limit=50&page=" + page,
                entity, ProductsCollection.class);

        return response.getBody().getList();
    }

    private int getPageAmount() {
        HttpHeaders headers = restService.generateHeaderWithAuthToken(authenticationService.getToken());
        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<ProductsCollection> response = restService.getJsonObject(
                SHOP_ALL_PRODUCTS_URI + "?limit=50",
                entity, ProductsCollection.class);

        return response.getBody().getPages();
    }

    @Synchronized
    public void updateAllProductsStocks() {
        try {
            loggingService.info("PREPUPD");
            productsUpdatedInLastUpdate = 0;
            progressMonitorService.trackProgress();
            Document productXml = getProductXml();
            List<Product> productsFromWholesale = getProductsFromXml(productXml);
            List<Product> productsFromShop = getAllProducts();
            progressMonitorService.getProgressMonitor().setMaxProgress(productsFromWholesale.size());

            loggingService.info("LOOKFORPROD");
            for (Product productFromWholesale : productsFromWholesale) {
                try {
                    //Slowing it down on purpose, so the webservice won't produce '409 Calls limit exceeded' error
                    Thread.sleep(250);

                    matchProductFromShopAndWholesale(productFromWholesale, productsFromShop);

                } catch (InterruptedException e) {
                    loggingService.error("THREADERR");
                    return;
                } catch (ProductNotFoundInTheShopException e2) {
                    loggingService.warn("PRODNOTINSHP", list(productFromWholesale.getName()));
                    progressMonitorService.advanceProgress();
                    continue;
                } catch (StockNotChangedException e3) {
                    loggingService.debug("STCKNOTCHNG", list(productFromWholesale.getName(),
                            productFromWholesale.getProductStock()));
                    progressMonitorService.advanceProgress();
                    continue;
                }

                updateProductStock(productFromWholesale.getId(), productFromWholesale.getProductStock());
                loggingService.update("PRODUPD", list(productFromWholesale.getName(),
                        productFromWholesale.getPreviousStock(), productFromWholesale.getProductStock()));
                progressMonitorService.advanceProgress();
            }
            loggingService.info("UPDSUCCESS", list(String.valueOf(productsUpdatedInLastUpdate)));
        } catch (HttpClientErrorException e) {
            loggingService.error("AUTHTOKENEXPRD");
        } catch (ResourceAccessException e2) {
            loggingService.error("CONNPROB");
        } finally {
            lastUpdateDateTime = now();
            additionalInfoService.updateAdditionalInfos();
            progressMonitorService.resetProgress();
        }
    }

    private void matchProductFromShopAndWholesale(Product productFromWholesale, List<Product> productsFromShop)
            throws StockNotChangedException, ProductNotFoundInTheShopException {
        for (Product productFromShop : productsFromShop) {
            if (productFromShop.getCode().equals(productFromWholesale.getCode())) {
                if (productFromShop.getProductStock().equals(productFromWholesale.getProductStock())) {
                    throw new StockNotChangedException("Product " + productFromWholesale.getName() + "Stock hasn't changed");
                }
                productFromWholesale.setId(productFromShop.getId());
                productFromWholesale.setPreviousStock(productFromShop.getProductStock());
                return;
            }
        }
        throw new ProductNotFoundInTheShopException("Product " + productFromWholesale.getName() + "not found in the shop");
    }

    private void updateProductStock(String id, String stock) {
        HttpHeaders headers = restService.generateHeaderWithAuthToken(authenticationService.getToken());
        Map<String, String> parameters = new HashMap<>();
        parameters.put("stock", stock);
        HttpEntity<?> entity = new HttpEntity<>(parameters, headers);
        restService.putJsonObject(SHOP_PRODUCT_STOCK_URI + "/" + id, entity, Void.class);
        productsUpdatedInLastUpdate++;
    }

    private Document getProductXml() {
        String productXmlAsString = restService.getString(WHOLESALE_PRODUCTS_XML_URI);
        return stringToXml(productXmlAsString);
    }

    private List<Product> getProductsFromXml(Document productXml) {
        List<Product> productList = new ArrayList<>();

        for (Node node : nodesToList(productXml, "/products/product")) {
            String index = getTextValue(node, "index");
            String name = getTextValue(node, "name");
            String availability = getTextValue(node, "availability");
            productList.add(new Product(index, name, availability));
        }
        return productList;
    }

}
