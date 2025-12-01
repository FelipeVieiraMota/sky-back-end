package com.sky.backend.ms.user.external.project.service;

import com.sky.backend.ms.user.external.project.domain.dto.request.UserRequestDto;
import com.sky.backend.ms.user.external.project.domain.dto.response.UserResponseDto;
import com.sky.backend.ms.user.external.project.entity.User;
import com.sky.backend.ms.user.external.project.exception.ConflictException;
import com.sky.backend.ms.user.external.project.exception.NotFoundException;
import com.sky.backend.ms.user.external.project.mappers.IUserMapper;
import com.sky.backend.ms.user.external.project.repository.IUserRepository;
import com.sky.backend.ms.user.external.project.service.contract.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final IUserRepository repository;
    private final IUserMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponseDto> findAll(int page, int size) {
        Page<User> result = repository.findAll(PageRequest.of(page, size));
        return result.map(mapper::toDto);
    }

    @Override
    public UserResponseDto findById(Long id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));

        return mapper.toDto(user);
    }

    @Override
    public UserResponseDto findByEmail(String email) {
        User user = repository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found with email: " + email));
        return mapper.toDto(user);
    }

    @Override
    public UserResponseDto create(UserRequestDto dto) {

        if (repository.existsByEmail(dto.email())) {
            throw new ConflictException("Email already in use: " + dto.email());
        }

        return mapper.toDto(repository.create(mapper.toEntity(dto)));
    }

    @Override
    public UserResponseDto update(Long id, UserRequestDto dto) {
        User existing = repository.findById(id).orElseThrow(() -> new NotFoundException("User not found with id: " + id));

        existing.setName(dto.name());
        existing.setPassword(dto.password());

        if (dto.email() != null && !dto.email().equals(existing.getEmail())) {
            if (repository.existsByEmail(dto.email())) {
                throw new ConflictException("Email already in use: " + dto.email());
            }
            existing.setEmail(dto.email());
        }

        return mapper.toDto(repository.create(existing));
    }

    @Override
    public void delete(Long id) {
        repository.delete(id);
    }
}
