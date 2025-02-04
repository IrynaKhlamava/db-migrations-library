package com.innowise.migration;

import com.innowise.exception.MigrationException;
import com.innowise.exception.MigrationScriptException;
import com.innowise.migration.model.DatabaseChangeLogScript;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Coordinates the migration process.
 * It locks the database, executes each script in a transaction, and commits changes.
 * If any script fails, it rolls back and releases the lock.
 **/

public class MigrationExecutor {

    private static final Logger logger = LogManager.getLogger(MigrationExecutor.class);

    private final ChangeLogLockManager changeLogLockManager;

    private final ScriptExecutor scriptExecutor;

    public MigrationExecutor(ChangeLogLockManager changeLogLockManager, ScriptExecutor scriptExecutor) {
        this.changeLogLockManager = changeLogLockManager;
        this.scriptExecutor = scriptExecutor;
    }

    public void executeMigration(Connection connection, List<DatabaseChangeLogScript> scripts) {
        try {
            setLock(connection);
            connection.setAutoCommit(false);
            runScripts(scripts, connection);
        } catch (SQLException e) {
            throw new MigrationException("Migration failed", e);
        } finally {
            removeLock(connection);
        }
    }

    private void runScripts(List<DatabaseChangeLogScript> scripts, Connection connection) {
        try {
            for (DatabaseChangeLogScript script : scripts) {
                scriptExecutor.executeScript(connection, script);
                connection.commit();
            }
        } catch (SQLException | MigrationScriptException e) {
            rollbackMigration(connection);
            throw new MigrationException("Migration failed, rollback executed", e);
        }
    }

    private void rollbackMigration(Connection connection) {
        try {
            connection.rollback();
            logger.info("Transaction rolled back successfully");
        } catch (SQLException e) {
            logger.error("Failed to rollback transaction", e);
        }
    }


    private void removeLock(Connection connection) {
        changeLogLockManager.removeLock(connection);
        logger.info("The lock is removed ");
    }

    private void setLock(Connection connection) {

        if (changeLogLockManager.isLocked(connection)) {
            throw new MigrationException("Database is already locked by another migration process");
        }
        try {
            changeLogLockManager.setLock(connection);
            logger.info("The lock is installed. Starting migrations...");
        } catch (Exception e) {
            throw new MigrationException("Failed to get database lock");
        }
    }

}

