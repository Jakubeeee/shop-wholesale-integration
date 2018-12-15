package com.jakubeeee.tasks.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class LogParam {

    public enum Type {
        TEXT, CODE
    }

    String value;
    Type type;

}
