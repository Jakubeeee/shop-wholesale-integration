package com.jakubeeee.core.impl;

import com.jakubeeee.core.Reloadable;
import com.jakubeeee.core.Switchable;
import com.jakubeeee.core.ImplementationSwitcherService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Default service bean used for switching {@link Switchable} implementations in {@link Reloadable} objects.
 */
@RequiredArgsConstructor
@Service
public class DefaultImplementationSwitcherService implements ImplementationSwitcherService {

    private final Map<String, Reloadable> reloaders;

    private final ApplicationContext context;

    public void switchImplementations(String reloaderCode,
                                      Map<String, Class<? extends Switchable>> implementationTypesMap) {
        Map<String, Switchable> implementationsMap = new HashMap<>();
        implementationTypesMap.forEach((k, v) -> implementationsMap.put(k, context.getBean(v)));
        reloaders.forEach((k, v) -> v.reloadImplementations(reloaderCode, implementationsMap));
    }

}
