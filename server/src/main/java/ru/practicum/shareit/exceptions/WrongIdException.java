package ru.practicum.shareit.exceptions;

public class WrongIdException extends RuntimeException {

    public WrongIdException(final String message) {
        super(message);
    }
}
