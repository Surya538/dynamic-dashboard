package com.axlero.dashboard.exceptions;

public class InvalidQueryException extends RuntimeException {
    public InvalidQueryException(String message) {
        super(message);
    }
}
