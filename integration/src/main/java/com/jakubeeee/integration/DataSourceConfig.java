package com.jakubeeee.integration;

import com.jakubeeee.core.RestService;
import com.jakubeeee.integration.datasource.BasicXmlDataSource;
import com.jakubeeee.integration.datasource.DataSourceType;
import com.jakubeeee.integration.product.ProductMappingKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.EnumSet;
import java.util.Set;

import static com.jakubeeee.integration.datasource.DataSourceType.WAREHOUSE;
import static com.jakubeeee.integration.product.ProductMappingKey.NAME;

@Configuration
public class DataSourceConfig {

    @Value("${dummyProductsXmlUri}")
    private String DUMMY_PRODUCTS_XML_URI;

    @Bean("dummyBasicXmlDataSource")
    public BasicXmlDataSource dummyBasicXmlDataSource(RestService restService) {
        return new BasicXmlDataSource(restService) {
            @Override
            protected String getProductsXmlWebServiceUri() {
                return DUMMY_PRODUCTS_XML_URI;
            }

            @Override
            public String getServiceName() {
                return "DUMMY_DATA_SOURCE";
            }

            @Override
            public DataSourceType getType() {
                return WAREHOUSE;
            }

            @Override
            public Set<ProductMappingKey> getAllowedProductMappingKeys() {
                return EnumSet.of(NAME);
            }
        };
    }
}
