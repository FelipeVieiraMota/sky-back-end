package com.sky.backend.authorization.infra;

import com.sky.backend.authorization.service.JWTTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.lang.NonNull;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class SecurityFilter extends OncePerRequestFilter {

    private final JWTTokenService tokenService;

    @Value("${api.security.token.secret}")
    private String secret;

    @Override
    protected void doFilterInternal (
        @NonNull final HttpServletRequest request,
        @NonNull final HttpServletResponse response,
        @NonNull final FilterChain filterChain
    ) throws IOException {
        tokenService.checkTokenRoles(secret, request, response, filterChain);
    }
}
