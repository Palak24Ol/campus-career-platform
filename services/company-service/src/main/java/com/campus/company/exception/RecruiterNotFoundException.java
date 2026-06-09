package com.campus.company.exception;

public class RecruiterNotFoundException extends RuntimeException {
    public RecruiterNotFoundException(String message) {
        super(message);
    }
}