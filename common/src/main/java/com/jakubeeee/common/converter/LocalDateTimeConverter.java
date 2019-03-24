package com.jakubeeee.common.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.time.LocalDateTime;

import static com.jakubeeee.common.util.DateTimeUtils.formatDateTime;
import static com.jakubeeee.common.util.DateTimeUtils.parseStringToDateTime;

/**
 * Class used to convert java {@link LocalDateTime} objects to properly
 * formatted strings and vice versa for JPA use.
 */
@Converter
public class LocalDateTimeConverter implements AttributeConverter<LocalDateTime, String> {

    @Override
    public String convertToDatabaseColumn(LocalDateTime localDateTime) {
        return formatDateTime(localDateTime);
    }

    @Override
    public LocalDateTime convertToEntityAttribute(String localDateTimeAsString) {
        return parseStringToDateTime(localDateTimeAsString);
    }

}
