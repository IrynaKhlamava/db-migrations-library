package com.innowise.exception;

public class ConfigurationLoadException extends MigrationException{

    public ConfigurationLoadException(String message) {
        super(message);
    }

    public ConfigurationLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}
