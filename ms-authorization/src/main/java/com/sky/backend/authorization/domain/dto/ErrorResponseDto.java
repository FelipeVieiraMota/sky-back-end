package com.sky.backend.authorization.domain.dto;

import java.time.LocalDateTime;

public record ErrorResponseDto (
    LocalDateTime timestamp,
    int status,
    String error,
    String message
) {
    @Override
    public String toString()
    {
        final var message =
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
            message,
            timestamp,
            status,
            error,
            message
        );
    }
}
