package com.jakubeeee.integration.datasource;

import com.jakubeeee.common.reflection.ReflectionTarget;
import com.jakubeeee.core.Switchable;
import com.jakubeeee.integration.product.CommonProduct;
import com.jakubeeee.integration.product.ExternalProduct;
import com.jakubeeee.integration.product.ProductMappingKey;

import java.util.List;
import java.util.Set;

/**
 * Interface for service beans used for extracting data from external sources.
 */
public interface DataSource<T extends ExternalProduct> extends Switchable {

    List<T> getExternalProducts();

    List<CommonProduct> convertToCommonProducts(List<T> externalProducts);

    String getServiceName();

    DataSourceType getType();

    @ReflectionTarget
    Set<ProductMappingKey> getAllowedProductMappingKeys();

}
