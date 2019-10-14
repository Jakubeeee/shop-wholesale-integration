package com.jakubeeee.integration.datasource;

import com.jakubeeee.core.RestService;
import com.jakubeeee.integration.product.BasicXmlProduct;
import com.jakubeeee.integration.product.CommonProduct;
import org.dom4j.Document;
import org.dom4j.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Base for service beans used for extracting data from simple xml rest web service that use common basic template.
 * Example xml with correct structure is published by /dummyProductsXml endpoint. Xsd schema file location:
 * schemas/basic_products_xml.xsd
 */
public abstract class BasicXmlDataSource extends XmlDataSource<BasicXmlProduct> {

    public BasicXmlDataSource(RestService restService) {
        super(restService);
    }

    @Override
    public List<BasicXmlProduct> getExternalProducts() {
        Document productsXml = getProductsXml();
        var productsList = new ArrayList<BasicXmlProduct>();
        for (Object object : productsXml.selectNodes("/products/product")) {
            var productNode = (Node) object;
            String index = productNode.selectSingleNode("index").getText();
            String name = productNode.selectSingleNode("name").getText();
            String availability = productNode.selectSingleNode("availability").getText();
            String ean = productNode.selectSingleNode("ean").getText();
            productsList.add(new BasicXmlProduct(index, name, availability, ean));
        }
        return productsList;
    }

    @Override
    public List<CommonProduct> convertToCommonProducts(List<BasicXmlProduct> externalProducts) {
        var commonProducts = new ArrayList<CommonProduct>();
        (externalProducts).forEach(externalProduct ->
                commonProducts.add(CommonProduct.builder()
                        .mappingKeyValue(externalProduct.getIndex())
                        .code(externalProduct.getIndex())
                        .name(externalProduct.getName())
                        .stock(externalProduct.getAvailability())
                        .ean(externalProduct.getEan())
                        .build()));
        return commonProducts;
    }

}
