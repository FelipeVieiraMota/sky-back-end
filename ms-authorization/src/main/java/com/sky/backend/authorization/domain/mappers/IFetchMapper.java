package com.sky.backend.authorization.domain.mappers;

import com.sky.backend.authorization.domain.dto.FetchUserDto;
import com.sky.backend.authorization.domain.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IFetchMapper {
    FetchUserDto toDto(User entity);
}
