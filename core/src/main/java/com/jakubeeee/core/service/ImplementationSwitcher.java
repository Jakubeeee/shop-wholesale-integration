package com.jakubeeee.core.service;

import com.jakubeeee.core.mixin.Reloadable;
import com.jakubeeee.core.mixin.Switchable;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Component class used to switch {@link Switchable} implementations in {@link Reloadable} objects.
 */
@Component
public class ImplementationSwitcher {

    private final Map<String, Reloadable> reloaders;

    private final ApplicationContext context;

    public ImplementationSwitcher(ApplicationContext context, Map<String, Reloadable> reloaders) {
        this.context = context;
        this.reloaders = reloaders;
    }

    public void switchImplementations(String reloaderCode, Map<String, Class<? extends Switchable>> implementationTypesMap) {
        Map<String, Switchable> implementationsMap = new HashMap<>();
        implementationTypesMap.forEach((k, v) -> implementationsMap.put(k, context.getBean(v)));
        reloaders.forEach((k, v) -> v.reloadImplementations(reloaderCode, implementationsMap));
    }

}
