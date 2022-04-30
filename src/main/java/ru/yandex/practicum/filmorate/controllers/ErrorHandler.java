package ru.yandex.practicum.filmorate.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exceptions.ModelAlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleModelAlreadyExistException(final ModelAlreadyExistException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleRunTimeUserExists(final ValidationException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public FieldErrorResponse handleMethodArgumentNotValid(final MethodArgumentNotValidException e) {
        BindingResult result = e.getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors();
        return processFieldErrors(fieldErrors);
    }

    private FieldErrorResponse processFieldErrors(List<FieldError> fieldErrors) {
        FieldErrorResponse error = new FieldErrorResponse("Not valid fields");
        for (FieldError fieldError : fieldErrors) {
            error.addFieldError(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return error;
    }


    static class ErrorResponse {
        private final String error;

        public ErrorResponse(String error) {
            this.error = error;
        }

        public String getError() {
            return error;
        }
    }

    static class FieldErrorResponse {
        ErrorResponse errorResponse;
        private final Map<String, String> fieldErrors;

        public FieldErrorResponse(String error) {
            this.errorResponse = new ErrorResponse(error);
            this.fieldErrors = new HashMap<>();
        }

        public void addFieldError(String path, String message) {
            fieldErrors.put(path, message);
        }

        public Map<String, String> getFieldErrors() {
            return fieldErrors;
        }
    }
}
