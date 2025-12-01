package com.sky.backend.ms.user.external.project.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.CONFLICT)
public class ConflictException extends RuntimeException {

    public ConflictException(String message, Throwable exception) {
        super(message, exception);
    }

    public ConflictException(String message) {
        super(message);
    }
}
