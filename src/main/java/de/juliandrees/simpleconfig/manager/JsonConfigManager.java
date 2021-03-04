package de.juliandrees.simpleconfig.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.juliandrees.simpleconfig.IConfigManager;
import de.juliandrees.simpleconfig.SimpleConfig;
import de.juliandrees.simpleconfig.type.ConfigType;

import java.io.File;

/**
 * // TODO documentation
 *
 * @author Julian Drees
 */
public class JsonConfigManager implements IConfigManager {
    private final Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();

    @Override
    public ConfigType getConfigType() {
        return ConfigType.JSON;
    }

    @Override
    public <C extends SimpleConfig> String transform(C config) {
        return gson.toJson(config);
    }

    @Override
    public <C extends SimpleConfig> C parse(String configString, Class<C> configClass) {
        return gson.fromJson(configString, configClass);
    }
}
