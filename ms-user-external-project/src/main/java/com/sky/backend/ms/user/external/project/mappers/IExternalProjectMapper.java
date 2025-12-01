package com.sky.backend.ms.user.external.project.mappers;

import com.sky.backend.ms.user.external.project.domain.dto.response.UserExternalProjectResponseDto;
import com.sky.backend.ms.user.external.project.entity.UserExternalProject;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IExternalProjectMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "projectId", target = "projectId")
    @Mapping(source = "name", target = "name")
    UserExternalProjectResponseDto toDto(UserExternalProject entity);
}
