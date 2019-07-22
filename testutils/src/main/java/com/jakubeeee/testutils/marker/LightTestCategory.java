package com.jakubeeee.testutils.marker;

import org.junit.experimental.categories.Category;

/**
 * Marker interface used as value of {@link Category#value()} annotation parameter.
 * Indicates that a test case consists of quick tests made to be launched by
 * maven-surefire-plugin.
 *
 * @apiNote this interface should not be used directly but as a parent of more specific marker interfaces
 */
interface LightTestCategory {
}
