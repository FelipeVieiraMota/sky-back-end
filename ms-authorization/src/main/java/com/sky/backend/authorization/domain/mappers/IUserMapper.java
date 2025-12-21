package com.sky.backend.authorization.domain.mappers;

import com.sky.backend.authorization.domain.dto.UserWithNoPasswordDto;
import com.sky.backend.authorization.domain.dto.UserDto;
import com.sky.backend.authorization.domain.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

@Mapper(componentModel = "spring")
public interface IUserMapper {

    UserDto toDto(User entity);

    @Mapping(target = "roles", expression = "java(authorities(dto))")
    UserWithNoPasswordDto toNoPass(UserDto dto);

    default List<String> authorities(UserDto dto) {
        return dto
            .authorities(dto.role())
            .stream()
            .map(GrantedAuthority::getAuthority).toList();
    }
}
