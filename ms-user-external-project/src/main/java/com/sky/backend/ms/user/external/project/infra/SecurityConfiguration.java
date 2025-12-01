package com.sky.backend.ms.user.external.project.infra;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static org.springframework.http.HttpMethod.*;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration implements WebMvcConfigurer {

    private final SecurityFilter securityFilter;

    private static final String   ALL_ALLOWED      = "/**";
    private static final String   ALLOWED_HEADERS  = "*";
    private static final String   ALLOWED_ORIGINS  = "*";
    private static final String[] ALLOWED_METHODS  = { "GET", "POST", "PUT", "DELETE", "OPTIONS" };
    private static final String   ERROR_PATH       = "/error";
    private static final String   H2_CONSOLE       = "/h2-console/**";
    private static final String   ACTUATOR         = "/actuator/**";

    // http://localhost:[port]/v1/authorization/swagger-ui/index.html
    private static final String[] SWAGGER_WHITELIST = {
        "/api/v1/user-external-projects/api-docs/**",
        "/api/v1/user-external-projects/swagger-ui/**",
        "/api/v1/user-external-projects/swagger-ui.html"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(final HttpSecurity httpSecurity) throws Exception {
        return  httpSecurity
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                .authorizeHttpRequests(getAuthorizationManagerRequestMatcherRegistryCustomizer())
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            final AuthenticationConfiguration authenticationConfiguration
    ) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Override
    public void addCorsMappings(final CorsRegistry registry) {
        registry
            .addMapping(ALL_ALLOWED)
            .allowedOriginPatterns(ALLOWED_ORIGINS)
            .allowedMethods(ALLOWED_METHODS)
            .allowedHeaders(ALLOWED_HEADERS)
            .allowCredentials(true);
    }

    private Customizer<AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry>
    getAuthorizationManagerRequestMatcherRegistryCustomizer() {
        return authorize ->
                authorize
                        .requestMatchers(SWAGGER_WHITELIST).permitAll()
                        .requestMatchers(H2_CONSOLE).permitAll()
                        .requestMatchers(GET, ACTUATOR).permitAll()
                        .requestMatchers(ERROR_PATH).permitAll()
                        .requestMatchers(OPTIONS,ALL_ALLOWED).permitAll()
                        .anyRequest().authenticated();
    }
}
