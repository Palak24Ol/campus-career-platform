package com.campus.application.exception;

public class EligibilityFailedException extends RuntimeException {
    public EligibilityFailedException(String reason) {
        super("Not eligible to apply: " + reason);
    }
}