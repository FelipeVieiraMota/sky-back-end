package com.sky.backend.authorization.controller.advice;

import com.sky.backend.authorization.domain.dto.ErrorResponseDto;
import com.sky.backend.authorization.exception.BadRequestException;
import com.sky.backend.authorization.exception.ForbiddenException;
import com.sky.backend.authorization.exception.NotFoundException;
import com.sky.backend.authorization.exception.TokenGenerationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Optional;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleNotFoundException(final NotFoundException exception) {
        exception.printStackTrace();
        final var errorResponse = new ErrorResponseDto(
            LocalDateTime.now(),
            HttpStatus.NOT_FOUND.value(),
            HttpStatus.NOT_FOUND.name(),
            exception.getMessage()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(org.hibernate.exception.ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponseDto handleIntegrityViolation(DataIntegrityViolationException exception) {
        exception.printStackTrace();
        final var detailedMessage = Optional.ofNullable(exception.getRootCause())
                .map(Throwable::getMessage)
                .orElse(exception.getMessage());

        log.info(detailedMessage);

        return new ErrorResponseDto(
            LocalDateTime.now(),
            HttpStatus.CONFLICT.value(),
            HttpStatus.CONFLICT.name(),
            null
        );
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleBadRequestException(BadRequestException exception) {
        exception.printStackTrace();
        return new ErrorResponseDto (
            LocalDateTime.now(),
            HttpStatus.BAD_REQUEST.value(),
            HttpStatus.BAD_REQUEST.name(),
            exception.getMessage()
        );
    }

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponseDto handleForbiddenException(ForbiddenException exception) {
        exception.printStackTrace();
        return new ErrorResponseDto (
            LocalDateTime.now(),
            HttpStatus.FORBIDDEN.value(),
            HttpStatus.FORBIDDEN.name(),
            exception.getMessage()
        );
    }

    @ExceptionHandler(TokenGenerationException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponseDto handleTokenGenerationException(TokenGenerationException exception) {
        exception.printStackTrace();
        return new ErrorResponseDto (
            LocalDateTime.now(),
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            HttpStatus.INTERNAL_SERVER_ERROR.name(),
            exception.getMessage()
        );
    }
}