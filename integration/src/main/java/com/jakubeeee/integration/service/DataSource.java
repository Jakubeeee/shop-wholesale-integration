package com.jakubeeee.integration.service;

import com.jakubeeee.common.reflection.ReflectionTarget;
import com.jakubeeee.core.mixin.Switchable;
import com.jakubeeee.integration.enums.DataSourceType;
import com.jakubeeee.integration.enums.ProductMappingKey;
import com.jakubeeee.integration.model.CommonProduct;
import com.jakubeeee.integration.model.ExternalProduct;

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
