package com.sky.backend.ms.user.external.project.service.contract;

import com.sky.backend.ms.user.external.project.domain.dto.request.UserRequestDto;
import com.sky.backend.ms.user.external.project.domain.dto.response.UserResponseDto;
import org.springframework.data.domain.Page;

public interface IUserService {
    Page<UserResponseDto> findAll(int page, int size);
    UserResponseDto findById(Long id);
    UserResponseDto findByEmail(String email);
    UserResponseDto create(UserRequestDto user);
    UserResponseDto update(Long id, UserRequestDto updated);
    void delete(Long id);
}
