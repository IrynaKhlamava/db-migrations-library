package com.innowise.database;

import com.innowise.exception.DatabaseConnectionException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

/**
 * Configures and manages a HikariCP connection pool.
 * It initializes a single DataSource instance with database credentials and pool settings
 * when the class is loaded. Provides getDataSource() to efficiently obtain and reuse database connections
*/

public class ConnectionPoolManager {

    private static HikariDataSource dataSource;

    static {
        try {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(DatabaseConnectionManager.getDatabaseConfig().getDatabase().getUrl());
            config.setUsername(DatabaseConnectionManager.getDatabaseConfig().getDatabase().getUsername());
            config.setPassword(DatabaseConnectionManager.getDatabaseConfig().getDatabase().getPassword());
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            config.setIdleTimeout(30000);
            config.setMaxLifetime(1800000);

            dataSource = new HikariDataSource(config);
        } catch (DatabaseConnectionException e) {
            throw new DatabaseConnectionException("Failed to initialize connection pool", e);
        }
    }

    public static DataSource getDataSource() {
        return dataSource;
    }

}
