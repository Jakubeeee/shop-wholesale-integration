package com.jakubeeee.testutils.factory;

import com.jakubeeee.testutils.model.TestSubject;
import lombok.experimental.UtilityClass;

import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Collections.unmodifiableSet;
import static java.util.Set.of;

/**
 * Factory class used to supply application tests with {@link TestSubject} objects.
 */
@UtilityClass
public final class TestSubjectFactory {

    private static final Set<TestSubject> cachedTestSubjects = unmodifiableSet(of(
            new TestSubject(1, "aaa", "bbb", "ccc"),
            new TestSubject(2, "bbb", "ccc", "ddd"),
            new TestSubject(3, "ccc", "ddd", "eee"),
            new TestSubject(4, "ddd", "eee", "fff"),
            new TestSubject(5, "eee", "fff", "ggg"),
            new TestSubject(6, "fff", "ggg", "hhh"),
            new TestSubject(7, "ggg", "hhh", "iii"),
            new TestSubject(8, "hhh", "iii", "jjj"),
            new TestSubject(9, "iii", "jjj", "kkk"),
            new TestSubject(10, "jjj", "kkk", "lll"),
            new TestSubject(11, "kkk", "lll", "mmm"),
            new TestSubject(12, "lll", "mmm", "nnn"),
            new TestSubject(13, "mmm", "nnn", "ooo"),
            new TestSubject(14, "nnn", "ooo", "ppp"),
            new TestSubject(15, "ooo", "ppp", "rrr"),
            new TestSubject(16, "ppp", "rrr", "sss"),
            new TestSubject(17, "rrr", "sss", "ttt"),
            new TestSubject(18, "sss", "ttt", "uuu"),
            new TestSubject(19, "ttt", "uuu", "www"),
            new TestSubject(20, "uuu", "www", "yyy")));

    public static TestSubject getTestSubject(int id) {
        if (id < 1 || id > cachedTestSubjects.size())
            throw new IllegalArgumentException("Tried to retrieve test subject with invalid id: " + id);
        return cachedTestSubjects
                .stream()
                .filter(subject -> subject.getUniqueId() == id)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("There is no registered test subject with given id: " + id));
    }

    public static Set<TestSubject> getTestSubjects(int startIndex, int endIndex) {
        if (startIndex < 1 || endIndex < 1 || endIndex > cachedTestSubjects.size() || endIndex < startIndex)
            throw new IllegalArgumentException("Tried to retrieve test subjects with invalid index filter: "
                    + "startIndex: " + startIndex + ", endIndex: " + endIndex);
        return cachedTestSubjects
                .stream()
                .filter(subject -> subject.getUniqueId() >= startIndex && subject.getUniqueId() <= endIndex)
                .collect(Collectors.toSet());
    }

}
