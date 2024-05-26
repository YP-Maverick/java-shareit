package ru.practicum.shareit.validation.exception;

public class NotOwnerException extends RuntimeException {
    public NotOwnerException(final String message) {
        super(message);
    }
}