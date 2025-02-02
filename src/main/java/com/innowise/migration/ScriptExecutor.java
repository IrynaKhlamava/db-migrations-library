package com.innowise.migration;

import com.innowise.exception.MigrationScriptException;
import com.innowise.migration.model.DatabaseChangeLogScript;
import com.innowise.util.ChecksumUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Handles the logic of executing a single database migration script.
 * It reads the script, checks if it's already applied, executes SQL commands, and updates the changelog.
 */

public class ScriptExecutor {

    private static final Logger logger = LogManager.getLogger(ScriptExecutor.class);

    private final ChangeLogManager changeLogManager;

    public ScriptExecutor(ChangeLogManager changeLogManager) {
        this.changeLogManager = changeLogManager;
    }

    public void executeScript(Connection connection, DatabaseChangeLogScript script) {

        String scriptPath = script.getFile();
        String version = script.getVersion();
        String scriptName = extractScriptName(scriptPath);

        String checksum = ChecksumUtil.calculateChecksum(scriptPath);

        if (changeLogManager.isScriptExecuted(scriptName, version, checksum, connection)) {
            logger.info("Script already executed: {}", scriptName);
            return;
        }

        try {
            runSql(connection, scriptPath);
            changeLogManager.markScriptExecuted(scriptName, version, checksum, connection);

        } catch (IOException | SQLException e) {
            logger.error("Error executing SQL script: {}", scriptName);
            throw new MigrationScriptException("Error executing SQL script: " + scriptName, e);
        }
    }

    private void runSql(Connection connection, String scriptPath) throws IOException, SQLException {
        try (BufferedReader reader = readScript(scriptPath);
             Statement statement = connection.createStatement()) {
            StringBuilder sql = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("--")) {
                    continue;
                }
                sql.append(line).append(" ");
                if (line.endsWith(";")) {
                    statement.execute(sql.toString());
                    sql.setLength(0);
                }
            }
        }
    }

    private BufferedReader readScript(String scriptPath) {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(scriptPath);
        if (inputStream == null) {
            throw new MigrationScriptException("File not found in resources: " + scriptPath);
        }
        return new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
    }

    private String extractScriptName(String scriptPath) {
        return scriptPath.substring(scriptPath.lastIndexOf("/") + 1);
    }

}
