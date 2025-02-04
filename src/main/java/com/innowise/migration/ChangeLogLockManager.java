package com.innowise.migration;

import com.innowise.exception.ChangeLogLockException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.UUID;

/**
 * Manages the database lock mechanism for migrations.
 * It ensures that only one migration process runs at a time by setting and releasing a lock
 * in the 'CHANGELOGLOCK' table.
 */


public class ChangeLogLockManager {

    private static final Logger logger = LogManager.getLogger(ChangeLogLockManager.class);

    private static final String COLUMN_LOCKED = "locked";

    private static boolean isLockTableChecked = false;

    private static boolean isLockTableExists = false;

    private static final String CHANGELOG_LOCK_TABLE = "changeloglock";

    private static final String SET_LOCK = "UPDATE CHANGELOGLOCK " +
            "SET LOCKED = TRUE, LOCKED_AT = ?, LOCKED_BY = ? " +
            "WHERE ID = 1 AND LOCKED = FALSE";

    private static final String UNLOCK_CHANGELOG = """
                UPDATE changeloglock
                SET locked = 0, locked_at = NULL, locked_by = NULL
                WHERE id = 1;
            """;

    private static final String CHECK_LOCK = "SELECT LOCKED FROM CHANGELOGLOCK WHERE ID = 1";

    private static final String CREATE_CHANGELOG_LOCK_TABLE = """
                CREATE TABLE IF NOT EXISTS changeloglock (
                    id INT PRIMARY KEY,
                    locked BOOLEAN NOT NULL,
                    locked_at TIMESTAMP NULL DEFAULT NULL,
                    locked_by VARCHAR(100) NULL DEFAULT NULL
                );
            """;

    private static final String INSERT_INITIAL_RECORD_INTO_CHANGELOGLOCK_TABLE = """
                INSERT INTO changeloglock (id, locked, locked_at, locked_by)
                VALUES (1, FALSE, NULL, NULL)
                ON DUPLICATE KEY UPDATE locked = VALUES(locked);
            """;

    public void initialize(Connection connection) {
        if (!isLockTableExists(connection)) {
            initializeLockTable(connection);
        } else {
            logger.info("CHANGELOGLOCK table already exists");
        }

    }

    private void initializeLockTable(Connection connection) {
        try (Statement statement = connection.createStatement()) {

            statement.execute(CREATE_CHANGELOG_LOCK_TABLE);
            logger.info("CHANGELOGLOCK table has been successfully created ");

            int rowsInserted = statement.executeUpdate(INSERT_INITIAL_RECORD_INTO_CHANGELOGLOCK_TABLE);
            if (rowsInserted > 0) {
                logger.info("Initial record inserted into CHANGELOGLOCK table");
            } else {
                logger.info("Initial record already exists in CHANGELOGLOCK table");
            }

            isLockTableExists = true;

        } catch (SQLException e) {
            throw new ChangeLogLockException("Failed to initialize CHANGELOGLOCK table", e);
        }
    }

    public boolean setLock(Connection connection) {
        try (PreparedStatement statement = connection.prepareStatement(SET_LOCK)) {
            statement.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            statement.setString(2, UUID.randomUUID().toString());
            int rowsUpdated = statement.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            throw new ChangeLogLockException("Failed to set lock for starting database migration", e);
        }
    }

    public void removeLock(Connection connection) {
        try (PreparedStatement statement = connection.prepareStatement(UNLOCK_CHANGELOG)) {
            statement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            throw new ChangeLogLockException("Failed to remove lock after database migration", e);
        }
    }

    public boolean isLocked(Connection connection) {
        try (PreparedStatement statement = connection.prepareStatement(CHECK_LOCK);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getBoolean(COLUMN_LOCKED);
            }
            return false;
        } catch (SQLException e) {
            throw new ChangeLogLockException("Failed to check lock status in CHANGELOGLOCK", e);
        }
    }

    public boolean isLockTableExists(Connection connection) {
        if (isLockTableChecked) {
            return isLockTableExists;
        }

        try (ResultSet resultSet = connection.getMetaData().getTables(null, null, CHANGELOG_LOCK_TABLE, null)) {

            isLockTableExists = resultSet.next();
            isLockTableChecked = true;

            return isLockTableExists;

        } catch (SQLException e) {
            throw new ChangeLogLockException("Failed to check if CHANGELOGLOCK table exists", e);
        }
    }

}
