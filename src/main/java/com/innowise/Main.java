package com.innowise;

import com.innowise.database.DatabaseConnectionManager;
import com.innowise.exception.MigrationException;
import com.innowise.migration.MigrationManager;

import java.sql.Connection;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {
        try (Connection connection = DatabaseConnectionManager.getConnection()) {

            MigrationManager migrationManager = MigrationManager.buildDefaultManager();

            migrationManager.runMigrations(connection);

        } catch (SQLException e) {
            throw new MigrationException("Error during migration", e);
        }
    }
}