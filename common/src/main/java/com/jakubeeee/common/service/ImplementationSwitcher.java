package com.jakubeeee.common.service;

import com.jakubeeee.common.misc.Reloadable;
import com.jakubeeee.common.misc.Switchable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ImplementationSwitcher {

    @Autowired
    Map<String, Reloadable> reloaders;

    @Autowired
    ApplicationContext context;

    public void switchImplementations(String reloaderCode, Map<String, Class<? extends Switchable>> implementationTypesMap) {
        Map<String, Switchable> implementationsMap = new HashMap<>();
        implementationTypesMap.forEach((k, v) -> implementationsMap.put(k, context.getBean(v)));
        reloaders.forEach((k, v) -> v.reloadImplementations(reloaderCode, implementationsMap));
    }

}
