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
    public void removeLastCharTest_stringBuilder_shouldRemove() {
        var expectedResult = new StringBuilder("abcdef");
        var result = removeLastChar(new StringBuilder("abcdefg"));
        assertThat(result.toString(), is(equalTo(expectedResult.toString())));
    }

    @Test(expected = NullPointerException.class)
    public void removeLastCharTest_stringBuilder_shouldThrowException_1() {
        var expectedResult = new StringBuilder("abcdef");
        var result = removeLastChar(new StringBuilder());
        assertThat(result.toString(), is(equalTo(expectedResult.toString())));
    }

    @Test(expected = NullPointerException.class)
    public void removeLastCharTest_stringBuilder_shouldThrowException_2() {
        var expectedResult = new StringBuilder("abcdef");
        var result = removeLastChar((StringBuilder) null);
        assertThat(result.toString(), is(equalTo(expectedResult.toString())));
    }

    @Test
    public void removeLastCharTest_string_shouldRemove() {
        String expectedResult = "abcdef";
        String result = removeLastChar("abcdefg");
        assertThat(result, is(equalTo(expectedResult)));
    }

    @Test(expected = NullPointerException.class)
    public void removeLastCharTest_string_shouldThrowException_1() {
        String expectedResult = "abcdef";
        String result = removeLastChar("");
        assertThat(result, is(equalTo(expectedResult)));
    }

    @Test(expected = NullPointerException.class)
    public void removeLastCharTest_string_shouldThrowException_2() {
        String expectedResult = "abcdef";
        String result = removeLastChar((String) null);
        assertThat(result, is(equalTo(expectedResult)));
    }

}
