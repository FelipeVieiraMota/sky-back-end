package com.sky.backend.ms.user.external.project;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition( info = @Info(
    title = "MS User External Project API",
    version = "1.0.0",
    description = "User data API"
))
@SpringBootApplication
public class ApiUserProjectApplication {
	public static void main(String[] args) {
		SpringApplication.run(ApiUserProjectApplication.class, args);
	}
}