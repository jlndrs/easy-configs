package de.juliandrees.simpleconfig;

import de.juliandrees.simpleconfig.manager.JsonConfigManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * // TODO documentation
 *
 * @author Julian Drees
 */
public final class ConfigService implements IConfigService {
    private static ConfigService instance;
    private List<IConfigManager> configManagers = new ArrayList<>();
    private HashMap<SimpleConfig, ConfigContext> configContext = new HashMap<>();

    protected ConfigService() {

    }

    /**
     * <p>Instantiates the {@link ConfigService}.</p>
     * @return the config service
     */
    public static ConfigService getConfigService() {
        if (instance == null) {
            instance = new ConfigService();
            instance.setConfigManagers(instance.getSupportedTypes());

        }
        return instance;
    }

    @Override
    public <C extends SimpleConfig> C instantiateConfig(File dataFolder, String fileName, Class<C> configClass) {
        File configFile = createConfig(dataFolder, fileName, configClass);
        if (!configFile.exists()) {
            throw new IllegalArgumentException("config file does not exists!");
        }
        try {
            String content = readAllLines(configFile);
            IConfigManager configManager = getConfigManager(configFile.getName());
            C config = configManager.parse(content, configClass);
            configContext.put(config, new ConfigContext(configManager, configFile.getPath()));
            return config;
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public <C extends SimpleConfig> void saveConfig(C config) {
        Optional<ConfigContext> optionalContext = getConfigContext(config);
        if (optionalContext.isEmpty()) {
            throw new IllegalArgumentException("there is no config context for the config, it has not been instantiated by this ConfigService!");
        }
        ConfigContext context = optionalContext.get();
        File file = new File(context.getConfigFilePath());
        if (!file.exists()) {
            throw new NullPointerException("the file does not exists, did you delete it?");
        }
        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(context.getConfigManager().transform(config));
            fileWriter.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * <p>Reads all lines from the given {@link File}.</p>
     * @param file the file
     * @return the content for the file
     * @throws IOException if an I/O error occurs
     */
    protected String readAllLines(File file) throws IOException {
        StringBuilder builder = new StringBuilder();
        try (FileReader fileReader = new FileReader(file); BufferedReader reader = new BufferedReader(fileReader)) {
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
        }
        return builder.toString();
    }

    /**
     * <p>Creates the default config for the given class.</p>
     *
     * @param dataFolder the data folder the config should be created in
     * @param configName the name of the config file
     * @param configClass the class of the config object
     * @param <C> the type of the configuration object
     * @return the {@link File}, representing the config
     */
    @SuppressWarnings("all")
    protected <C extends SimpleConfig> File createConfig(File dataFolder, String configName, Class<C> configClass) {
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        File configFile = new File(dataFolder, configName);
        try {
            if (configFile.createNewFile()) {
                C defaultConfig = createDefault(configClass);
                writeDefaultConfig(configFile, defaultConfig);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return configFile;
    }

    /**
     * <p>Creates the default config object for the given type configClass.</p>
     * @param configClass the class type of the config object
     * @param <C> the type of the config object
     * @return the default config object
     */
    protected <C extends SimpleConfig> C createDefault(Class<C> configClass) {
        try {
            return configClass.getConstructor().newInstance();
        } catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new NullPointerException("can not instantiate default config");
        }
    }

    /**
     * <p>Writes the default config to the given file.</p>
     *
     * @param config the config file
     * @param defaultConfig the default config
     * @param <C> the type of the default config
     * @throws IOException if an I/O error occurs
     */
    protected <C extends SimpleConfig> void writeDefaultConfig(File config, C defaultConfig) throws IOException {
        String fileName = config.getName();
        IConfigManager configManager = getConfigManager(fileName);
        if (configManager == null) {
            throw new NullPointerException("can not save file of this type, there is no manager!");
        }
        String fileContent = configManager.transform(defaultConfig);
        try (FileWriter fileWriter = new FileWriter(config)) {
            fileWriter.write(fileContent);
            fileWriter.flush();
        }
    }

    /**
     * <p>Returns a list of the supported types, represented as a list of {@link IConfigManager}'s.</p>
     * @return the supported types
     */
    protected List<IConfigManager> getSupportedTypes() {
        List<IConfigManager> configManagers = new ArrayList<>();
        configManagers.add(new JsonConfigManager());
        return configManagers;
    }

    /**
     * <p>Sets the config managers.</p>
     * @param iConfigManagers the list of {@link IConfigManager}
     */
    private void setConfigManagers(List<IConfigManager> iConfigManagers) {
        this.configManagers = iConfigManagers;
    }

    /**
     * <p>Returns the config manager for the given fileName by the extension of the file name.</p>
     * @param fileName the file name
     * @return the config maanager
     */
    protected IConfigManager getConfigManager(String fileName) {
        String extension = getExtension(fileName);
        return configManagers.stream().filter((configManager) -> configManager.getConfigType().getExtension().equalsIgnoreCase(extension)).findFirst().orElse(null);
    }

    /**
     * <p>Parses the extension of the file for the given fileName.</p>
     * @param fileName the file's names
     * @return the extension for the file name
     */
    protected String getExtension(String fileName) {
        String[] parts = fileName.split("\\.");
        if (parts.length > 0) {
            return parts[parts.length - 1];
        }
        throw new NullPointerException("fileName is not valid!");
    }

    /**
     * <p>Parses the extension of the file.</p>
     * @param file the file
     * @return the extension for the file
     */
    protected String getExtension(File file) {
        if (file == null) {
            throw new IllegalArgumentException("file must not be null");
        }
        return getExtension(file.getName());
    }

    /**
     * <p>Returns the {@link ConfigContext} for the given {@link SimpleConfig}.
     * If no Context is found, an empty {@link Optional} will be returned.</p>
     *
     * @param config the config
     * @param <C> the type of the config
     * @return the {@link Optional} config context
     */
    protected <C extends SimpleConfig> Optional<ConfigContext> getConfigContext(C config) {
        return Optional.ofNullable(configContext.get(config));
    }
}
