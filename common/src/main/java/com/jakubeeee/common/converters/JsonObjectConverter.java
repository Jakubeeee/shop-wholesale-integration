package com.jakubeeee.common.converters;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Optional;

import static com.jakubeeee.common.utils.JsonUtils.jsonToObject;
import static com.jakubeeee.common.utils.JsonUtils.objectToJson;

@Converter
public class JsonObjectConverter implements AttributeConverter<Object, String> {

    @Override
    public String convertToDatabaseColumn(Object object) {
        Optional<String> objectAsJsonO = objectToJson(object);
        return objectAsJsonO.orElse("");
    }

    @Override
    public Object convertToEntityAttribute(String json) {
        Optional<Object> jsonAsObjectO = jsonToObject(json);
        return jsonAsObjectO.orElse(new Object());
    }

}