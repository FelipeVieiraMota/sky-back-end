package com.sky.backend.ms.user.external.project.repository;

import com.sky.backend.ms.user.external.project.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface IUserRepository {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    Page<User> findAll(Pageable pageable);
    Optional<User> findById(Long id);
    User create(User user);
    User update(Long id, User updated);
    void delete(Long id);
}
