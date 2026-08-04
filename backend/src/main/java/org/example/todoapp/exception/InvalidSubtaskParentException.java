package org.example.todoapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidSubtaskParentException extends RuntimeException {

    public InvalidSubtaskParentException() {
    }

    public InvalidSubtaskParentException(String message) {
        super(message);
    }

    public InvalidSubtaskParentException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidSubtaskParentException(Throwable cause) {
        super(cause);
    }

    public InvalidSubtaskParentException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
