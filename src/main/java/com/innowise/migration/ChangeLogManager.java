package com.innowise.migration;

import com.innowise.exception.ChangeLogException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.UUID;

/**
 * Manages the 'CHANGELOG' table, which stores information about applied migrations, preventing duplicate executions.
 */

public class ChangeLogManager {

    private static final Logger logger = LogManager.getLogger(ChangeLogManager.class);

    private static final String CHANGELOG_TABLE = "changelog";

    private static final String STATUS_SUCCESS = "SUCCESS";

    private boolean isChangeLogTableChecked = false;

    private boolean isChangeLogTableExist = false;

    private static final String CREATE_CHANGELOG_TABLE = """
                CREATE TABLE IF NOT EXISTS changelog (
                    id CHAR(36) PRIMARY KEY,
                    script_name VARCHAR(255) NOT NULL,
                    script_version VARCHAR(50) NOT NULL,
                    checksum VARCHAR(64) NOT NULL,
                    executed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    status VARCHAR(20) DEFAULT 'SUCCESS'
                );
            """;

    public void initialize(Connection connection) {
        if (!isChangeLogTableExist(connection)) {
            initializeChangeLogTable(connection);
        } else {
            logger.info("CHANGELOG table already exists. Skipping initialization");
        }
    }

    private void initializeChangeLogTable(Connection connection) {

        try (Statement statement = connection.createStatement()) {

            statement.execute(CREATE_CHANGELOG_TABLE);
            logger.info("CHANGELOG table has been successfully created");
            isChangeLogTableExist = true;

        } catch (SQLException e) {
            throw new ChangeLogException("Failed to initialize CHANGELOG table", e);
        }
    }

    private boolean isChangeLogTableExist(Connection connection) {
        if (isChangeLogTableChecked) {
            return isChangeLogTableExist;
        }

        try (ResultSet resultSet = connection.getMetaData().getTables(null, null, CHANGELOG_TABLE, null)) {

            isChangeLogTableExist = resultSet.next();
            isChangeLogTableChecked = true;

            return isChangeLogTableExist;

        } catch (SQLException e) {
            throw new ChangeLogException("Failed to check if CHANGELOG table exists", e);
        }
    }

    public boolean isScriptExecuted(String scriptName, String version, String checksum, Connection connection) {
        String isScriptExecutedQuery = "SELECT COUNT(*) FROM changelog WHERE script_name = ? AND script_version = ? AND checksum = ?";
        try (PreparedStatement stmt = connection.prepareStatement(isScriptExecutedQuery)) {

            stmt.setString(1, scriptName);
            stmt.setString(2, version);
            stmt.setString(3, checksum);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new ChangeLogException("Failed to check script execution status", e);
        }
        return false;
    }

    public void markScriptExecuted(String scriptName, String version, String checksum, Connection connection) {
        String markScriptExecutedQuery = """
                    INSERT INTO changelog (id, script_name, script_version, checksum, executed_at, status)
                    VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement stmt = connection.prepareStatement(markScriptExecutedQuery)) {

            stmt.setString(1, UUID.randomUUID().toString());
            stmt.setString(2, scriptName);
            stmt.setString(3, version);
            stmt.setString(4, checksum);
            stmt.setTimestamp(5, java.sql.Timestamp.valueOf(java.time.LocalDateTime.now()));
            stmt.setString(6, STATUS_SUCCESS);

            stmt.executeUpdate();
            logger.info("Script marked as executed: " + scriptName);

        } catch (SQLException e) {
            throw new ChangeLogException("Failed to mark script as executed", e);
        }
    }

}