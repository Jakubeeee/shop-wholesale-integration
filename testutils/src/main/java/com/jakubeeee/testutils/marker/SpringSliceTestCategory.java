package com.jakubeeee.testutils.marker;

import org.junit.experimental.categories.Category;

/**
 * Marker interface used as value of {@link Category#value()} annotation parameter.
 * Indicates that a test case consists of tests that use spring boot mechanisms to
 * partially initialize the beans container in order to test spring components such as
 * controllers, rest clients and data jpa repositories.
 */
public interface SpringSliceTestCategory extends LightTestCategory {
}
