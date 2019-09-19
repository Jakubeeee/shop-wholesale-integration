package com.jakubeeee.core.mixin;


import com.jakubeeee.core.service.ImplementationSwitcherService;

import java.util.Map;

/**
 * Interface extended by service classes that have dependencies with dynamically switched types.
 *
 * @see ImplementationSwitcherService
 */
public interface Reloadable {

    void reloadImplementations(String reloaderCode, Map<String, ? extends Switchable> implementationsMap);

}
