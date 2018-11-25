package com.jakubeeee.integration.service;

import com.jakubeeee.integration.model.CommonProduct;
import com.jakubeeee.integration.model.ExternalProduct;

import java.util.List;

import static com.jakubeeee.integration.model.ProductsTask.UpdatableProperty;

/**
 * An extension of DataSource implemented by classes used to extract
 * or modify data from external sources
 */
public interface UpdatableDataSource<T extends ExternalProduct> extends DataSource<T> {

    void updateSingleProduct(CommonProduct commonProducts, List<UpdatableProperty> properties, boolean isTesting);

}
