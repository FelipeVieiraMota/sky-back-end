package com.sky.backend.ms.user.external.project.mappers;

import com.sky.backend.ms.user.external.project.domain.dto.request.UserRequestDto;
import com.sky.backend.ms.user.external.project.domain.dto.response.UserResponseDto;
import com.sky.backend.ms.user.external.project.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IUserMapper {
    UserResponseDto toDto(User entity);
    User toEntity(UserRequestDto dto);
}
