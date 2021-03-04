package de.juliandrees.simpleconfig;

/**
 * // TODO documentation
 *
 * @author Julian Drees
 */
public class ConfigContext {
    private final IConfigManager configManager;
    private final String configFilePath;

    public ConfigContext(IConfigManager configManager, String configFilePath) {
        super();
        this.configManager = configManager;
        this.configFilePath = configFilePath;
    }

    public IConfigManager getConfigManager() {
        return configManager;
    }

    public String getConfigFilePath() {
        return configFilePath;
    }
}
