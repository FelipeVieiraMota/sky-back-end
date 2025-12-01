package com.sky.backend.ms.user.external.project.domain.dto.request;

public record CreateExternalProjectRequestDto(
    String userId,
    String name
) {}