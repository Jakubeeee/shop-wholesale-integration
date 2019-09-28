package com.jakubeeee.tasks.logging;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class LogMessage {

    public enum Type {
        ERROR, WARN, UPDATE, INFO, DEBUG
    }

    private long taskId;
    private String code;
    private Type type;
    private String time;
    private List<LogParam> params;
    private int dynamicParts;

}
