package com.sky.backend.authorization.domain.mappers;

import com.sky.backend.authorization.domain.dto.UserDto;
import com.sky.backend.authorization.domain.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IUserMapper {
    UserDto toDto(User entity);
}
