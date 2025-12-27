package com.sky.backend.authorization.repository;

import com.sky.backend.authorization.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface IUserRepository extends JpaRepository<User, UUID> {
    UserDetails findByLogin(String login);
    User findUserById(UUID id);
}