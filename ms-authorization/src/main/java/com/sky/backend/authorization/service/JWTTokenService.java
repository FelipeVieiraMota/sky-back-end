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
    public void processRequest (
        final HttpServletRequest request,
        final HttpServletResponse response,
        final FilterChain filterChain
    ) throws IOException {

        final String token = recoverToken(request);

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

        } catch (ExpiredJwtException exception) {
            log.warn (
                "JWT expired: method={} path={} ip={}",
                request.getMethod(),
                request.getRequestURI(),
                request.getRemoteAddr()
            );
            writeUnauthorized(response, "TOKEN_EXPIRED", "Token expired");
        } catch (JwtException exception) {
            log.warn (
                "JWT invalid: method={} path={} ip={} msg={}",
                request.getMethod(),
                request.getRequestURI(),
                request.getRemoteAddr(),
                exception.getMessage()
            );
            writeUnauthorized(response, "TOKEN_INVALID", "Invalid token");

        } catch (Exception exception) {
            log.error (
                "Unexpected auth error: method={} path={}",
                request.getMethod(),
                request.getRequestURI(),
                exception
            );
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
            token = filterToken(token);

            if (token == null || token.isBlank()){
                log.error("Token is empty: {}", token);
                return false;
            }
            extractClaims(token);
            return true;
        } catch (JwtException e) {
            e.printStackTrace();
            log.error(e.getMessage());
            log.error("Token {}", token);
            return false;
        }
    }

    private String filterToken(String token) {
        final var prefix = "Bearer ";

        if (token.startsWith(prefix)) {
            token = token.split(prefix)[1];
        }

        return token;
    }

    private Claims extractClaims(final String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
                .requireIssuer(issuer)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private String recoverToken(final HttpServletRequest request) {
        final var authHeader = request.getHeader("Authorization");
        if (authHeader == null) return null;

        final var prefix = "Bearer ";
        if (!authHeader.startsWith(prefix)) return null;

        final var token = authHeader.substring(prefix.length()).trim();
        return token.isEmpty() ? null : token;
    }

    private List<SimpleGrantedAuthority> extractAuthorities(final Claims claims) {

        final var  rolesObj = claims.get("roles");
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

    private void writeUnauthorized (
        final HttpServletResponse response,
        final String error,
        final String message
    ) throws IOException {

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
