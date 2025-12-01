package com.sky.backend.ms.user.external.project.repository;

import com.sky.backend.ms.user.external.project.entity.User;
import com.sky.backend.ms.user.external.project.entity.UserExternalProject;

import java.util.List;
import java.util.Optional;

public interface IUserExternalProjectRepository {
    List<UserExternalProject> findByUser(User user);
    List<UserExternalProject> findByUserId(Long userId);
    UserExternalProject save(UserExternalProject project);
    void deleteById(Long id);
    boolean existsById(Long id);
    Optional<UserExternalProject> findById(Long id);
}
