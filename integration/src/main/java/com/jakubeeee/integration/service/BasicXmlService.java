package com.jakubeeee.integration.service;

import com.jakubeeee.common.service.RestService;
import com.jakubeeee.integration.model.BasicXmlProduct;
import com.jakubeeee.integration.model.CommonProduct;
import org.dom4j.Document;
import org.dom4j.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.jakubeeee.common.utils.XmlUtils.*;

/**
 * Extended by classes used to extract data from simple xml rest web service.
 * Example xml with correct structure is published by /dummyProductsXml endpoint.
 * Xsd schema file location: schemas/basic_products_xml.xsd
 */
@Service
public abstract class BasicXmlService implements DataSource<BasicXmlProduct> {

    @Autowired
    RestService restService;

    @Override
    public List<BasicXmlProduct> getExternalProducts() {
        Document productsXml = getProductsXml();
        var basicXmlProductsList = new ArrayList<BasicXmlProduct>();
        for (Node node : toNodeList(productsXml, "/products/product")) {
            String index = getTextValue(node, "index");
            String name = getTextValue(node, "name");
            String availability = getTextValue(node, "availability");
            String brutto = getTextValue(node, "brutto");
            String ean = getTextValue(node, "ean");
            basicXmlProductsList.add(new BasicXmlProduct(index, name, availability, brutto, ean));
        }
        return basicXmlProductsList;
    }

    private Document getProductsXml() {
        String productsXmlAsString = restService.getString(getProductsXmlWebServiceUri());
        return stringToXml(productsXmlAsString).orElseThrow(
                () -> new RuntimeException("An error has occurred while reading products xml file"));
    }

    @Override
    public List<CommonProduct> convertToCommonProducts(List<BasicXmlProduct> externalProducts) {
        var commonProducts = new ArrayList<CommonProduct>();
        (externalProducts).forEach(externalProduct ->
                commonProducts.add(CommonProduct.builder()
                        .code(externalProduct.getIndex())
                        .name(externalProduct.getName())
                        .stock(externalProduct.getAvailability())
                        .price(externalProduct.getBrutto())
                        .ean(externalProduct.getEan())
                        .build()));
        return commonProducts;
    }

    protected abstract String getProductsXmlWebServiceUri();

}
