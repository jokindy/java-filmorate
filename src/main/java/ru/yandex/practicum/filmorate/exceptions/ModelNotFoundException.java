package ru.yandex.practicum.filmorate.exceptions;

public class ModelNotFoundException extends RuntimeException {

    public ModelNotFoundException(String message) {
        super(message);
    }
}
