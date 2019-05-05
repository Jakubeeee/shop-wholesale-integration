package com.jakubeeee.common.util;

import com.jakubeeee.testutils.model.TestSubject;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Field;

import static com.jakubeeee.common.util.ReflectUtils.getFieldValue;
import static com.jakubeeee.common.util.ReflectUtils.getMethod;
import static com.jakubeeee.testutils.utils.TestSubjectUtils.getTestSubject;

public class ReflectUtilsTest {

    private static Class<TestSubject> TEST_SUBJECT_CLASS;

    @BeforeClass
    public static void setUp() {
        TEST_SUBJECT_CLASS = TestSubject.class;
    }

    @Test
    public void getFieldValueTest_shouldAccess_1() throws IllegalAccessException {
        Field field = FieldUtils.getField(TEST_SUBJECT_CLASS, "uniqueId", true);
        getFieldValue(field, getTestSubject(1), TEST_SUBJECT_CLASS);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getFieldValueTest_shouldNotAccess_1() throws IllegalAccessException {
        Field field = FieldUtils.getField(TEST_SUBJECT_CLASS, "nonExistingField", true);
        getFieldValue(field, getTestSubject(1), TEST_SUBJECT_CLASS);
    }

    @Test
    public void getFieldValueTest_shouldAccess_2() throws IllegalAccessException {
        getFieldValue("uniqueId", getTestSubject(1), TEST_SUBJECT_CLASS);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getFieldValueTest_shouldNotAccess_2() throws IllegalAccessException {
        getFieldValue("nonExistingField", getTestSubject(1), TEST_SUBJECT_CLASS);
    }

    @Test
    public void getMethodTest_shouldFind() throws NoSuchMethodException {
        getMethod(TEST_SUBJECT_CLASS, "getUniqueId");
    }

    @Test(expected = NoSuchMethodException.class)
    public void getMethodTest_shouldNotFind() throws NoSuchMethodException {
        getMethod(TEST_SUBJECT_CLASS, "nonExistingMethod");
    }

}
