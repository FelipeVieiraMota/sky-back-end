package com.sky.backend.ms.user.external.project.service.contract;

import com.sky.backend.ms.user.external.project.domain.dto.response.UserExternalProjectResponseDto;

import java.util.List;

public interface IUserExternalProjectService {
    List<UserExternalProjectResponseDto> findByUserId(Long userId);
    UserExternalProjectResponseDto addExternalProject(Long userId, String name);
    UserExternalProjectResponseDto updateExternalProjectName(Long userId, Long projectId, String newName);
    void removeExternalProject(Long userId, Long projectId);
}
