package com.jakubeeee.testutils.marker;

import org.junit.experimental.categories.Category;

/**
 * Marker interface used as value of {@link Category#value()} annotation parameter.
 * Indicates that a test case consists of integration tests that require the creation
 * of a full test environment and check the compatibility of multiple components.
 */
public interface IntegrationTestCategory extends HeavyTestCategory {
}
