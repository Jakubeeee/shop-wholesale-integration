package com.jakubeeee.common.utils;


import com.jakubeeee.testutils.model.TestSubject;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static com.jakubeeee.common.utils.LangUtils.*;
import static com.jakubeeee.testutils.utils.TestSubjectUtils.getTestSubject;
import static com.jakubeeee.testutils.utils.TestSubjectUtils.getTestSubjects;
import static java.util.Collections.sort;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@RunWith(SpringRunner.class)
public class LangUtilsTest {

    private static List<TestSubject> testSubjectList;
    private static List<TestSubject> sortedTestSubjectList;

    private final static int TEST_LIST_SIZE = 10;

    @BeforeClass
    public static void setUp() {
        testSubjectList = new ArrayList<>();
        testSubjectList.addAll(getTestSubjects(1, TEST_LIST_SIZE));
        sortedTestSubjectList = new ArrayList<>(testSubjectList);
        sort(sortedTestSubjectList);
    }

    @Test
    public void nvlTest_shouldReturnNullable() {
        var nullableObject = getTestSubject(1);
        var reserveObject = getTestSubject(2);
        var result = nvl(nullableObject, reserveObject);
        assertThat(result, is(equalTo(nullableObject)));
    }

    @Test
    public void nvlTest_shouldReturnReserve() {
        TestSubject nullableObject = null;
        var reserveObject = getTestSubject(1);
        var result = nvl(nullableObject, reserveObject);
        assertThat(result, is(equalTo(reserveObject)));
    }

    @Test
    public void filterListTest() {
        Predicate<TestSubject> predicate = testSubject -> testSubject.getUniqueId() > 3;
        List<TestSubject> localTestSubjectList = filterList(testSubjectList, predicate);
        assertThat(localTestSubjectList, everyItem(hasProperty("uniqueId", greaterThan(3))));
    }

    @Test
    public void findMatchInListTest_shouldFind() {
        var testSubject = getTestSubject(5);
        Optional<TestSubject> expectedResultO = Optional.of(testSubject);
        Optional<TestSubject> result = findMatchInList(testSubjectList, testSubject, true);
        assertThat(result, is(equalTo(expectedResultO)));
    }

    @Test
    public void findMatchInListTest_shouldNotFind() {
        var testSubject = getTestSubject(12);
        Optional<TestSubject> expectedResult = Optional.empty();
        Optional<TestSubject> result = findMatchInList(testSubjectList, testSubject, true);
        assertThat(result, is(equalTo(expectedResult)));
    }

    @Test
    public void isListSortedTest_shouldReturnTrue() {
        boolean isListSorted = isListSorted(sortedTestSubjectList);
        assertThat(isListSorted, is(equalTo(true)));
    }

    @Test
    public void isListSortedTest_shouldReturnFalse() {
        boolean isListSorted = isListSorted(testSubjectList);
        assertThat(isListSorted, is(equalTo(false)));
    }

    @Test
    public void mergeMapsTest_shouldMerge() {
        var map1 = new HashMap<String, Object>();
        map1.put("KEY1", getTestSubject(1));
        var map2 = new HashMap<String, Object>();
        map2.put("KEY2", getTestSubject(2));
        map2.put("KEY3", getTestSubject(3));
        var map3 = new HashMap<String, Object>();
        map3.put("KEY4", getTestSubject(4));
        map3.put("KEY5", getTestSubject(5));
        map3.put("KEY6", getTestSubject(6));
        var result = mergeMaps(map1, map2, map3);
        assertThat(result.keySet(), hasItems("KEY1", "KEY2", "KEY3", "KEY4", "KEY5", "KEY6"));
    }

    @Test(expected = AssertionError.class)
    public void mergeMapsTest_shouldThrowException() {
        var map1 = new HashMap<String, Object>();
        map1.put("KEY1", getTestSubject(1));
        var map2 = new HashMap<String, Object>();
        map2.put("KEY1", getTestSubject(2));
        mergeMaps(map1, map2);
    }

}