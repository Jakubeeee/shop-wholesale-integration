package com.jakubeeee.integration.controller;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.jakubeeee.common.utils.DateTimeUtils.formatDateTime;
import static java.time.LocalDateTime.now;
import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;

@RestController
public class IntegrationController {

    @GetMapping(path = "dummyProductsXml", produces = {APPLICATION_XML_VALUE})
    public String getDummyProductsXml() {
        Document document = DocumentHelper.createDocument();
        Element productsElement = document.addElement("products").addAttribute("created_at", formatDateTime(now()));
        Element productElement = productsElement.addElement("product");
        productElement.addElement("index").addText("prod-2");
        productElement.addElement("availability").addText("50");
        productElement.addElement("name").addText("Product 1");
        productElement.addElement("tax").addText("23");
        productElement.addElement("netto").addText("1.62");
        productElement.addElement("brutto").addText("1.99");
        productElement.addElement("ean").addText("1234567898765");
        productElement = productsElement.addElement("product");
        productElement.addElement("index").addText("prod-2");
        productElement.addElement("availability").addText("120");
        productElement.addElement("name").addText("Product 2");
        productElement.addElement("tax").addText("23");
        productElement.addElement("netto").addText("2.43");
        productElement.addElement("brutto").addText("2.99");
        productElement.addElement("ean").addText("98765432123456");
        return document.asXML();
    }

}
