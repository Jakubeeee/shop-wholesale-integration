package com.jakubeeee.common.serialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Optional;

import static com.jakubeeee.common.serialization.MessageWrapper.Action;
import static com.jakubeeee.common.DateTimeUtils.DATE_TIME_FORMAT;

/**
 * Utility class providing useful static methods related to parsing and processing of json files.
 */
@UtilityClass
public final class JsonUtils {

    private static ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(new SimpleDateFormat(DATE_TIME_FORMAT));
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
