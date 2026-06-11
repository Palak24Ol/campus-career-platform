package com.campus.application.exception;

public class InvalidStatusTransitionException extends RuntimeException {
    public InvalidStatusTransitionException(String from, String to) {
        super("Cannot transition application from " + from + " to " + to);
    }
}