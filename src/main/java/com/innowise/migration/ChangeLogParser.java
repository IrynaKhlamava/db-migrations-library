package com.innowise.migration;

import com.innowise.exception.ConfigurationFileNotFoundException;
import com.innowise.exception.DatabaseConfigurationException;
import com.innowise.exception.MigrationScriptException;
import com.innowise.migration.model.DatabaseChangeLogScript;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Loads database migration scripts from a YAML configuration file.
 * Reads and parses the 'databaseChangeLog' section from the specified file,
 * extracting script file paths and versions.
 * Returns a list of DatabaseChangeLogScript containing the script name and its version
 */

 public class ChangeLogParser {

    public List<DatabaseChangeLogScript> parse(String filePath) {
        try (InputStream inputStream = ChangeLogParser.class.getClassLoader().getResourceAsStream(filePath)) {
            if (inputStream == null) {
                throw new ConfigurationFileNotFoundException("File not found: " + filePath);
            }

            Yaml yaml = new Yaml();
            Map<String, Object> data = yaml.load(inputStream);
            if (data == null || !data.containsKey("databaseChangeLog")) {
                throw new DatabaseConfigurationException("Missing 'databaseChangeLog' section in YAML file: " + filePath);
            }

            Object scriptsDataFromFile = data.get("databaseChangeLog");

            if (!(scriptsDataFromFile instanceof List)) {
                throw new DatabaseConfigurationException("Invalid format: 'databaseChangeLog' must be a list");
            }

            List<Map<String, Map<String, String>>> allScripts =
                    (List<Map<String, Map<String, String>>>) scriptsDataFromFile;

            List<DatabaseChangeLogScript> scripts = new ArrayList<>();
            for (Map<String, Map<String, String>> entry : allScripts) {
                Map<String, String> include = entry.get("include");
                if (include == null || !include.containsKey("file") || !include.containsKey("version")) {
                    throw new MigrationScriptException("Invalid script record in YAML: " + entry);
                }

                String file = include.get("file");
                    String version = include.get("version");
                    scripts.add(new DatabaseChangeLogScript(file, version));

            }

            return scripts;
        } catch (IOException e) {
            throw new ConfigurationFileNotFoundException("Failed to load scripts file", e);
        }
    }
}

