package org.example.todoapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class TodoAlreadyExcistsException extends RuntimeException {

    public TodoAlreadyExcistsException() {
    }

    public TodoAlreadyExcistsException(String message) {
        super(message);
    }

    public TodoAlreadyExcistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public TodoAlreadyExcistsException(Throwable cause) {
        super(cause);
    }

    public TodoAlreadyExcistsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
