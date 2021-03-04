package de.juliandrees.simpleconfig;

import java.io.File;

/**
 * // TODO documentation
 *
 * @author Julian Drees
 */
public interface IConfigService {
    /**
     * <p>Instantiate the config for the given class.</p>
     *
     * @param dataFolder the folder the configuration file is created in
     * @param fileName the name of the configuration file
     * @param configClass the class of the configuration object
     * @param <C> the type of the configuration object
     * @return the configuration
     */
    <C extends SimpleConfig> C instantiateConfig(File dataFolder, String fileName, Class<C> configClass);

    /**
     * <p>Saves the configuration.</p>
     *
     * @param config the configuration
     * @param <C> the type of the configuration object
     */
    <C extends SimpleConfig> void saveConfig(C config);
}
