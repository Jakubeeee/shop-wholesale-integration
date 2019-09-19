package com.jakubeeee.core.service;

import com.jakubeeee.core.mixin.Reloadable;
import com.jakubeeee.core.mixin.Switchable;

import java.util.Map;

/**
 * Interface for service beans used for switching {@link Switchable} implementations in {@link Reloadable} objects.
 */
public interface ImplementationSwitcherService {

    void switchImplementations(String reloaderCode, Map<String, Class<? extends Switchable>> implementationTypesMap);

}
