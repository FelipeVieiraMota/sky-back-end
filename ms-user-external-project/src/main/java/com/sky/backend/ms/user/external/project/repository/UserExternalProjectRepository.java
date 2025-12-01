package com.sky.backend.ms.user.external.project.repository;

import com.sky.backend.ms.user.external.project.entity.User;
import com.sky.backend.ms.user.external.project.entity.UserExternalProject;
import com.sky.backend.ms.user.external.project.repository.jpa.IUserExternalProjectJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserExternalProjectRepository implements IUserExternalProjectRepository {

    private final IUserExternalProjectJpaRepository jpa;

    @Override
    public List<UserExternalProject> findByUser(User user) {
        return jpa.findByUser(user);
    }

    @Override
    public List<UserExternalProject> findByUserId(Long userId) {
        return jpa.findByUser_Id(userId);
    }

    @Override
    public UserExternalProject save(UserExternalProject project) {
        return jpa.save(project);
    }

    @Override
    public void deleteById(Long id) {
        jpa.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return jpa.existsById(id);
    }

    @Override
    public Optional<UserExternalProject> findById(Long id) {
        return jpa.findById(id);
    }
}
