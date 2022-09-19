package ru.practicum.shareit.exceptions;

public class WrongParameterException extends RuntimeException {

    public WrongParameterException(final String message) {
        super(message);
    }
}
