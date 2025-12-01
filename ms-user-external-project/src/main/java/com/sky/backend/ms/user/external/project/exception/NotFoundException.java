package com.sky.backend.ms.user.external.project.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message, Throwable exception) {
        super(message, exception);
    }

    public NotFoundException(String message) {
        super(message);
    }
}
