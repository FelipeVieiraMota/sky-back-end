package com.sky.backend.ms.user.external.project.controller;

import com.sky.backend.ms.user.external.project.domain.dto.request.CreateExternalProjectRequestDto;
import com.sky.backend.ms.user.external.project.domain.dto.request.UpdateExternalProjectNameRequestDto;
import com.sky.backend.ms.user.external.project.domain.dto.response.UserExternalProjectResponseDto;
import com.sky.backend.ms.user.external.project.service.contract.IUserExternalProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user-external-projects/users/{userId}/projects")
public class UserExternalProjectController {

    private final IUserExternalProjectService service;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserExternalProjectResponseDto> list(@PathVariable Long userId) {
        return service.findByUserId(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserExternalProjectResponseDto create(
        @PathVariable Long userId,
        @RequestBody CreateExternalProjectRequestDto request
    ){
        return  service.addExternalProject(userId, request.name());
    }

    @PutMapping("/{projectId}")
    @ResponseStatus(HttpStatus.OK)
    public UserExternalProjectResponseDto updateName(
        @PathVariable Long userId,
        @PathVariable Long projectId,
        @RequestBody UpdateExternalProjectNameRequestDto request
    ){
        return service.updateExternalProjectName(userId, projectId, request.name());
    }

    @DeleteMapping("/{projectId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
        @PathVariable Long userId,
        @PathVariable Long projectId
    ){
        service.removeExternalProject(userId, projectId);
    }
}