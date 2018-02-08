package pl.jakubeeee.integration.services;

import lombok.AccessLevel;
import lombok.Synchronized;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import pl.jakubeeee.integration.domain.Product;
import pl.jakubeeee.integration.domain.ProductsCollection;
import pl.jakubeeee.integration.exceptions.ProductNotFoundInTheShopException;
import pl.jakubeeee.integration.exceptions.StockNotChangedException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static pl.jakubeeee.integration.utils.XmlUtils.*;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class ProductService {

    @Value("${shopProductStockUri}")
    String SHOP_PRODUCT_STOCK_URI;
    @Value("${shopAllProductsUri}")
    String SHOP_ALL_PRODUCTS_URI;
    @Value("${wholesaleAllProductsUri}")
    String WHOLESALE_PRODUCTS_XML_URI;

    @Autowired
    LoggingService loggingService;

    @Autowired
    RestService restService;

    @Autowired
    ProgressMonitorService progressMonitorService;

    private List<Product> getAllProducts() {
        List<Product> productList = new ArrayList<>();

        for (int i = 1; i <= getPageAmount(); i++)
            productList.addAll(getAllProductsFromPage(i));

        return productList;
    }

    private List<Product> getAllProductsFromPage(int page) {
        HttpHeaders headers = restService.generateHeaderWithAuthToken();
        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<ProductsCollection> response = restService.getJsonObject(
                SHOP_ALL_PRODUCTS_URI + "?limit=50&page=" + page,
                entity, ProductsCollection.class);

        return response.getBody().getList();
    }

    private int getPageAmount() {
        HttpHeaders headers = restService.generateHeaderWithAuthToken();
        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<ProductsCollection> response = restService.getJsonObject(
                SHOP_ALL_PRODUCTS_URI + "?limit=50",
                entity, ProductsCollection.class);

        return response.getBody().getPages();
    }

    @Synchronized
    public void updateAllProductsStocks() {
        try {
            loggingService.info("Preparing for update...");
            progressMonitorService.startProgress();
            Document productXml = getProductXml();
            List<Product> productsFromWholesale = getProductsFromXml(productXml);
            List<Product> productsFromShop = getAllProducts();
            progressMonitorService.getProgressMonitor().setMaxProgress(productsFromWholesale.size());

            loggingService.info("Searching for products to update...");
            for (Product productFromWholesale : productsFromWholesale) {
                try {
                    //Slowing it down on purpose, so the webservice won't produce '409 Calls limit exceeded' error
                    Thread.sleep(250);

                    matchProductFromShopAndWholesale(productFromWholesale, productsFromShop);

                } catch (InterruptedException e) {
                    loggingService.error("Thread error has occurred");
                    return;
                } catch (ProductNotFoundInTheShopException e2) {
                    loggingService.warn("Product '" + productFromWholesale.getName() + "' found in the wholesale, but not found in the shop");
                    progressMonitorService.advanceProgress();
                    continue;
                } catch (StockNotChangedException e3) {
                    loggingService.debug("Product '" + productFromWholesale.getName() + "' stock hasn't changed (Stock: "
                            + productFromWholesale.getStock() + ")");
                    progressMonitorService.advanceProgress();
                    continue;
                }

                updateProductStock(productFromWholesale.getId(), productFromWholesale.getStock());
                loggingService.update("Product '" + productFromWholesale.getName() + "' stock has been updated from " +
                        "" + productFromWholesale.getPreviousStock() + " to " + productFromWholesale.getStock());
                progressMonitorService.advanceProgress();
            }
            progressMonitorService.resetProgress();
            loggingService.info("Updating has finished successfully");
        } catch (HttpClientErrorException e) {
            progressMonitorService.resetProgress();
            loggingService.error("Authorization token has expired. Try again");
        } catch (ResourceAccessException e2) {
            progressMonitorService.resetProgress();
            loggingService.error("Connection problem. Try again later");
        }
    }

    private void updateProductStock(String id, String stock) {
        HttpHeaders headers = restService.generateHeaderWithAuthToken();
        Map<String, String> parameters = new HashMap<>();
        parameters.put("stock", stock);
        HttpEntity<?> entity = new HttpEntity<>(parameters, headers);
        restService.putJsonObject(SHOP_PRODUCT_STOCK_URI + "/" + id, entity, Void.class);
    }

    private void matchProductFromShopAndWholesale(Product productFromWholesale, List<Product> productsFromShop)
            throws StockNotChangedException, ProductNotFoundInTheShopException {
        for (Product productFromShop : productsFromShop) {
            if (productFromShop.getCode().equals(productFromWholesale.getCode())) {
                if (productFromShop.getStock().equals(productFromWholesale.getStock())) {
                    throw new StockNotChangedException("Product " + productFromWholesale.getName() + "Stock hasn't changed");
                }
                productFromWholesale.setId(productFromShop.getId());
                productFromWholesale.setPreviousStock(productFromShop.getStock());
                return;
            }
        }
        throw new ProductNotFoundInTheShopException("Product " + productFromWholesale.getName() + "not found in the shop");
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
