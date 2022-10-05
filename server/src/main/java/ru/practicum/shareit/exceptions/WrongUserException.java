package ru.practicum.shareit.exceptions;

public class WrongUserException extends RuntimeException {

    public WrongUserException(final String message) {
        super(message);
    }
}
