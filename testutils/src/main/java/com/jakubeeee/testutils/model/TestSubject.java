package com.jakubeeee.testutils.model;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.Wither;

import static java.util.Comparator.comparing;

/**
 * Class whose instances are used as value containers during testing.
 */
@Value
@Wither
@AllArgsConstructor
public class TestSubject implements Comparable<TestSubject> {

    @NonNull
    private final int uniqueId;

    @NonNull
    private final String stringField1;

    @NonNull
    private final String stringField2;

    @NonNull
    private final String stringField3;

    public TestSubject(TestSubject subject) {
        uniqueId = subject.getUniqueId();
        stringField1 = subject.getStringField1();
        stringField2 = subject.getStringField2();
        stringField3 = subject.getStringField3();
    }

    @Override
    public int compareTo(TestSubject other) {
        return comparing(TestSubject::getStringField1)
                .thenComparing(TestSubject::getStringField2)
                .thenComparing(TestSubject::getStringField3)
                .compare(this, other);
    }
}
