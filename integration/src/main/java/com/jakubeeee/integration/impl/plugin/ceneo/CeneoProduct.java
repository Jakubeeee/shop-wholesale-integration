package com.jakubeeee.integration.impl.plugin.ceneo;


import com.jakubeeee.integration.product.ExternalProduct;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class CeneoProduct implements ExternalProduct {

    String id;
    String name;
    String stock;
    String ean;

}
