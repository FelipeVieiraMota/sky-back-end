package com.sky.backend.authorization.service;

import com.sky.backend.authorization.domain.dto.AuthenticationDto;
import com.sky.backend.authorization.domain.dto.TokenDto;
import com.sky.backend.authorization.domain.dto.UserDto;
import com.sky.backend.authorization.domain.entity.User;
import com.sky.backend.authorization.domain.mappers.IUserMapper;
import com.sky.backend.authorization.exception.ForbiddenException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final AuthenticationManager authenticationManager;
    private final JWTTokenService tokenService;
    private final IUserMapper mapper;

    @Value("${api.security.token.secret}")
    private String secret;

    public String login(AuthenticationDto data) {
        try {
            final var usernamePassword = new UsernamePasswordAuthenticationToken(data.login(), data.password());
            final var authenticate = this.authenticationManager.authenticate(usernamePassword);
            final var principal = (User) authenticate.getPrincipal();
            final var dto = mapper.toDto(principal);
            return generateToken(dto);
        } catch (Throwable throwable) {
            throw new ForbiddenException(throwable.getMessage());
        }
    }

    private String generateToken(UserDto dto) {
        return "Bearer " + tokenService.generateToken(secret, dto);
    }

    public boolean validateToken(final TokenDto data) {
        return tokenService.validateToken(secret, data.token());
    }
}
