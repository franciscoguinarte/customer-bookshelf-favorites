package com.ancora.customerbookshelf.exception;

public class ExternalBookNotFoundException extends ResourceNotFoundException {
    public ExternalBookNotFoundException(String message) {
        super(message);
    }
}
