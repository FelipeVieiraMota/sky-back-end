package com.sky.backend.authorization.service;

import com.sky.backend.authorization.domain.dto.AuthenticationDto;
import com.sky.backend.authorization.domain.dto.TokenDto;
import com.sky.backend.authorization.domain.entity.User;
import com.sky.backend.authorization.domain.mappers.IUserMapper;
import com.sky.backend.authorization.exception.ForbiddenException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginService {

    private final AuthenticationManager authenticationManager;
    private final JWTTokenService tokenService;
    private final IUserMapper mapper;

    public boolean validateToken(final TokenDto data) {
        return tokenService.validateToken(data.token());
    }

    public String login(final AuthenticationDto data) {
        try {
            final var usernamePassword = usernamePassword(data);
            final var authenticate = authenticate(usernamePassword);
            final var principal = principal(authenticate);
            return generateBearerToken(principal);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            log.error(throwable.getMessage());
            throw new ForbiddenException("FORBIDDEN");
        }
    }

    private String generateBearerToken(final User principal) {
        return tokenService.generateBearerToken(mapper.toDto(principal));
    }

    private UsernamePasswordAuthenticationToken usernamePassword(final AuthenticationDto data) {
        return new UsernamePasswordAuthenticationToken(data.login(), data.password());
    }

    private Authentication authenticate(final UsernamePasswordAuthenticationToken usernamePassword) {
        return this.authenticationManager.authenticate(usernamePassword);
    }

    private User principal(final Authentication authenticate) {
        return (User) authenticate.getPrincipal();
    }
}
