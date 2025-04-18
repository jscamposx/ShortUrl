package com.write_api.exceptions;

public class ShortIdCollisionException extends RuntimeException {
    public ShortIdCollisionException(String message) {
        super(message);
    }
}