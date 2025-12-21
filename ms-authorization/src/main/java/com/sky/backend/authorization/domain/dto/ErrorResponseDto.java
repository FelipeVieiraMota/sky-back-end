package com.sky.backend.authorization.domain.dto;

import org.springframework.lang.NonNull;

import java.time.LocalDateTime;

public record ErrorResponseDto (
    LocalDateTime timestamp,
    int status,
    String error,
    String message
) {
    @Override
    @NonNull
    public String toString()
    {
        final var formattedText =
        """
            {
                "timestamp": %s,
                "status": "%s",
                "error": "%s",
                "message": %s
            }
        """;
        return String.format
        (
            formattedText,
            timestamp,
            status,
            error,
            message
        );
    }
}
