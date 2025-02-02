package com.innowise.exception;

public class ChangeLogLockException extends MigrationException{

    public ChangeLogLockException(String message) {
        super(message);
    }

    public ChangeLogLockException(String message, Throwable cause) {
        super(message, cause);
    }
}
