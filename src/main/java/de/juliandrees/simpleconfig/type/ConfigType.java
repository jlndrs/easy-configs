package de.juliandrees.simpleconfig.type;

/**
 * // TODO documentation
 *
 * @author Julian Drees
 */
public enum ConfigType {
    JSON("json"),
    YAML("yml")

    ;

    private final String extension;

    ConfigType(String extension) {
        this.extension = extension;
    }

    public String getExtension() {
        return extension;
    }
}
