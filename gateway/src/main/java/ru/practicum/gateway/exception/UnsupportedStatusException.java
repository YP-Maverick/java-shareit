package ru.practicum.gateway.exception;

public class UnsupportedStatusException extends RuntimeException {
    public UnsupportedStatusException(String message) {
        super(message);
    }
}
