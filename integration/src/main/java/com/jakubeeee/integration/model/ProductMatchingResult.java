package com.jakubeeee.integration.model;

import com.jakubeeee.common.ChangeRegistry;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductMatchingResult {

    List<ChangeRegistry<CommonProduct>> matchedProductsRegistry = new ArrayList<>();
    List<CommonProduct> unmatchedUpdatableDataSourceProducts = new ArrayList<>();
    List<CommonProduct> unmatchedDataSourceProducts = new ArrayList<>();

}
