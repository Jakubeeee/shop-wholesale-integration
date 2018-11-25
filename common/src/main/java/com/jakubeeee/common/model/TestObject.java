package com.jakubeeee.common.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import static java.util.Comparator.comparing;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class TestObject implements Comparable<TestObject> {

    String stringField;
    String anotherStringField;
    int intField;

    @Override
    public int compareTo(TestObject other) {
        return comparing(TestObject::getStringField)
                .thenComparingInt(TestObject::getIntField)
                .thenComparing(TestObject::getAnotherStringField)
                .compare(this, other);
    }

}
