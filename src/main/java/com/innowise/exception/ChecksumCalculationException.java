package com.innowise.exception;

public class ChecksumCalculationException extends MigrationException{

    public ChecksumCalculationException(String message) {
        super(message);
    }

    public ChecksumCalculationException(String message, Throwable cause) {
        super(message, cause);
    }
}
