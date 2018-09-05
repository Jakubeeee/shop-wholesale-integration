package com.jakubeeee.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;

import java.util.Optional;

@UtilityClass
public class JsonUtils {

    public static <T> Optional<String> objectToJsonString(T object) {
        String jsonString = null;
        try {
            jsonString = new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException ignored) {
        }
        return Optional.ofNullable(jsonString);
    }
}
