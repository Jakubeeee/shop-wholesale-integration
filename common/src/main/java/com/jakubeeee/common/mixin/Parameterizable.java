package com.jakubeeee.common.mixin;

import lombok.NonNull;

import java.util.Map;

import static com.jakubeeee.common.util.CollectionUtils.mergeMaps;

/**
 * Interface implemented by model classes having the ability to possess additional parameters.
 */
public interface Parameterizable {

    default void addProperty(@NonNull String key) {
        addParam(key, "NONE");
    }

    default void addParam(@NonNull String key, @NonNull Object value) {
        getParams().put(key, value);
    }

    default boolean hasProperty(@NonNull String key) {
        return getParams().containsKey(key);
    }

    default Object getParam(@NonNull String key) {
        return getParams().get(key);
    }

    default Map<String, Object> mergeParamsWith(@NonNull Parameterizable otherParameterizable) {
        return mergeMaps(getParams(), otherParameterizable.getParams());
    }

    Map<String, Object> getParams();

}
