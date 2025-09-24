package com.ancora.customerbookshelf.exception;
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}