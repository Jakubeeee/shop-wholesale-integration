package com.jakubeeee.testutils.factory;

import com.jakubeeee.testutils.marker.BehaviourUnitTest;
import com.jakubeeee.testutils.model.TestSubject;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.Set;

import static com.jakubeeee.testutils.factory.TestSubjectFactory.getTestSubject;
import static com.jakubeeee.testutils.factory.TestSubjectFactory.getTestSubjects;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@Category(BehaviourUnitTest.class)
public class TestSubjectFactoryTest {

    @Test
    public void getTestSubjectTest_shouldGet() {
        var expectedResult = new TestSubject(1, "aaa", "bbb", "ccc");
        var result = getTestSubject(1);
        assertThat(result, is(equalTo(expectedResult)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getTestSubjectTest_shouldThrowException_1() {
        getTestSubject(-5);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getTestSubjectTest_shouldThrowException_2() {
        getTestSubject(25);
    }

    @Test
    public void getTestSubjectsTest_shouldGet() {
        var expectedResult = Set.of(
                new TestSubject(1, "aaa", "bbb", "ccc"),
                new TestSubject(2, "bbb", "ccc", "ddd"),
                new TestSubject(3, "ccc", "ddd", "eee"),
                new TestSubject(4, "ddd", "eee", "fff"),
                new TestSubject(5, "eee", "fff", "ggg"));
        var result = getTestSubjects(1, 5);
        assertThat(result, is(equalTo(expectedResult)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getTestSubjectsTest_shouldThrowException_1() {
        getTestSubjects(-5, 5);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getTestSubjectsTest_shouldThrowException_2() {
        getTestSubjects(5, 25);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getTestSubjectsTest_shouldThrowException_3() {
        getTestSubjects(10, 5);
    }

}
