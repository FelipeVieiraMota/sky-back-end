package com.sky.backend.authorization.domain.dto;

import com.sky.backend.authorization.domain.enums.UserRole;

public record RegisterDto(String login, String password, UserRole role) { }