package com.jakubeeee.integration.service;

import com.jakubeeee.common.misc.Switchable;
import com.jakubeeee.integration.model.CommonProduct;
import com.jakubeeee.integration.model.ExternalProduct;

import java.util.List;

/**
 * Service Interface implemented by classes used to extract data from external sources
 */
public interface DataSource<T extends ExternalProduct> extends Switchable {

    List<T> getExternalProducts();

    List<CommonProduct> convertToCommonProducts(List<T> externalProducts);

    String getServiceName();

}