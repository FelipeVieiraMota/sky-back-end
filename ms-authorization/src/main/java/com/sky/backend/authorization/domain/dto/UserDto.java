package com.sky.backend.authorization.domain.dto;

import com.sky.backend.authorization.domain.enums.UserRole;
import com.sky.backend.authorization.domain.interfaces.IAuthorities;

public record UserDto(
    String id,
    String login,
    String password,
    UserRole role
) implements IAuthorities { }