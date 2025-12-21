package com.sky.backend.authorization.service;

import com.sky.backend.authorization.domain.dto.AuthenticationDto;
import com.sky.backend.authorization.domain.dto.TokenDto;
import com.sky.backend.authorization.domain.entity.User;
import com.sky.backend.authorization.domain.mappers.IUserMapper;
import com.sky.backend.authorization.exception.ForbiddenException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final AuthenticationManager authenticationManager;
    private final JWTTokenService tokenService;
    private final IUserMapper mapper;

    public String login(AuthenticationDto data) {
        try {
            final var usernamePassword = new UsernamePasswordAuthenticationToken(data.login(), data.password());
            final var authenticate = this.authenticationManager.authenticate(usernamePassword);
            final var principal = (User) authenticate.getPrincipal();
            return tokenService.generateBearerToken(mapper.toDto(principal));
        } catch (Throwable throwable) {
            throw new ForbiddenException("FORBIDDEN");
        }
    }

    public boolean validateToken(final TokenDto data) {
        return tokenService.validateToken(data.token());
    }
}
