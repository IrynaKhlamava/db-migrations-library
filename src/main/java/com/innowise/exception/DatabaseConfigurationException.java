package com.innowise.exception;

public class DatabaseConfigurationException extends MigrationException{

    public DatabaseConfigurationException(String message) {
        super(message);
    }

    public DatabaseConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
