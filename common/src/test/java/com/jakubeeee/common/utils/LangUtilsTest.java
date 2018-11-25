package com.jakubeeee.common.utils;


import com.jakubeeee.common.model.TestObject;
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
import static java.util.Collections.sort;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasProperty;

@RunWith(SpringRunner.class)
public class LangUtilsTest {

    private static List<TestObject> testObjectList;
    private static List<TestObject> sortedTestObjectList;

    @BeforeClass
    public static void setUp() {
        testObjectList = new ArrayList<>();
        testObjectList.add(new TestObject("aaa", "bbb", 25));
        testObjectList.add(new TestObject("555", "666", 10));
        testObjectList.add(new TestObject("ccc", "ddd", 67));
        testObjectList.add(new TestObject("bbb", "aaa", 3435));
        testObjectList.add(new TestObject("333", "222", -28));
        testObjectList.add(new TestObject("ttt", "uuu", 50));
        testObjectList.add(new TestObject("ooo", "ppp", 43));
        testObjectList.add(new TestObject("ggg", "fff", 0));
        testObjectList.add(new TestObject("mmm", "nnn", -4));
        sortedTestObjectList = new ArrayList<>();
        sortedTestObjectList.addAll(testObjectList);
        sort(sortedTestObjectList);
    }

    @Test
    public void nvlTest_shouldReturnNullable() {
        var nullableObject = new TestObject("a", "b", 0);
        var reserveObject = new TestObject("c", "d", 0);
        var result = nvl(nullableObject, reserveObject);
        assertThat(result, is(equalTo(nullableObject)));
    }

    @Test
    public void nvlTest_shouldReturnReserve() {
        TestObject nullableObject = null;
        var reserveObject = new TestObject("c", "d", 0);
        var result = nvl(nullableObject, reserveObject);
        assertThat(result, is(equalTo(reserveObject)));
    }

    @Test
    public void filterListTest() {
        Predicate<TestObject> predicate = testObject -> testObject.getIntField() > 20;
        List<TestObject> localTestObjectList = filterList(testObjectList, predicate);
        assertThat(localTestObjectList, everyItem(hasProperty("intField", greaterThan(20))));
    }

    @Test
    public void findMatchInListTest_shouldFind() {
        var testObject = new TestObject("ttt", "uuu", 50);
        Optional<TestObject> expectedStringO = Optional.of(testObject);
        Optional<TestObject> methodResult = findMatchInList(testObjectList, testObject, true);
        assertThat(methodResult, is(equalTo(expectedStringO)));
    }

    @Test
    public void findMatchInListTest_shouldNotFind() {
        var testObject = new TestObject("vvv", "www", 12);
        Optional<TestObject> expectedStringO = Optional.empty();
        Optional<TestObject> methodResult = findMatchInList(testObjectList, testObject, true);
        assertThat(methodResult, is(equalTo(expectedStringO)));
    }

    @Test
    public void isListSortedTest_shouldReturnTrue() {
        boolean isListSorted = isListSorted(sortedTestObjectList);
        assertThat(isListSorted, is(equalTo(true)));
    }

    @Test
    public void isListSortedTest_shouldReturnFalse() {
        boolean isListSorted = isListSorted(testObjectList);
        assertThat(isListSorted, is(equalTo(false)));
    }

    @Test
    public void mergeMapsTest_shouldMerge() {
        var map1 = new HashMap<String, Object>();
        map1.put("KEY1", "VALUE1");
        var map2 = new HashMap<String, Object>();
        map2.put("KEY2", "VALUE2");
        map2.put("KEY3", "VALUE3");
        var map3 = new HashMap<String, Object>();
        map3.put("KEY4", "VALUE4");
        map3.put("KEY5", "VALUE5");
        map3.put("KEY6", "VALUE6");
        var result = mergeMaps(map1, map2, map3);
        assertThat(result.keySet(), hasItems("KEY1", "KEY2", "KEY3", "KEY4", "KEY5", "KEY6"));
    }

    @Test(expected = AssertionError.class)
    public void mergeMapsTest_shouldThrowException() {
        var map1 = new HashMap<String, Object>();
        map1.put("KEY1", "VALUE1");
        var map2 = new HashMap<String, Object>();
        map2.put("KEY1", "VALUE1");
        var result = mergeMaps(map1, map2);
    }

}