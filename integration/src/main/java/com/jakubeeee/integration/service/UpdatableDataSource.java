package com.jakubeeee.integration.service;

import com.jakubeeee.common.annotation.ReflectionTarget;
import com.jakubeeee.integration.model.CommonProduct;
import com.jakubeeee.integration.model.ExternalProduct;

import java.util.List;
import java.util.Set;

import static com.jakubeeee.integration.model.ProductsTask.UpdatableProperty;

/**
 * An extension of DataSource implemented by classes used to extract
 * or modify data from external sources
 */
public interface UpdatableDataSource<T extends ExternalProduct> extends DataSource<T> {

    @ReflectionTarget
    Set<UpdatableProperty> getAllowedUpdatableProperties();

    void handleSingleProductUpdate(CommonProduct commonProducts, List<UpdatableProperty> properties, boolean isTesting);

    void afterHandlingFinishedAction();
}
