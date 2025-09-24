package com.ancora.customerbookshelf.exception;

public class BookAlreadyInFavoritesException extends ConflictException {
    public BookAlreadyInFavoritesException(String message) {
        super(message);
    }
}
