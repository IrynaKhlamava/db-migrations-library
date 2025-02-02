package com.innowise.database;

import com.innowise.config.ConfigurationManager;
import com.innowise.config.DatabaseConfig;
import com.innowise.exception.DatabaseConnectionException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Manages database connections by retrieving them from a connection pool.
 * Wraps each Connection in a dynamic proxy for logging.
 */

public class DatabaseConnectionManager {

    private static final Logger logger = LogManager.getLogger(DatabaseConnectionManager.class);

    private static final DatabaseConfig databaseConfig = ConfigurationManager.getDatabaseConfig();

    public static DatabaseConfig getDatabaseConfig() {
        return databaseConfig;
    }


    public static Connection getConnection() throws SQLException {
        try {
            Connection connection = ConnectionPoolManager.getDataSource().getConnection();
            logger.info("Connection obtained from connection pool");
            return ConnectionProxy.createProxy(connection);
        } catch (SQLException e) {
            logger.warn("Failed to connect to the database: " + e.getMessage());
            throw new DatabaseConnectionException("Failed to connect to the database", e);
        }
    }

}