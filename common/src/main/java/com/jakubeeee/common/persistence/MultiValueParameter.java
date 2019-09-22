package com.jakubeeee.common.persistence;

import lombok.Value;

import java.util.List;

/**
 * Immutable data transfer object that serves as a parameter holding an unique key and multiple values.
 */
@Value
public final class MultiValueParameter {

    private final String key;

    private final List<String> values;

    public MultiValueParameter(String key, List<String> values) {
        this.key = key;
        this.values = List.copyOf(values);
    }
}
