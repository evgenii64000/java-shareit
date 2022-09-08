package ru.practicum.shareit.exceptions;

public class ItemUnavailableException extends RuntimeException {

    public ItemUnavailableException(final String message) {
        super(message);
    }
}
