package com.jakubeeee.integration.config;

import com.jakubeeee.integration.service.BasicXmlService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSourceConfig {

    @Value("${dummyProductsXmlUri}")
    private String DUMMY_PRODUCTS_XML_URI;

    @Bean("dummyProductsService")
    public BasicXmlService dummyProductsService() {
        return new BasicXmlService() {
            @Override
            protected String getProductsXmlWebServiceUri() {
                return DUMMY_PRODUCTS_XML_URI;
            }
            @Override
            public String getServiceName() {
                return "DUMMY DATA SOURCE";
            }
        };
    }

}
