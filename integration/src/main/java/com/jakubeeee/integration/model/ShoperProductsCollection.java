package com.jakubeeee.integration.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShoperProductsCollection {

    @JsonProperty(value = "count")
    int count;

    @JsonProperty(value = "pages")
    int pages;

    @JsonProperty(value = "page")
    int page;

    @JsonProperty(value = "list")
    List<ShoperProduct> list;

}
