package com.jakubeeee.common.util;

import com.jakubeeee.testutils.marker.BehaviourUnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static com.jakubeeee.common.util.StringUtils.removeLastChar;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@Category(BehaviourUnitTest.class)
public class StringUtilsTest {

    @Test
    public void removeLastCharTest_shouldRemove() {
        String expectedResult = "abcdef";
        String result = removeLastChar("abcdefg");
        assertThat(result, is(equalTo(expectedResult)));
    }

    @Test(expected = NullPointerException.class)
    public void removeLastCharTest_shouldThrowException_1() {
        String expectedResult = "abcdef";
        String result = removeLastChar("");
        assertThat(result, is(equalTo(expectedResult)));
    }

    @Test(expected = NullPointerException.class)
    public void removeLastCharTest_shouldThrowException_2() {
        String expectedResult = "abcdef";
        String result = removeLastChar(null);
        assertThat(result, is(equalTo(expectedResult)));
    }

}
