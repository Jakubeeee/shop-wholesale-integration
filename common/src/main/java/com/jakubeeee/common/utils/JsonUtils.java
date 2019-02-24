package com.jakubeeee.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jakubeeee.common.model.MessageWrapper;
import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Optional;

import static com.jakubeeee.common.model.MessageWrapper.Action;

@UtilityClass
public class JsonUtils {

    private static ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    }

    public static <T> Optional<String> objectToJson(T object) {
        String json;
        try {
            json = objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            json = null;
        }
        return Optional.ofNullable(json);
    }

    public static Optional<Object> jsonToObject(String json) {
        Object object;
        try {
            object = objectMapper.readValue(json, Object.class);
        } catch (IOException e) {
            object = null;
        }
        return Optional.ofNullable(object);
    }

    public static <T> MessageWrapper wrap(T object, Action action) {
        return new MessageWrapper(object, action);
    }

}
