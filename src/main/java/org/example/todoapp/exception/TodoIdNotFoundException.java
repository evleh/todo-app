package org.example.todoapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class TodoIdNotFoundException extends RuntimeException{
    public TodoIdNotFoundException() {
    }

    public TodoIdNotFoundException(String message) {
        super(message);
    }

    public TodoIdNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public TodoIdNotFoundException(Throwable cause) {
        super(cause);
    }

    public TodoIdNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
