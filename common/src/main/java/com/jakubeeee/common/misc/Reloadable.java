package com.jakubeeee.common.misc;

import com.jakubeeee.common.service.ImplementationSwitcher;

import java.util.Map;

/**
 * An interface extended by service classes that have dependencies with dynamically switched types
 *
 * @see ImplementationSwitcher
 */
public interface Reloadable {

    void reloadImplementations(String reloaderCode, Map<String, ? extends Switchable> implementationsMap);

}
