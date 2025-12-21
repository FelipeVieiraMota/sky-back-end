package com.sky.backend.authorization.controller;

import com.sky.backend.authorization.domain.dto.AuthenticationDto;
import com.sky.backend.authorization.domain.dto.LoginResponseDto;
import com.sky.backend.authorization.domain.dto.TokenDto;
import com.sky.backend.authorization.service.LoginService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/authorization")
@RequiredArgsConstructor
public class SecurityController {

    private final LoginService loginService;

    @GetMapping("/ping")
    public String pong() {
        return "Pong " + UUID.randomUUID();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody @Valid AuthenticationDto data){
        return ResponseEntity.ok(new LoginResponseDto(loginService.login(data)));
    }

    @PostMapping("/token-validation")
    public ResponseEntity<Boolean> tokenValidation(@RequestBody TokenDto data) {

        if(loginService.validateToken(data)) {
            return ResponseEntity.ok(true);
        }

        return ResponseEntity.ok(false);
    }
}
