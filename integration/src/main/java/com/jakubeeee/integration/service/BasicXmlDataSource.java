package com.jakubeeee.integration.service;

import com.jakubeeee.integration.model.BasicXmlProduct;
import com.jakubeeee.integration.model.CommonProduct;
import org.dom4j.Document;
import org.dom4j.Node;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Extended by classes used to extract data from simple xml rest web service.
 * Example xml with correct structure is published by /dummyProductsXml endpoint.
 * Xsd schema file location: schemas/basic_products_xml.xsd
 */
@Service
public abstract class BasicXmlDataSource extends XmlDataSource<BasicXmlProduct> {

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
