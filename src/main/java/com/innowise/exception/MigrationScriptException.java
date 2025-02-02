package com.innowise.exception;

public class MigrationScriptException extends MigrationException{

    public MigrationScriptException(String message) {
        super(message);
    }

    public MigrationScriptException(String message, Throwable cause) {
        super(message, cause);
    }
}
