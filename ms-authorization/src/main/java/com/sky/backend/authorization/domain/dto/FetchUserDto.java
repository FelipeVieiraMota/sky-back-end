package com.sky.backend.authorization.domain.dto;

import com.sky.backend.authorization.domain.enums.UserRole;
import com.sky.backend.authorization.domain.interfaces.IAuthorities;

public record FetchUserDto(
    String id,
    String login,
    UserRole role
) implements IAuthorities { }