package ru.practicum.shareit.exceptions;

public class WrongStatusException extends RuntimeException {

    public WrongStatusException(final String message) {
        super(message);
    }
}
