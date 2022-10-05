package ru.practicum.shareit.exceptions;

public class WrongTimeException extends RuntimeException {

    public WrongTimeException(final String message) {
        super(message);
    }
}
