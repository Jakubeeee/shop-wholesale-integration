package com.jakubeeee.testutils.marker;

import org.junit.experimental.categories.Category;

/**
 * Marker interface used as value of {@link Category#value()} annotation parameter.
 * Indicates that a test case consists of unit tests that check the flow of methods,
 * i.e. whether the sequence of code execution is the same as the expected one for given parameters.
 */
public interface FlowControlUnitTest extends UnitTest {
}
