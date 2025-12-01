package com.sky.backend.ms.user.external.project.repository.jpa;

import com.sky.backend.ms.user.external.project.entity.User;
import com.sky.backend.ms.user.external.project.entity.UserExternalProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IUserExternalProjectJpaRepository extends JpaRepository<UserExternalProject, Long> {
    List<UserExternalProject> findByUser(User user);
    List<UserExternalProject> findByUser_Id(Long userId);
}

