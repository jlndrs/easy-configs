package de.juliandrees.simpleconfig;

import de.juliandrees.simpleconfig.type.ConfigType;

/**
 * // TODO documentation
 *
 * @author Julian Drees
 */
public interface IConfigManager {

    /**
     * <p>Returns the config type for this manager</p>
     * @return the config type
     */
    ConfigType getConfigType();

    <C extends SimpleConfig> String transform(C config);

    <C extends SimpleConfig> C parse(String configString, Class<C> configClass);
}
