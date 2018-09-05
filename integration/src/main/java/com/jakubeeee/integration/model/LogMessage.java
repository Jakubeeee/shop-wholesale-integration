package com.jakubeeee.integration.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class LogMessage {

    public enum Type {
        ERROR, WARN, UPDATE, INFO, DEBUG
    }

    String code;
    Type type;
    String time;
    List<String> params;

}
