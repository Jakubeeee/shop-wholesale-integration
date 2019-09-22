package com.jakubeeee.integration.model;

import com.jakubeeee.common.Parameterizable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

import static com.jakubeeee.common.LangUtils.nvl;

/**
 * A model class whose instances are the basic elements used during product integration.
 */
@Slf4j
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class CommonProduct implements Comparable<CommonProduct>, Parameterizable {

    String mappingKeyValue;
    String code;
    String name;
    String stock;
    String priceBrutto;
    String ean;

    Map<String, Object> params;

    @Override
    public int compareTo(CommonProduct other) {
        return mappingKeyValue.compareTo(other.getMappingKeyValue());
    }

    @Override
    public Map<String, Object> getParams() {
        return nvl(params, () -> {
            params = new HashMap<>();
            return params;
        });
    }

}
