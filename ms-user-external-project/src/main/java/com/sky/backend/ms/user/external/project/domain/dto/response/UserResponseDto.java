package com.sky.backend.ms.user.external.project.domain.dto.response;

import com.sky.backend.ms.user.external.project.domain.enums.UserRole;

public record UserResponseDto(
    Long id,
    String email,
    String name,
    UserRole role
) {}
