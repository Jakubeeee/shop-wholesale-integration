package com.jakubeeee.common;

import com.jakubeeee.testutils.model.TestSubject;
import com.jakubeeee.testutils.marker.BehavioralUnitTestCategory;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static com.jakubeeee.common.LangUtils.nvl;
import static com.jakubeeee.testutils.factory.TestSubjectFactory.getTestSubject;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@Category(BehavioralUnitTestCategory.class)
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
