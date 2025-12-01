package com.sky.backend.ms.user.external.project.service;

import com.sky.backend.ms.user.external.project.domain.dto.response.UserExternalProjectResponseDto;
import com.sky.backend.ms.user.external.project.entity.User;
import com.sky.backend.ms.user.external.project.entity.UserExternalProject;
import com.sky.backend.ms.user.external.project.exception.NotFoundException;
import com.sky.backend.ms.user.external.project.mappers.IExternalProjectMapper;
import com.sky.backend.ms.user.external.project.repository.IUserExternalProjectRepository;
import com.sky.backend.ms.user.external.project.repository.IUserRepository;
import com.sky.backend.ms.user.external.project.service.contract.IUserExternalProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserExternalProjectService implements IUserExternalProjectService {

    private final IUserExternalProjectRepository userExternalProjectRepository;
    private final IUserRepository userRepository;
    private final IExternalProjectMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<UserExternalProjectResponseDto> findByUserId(Long userId) {
        return userExternalProjectRepository.findByUserId(userId).stream().map(mapper::toDto).toList();
    }

    @Override
    public UserExternalProjectResponseDto addExternalProject(Long userId, String name) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

        UserExternalProject project = new UserExternalProject();
        project.setUser(user);
        project.setName(name);

        return mapper.toDto(userExternalProjectRepository.save(project));
    }

    @Override
    public UserExternalProjectResponseDto updateExternalProjectName(Long userId, Long projectId, String newName) {

        UserExternalProject project = userExternalProjectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundException("External project not found with id: " + projectId));

        // se quiser garantir que o projeto pertence ao user:
        if (!project.getUser().getId().equals(userId)) {
            throw new NotFoundException("External project does not belong to user with id: " + userId);
        }

        project.setName(newName);
        return mapper.toDto(userExternalProjectRepository.save(project));
    }

    @Override
    public void removeExternalProject(Long userId, Long projectId) {

        UserExternalProject project = userExternalProjectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundException("External project not found with id: " + projectId));

        if (!project.getUser().getId().equals(userId)) {
            throw new NotFoundException("External project does not belong to user with id: " + userId);
        }

        userExternalProjectRepository.deleteById(projectId);
    }
}
