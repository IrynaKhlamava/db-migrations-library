package com.innowise.exception;

public class DatabaseConnectionException extends MigrationException{

    public DatabaseConnectionException(String message) {
        super(message);
    }

    public DatabaseConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
