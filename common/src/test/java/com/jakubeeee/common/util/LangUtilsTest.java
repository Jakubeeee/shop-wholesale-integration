package com.jakubeeee.common.util;

import com.jakubeeee.testutils.model.TestSubject;
import org.junit.Test;

import static com.jakubeeee.common.util.LangUtils.nvl;
import static com.jakubeeee.testutils.utils.TestSubjectUtils.getTestSubject;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class LangUtilsTest {

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

}
