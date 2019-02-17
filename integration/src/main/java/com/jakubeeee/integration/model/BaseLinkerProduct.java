package com.jakubeeee.integration.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BaseLinkerProduct implements ExternalProduct {

    @JsonProperty(value = "product_id")
    String id;

    @JsonProperty(value = "name")
    String name;

    @JsonProperty(value = "quantity")
    String quantity;

    @JsonProperty(value = "price_brutto")
    String priceBrutto;

    @JsonProperty(value = "ean")
    String ean;

}
