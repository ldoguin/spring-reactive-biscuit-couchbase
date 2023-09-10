package com.laudog.domain.error;

public class InvalidEntryException extends RuntimeException {
    public InvalidEntryException() {
        super("Invalid data provided");
    }
}
