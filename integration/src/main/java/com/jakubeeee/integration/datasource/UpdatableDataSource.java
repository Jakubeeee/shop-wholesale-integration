package com.jakubeeee.integration.datasource;

import com.jakubeeee.common.reflection.ReflectionTarget;
import com.jakubeeee.integration.product.CommonProduct;
import com.jakubeeee.integration.product.ExternalProduct;

import java.util.List;
import java.util.Set;

import static com.jakubeeee.integration.product.ProductsTask.UpdatableProperty;

/**
 * Interface that extends the functionality of {@link DataSource} with ability to modify data from external sources.
 */
public interface UpdatableDataSource<T extends ExternalProduct> extends DataSource<T> {

    @ReflectionTarget
    Set<UpdatableProperty> getAllowedUpdatableProperties();

    void handleSingleProductUpdate(CommonProduct commonProducts, List<UpdatableProperty> properties, boolean isTesting);

    void afterHandlingFinishedAction();
}
