package com.jakubeeee.integration.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class AdditionalInfoContainer {

    public enum Type {
        LAST_UPDATE,
        PRODUCTS_UPDATED,
        NEXT_UPDATE,
        UPDATE_INTERVAL
    }
    
    String information;
    Type type;
}
