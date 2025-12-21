package com.sky.backend.authorization.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sky.backend.authorization.domain.dto.ErrorResponseDto;
import com.sky.backend.authorization.domain.dto.UserDto;
import com.sky.backend.authorization.domain.mappers.IUserMapper;
import com.sky.backend.authorization.exception.TokenGenerationException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

@Service
@RequiredArgsConstructor
@Slf4j
public class JWTTokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    @Value("${api.security.token.issuer:auth-api}")
    private String issuer;

    @Value("${plus-hours:0}")
    private long plusHours;

    @Value("${content-type:application/json}")
    private String contentType;

    private final ObjectMapper objectMapper;
    private final IUserMapper mapper;

    /**
     * Use this method to validate JWT inside your API filter.
     */
    public void checkTokenRoles(
        final HttpServletRequest request,
        final HttpServletResponse response,
        final FilterChain filterChain
    ) throws IOException {

        String token = recoverToken(request);

        try {
            if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                Claims claims = extractClaims(token);
                var authorities = extractAuthorities(claims);
                var auth = new UsernamePasswordAuthenticationToken(
                    claims.getSubject(),
                    null,
                    authorities
                );

                SecurityContextHolder.getContext().setAuthentication(auth);
            }

            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            log.warn("JWT expired: method={} path={} ip={}",
                    request.getMethod(), request.getRequestURI(), request.getRemoteAddr());
            writeUnauthorized(response, "TOKEN_EXPIRED", "Token expired");

        } catch (JwtException e) {
            // inválido: assinatura, formato, etc.
            log.warn("JWT invalid: method={} path={} ip={} msg={}",
                    request.getMethod(), request.getRequestURI(), request.getRemoteAddr(), e.getMessage());
            writeUnauthorized(response, "TOKEN_INVALID", "Invalid token");

        } catch (Exception e) {
            // erro inesperado (bug)
            log.error("Unexpected auth error: method={} path={}",
                    request.getMethod(), request.getRequestURI(), e);
            writeUnauthorized(response, "UNAUTHORIZED", "Unauthorized access");
        }
    }

    public String generateBearerToken(final UserDto dto) {
        try {
            return "Bearer " + generateToken(dto);
        } catch (JsonProcessingException e) {
            throw new TokenGenerationException("Failed to serialize token subject", e);
        } catch (JwtException e) {
            throw new TokenGenerationException("Failed to generate JWT", e);
        } catch (Exception e) {
            throw new TokenGenerationException("Unexpected error generating bearer token", e);
        }
    }

    /**
     * Generates an access token for a subject with roles.
     */
    private String generateToken(final UserDto dto) throws JsonProcessingException {
        Instant now = Instant.now();
        String subject = dto.id();

        return Jwts.builder()
            .setIssuer(issuer)
            .setSubject(subject)
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(now.plus(plusHours, ChronoUnit.HOURS)))
            .claim("roles", mapper.authorities(dto))
            .claim("user", mapper.toNoPass(dto))
            .signWith(
                Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)),
                SignatureAlgorithm.HS256
            )
            .compact();
    }

    public boolean validateToken(String token) {
        try {
            extractClaims(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public String tokenSubject(String token) {
        return extractClaims(token).getSubject();
    }

    private Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
                .requireIssuer(issuer)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private String recoverToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null) return null;

        String prefix = "Bearer ";
        if (!authHeader.startsWith(prefix)) return null;

        String token = authHeader.substring(prefix.length()).trim();
        return token.isEmpty() ? null : token;
    }

    private List<SimpleGrantedAuthority> extractAuthorities(Claims claims) {
        Object rolesObj = claims.get("roles");
        if (rolesObj == null) return List.of();

        if (rolesObj instanceof List<?> list) {
            return list.stream()
                    .filter(Objects::nonNull)
                    .map(Object::toString)
                    .map(SimpleGrantedAuthority::new)
                    .toList();
        }

        return List.of(new SimpleGrantedAuthority(rolesObj.toString()));
    }

    private void writeUnauthorized(HttpServletResponse response, String error, String message) throws IOException {
        response.setStatus(SC_UNAUTHORIZED);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        var body = new ErrorResponseDto(
                LocalDateTime.now(),
                SC_UNAUTHORIZED,
                error,
                message
        );

        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
