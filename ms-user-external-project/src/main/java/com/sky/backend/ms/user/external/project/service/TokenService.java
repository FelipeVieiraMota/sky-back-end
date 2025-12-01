package com.sky.backend.ms.user.external.project.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.sky.backend.ms.user.external.project.exception.ForbiddenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TokenService {
    private static final String APPLICATION_JSON = "application/json";
    public static final String AUTH_API = "auth-api";

    public void checkTokenRoles(String secret, HttpServletRequest request,
                                HttpServletResponse response, FilterChain filterChain) throws IOException {
        try {
            val token = recoverToken(request);

            if (token != null) {
                val claims = extractClaims(secret, token);

                if (isTokenExpired(claims)) {
                    throw new ForbiddenException("Sem permissão.");
                }

                List<?> rawRoles = claims.get("roles", List.class);
                List<String> roles = rawRoles.stream()
                        .filter(String.class::isInstance)
                        .map(String.class::cast)
                        .toList();

                List<GrantedAuthority> autorizacao = getAuthorities(roles);

                val autenticacao = new UsernamePasswordAuthenticationToken(
                        claims.getSubject(),
                        null,
                        autorizacao
                );
                SecurityContextHolder.getContext().setAuthentication(autenticacao);
            }

            filterChain.doFilter(request, response);
        } catch (Exception exception) {
            exception.printStackTrace(); // <-- Isso ajuda!
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(APPLICATION_JSON);
            response.getWriter().write("{\"error\":\"Unauthorized access\"}");
        }

    }

    private static List<GrantedAuthority> getAuthorities(List<String> roles) {
        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    public Claims extractClaims(final String secret, final String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes()))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            throw new RuntimeException("Token validation failed", e);
        }
    }

    public boolean isTokenExpired(Claims claims) {
        return claims.getExpiration().before(new Date());
    }

    public String generateToken(String secret, UserDetails user){
        try{
            final List<String> roles = getRoles(user);
            final Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer(AUTH_API)
                    .withSubject(user.getUsername())
                    .withClaim("roles", roles)
                    .withExpiresAt(genExpirationDate())
                    .sign(algorithm);
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Error while generating token", exception);
        }
    }

    private static List<String> getRoles(UserDetails user) {
        final Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
    }

    public String tokenSubject(final String secret, final String token){
        try {
            return JWT.require(Algorithm.HMAC256(secret))
                    .withIssuer(AUTH_API)
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException exception){
            return "";
        }
    }

    public boolean validateToken(final String secret, final String token) {
        try {
            JWT.require(Algorithm.HMAC256(secret))
                    .withIssuer(AUTH_API)
                    .build()
                    .verify(token);
            return true;
        } catch (JWTVerificationException exception) {
            return false;
        }
    }

    private Instant genExpirationDate()  {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }

    public String recoverToken(HttpServletRequest request){
        var authHeader = request.getHeader("Authorization");
        if(authHeader == null) return null;
        return authHeader.replace("Bearer ", "");
    }
}