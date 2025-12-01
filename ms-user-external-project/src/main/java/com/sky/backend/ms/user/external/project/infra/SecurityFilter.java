package com.sky.backend.ms.user.external.project.infra;

import com.sky.backend.ms.user.external.project.service.TokenService;
import io.micrometer.common.lang.NonNullApi;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@NonNullApi
public class SecurityFilter extends OncePerRequestFilter {

    private final TokenService tokenService;

    @Value("${api.security.token.secret}")
    private String secret;

    @Override
    protected void doFilterInternal(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final FilterChain filterChain
    ) throws IOException {
        tokenService.checkTokenRoles(secret, request, response, filterChain);
    }
}
