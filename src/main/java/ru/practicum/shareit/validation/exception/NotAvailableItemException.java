package ru.practicum.shareit.validation.exception;

public class NotAvailableItemException extends RuntimeException {
    public NotAvailableItemException(final String message) {
        super(message);
    }
}