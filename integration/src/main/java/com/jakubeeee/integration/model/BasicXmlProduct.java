package com.jakubeeee.integration.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class BasicXmlProduct implements ExternalProduct {

    String index;
    String name;
    String availability;
    String brutto;
    String ean;

}
