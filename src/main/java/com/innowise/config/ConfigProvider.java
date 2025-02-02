package com.innowise.config;

import com.innowise.exception.ConfigurationLoadException;

import java.util.Map;

/**
 * Provides access to script configuration from 'application.yml'.
 */

public class ConfigProvider {

    public static String getScriptsPath() {
        Map<String, Object> config = ConfigurationManager.getConfig();
        if (!config.containsKey("scripts")) {
            throw new ConfigurationLoadException("Missing 'scripts' section in configuration file");
        }

        Map<String, String> scriptsConfig = (Map<String, String>) config.get("scripts");
        if (!scriptsConfig.containsKey("path")) {
            throw new ConfigurationLoadException("Missing 'path' key in 'scripts' section");
        }

        return scriptsConfig.get("path");
    }

}
