package com.sky.backend.ms.user.external.project.repository;

import com.sky.backend.ms.user.external.project.entity.User;
import com.sky.backend.ms.user.external.project.repository.jpa.IUserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserRepository implements IUserRepository {

    private final IUserJpaRepository jpa;

    @Override
    public Optional<User> findByEmail(String email) {
        return jpa.findByEmail(email);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpa.existsByEmail(email);
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        return jpa.findAll(pageable);
    }

    @Override
    public Optional<User> findById(Long id) {
        return jpa.findById(id);
    }

    @Override
    public User create(User user) {
        return jpa.save(user);
    }

    @Override
    public User update(Long id, User updated) {
        return jpa.save(updated);
    }

    @Override
    public void delete(Long id) {
        jpa.deleteById(id);
    }
}
