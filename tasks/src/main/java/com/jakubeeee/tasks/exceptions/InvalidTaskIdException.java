package com.jakubeeee.tasks.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "There is no registered task with given id")
public class InvalidTaskIdException extends Exception {

    public InvalidTaskIdException(String message) {
        super(message);
    }
}
