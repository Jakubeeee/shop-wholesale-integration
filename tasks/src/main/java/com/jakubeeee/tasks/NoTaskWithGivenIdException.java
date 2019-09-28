package com.jakubeeee.tasks;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "There is no registered task with given id")
public class NoTaskWithGivenIdException extends Exception {

    public NoTaskWithGivenIdException(String message) {
        super(message);
    }
}
