package com.sky.backend.authorization.infra;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class OpenApiConfig {

    @Value("${springdoc.details.security-header}")
    private String AUTHORIZATION;

    @Value("${springdoc.details.description}")
    private String DESCRIPTION;

    @Value("${springdoc.details.title}")
    private String TITLE;

    @Value("${springdoc.details.version}")
    private String VERSION;

    @Value("${springdoc.details.spring-doc-url}")
    private String SPRING_DOC_URL;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .addServersItem(new Server().url(SPRING_DOC_URL))
                .info(getInfo())
                .addSecurityItem(getSecurityRequirements(AUTHORIZATION))
                .components(getComponents());
    }

    private Components getComponents() {
        return new Components().addSecuritySchemes (
                AUTHORIZATION,
                getAuthorization()
        );
    }

    private SecurityScheme getAuthorization() {

        return new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.HEADER)
                .name(AUTHORIZATION)
                .description(DESCRIPTION);
    }

    private SecurityRequirement getSecurityRequirements(final String ...requirements) {
        final var securityRequirement = new SecurityRequirement();
        Arrays.asList(requirements).forEach(securityRequirement::addList);
        return securityRequirement;
    }

    private Info getInfo() {
        return new Info().title(TITLE).version(VERSION);
    }
}
