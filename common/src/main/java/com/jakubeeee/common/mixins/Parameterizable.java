package com.jakubeeee.common.mixins;

import lombok.NonNull;

import java.util.Map;

import static com.jakubeeee.common.utils.LangUtils.mergeMaps;

/**
 * An interface implemented by model classes having the ability to possess additional parameters
 */
public interface Parameterizable {

    default void addProperty(@NonNull String key) {
        addParam(key, "NONE");
    }

    default void addParam(@NonNull String key, @NonNull Object value) {
        getParams().put(key, value);
    }

    default boolean hasProperty(String key) {
        return getParams().containsKey(key);
    }

    default Object getParam(String key) {
        return getParams().get(key);
    }

    default Map<String, Object> getMergedParams(Parameterizable otherParameterizable) {
        return mergeMaps(getParams(), otherParameterizable.getParams());
    }

    Map<String, Object> getParams();

}
