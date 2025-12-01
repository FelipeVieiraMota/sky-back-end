package com.sky.backend.ms.user.external.project.domain.dto.request;

import com.sky.backend.ms.user.external.project.domain.enums.UserRole;

public record UserRequestDto(
    String email,
    String password,
    String name,
    UserRole role
) {}
