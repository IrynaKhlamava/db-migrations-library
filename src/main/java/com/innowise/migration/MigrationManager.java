package com.innowise.migration;

import com.innowise.config.ConfigProvider;
import com.innowise.migration.model.DatabaseChangeLogScript;

import java.sql.Connection;
import java.util.List;

/**
 * Manages the execution of database migrations.
 * Initializes the changelog and lock tables before running migrations.
 * Ensures that migrations are executed sequentially and prevents concurrent modifications
 * using a lock mechanism.
 */

 public class MigrationManager {
    private final ChangeLogManager changeLogManager;

    private final ChangeLogLockManager changeLogLockManager;

    private final MigrationExecutor migrationExecutor;

    private final ChangeLogParser changeLogParser;

    public static MigrationManager buildDefaultManager() {
        ChangeLogManager changeLogManager = new ChangeLogManager();
        ChangeLogLockManager lockManager = new ChangeLogLockManager();
        ScriptExecutor scriptExecutor = new ScriptExecutor(changeLogManager);
        MigrationExecutor executor = new MigrationExecutor(lockManager, scriptExecutor);
        ChangeLogParser changeLogParser= new ChangeLogParser();
        return new MigrationManager(changeLogManager, lockManager, executor, changeLogParser);
    }

    public MigrationManager(ChangeLogManager changeLogManager, ChangeLogLockManager changeLogLockManager,
                            MigrationExecutor migrationExecutor, ChangeLogParser changeLogParser) {
        this.changeLogManager = changeLogManager;
        this.changeLogLockManager = changeLogLockManager;
        this.migrationExecutor = migrationExecutor;
        this.changeLogParser = changeLogParser;
    }

    public void runMigrations(Connection connection) {

            changeLogLockManager.initialize(connection);

            changeLogManager.initialize(connection);

            List<DatabaseChangeLogScript> scripts = changeLogParser.parse(ConfigProvider.getScriptsPath());

            migrationExecutor.executeMigration(connection, scripts);

    }
}
