package com.jakubeeee.integration.service;

import com.jakubeeee.core.service.RestService;
import com.jakubeeee.integration.model.ExternalProduct;
import org.dom4j.Document;

import static com.jakubeeee.common.util.XmlUtils.stringToXml;

/**
 * Base for service beans used for extracting data from simple xml rest web service.
 */
public abstract class XmlDataSource<T extends ExternalProduct> implements DataSource<T> {

    private final RestService restService;

    public XmlDataSource(RestService restService) {
        this.restService = restService;
    }

    protected Document getProductsXml() {
        String productsXmlAsString = restService.getString(getProductsXmlWebServiceUri());
        return stringToXml(productsXmlAsString).orElseThrow(
                () -> new RuntimeException("An error has occurred while reading products xml file"));
    }

    protected abstract String getProductsXmlWebServiceUri();

}
