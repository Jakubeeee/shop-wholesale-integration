package com.jakubeeee.integration.config;

import com.jakubeeee.core.service.RestService;
import com.jakubeeee.integration.enums.DataSourceType;
import com.jakubeeee.integration.enums.ProductMappingKey;
import com.jakubeeee.integration.service.BasicXmlDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

import static com.jakubeeee.integration.enums.DataSourceType.WAREHOUSE;
import static com.jakubeeee.integration.enums.ProductMappingKey.NAME;

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
                return Set.of(NAME);
            }
        };
    }
}
