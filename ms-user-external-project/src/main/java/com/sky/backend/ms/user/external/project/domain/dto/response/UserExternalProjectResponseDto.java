package com.sky.backend.ms.user.external.project.domain.dto.response;

public record UserExternalProjectResponseDto(
    Long userId,
    Long projectId,
    String name
) {}
