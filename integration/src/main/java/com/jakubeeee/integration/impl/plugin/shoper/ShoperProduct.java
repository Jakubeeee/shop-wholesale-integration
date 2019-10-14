package com.jakubeeee.integration.impl.plugin.shoper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jakubeeee.integration.product.ExternalProduct;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShoperProduct implements ExternalProduct {

    @JsonProperty(value = "product_id")
    String id;

    @JsonProperty(value = "code")
    String code;

    @JsonProperty(value = "stock")
    StockDetails stockDetails;

    @JsonProperty(value = "price")
    String price;

    @JsonProperty(value = "ean")
    String ean;

    public String getProductStock() {
        return stockDetails.getStock();
    }

    public String getProductPrice() {
        return stockDetails.getPrice();
    }

    @Data
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    private class StockDetails {

        @JsonProperty(value = "stock")
        String stock;

        @JsonProperty(value = "price")
        String price;

    }
}

