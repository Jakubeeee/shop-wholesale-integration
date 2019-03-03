package com.jakubeeee.testutils.utils;

import com.jakubeeee.testutils.model.TestSubject;
import lombok.experimental.UtilityClass;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Utility class providing useful static methods for operations on {@link TestSubject} objects.
 */
@UtilityClass
public final class TestSubjectUtils {

    private static Set<TestSubject> testSubjectSet;

    static {
        testSubjectSet = new HashSet<>();
        testSubjectSet.add(new TestSubject(1, "aaa", "bbb", "ccc"));
        testSubjectSet.add(new TestSubject(2, "bbb", "ccc", "ddd"));
        testSubjectSet.add(new TestSubject(3, "ccc", "ddd", "eee"));
        testSubjectSet.add(new TestSubject(4, "ddd", "eee", "fff"));
        testSubjectSet.add(new TestSubject(5, "eee", "fff", "ggg"));
        testSubjectSet.add(new TestSubject(6, "fff", "ggg", "hhh"));
        testSubjectSet.add(new TestSubject(7, "ggg", "hhh", "iii"));
        testSubjectSet.add(new TestSubject(8, "hhh", "iii", "jjj"));
        testSubjectSet.add(new TestSubject(9, "iii", "jjj", "kkk"));
        testSubjectSet.add(new TestSubject(10, "jjj", "kkk", "lll"));
        testSubjectSet.add(new TestSubject(11, "kkk", "lll", "mmm"));
        testSubjectSet.add(new TestSubject(12, "lll", "mmm", "nnn"));
        testSubjectSet.add(new TestSubject(13, "mmm", "nnn", "ooo"));
        testSubjectSet.add(new TestSubject(14, "nnn", "ooo", "ppp"));
        testSubjectSet.add(new TestSubject(15, "ooo", "ppp", "rrr"));
        testSubjectSet.add(new TestSubject(16, "ppp", "rrr", "sss"));
        testSubjectSet.add(new TestSubject(17, "rrr", "sss", "ttt"));
        testSubjectSet.add(new TestSubject(18, "sss", "ttt", "uuu"));
        testSubjectSet.add(new TestSubject(19, "ttt", "uuu", "www"));
        testSubjectSet.add(new TestSubject(20, "uuu", "www", "yyy"));
    }

    public static TestSubject getTestSubject(int id) {
        if (id < 1 || id > 20)
            throw new IllegalArgumentException("Tried to retrieve test subject with invalid id: " + id);
        return testSubjectSet
                .stream()
                .filter(subject -> subject.getUniqueId() == id)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("There is no registered test subject with given id: " + id));
    }

    public static Set<TestSubject> getTestSubjects(int startIndex, int endIndex) {
        if (startIndex < 1 || startIndex > 20 || endIndex < 1 || endIndex > 20)
            throw new IllegalArgumentException("Tried to retrieve test subjects with invalid index filter: "
                    + "startIndex: " + startIndex + ", endIndex: " + endIndex);
        return testSubjectSet
                .stream()
                .filter(subject -> subject.getUniqueId() >= startIndex && subject.getUniqueId() <= endIndex)
                .collect(Collectors.toSet());
    }

}
