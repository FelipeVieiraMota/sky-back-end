package com.sky.backend.authorization.domain.dto;

import java.util.List;

public record UserWithNoPasswordDto(
    String id,
    String login,
    List<String> roles
) {}