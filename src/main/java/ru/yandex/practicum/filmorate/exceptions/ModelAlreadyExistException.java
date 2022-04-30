package ru.yandex.practicum.filmorate.exceptions;

public class ModelAlreadyExistException extends RuntimeException {
    public ModelAlreadyExistException(String s) {
        super(s);
    }
}
