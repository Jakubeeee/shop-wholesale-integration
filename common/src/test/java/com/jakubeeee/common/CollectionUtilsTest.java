package com.jakubeeee.common;

import com.jakubeeee.testutils.TestSubject;
import com.jakubeeee.testutils.marker.BehavioralUnitTestCategory;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.*;
import java.util.function.Predicate;

import static com.jakubeeee.common.CollectionUtils.*;
import static com.jakubeeee.testutils.TestSubjectFactory.getTestSubject;
import static com.jakubeeee.testutils.TestSubjectFactory.getTestSubjects;
import static java.util.Collections.sort;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Category(BehavioralUnitTestCategory.class)
public class CollectionUtilsTest {

    private static List<TestSubject> TEST_SUBJECT_LIST;
    private static List<TestSubject> SORTED_TEST_SUBJECT_LIST;
    private static int TEST_LIST_SIZE;

    @BeforeClass
    public static void setUp() {
        TEST_LIST_SIZE = 10;
        TEST_SUBJECT_LIST = new ArrayList<>();
        TEST_SUBJECT_LIST.addAll(getTestSubjects(1, TEST_LIST_SIZE));
        SORTED_TEST_SUBJECT_LIST = new ArrayList<>(TEST_SUBJECT_LIST);
        sort(SORTED_TEST_SUBJECT_LIST);
    }

    @Test
    public void shallowCloneListTest_shouldClone() {
        var clonedList = shallowCloneList(TEST_SUBJECT_LIST);
        assertThat(TEST_SUBJECT_LIST, is(equalTo(clonedList)));
        assertThat(TEST_SUBJECT_LIST, is(not(sameInstance(clonedList))));
        for (int i = 0; i < clonedList.size(); i++)
            assertThat(TEST_SUBJECT_LIST.get(i), is(sameInstance(clonedList.get(i))));
    }

    @Test
    public void extractListTest_shouldExtract() {
        var testSubjectListCloned = new ArrayList<>(TEST_SUBJECT_LIST);
        var extractedList = extractList(testSubjectListCloned);
        assertThat(testSubjectListCloned, is(empty()));
        assertThat(TEST_SUBJECT_LIST.size(), is(equalTo(extractedList.size())));
    }

    @Test
    public void filterListTest_shouldFilter() {
        Predicate<TestSubject> predicate = testSubject -> testSubject.getUniqueId() > 3;
        List<TestSubject> localTestSubjectList = filterList(TEST_SUBJECT_LIST, predicate);
        assertThat(localTestSubjectList.size(), is(equalTo(TEST_LIST_SIZE - 3)));
        assertThat(localTestSubjectList, everyItem(hasProperty("uniqueId", greaterThan(3))));
    }

    @Test
    public void findMatchInListTest_shouldFind() {
        var testSubject = getTestSubject(5);
        Optional<TestSubject> expectedResultO = Optional.of(testSubject);
        Optional<TestSubject> result = findMatchInList(TEST_SUBJECT_LIST, testSubject, true);
        assertThat(result, is(equalTo(expectedResultO)));
    }

    @Test
    public void findMatchInListTest_shouldNotFind() {
        var testSubject = getTestSubject(12);
        Optional<TestSubject> expectedResult = Optional.empty();
        Optional<TestSubject> result = findMatchInList(TEST_SUBJECT_LIST, testSubject, true);
        assertThat(result, is(equalTo(expectedResult)));
    }

    @Test
    public void isListSortedTest_shouldReturnTrue() {
        boolean isListSorted = isListSorted(SORTED_TEST_SUBJECT_LIST);
        assertThat(isListSorted, is(equalTo(true)));
    }

    @Test
    public void isListSortedTest_shouldReturnFalse() {
        boolean isListSorted = isListSorted(TEST_SUBJECT_LIST);
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
    public void mergeMapsTest_shouldThrowError() {
        var map1 = new HashMap<String, Object>();
        map1.put("KEY1", getTestSubject(1));
        var map2 = new HashMap<String, Object>();
        map2.put("KEY1", getTestSubject(2));
        mergeMaps(map1, map2);
    }

    @Test
    public void sortedMapOfTest_shouldReturnSortedMap() {
        var map = new HashMap<String, Object>();
        map.put("KEY1", getTestSubject(1));
        Map<String, Object> sortedMap = sortedMapOf(map);
        assertThat(sortedMap, is(instanceOf(SortedMap.class)));
    }

}