package com.innowise.config;

import com.innowise.exception.ConfigurationFileNotFoundException;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Loads and stores application configuration from 'application.yml'.
 * Provides access to different configuration sections.
 */

public class ConfigurationManager {
    private static final Map<String, Object> config;

    static {
        config = loadConfig();
    }

    private static Map<String, Object> loadConfig() {
        try (InputStream inputStream = ConfigurationManager.class.getClassLoader().getResourceAsStream("application.yml")) {
            if (inputStream == null) {
                throw new ConfigurationFileNotFoundException("Configuration file 'application.yml' not found in resources");
            }
            Yaml yaml = new Yaml(new LoaderOptions());
            return yaml.load(inputStream);
        } catch (IOException e) {
            throw new ConfigurationFileNotFoundException("Failed to load configuration file", e);
        }
    }

    public static Map<String, Object> getConfig() {
        return config;
    }

    public static DatabaseConfig getDatabaseConfig() {
        InputStream inputStream = ConfigurationManager.class.getClassLoader().getResourceAsStream("application.yml");

        if (inputStream == null) {
            throw new ConfigurationFileNotFoundException("Configuration file 'application.yml' not found in resources");
        }

        return new Yaml().loadAs(inputStream, DatabaseConfig.class);
    }

}
