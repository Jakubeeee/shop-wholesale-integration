package com.jakubeeee.testutils.marker;

import org.junit.experimental.categories.Category;

/**
 * Marker interface used as value of {@link Category#value()} annotation parameter.
 * Indicates that a test case consists of unit tests that check the behavior of methods,
 * i.e. whether they return an appropriate result for given parameters.
 * Those tests are flexible and are not impacted by implementation changes in tested methods.
 */
public interface BehavioralUnitTestCategory extends LightTestCategory {
}
