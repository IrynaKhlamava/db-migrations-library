package com.innowise.exception;

public class ConfigurationFileNotFoundException extends MigrationException{

    public ConfigurationFileNotFoundException(String message) {
        super(message);
    }

    public ConfigurationFileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
