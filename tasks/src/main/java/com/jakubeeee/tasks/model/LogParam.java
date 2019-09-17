package com.jakubeeee.tasks.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LogParam {

    public enum Type {
        TEXT, CODE
    }

    private String value;
    private Type type;

}
