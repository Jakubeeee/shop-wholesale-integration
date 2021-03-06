package com.jakubeeee.integration.impl.plugin.ceneo;

import com.jakubeeee.core.RestService;
import com.jakubeeee.integration.datasource.XmlDataSource;
import com.jakubeeee.integration.product.CommonProduct;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Base for service beans used for extracting data from ceneo based xml rest web service.
 * Xsd schema file location: schemas/ceneo_products_xml.xsd
 */
public abstract class CeneoDataSource extends XmlDataSource<CeneoProduct> {

    public CeneoDataSource(RestService restService) {
        super(restService);
    }

    @Override
    public List<CeneoProduct> getExternalProducts() {
        Document productsXml = getProductsXml();
        var productsList = new ArrayList<CeneoProduct>();
        for (Object object : productsXml.selectNodes("/offers/o")) {
            var offerNode = (Node) object;
            String id = ((Element) offerNode).attributeValue("id");
            String name = offerNode.selectSingleNode("name").getText();
            String stock = ((Element) offerNode).attributeValue("stock");
            String ean = null;
            Node attrsNode = offerNode.selectSingleNode("attrs");
            Element attrsElement = (Element) attrsNode;
            Iterator iterator = attrsElement.elementIterator("a");
            while (iterator.hasNext()) {
                var aElement = (Element) iterator.next();
                if (aElement.attributeValue("name").equals("EAN"))
                    ean = aElement.getText();
            }
            productsList.add(new CeneoProduct(id, name, stock, ean));
        }
        return productsList;
    }


    @Override
    public List<CommonProduct> convertToCommonProducts(List<CeneoProduct> externalProducts) {
        var commonProducts = new ArrayList<CommonProduct>();
        (externalProducts).forEach(externalProduct ->
                commonProducts.add(CommonProduct.builder()
                        .mappingKeyValue(externalProduct.getName())
                        .name(externalProduct.getName())
                        .stock(externalProduct.getStock())
                        .ean(externalProduct.getEan())
                        .build()));
        return commonProducts;
    }

}
