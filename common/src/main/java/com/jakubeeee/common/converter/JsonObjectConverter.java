package com.jakubeeee.common.converter;

import com.jakubeeee.common.exception.ConversionError;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Optional;

import static com.jakubeeee.common.util.JsonUtils.jsonToObject;
import static com.jakubeeee.common.util.JsonUtils.objectToJson;

/**
 * Class used to convert java objects to strings in json format and vice versa for JPA use.
 */
@Converter
public class JsonObjectConverter implements AttributeConverter<Object, String> {

    @Override
    public String convertToDatabaseColumn(Object object) {
        Optional<String> objectAsJsonO = objectToJson(object);
        return objectAsJsonO.orElseThrow(() -> new ConversionError("Cannot convert " +
                object + " to database column using: " + this.getClass().getName()));
    }

    @Override
    public Object convertToEntityAttribute(String json) {
        Optional<Object> jsonAsObjectO = jsonToObject(json);
        return jsonAsObjectO.orElseThrow(() -> new ConversionError("Cannot convert " +
                json + " to entity attribute using: " + this.getClass().getName()));
    }

}