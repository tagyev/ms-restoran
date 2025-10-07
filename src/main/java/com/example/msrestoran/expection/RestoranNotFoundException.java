package com.example.msrestoran.expection;

public class RestoranNotFoundException extends RuntimeException {
    public RestoranNotFoundException(String message) {
        super(message);
    }
}
