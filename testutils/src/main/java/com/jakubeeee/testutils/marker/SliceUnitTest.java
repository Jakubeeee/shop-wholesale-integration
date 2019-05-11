package com.jakubeeee.testutils.marker;

import org.junit.experimental.categories.Category;

/**
 * Marker interface used as value of {@link Category#value()} annotation parameter.
 * Indicates that a test case consists of unit tests that use spring boot testing mechanisms to
 * partially initialize the beans container in order to unit test components such as controllers,
 * repositories and rest clients.
 */
public interface SliceUnitTest extends UnitTest {
}
