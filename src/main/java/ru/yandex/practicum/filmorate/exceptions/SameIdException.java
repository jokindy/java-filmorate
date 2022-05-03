package ru.yandex.practicum.filmorate.exceptions;

public class SameIdException extends RuntimeException {
    public SameIdException(String message) {
        super(message);
    }
}
