package com.jakubeeee.core;

import java.util.Map;

/**
 * Interface for service beans used for switching {@link Switchable} implementations in {@link Reloadable} objects.
 */
public interface ImplementationSwitcherService {

    void switchImplementations(String reloaderCode, Map<String, Class<? extends Switchable>> implementationTypesMap);

}
