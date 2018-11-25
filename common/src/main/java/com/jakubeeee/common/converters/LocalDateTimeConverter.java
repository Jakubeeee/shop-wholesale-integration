package com.jakubeeee.common.converters;

import org.springframework.stereotype.Component;

import javax.persistence.AttributeConverter;
import java.time.LocalDateTime;

import static com.jakubeeee.common.utils.DateTimeUtils.formatDateTime;
import static com.jakubeeee.common.utils.DateTimeUtils.stringToTime;

@Component
public class LocalDateTimeConverter implements AttributeConverter<LocalDateTime, String> {

    @Override
    public String convertToDatabaseColumn(LocalDateTime localDateTime) {
        return formatDateTime(localDateTime);
    }

    @Override
    public LocalDateTime convertToEntityAttribute(String localDateTimeAsString) {
        return stringToTime(localDateTimeAsString);
    }

}
