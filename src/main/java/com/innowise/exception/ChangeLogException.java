package com.innowise.exception;

public class ChangeLogException extends MigrationException{

    public ChangeLogException(String message) {
        super(message);
    }

    public ChangeLogException(String message, Throwable cause) {
        super(message, cause);
    }
}
