package pl.jakubeeee.integration.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class LogMessage {

    public enum Type {
        ERROR, WARN, UPDATE, INFO, DEBUG
    }

    String message;
    Type type;
    String time;

}
